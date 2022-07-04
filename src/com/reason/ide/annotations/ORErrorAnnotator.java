package com.reason.ide.annotations;

import com.intellij.execution.process.*;
import com.intellij.lang.annotation.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.impl.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.problems.*;
import com.intellij.psi.*;
import com.reason.comp.Compiler;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.comp.rescript.*;
import com.reason.hints.*;
import com.reason.ide.files.*;
import com.reason.ide.hints.*;
import com.reason.lang.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static com.reason.ide.annotations.ORErrorAnnotator.*;

public class ORErrorAnnotator extends ExternalAnnotator<InitialInfo<? extends ORResolvedCompiler<?>>, AnnotationResult> implements DumbAware {
    protected static final Log LOG = Log.create("annotator");

    @Override
    public @Nullable InitialInfo<?> collectInformation(@NotNull PsiFile psiFile, @NotNull Editor editor, boolean hasErrors) {
        if (hasErrors) {
            LOG.info("Annotator was initialized with errors. This isn't supported.");
        } else {
            ORCompilerManager compilerManager = psiFile.getProject().getService(ORCompilerManager.class);
            ORResolvedCompiler<?> compiler = compilerManager.getCompiler(psiFile.getVirtualFile());

            if (compiler != null) {
                switch (compiler.getType()) {
                    case BS:
                        return BsErrorAnnotator.collectInformation((BsResolvedCompiler) compiler, editor, psiFile);
                    case RESCRIPT:
                        return ResErrorAnnotator.collectInformation((ResResolvedCompiler) compiler, editor, psiFile);
                    default:
                        return null;
                }
            }
        }

        return null;
    }

    @Override
    public @Nullable AnnotationResult doAnnotate(@NotNull InitialInfo<? extends ORResolvedCompiler<?>> initialInfo) {
        long compilationStartTime = System.currentTimeMillis();

        AnnotationResult result;
        switch (initialInfo.compiler.getType()) {
            case BS:
                result = BsErrorAnnotator.doAnnotate(initialInfo);
                break;
            case RESCRIPT:
                result = ResErrorAnnotator.doAnnotate(initialInfo);
                break;
            default:
                result = null;
                break;
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("Annotation done in " + (System.currentTimeMillis() - compilationStartTime) + "ms");
        }

        return result;
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
            updateCodeLens(project, ORLanguageProperties.cast(sourcePsiFile.getLanguage()), sourceFile, cmtFile);
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

    private void updateCodeLens(@NotNull Project project, @Nullable ORLanguageProperties lang, @NotNull VirtualFile sourceFile, @NotNull File cmtFile) {
        InsightManager insightManager = project.getService(InsightManager.class);
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

        LogicalPosition start = new LogicalPosition(lineStart < 1 ? 0 : lineStart - 1, colStart < 1 ? 0 : colStart - 1);
        LogicalPosition end = new LogicalPosition(lineEnd < 1 ? 0 : lineEnd - 1, colEnd < 1 ? 0 : colEnd - 1);
        int startOffset = editor.isDisposed() ? 0 : editor.logicalPositionToOffset(start);
        int endOffset = editor.isDisposed() ? 0 : editor.logicalPositionToOffset(end);

        if (0 < startOffset && 0 < endOffset && startOffset <= endOffset) {
            TextRangeInterval range = new TextRangeInterval(startOffset, endOffset);
            String message = info.message.replace('\n', ' ').replaceAll("\\s+", " ").trim();
            if (LOG.isDebugEnabled()) {
                LOG.debug("annotate " + startOffset + ":" + endOffset + " '" + message + "'");
            }
            return new Annotation(info.isError, message, range, start);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(info.message.replace('\n', ' ').replaceAll("\\s+", " ").trim());
            LOG.debug("Failed to locate info: "
                    + start + "->" + end
                    + ", offsets " + startOffset + "->" + endOffset
                    + ", info " + info);
        }
        return null;
    }

    static @NotNull File getOrCreateTempDirectory(@NotNull Project project) {
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
    static void cleanTempDirectory(@NotNull File directory, @NotNull String fileName) {
        File[] files = directory.listFiles((dir, name) -> name.startsWith(fileName) && !name.endsWith(".cmt"));
        if (files != null) {
            Arrays.stream(files).parallel().forEach(FileUtil::asyncDelete);
        }
    }

    @Nullable static File copyToTempFile(@NotNull File tempCompilationDirectory, @NotNull PsiFile psiFile, @NotNull String nameWithoutExtension) {
        File sourceTempFile;

        try {
            sourceTempFile = FileUtil.createTempFile(tempCompilationDirectory, nameWithoutExtension, "." + psiFile.getVirtualFile().getExtension());
        } catch (IOException e) {
            LOG.info("Temporary file creation failed", e); // log error but do not show it in UI
            return null;
        }

        try {
            String psiText = ReadAction.compute(psiFile::getText);
            FileUtil.writeToFile(sourceTempFile, psiText.getBytes());
        } catch (IOException e) {
            // Sometimes, file is locked by another process, not a big deal, skip it
            LOG.trace("Write failed: " + e.getLocalizedMessage());
            return null;
        }

        return sourceTempFile;
    }

    static class CompilerProcessListener {
        private final AnsiEscapeDecoder myAnsiEscapeDecoder = new AnsiEscapeDecoder();
        private final CompilerOutputAnalyzer myLineProcessor;

        CompilerProcessListener(CompilerOutputAnalyzer lineProcessor) {
            myLineProcessor = lineProcessor;
        }

        public void onTextAvailable(@NotNull String line, @NotNull Key<?> outputType) {
            StringBuilder sb = new StringBuilder();
            myAnsiEscapeDecoder.escapeText(line, outputType, (chunk, attributes) -> sb.append(chunk));
            myLineProcessor.onTextAvailable(sb.toString());
        }

        public @NotNull List<OutputInfo> getOutputInfo() {
            return myLineProcessor.getOutputInfo();
        }
    }

    static class InitialInfo<R extends ORResolvedCompiler<? extends Compiler>> {
        final R compiler;
        final PsiFile sourcePsiFile;
        final VirtualFile libRoot;
        final File tempFile;
        final Editor editor;
        final List<String> arguments;
        final boolean oldFormat;
        final String jsxVersion;

        InitialInfo(R compiler, @NotNull PsiFile sourcePsiFile, @NotNull VirtualFile libRoot, @Nullable File tempFile, @NotNull Editor editor, @NotNull List<String> arguments, String jsxVersion) {
            this.compiler = compiler;
            this.sourcePsiFile = sourcePsiFile;
            this.libRoot = libRoot;
            this.tempFile = tempFile;
            this.editor = editor;
            this.arguments = arguments;
            this.oldFormat = tempFile != null;
            this.jsxVersion = jsxVersion;
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
