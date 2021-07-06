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
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.hints.*;
import com.reason.ide.files.*;
import com.reason.ide.hints.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static com.reason.ide.annotations.ORErrorAnnotator.*;
import static java.util.Collections.*;

abstract class ORErrorAnnotator extends ExternalAnnotator<InitialInfo, AnnotationResult> implements DumbAware {
    protected static final Log LOG = Log.create("annotator");

    abstract @Nullable VirtualFile getContentRoot(Project project, VirtualFile sourceFile);

    abstract Ninja readNinja(@NotNull Project project, @NotNull VirtualFile contentRoot);

    abstract List<OutputInfo> compile(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull ArrayList<String> arguments, @NotNull VirtualFile workDir);

    @Override
    public @Nullable InitialInfo collectInformation(@NotNull PsiFile psiFile, @NotNull Editor editor, boolean hasErrors) {
        if (hasErrors) {
            LOG.info("Annotator was initialized with errors. This isn't supported.");
            return null;
        }

        Project project = psiFile.getProject();
        VirtualFile sourceFile = psiFile.getVirtualFile();
        VirtualFile contentRoot = getContentRoot(project, sourceFile);

        // Read bsConfig to get the compilation directives
        VirtualFile bsConfigFile = contentRoot == null ? null : contentRoot.findFileByRelativePath(ORConstants.BS_CONFIG_FILENAME);
        BsConfig config = bsConfigFile == null ? null : BsConfigReader.read(bsConfigFile);
        if (config == null) {
            LOG.info("No bsconfig.json found for content root: " + contentRoot);
            return null;
        }

        VirtualFile libRoot = contentRoot.findFileByRelativePath("lib/bs");
        if (libRoot == null) {
            LOG.info("Unable to find BuckleScript lib root.");
            return null;
        }

        Ninja ninja = readNinja(project, contentRoot);

        if (ninja.isRescriptFormat()) {
            List<String> args = isDevSource(sourceFile, contentRoot, config) ? ninja.getArgsDev() : ninja.getArgs();
            return new InitialInfo(psiFile, libRoot, null, editor, args);
        } else {
            // create temporary compilation directory
            File tempCompilationDirectory = getOrCreateTempDirectory(project);
            cleanTempDirectory(tempCompilationDirectory, sourceFile.getNameWithoutExtension());

            String jsxVersion = config.getJsxVersion();
            String namespace = config.getNamespace();

            // If a directory is marked as dev-only, it won't be built and exposed to other "dev"
            // directories in the same project
            // https://bucklescript.github.io/docs/en/build-configuration#sources
            for (String devSource : config.getDevSources()) {
                VirtualFile devFile = contentRoot.findFileByRelativePath(devSource);
                if (devFile != null && FileUtil.isAncestor(devFile.getPath(), sourceFile.getPath(), true)) {
                    ninja.addInclude(devSource);
                }
            }

            // Creates a temporary file on disk with a copy of the current document.
            // It'll be used by bsc for a temporary compilation

            File sourceTempFile = copyToTempFile(tempCompilationDirectory, psiFile, sourceFile.getNameWithoutExtension());
            if (sourceTempFile == null) {
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
            arguments.add("-bin-annot");
            arguments.add("-o");
            arguments.add(cmtFile.getPath());
            arguments.add(sourceTempFile.getPath());

            return new InitialInfo(psiFile, libRoot, sourceTempFile, editor, arguments);
        }
    }

    @Override
    public @Nullable AnnotationResult doAnnotate(@NotNull InitialInfo initialInfo) {
        long compilationStartTime = System.currentTimeMillis();

        PsiFile sourcePsiFile = initialInfo.sourcePsiFile;
        Project project = sourcePsiFile.getProject();
        VirtualFile sourceFile = sourcePsiFile.getVirtualFile();

        if (initialInfo.oldFormat) {
            assert initialInfo.tempFile != null;

            BscProcessListener bscListener = new BscProcessListener();

            Integer exitCode = new BscProcess(project).run(sourceFile, initialInfo.libRoot, initialInfo.arguments, bscListener);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Compilation done in " + (System.currentTimeMillis() - compilationStartTime) + "ms");
            }

            File baseFile = new File(initialInfo.tempFile.getParent(), FileUtil.getNameWithoutExtension(initialInfo.tempFile));
            LOG.debug("Clear temporary files from base", baseFile);
            FileUtil.delete(initialInfo.tempFile);
            FileUtil.delete(new File(baseFile.getPath() + ".cmi"));
            FileUtil.delete(new File(baseFile.getPath() + ".cmj"));
            // cmt is never deleted

            if (exitCode != null && exitCode == 0) {
                // No error/warning found, nothing to display.
                return new AnnotationResult(emptyList(), initialInfo.editor, new File(baseFile + ".cmt"));
            }

            List<OutputInfo> outputInfo = bscListener.getInfo();
            LOG.debug("Found info", outputInfo);

            return new AnnotationResult(outputInfo, initialInfo.editor, new File(baseFile + ".cmt"));
        } else {
            String nameWithoutExtension = sourceFile.getNameWithoutExtension();

            // Create and clean temporary compilation directory
            File tempCompilationDirectory = getOrCreateTempDirectory(project);
            cleanTempDirectory(tempCompilationDirectory, nameWithoutExtension);

            // Creates a temporary file on disk with a copy of the current document.
            // It'll be used by bsc for a temporary compilation
            File sourceTempFile = copyToTempFile(tempCompilationDirectory, sourcePsiFile, nameWithoutExtension);
            if (sourceTempFile == null) {
                return null;
            }
            LOG.trace("Wrote contents to temporary file", sourceTempFile);

            VirtualFile contentRoot = initialInfo.libRoot.getParent().getParent();
            VirtualFile bsConfigFile = contentRoot.findFileByRelativePath(ORConstants.BS_CONFIG_FILENAME);
            BsConfig config = bsConfigFile == null ? null : BsConfigReader.read(bsConfigFile);
            if (config == null) {
                LOG.info("No bsconfig.json found for content root: " + contentRoot);
                return null;
            }

            File cmtFile = new File(tempCompilationDirectory, nameWithoutExtension + ".cmt");

            ArrayList<String> arguments = new ArrayList<>(initialInfo.arguments);
            String jsxVersion = config.getJsxVersion();
            if (jsxVersion != null) {
                arguments.add("-bs-jsx");
                arguments.add(jsxVersion);
            }
            arguments.add("-bin-annot");
            arguments.add("-o");
            arguments.add(cmtFile.getPath());
            arguments.add(sourceTempFile.getPath());

            List<OutputInfo> info = compile(project, sourceFile, arguments, initialInfo.libRoot);
            LOG.debug("Found info", info);

            if (LOG.isTraceEnabled()) {
                LOG.trace("Annotation done in " + (System.currentTimeMillis() - compilationStartTime) + "ms");
            }

            return new AnnotationResult(info, initialInfo.editor, cmtFile);
        }
    }

    @Override
    public void apply(@NotNull PsiFile sourcePsiFile, @NotNull AnnotationResult annotationResult, @NotNull AnnotationHolder holder) {
        Project project = sourcePsiFile.getProject();
        VirtualFile sourceFile = sourcePsiFile.getVirtualFile();
        Editor editor = annotationResult.editor;
        File cmtFile = annotationResult.cmtFile;

        List<Annotation> annotations = annotationResult.outputInfo.stream()
                .map(info -> makeAnnotation(info, editor))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        WolfTheProblemSolver problemSolver = WolfTheProblemSolver.getInstance(project);

        if (annotations.isEmpty()) {
            LOG.trace("Clear problems", sourceFile);
            problemSolver.clearProblems(sourceFile);
            // Call rincewind on the generated cmt file !
            updateCodeLens(project, sourcePsiFile.getLanguage(), sourceFile, cmtFile);
        } else {
            Collection<Problem> problems = new ArrayList<>();

            for (Annotation annotation : annotations) {
                if (annotation.isError) {
                    holder.newAnnotation(HighlightSeverity.ERROR, annotation.message)
                            .range(annotation.range)
                            .create();
                    // mark error in Project View
                    problems.add(problemSolver.convertToProblem(sourceFile, annotation.startPos.line, annotation.startPos.column, new String[]{annotation.message}));
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

    private boolean isDevSource(@NotNull VirtualFile sourceFile, @NotNull VirtualFile contentRoot, @NotNull BsConfig config) {
        for (String devSource : config.getDevSources()) {
            VirtualFile devFile = contentRoot.findFileByRelativePath(devSource);
            if (devFile != null && FileUtil.isAncestor(devFile.getPath(), sourceFile.getPath(), true)) {
                return true;
            }
        }
        return false;
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
        int startOffset = editor.isDisposed() ? 0 : editor.logicalPositionToOffset(start);
        int endOffset = editor.isDisposed() ? 0 : editor.logicalPositionToOffset(end);

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

        return result;
    }

    // Annotator functions are called asynchronously and can be interrupted, leaving files on disk if operation is
    // aborted. -> Clean current temp directory, but for the current file only: avoid erasing files when concurrent
    // compilation happens.
    private void cleanTempDirectory(@NotNull File directory, @NotNull String fileName) {
        File[] files = directory.listFiles((dir, name) -> name.startsWith(fileName) && !name.endsWith(".cmt"));
        if (files != null) {
            Arrays.stream(files).parallel().forEach(FileUtil::asyncDelete);
        }
    }

    private @Nullable File copyToTempFile(@NotNull File tempCompilationDirectory, @NotNull PsiFile psiFile, @NotNull String nameWithoutExtension) {
        File sourceTempFile;

        try {
            sourceTempFile = FileUtil.createTempFile(tempCompilationDirectory, nameWithoutExtension, "." + psiFile.getVirtualFile().getExtension());
        } catch (IOException e) {
            LOG.info("Temporary file creation failed", e); // log error but do not show it in UI
            return null;
        }

        try {
            FileUtil.writeToFile(sourceTempFile, psiFile.getText().getBytes());
        } catch (IOException e) {
            // Sometimes, file is locked by another process, not a big deal, skip it
            LOG.trace("Write failed: " + e.getLocalizedMessage());
            return null;
        }

        return sourceTempFile;
    }

    static class InitialInfo {
        final PsiFile sourcePsiFile;
        final VirtualFile libRoot;
        final File tempFile;
        final Editor editor;
        final List<String> arguments;
        final boolean oldFormat;

        InitialInfo(@NotNull PsiFile sourcePsiFile, @NotNull VirtualFile libRoot, @Nullable File tempFile, @NotNull Editor editor, @NotNull List<String> arguments) {
            this.sourcePsiFile = sourcePsiFile;
            this.libRoot = libRoot;
            this.tempFile = tempFile;
            this.editor = editor;
            this.arguments = arguments;
            this.oldFormat = tempFile != null;
        }
    }

    static class AnnotationResult {
        final List<OutputInfo> outputInfo;
        final Editor editor;
        public final File cmtFile;

        AnnotationResult(@NotNull List<OutputInfo> outputInfo, @NotNull Editor editor, @NotNull File cmtFile) {
            this.outputInfo = outputInfo;
            this.editor = editor;
            this.cmtFile = cmtFile;
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
