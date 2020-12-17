package com.reason.ide.annotations;

import com.intellij.lang.*;
import com.intellij.lang.annotation.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.impl.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.problems.*;
import com.intellij.psi.*;
import com.reason.*;
import com.reason.bs.*;
import com.reason.hints.*;
import com.reason.ide.files.*;
import com.reason.ide.hints.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static com.reason.ide.annotations.ErrorAnnotator.*;

public class ErrorAnnotator extends ExternalAnnotator<InitialInfo, AnnotationResult> implements DumbAware {
    private static final Log LOG = Log.create("annotator");

    @Override
    public @Nullable InitialInfo collectInformation(@NotNull PsiFile psiFile, @NotNull Editor editor, boolean hasErrors) {
        if (hasErrors) {
            LOG.info("Annotator was initialized with errors. This isn't supported.");
            return null;
        }

        Project project = psiFile.getProject();
        VirtualFile sourceFile = psiFile.getVirtualFile();

        // create temporary compilation directory
        File tempCompilationDirectory = getOrCreateTempDirectory(project);

        Optional<VirtualFile> contentRootOpt = BsPlatform.findContentRootForFile(project, sourceFile);
        Optional<VirtualFile> libRoot = contentRootOpt.map(root -> root.findFileByRelativePath("lib/bs"));
        if (!libRoot.isPresent()) {
            LOG.info("Unable to find BuckleScript lib root.");
            return null;
        }
        VirtualFile contentRoot = contentRootOpt.get();

        // Read bsConfig to get the compilation directives
        VirtualFile bsConfigFile = contentRoot.findFileByRelativePath(BsConfigJsonFileType.FILENAME);
        BsConfig config = bsConfigFile == null ? null : BsConfigReader.read(bsConfigFile);
        if (config == null) {
            LOG.info("No bsconfig.json found for content root: " + contentRoot);
            return null;
        }

        String jsxVersion = config.getJsxVersion();
        String namespace = config.getNamespace();

        // If a directory is marked as dev-only, it won't be built and exposed to other "dev"
        // directories in the same project
        // https://bucklescript.github.io/docs/en/build-configuration#sources
        // @TODO register a file listener and read the values from memory
        BsCompiler bucklescript = ServiceManager.getService(project, BsCompiler.class);
        Ninja ninja = bucklescript.readNinjaBuild(contentRoot);
        for (String devSource : config.getDevSources()) {
            VirtualFile devFile = contentRoot.findFileByRelativePath(devSource);
            if (devFile != null && FileUtil.isAncestor(devFile.getPath(), sourceFile.getPath(), true)) {
                ninja.addInclude(devSource);
            }
        }

        // Creates a temporary file on disk with a copy of the current document.
        // It'll be used by bsc for a temporary compilation

        File sourceTempFile;
        try {
            sourceTempFile = FileUtil.createTempFile(tempCompilationDirectory, sourceFile.getNameWithoutExtension(), "." + sourceFile.getExtension());
        } catch (IOException e) {
            LOG.info("Temporary file creation failed", e); // log error but do not show it in UI
            return null;
        }

        try {
            FileUtil.writeToFile(sourceTempFile, psiFile.getText().getBytes());
        }
        catch (IOException e) {
            // Sometimes, file is locked by another process, not a big deal, skip it
            LOG.trace("Write failed: " + e.getLocalizedMessage());
            return null;
        }

        LOG.trace("Wrote contents to temporary file", sourceTempFile);

        String tempNameWithoutExtension = FileUtil.getNameWithoutExtension(sourceTempFile);
        File cmtFile = new File(sourceTempFile.getParent(), tempNameWithoutExtension + ".cmt");

        List<String> arguments = new ArrayList<>();
        arguments.add("-bs-super-errors");
        arguments.add("-color");
        arguments.add("never");
        arguments.addAll(ninja.getPkgFlags());
        arguments.addAll(ninja.getBscFlags());
        for (String ppxPath : ninja.getPpxIncludes()) {
            arguments.add("-ppx");
            arguments.add(ppxPath);
        }
        if (!namespace.isEmpty()) {
            arguments.add("-bs-ns");
            arguments.add(namespace);
        }
        if (jsxVersion != null) {
            arguments.add("-bs-jsx");
            arguments.add(jsxVersion);
        }
        for (String bscInclude : ninja.getIncludes()) {
            arguments.add("-I");
            arguments.add(bscInclude);
        }
        arguments.add("-o");
        arguments.add(cmtFile.getPath());
        arguments.add("-bin-annot");
        arguments.add(sourceTempFile.getPath());

        return new InitialInfo(psiFile, libRoot.get(), sourceTempFile, editor, arguments);

    }

    @Override
    public @Nullable AnnotationResult doAnnotate(@NotNull InitialInfo initialInfo) {
        PsiFile sourcePsiFile = initialInfo.sourcePsiFile;
        Project project = sourcePsiFile.getProject();
        VirtualFile sourceFile = sourcePsiFile.getVirtualFile();

        long compilationStartTime = System.currentTimeMillis();

        BscProcess bscProcess = BscProcess.getInstance(project);
        BscProcessListener bscListener = new BscProcessListener();

        Integer exitCode = bscProcess.run(sourceFile, initialInfo.libRoot, initialInfo.arguments, bscListener);
        if (LOG.isTraceEnabled()) {
            LOG.trace("Compilation done in " + (System.currentTimeMillis() - compilationStartTime) + "ms");
        }

        File baseFile = getBaseFile(initialInfo.tempFile);
        LOG.debug("Clear temporary files from base", baseFile);
        FileUtil.delete(initialInfo.tempFile);
        FileUtil.delete(new File(baseFile.getPath() + ".cmi"));
        FileUtil.delete(new File(baseFile.getPath() + ".cmj"));
        // cmt is never deleted

        if (exitCode != null && exitCode == 0) {
            // No error/warning found, nothing to display.
            return new AnnotationResult(Collections.emptyList(), initialInfo);
        }

        List<OutputInfo> outputInfo = bscListener.getInfo();
        LOG.debug("Found info", outputInfo);

        return new AnnotationResult(outputInfo, initialInfo);
    }

    @Override
    public void apply(@NotNull PsiFile sourcePsiFile, @NotNull AnnotationResult annotationResult, @NotNull AnnotationHolder holder) {
        Project project = sourcePsiFile.getProject();
        VirtualFile sourceFile = sourcePsiFile.getVirtualFile();
        List<OutputInfo> outputInfo = annotationResult.outputInfo;
        Editor editor = annotationResult.initialInfo.editor;
        File cmtFile = new File(getBaseFile(annotationResult.initialInfo.tempFile) + ".cmt");

        List<Annotation> annotations =
                outputInfo.stream()
                        .map(info -> makeAnnotation(info, editor))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

        WolfTheProblemSolver problemSolver = WolfTheProblemSolver.getInstance(project);
        Collection<Problem> problems = new ArrayList<>();

        if (annotations.isEmpty()) {
            LOG.trace("Clear problems");
            problemSolver.clearProblems(sourceFile);
            // Call rincewind on the generated cmt file !
            updateCodeLens(project, sourcePsiFile.getLanguage(), sourceFile, cmtFile);
        } else {
            for (Annotation annotation : annotations) {
                if (annotation.isError) {
                    holder
                            .newAnnotation(HighlightSeverity.ERROR, annotation.message)
                            .range(annotation.range)
                            .create();
                    // mark error in Project View
                    problems.add(
                            problemSolver.convertToProblem(sourceFile, annotation.startPos.line, annotation.startPos.column, new String[]{annotation.message}));
                    // Remove hint with corresponding line
                    ApplicationManager.getApplication().invokeLater(
                            () -> ReadAction.run(
                                    () -> InferredTypesService.getSignatures(sourceFile).remove(annotation.startPos.line)));
                } else {
                    holder
                            .newAnnotation(HighlightSeverity.WARNING, annotation.message)
                            .range(annotation.range)
                            .create();
                }
            }

            problemSolver.reportProblems(sourceFile, problems);
        }

    }

    private void updateCodeLens(@NotNull Project project, @NotNull Language lang, @NotNull VirtualFile sourceFile, @NotNull File cmtFile) {
        InsightManager insightManager = ServiceManager.getService(project, InsightManager.class);
        if (insightManager != null && !FileHelper.isInterface(sourceFile.getFileType())) {
            insightManager.queryTypes(sourceFile, cmtFile.toPath(), types -> {
                LOG.debug("Updating signatures in user data cache for file", sourceFile);
                InferredTypesService.getSignatures(sourceFile).putAll(types.signaturesByLines(lang));
            });
        }
    }

    private static @Nullable Annotation makeAnnotation(@NotNull OutputInfo info, @NotNull Editor editor) {
        int colStart = info.colStart;
        int colEnd = info.colEnd;
        int lineStart = info.lineStart;
        int lineEnd = info.lineEnd;
        LogicalPosition start = new LogicalPosition(lineStart < 1 ? 0 : lineStart - 1, colStart < 1 ? 0 : colStart);
        LogicalPosition end = new LogicalPosition(lineEnd < 1 ? 0 : lineEnd - 1, colEnd < 1 ? 0 : colEnd);
        int startOffset = editor.logicalPositionToOffset(start);
        int endOffset = editor.logicalPositionToOffset(end);

        if (0 < startOffset && 0 < endOffset && startOffset <= endOffset) {
            TextRangeInterval range = new TextRangeInterval(startOffset - 1, endOffset - 1);
            String message = info.message.replace('\n', ' ').replaceAll("\\s+", " ").trim();
            if (LOG.isDebugEnabled()) {
                LOG.debug("annotate " + startOffset + ":" + endOffset + " '" + message + "'");
            }
            return new Annotation(info.isError, message, range, start);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Failed to locate info: "
                    + start + "->" + end
                    + ", offsets " + startOffset + "->" + endOffset
                    + ", info " + info);
        }
        return null;
    }

    private @NotNull File getOrCreateTempDirectory(@NotNull Project project) {
        File result;

        String directoryName = "BS_" + project.getName().replaceAll(" ", "_");
        String tempDirectory = FileUtil.getTempDirectory();
        result = new File(tempDirectory, directoryName);
        if (!result.exists()) {
            try {
                result = FileUtil.createTempDirectory(directoryName, null, true);
                LOG.trace("Created temporary annotator directory", result);
            } catch (IOException e) {
                LOG.error("Failed to create temporary directory", e);
                throw new RuntimeException(e);
            }
        }

        // Clean current temp directory.
        // Annotator functions are called asynchronously and can be interrupted,
        // leaving files on disk if operation is aborted.
        Arrays.stream(result.listFiles((dir, name) -> !name.endsWith(".cmt"))).parallel().forEach(FileUtil::asyncDelete);

        return result;
    }

    static class InitialInfo {
        final PsiFile sourcePsiFile;
        final VirtualFile libRoot;
        final File tempFile;
        final Editor editor;
        final List<String> arguments;

        InitialInfo(@NotNull PsiFile sourcePsiFile, @NotNull VirtualFile libRoot, @NotNull File tempFile, @NotNull Editor editor, @NotNull List<String> arguments) {
            this.sourcePsiFile = sourcePsiFile;
            this.libRoot = libRoot;
            this.tempFile = tempFile;
            this.editor = editor;
            this.arguments = arguments;
        }
    }

    @NotNull File getBaseFile(@NotNull File file) {
        String tempNameWithoutExtension = FileUtil.getNameWithoutExtension(file);
        return new File(file.getParent(), tempNameWithoutExtension);
    }

    static class AnnotationResult {
        final List<OutputInfo> outputInfo;
        final InitialInfo initialInfo;

        AnnotationResult(@NotNull List<OutputInfo> outputInfo, InitialInfo initialInfo) {
            this.outputInfo = outputInfo;
            this.initialInfo = initialInfo;
        }
    }

    static class Annotation {
        final boolean isError;
        final String message;
        final TextRangeInterval range;
        final LogicalPosition startPos;

        Annotation(boolean isError, @NotNull String message, @NotNull TextRangeInterval textRange, @NotNull LogicalPosition startPos) {
            this.isError = isError;
            this.message = message;
            this.range = textRange;
            this.startPos = startPos;
        }
    }
}
