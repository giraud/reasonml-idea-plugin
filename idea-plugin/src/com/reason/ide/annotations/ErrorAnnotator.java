package com.reason.ide.annotations;

import com.intellij.codeInsight.daemon.*;
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
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

import static com.reason.ide.annotations.ErrorAnnotator.*;

public class ErrorAnnotator extends ExternalAnnotator<InitialInfo, AnnotationResult>
    implements DumbAware {

  private static final Log LOG = Log.create("annotator");

  @Override
  public @Nullable InitialInfo collectInformation(@NotNull PsiFile psiFile, @NotNull Editor editor, boolean hasErrors) {
    if (hasErrors) {
      LOG.error("Annotator was initialized with errors. This isn't supported.");
      return null;
    }

    Project project = psiFile.getProject();
    VirtualFile sourceFile = psiFile.getVirtualFile();

    // create temporary compilation directory
    File compilationDirectory = createTempCompilationDirectory(project);
    LOG.debug("Created temporary annotator directory", compilationDirectory);

    Optional<VirtualFile> contentRoot = BsPlatform.findContentRootForFile(project, sourceFile);
    Optional<VirtualFile> libRoot = contentRoot.map(root -> root.findFileByRelativePath("lib/bs"));
    if (!libRoot.isPresent()) {
      LOG.info("Unable to find BuckleScript lib root.");
      return null;
    }

    // Read bsConfig to get the jsx value and ppx
    VirtualFile bsConfigFile =
        contentRoot.get().findFileByRelativePath(BsConfigJsonFileType.FILENAME);
    if (bsConfigFile == null) {
      LOG.info("No bsconfig.json found for content root: " + contentRoot);
      return null;
    }
    BsConfig config = BsConfigReader.read(bsConfigFile);
    String jsxVersion = config.getJsxVersion();
    String namespace = config.getNamespace();

    // If a directory is marked as dev-only, it won't be built and exposed to other "dev"
    // directories in the same project
    // https://bucklescript.github.io/docs/en/build-configuration#sources
    // @TODO register a file listener and read the values from memory
    BsCompiler bucklescript = ServiceManager.getService(project, BsCompiler.class);
    Ninja ninja = bucklescript.readNinjaBuild(contentRoot.get());
    for (String devSource : config.getDevSources()) {
      VirtualFile devFile = contentRoot.get().findFileByRelativePath(devSource);
      if (devFile != null && FileUtil.isAncestor(devFile.getPath(), sourceFile.getPath(), true)) {
        ninja.addInclude(devSource);
      }
    }

    // Creates a temporary file on disk with a copy of the current document.
    // It'll be used by bsc for a temporary compilation
    Path tempFilePath = Paths.get(compilationDirectory.getPath(), sourceFile.getName());
    try {
      Files.write(tempFilePath, psiFile.getText().getBytes());
    } catch (IOException e) {
      LOG.error("Failed to write to temporary file.", e);
      return null;
    }
    LOG.trace("Wrote contents to temporary file. Path = " + tempFilePath);

    File cmtFile = new File(compilationDirectory, sourceFile.getNameWithoutExtension() + ".cmt");

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
    arguments.add(cmtFile.getAbsolutePath());
    arguments.add("-bin-annot");
    arguments.add(tempFilePath.toString());

    return new InitialInfo(psiFile, libRoot.get(), cmtFile, editor, arguments);
  }

  @Nullable
  @Override
  public AnnotationResult doAnnotate(@Nullable InitialInfo initialInfo) {
    if (initialInfo == null) {
      LOG.warn("Unable to annotate file. Annotator not ready.");
      return null;
    }

    PsiFile sourcePsiFile = initialInfo.sourcePsiFile;
    Project project = sourcePsiFile.getProject();
    VirtualFile sourceFile = sourcePsiFile.getVirtualFile();

    long compilationStartTime = System.currentTimeMillis();

    BscProcess bscProcess = BscProcess.getInstance(project);
    BscProcessListener bscListener = new BscProcessListener();

    Integer exitCode = bscProcess.run(sourceFile, initialInfo.libRoot, initialInfo.arguments, bscListener);
    LOG.trace("Compilation done in " + (System.currentTimeMillis() - compilationStartTime) + "ms");

    if (exitCode != null && exitCode == 0) {
      ApplicationManager.getApplication()
          .invokeLater(
              () -> {
                if (!project.isDisposed()) {
                  PsiFile psiFile = PsiManager.getInstance(project).findFile(sourceFile);
                  if (psiFile != null) {
                    LOG.trace("Restart daemon code analyzer for " + psiFile);
                    DaemonCodeAnalyzer.getInstance(project).restart(psiFile);
                  }
                }
              });
      return null;
    }

    List<OutputInfo> outputInfo = bscListener.getInfo();
    LOG.debug("Found info", outputInfo);
    if (LOG.isTraceEnabled()) {
      String name = sourceFile.getName();
      for (OutputInfo info : outputInfo) {
        info.path = name;
        LOG.trace("  -> " + info);
      }
    }

    return new AnnotationResult(outputInfo, initialInfo);
  }

  @Override
  public void apply(
      @NotNull PsiFile sourcePsiFile,
      @NotNull AnnotationResult annotationResult,
      @NotNull AnnotationHolder holder) {
    Project project = sourcePsiFile.getProject();
    VirtualFile sourceFile = sourcePsiFile.getVirtualFile();
    List<OutputInfo> outputInfo = annotationResult.outputInfo;
    Editor editor = annotationResult.initialInfo.editor;
    File cmtFile = annotationResult.initialInfo.cmtFile;

    List<Annotation> annotations =
        outputInfo.stream()
            .map(info -> makeAnnotation(info, editor))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

    WolfTheProblemSolver problemSolver = WolfTheProblemSolver.getInstance(project);
    Collection<Problem> problems = new ArrayList<>();
    for (Annotation annotation : annotations) {
      if (annotation.isError) {
        holder
            .newAnnotation(HighlightSeverity.ERROR, annotation.message)
            .range(annotation.range)
            .create();
        // mark error in Project View
        problems.add(
            problemSolver.convertToProblem(
                sourceFile,
                annotation.startPos.line,
                annotation.startPos.column,
                new String[]{annotation.message}));
      } else {
        holder
            .newAnnotation(HighlightSeverity.WARNING, annotation.message)
            .range(annotation.range)
            .create();
      }
    }

    // Call rincewind on the generated cmt file !
    ReadAction.run(() -> annotateTypes(project, sourceFile, cmtFile));

    problemSolver.reportProblems(sourceFile, problems);
  }

  @NotNull
  public File createTempCompilationDirectory(Project project) {
    String directoryName = "BS_" + project.getName().replaceAll(" ", "_");
    try {
      FileUtil.delete(Paths.get(FileUtil.getTempDirectory(), directoryName));
      return FileUtil.createTempDirectory(directoryName, null, true);
    } catch (IOException e) {
      LOG.error("Failed to create temporary directory.", e);
      throw new RuntimeException(e);
    }
  }

  private static void annotateTypes(Project project, VirtualFile sourceFile, File cmtFile) {
    ServiceManager.getService(project, InsightManager.class)
        .queryTypes(
            sourceFile,
            cmtFile.toPath(),
            types -> {
              PsiManager psiManager = PsiManager.getInstance(project);
              PsiFile psiFile = psiManager.findFile(sourceFile);

              LOG.debug("Annotate types");
              InferredTypesService.annotatePsiFile(project, RmlLanguage.INSTANCE, psiFile, types);
            });
  }

  @Nullable
  private static Annotation makeAnnotation(OutputInfo info, Editor editor) {
    int colStart = info.colStart;
    int colEnd = info.colEnd;
    int lineStart = info.lineStart;
    int lineEnd = info.lineEnd;
    LogicalPosition start =
        new LogicalPosition(lineStart < 1 ? 0 : lineStart - 1, colStart < 1 ? 0 : colStart);
    LogicalPosition end =
        new LogicalPosition(lineEnd < 1 ? 0 : lineEnd - 1, colEnd < 1 ? 0 : colEnd);
    int startOffset = editor.logicalPositionToOffset(start);
    int endOffset = editor.logicalPositionToOffset(end);
    if (0 < startOffset && 0 < endOffset && startOffset < endOffset) {
      TextRangeInterval range = new TextRangeInterval(startOffset - 1, endOffset - 1);
      String message = info.message.replace('\n', ' ').replaceAll("\\s+", " ").trim();
      LOG.debug("annotate " + startOffset + ":" + endOffset + " '" + message + "'");
      return new Annotation(info.isError, message, range, start);
    } else {
      if (LOG.isDebugEnabled()) {
        LOG.debug(
            "Failed to locate info: "
                + start
                + "->"
                + end
                + ", offsets "
                + startOffset
                + "->"
                + endOffset
                + ", info "
                + info);
      }
      return null;
    }
  }

  static class InitialInfo {
    final PsiFile sourcePsiFile;
    final VirtualFile libRoot;
    final File cmtFile;
    final Editor editor;
    final List<String> arguments;

    private InitialInfo(@NotNull PsiFile sourcePsiFile, @NotNull VirtualFile libRoot, @NotNull File cmtFile, @NotNull Editor editor, @NotNull List<String> arguments) {
      this.sourcePsiFile = sourcePsiFile;
      this.libRoot = libRoot;
      this.cmtFile = cmtFile;
      this.editor = editor;
      this.arguments = arguments;
    }
  }

  static class AnnotationResult {
    final List<OutputInfo> outputInfo;
    final InitialInfo initialInfo;

    public AnnotationResult(@NotNull List<OutputInfo> outputInfo, InitialInfo initialInfo) {
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
