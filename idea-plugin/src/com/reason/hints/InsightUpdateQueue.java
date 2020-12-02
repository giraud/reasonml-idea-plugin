package com.reason.hints;

import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ColoredProcessHandler;
import com.intellij.execution.process.OSProcessHandler;
import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ReadAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.ui.update.MergingUpdateQueue;
import com.intellij.util.ui.update.Update;
import com.reason.Log;
import com.reason.bs.*;
import com.reason.ide.annotations.ErrorsManager;
import com.reason.ide.annotations.OutputInfo;
import com.reason.ide.hints.InferredTypesService;
import com.reason.lang.reason.RmlLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InsightUpdateQueue extends MergingUpdateQueue {

  private static final Log LOG = Log.create("hints.queue");

  private final @NotNull File m_tempDirectory;
  private final VirtualFile m_contentRoot;
  private final @NotNull VirtualFile m_sourceFile;

  private @Nullable String m_jsxVersion;
  private final AtomicLong m_lastModificationStamp = new AtomicLong(0);
  private Ninja m_ninja;
  private String m_namespace;

  private @Nullable VirtualFile m_libRoot;

  public InsightUpdateQueue(@NotNull Project project, @NotNull VirtualFile sourceFile) {
    super("hints", 200, true, null);
    setRestartTimerOnAdd(true);

    m_contentRoot = BsPlatform.findContentRootForFile(project, sourceFile).orElse(null);
    m_libRoot = m_contentRoot == null ? null : m_contentRoot.findFileByRelativePath("lib/bs");
    m_sourceFile = sourceFile;

    try {
      m_tempDirectory =
          FileUtil.createTempDirectory("BS_" + project.getName().replaceAll(" ", "_"), null);
    } catch (IOException e) {
      throw new RuntimeException(e); // TODO handle exception
    }

    initConfig(project);
  }

  public void initConfig(@NotNull Project project) {
    LOG.debug("Refresh bsconfig");

    BsCompiler bucklescript = ServiceManager.getService(project, BsCompiler.class);

    // Read build.ninja
    m_ninja = bucklescript.readNinjaBuild(m_contentRoot);

    // Read bsConfig to get the jsx value and ppx
    VirtualFile configJson =
        m_contentRoot == null ? null : m_contentRoot.findFileByRelativePath("bsconfig.json");
    BsConfig config = configJson == null ? null : BsConfigReader.read(configJson);
    if (config == null) {
      LOG.debug("No bsconfig.json found for content root: " + m_contentRoot);
      m_jsxVersion = null;
      m_namespace = "";
    } else {
      m_jsxVersion = config.getJsxVersion();
      m_namespace = config.getNamespace();

      // If a directory is marked as dev-only, it won't be built and exposed to other "dev"
      // directories in the same project
      // https://bucklescript.github.io/docs/en/build-configuration#sources
      for (String devSource : config.getDevSources()) {
        VirtualFile devFile = m_contentRoot.findFileByRelativePath(devSource);
        if (devFile != null
            && FileUtil.isAncestor(devFile.getPath(), m_sourceFile.getPath(), true)) {
          m_ninja.addInclude(devSource);
        }
      }
    }
  }

  @Override
  public void dispose() {
    if (m_tempDirectory.exists()) {
      FileUtil.asyncDelete(m_tempDirectory);
    }
  }

  public void queue(@NotNull Project project, @NotNull Document document) {
    m_lastModificationStamp.set(document.getModificationStamp());
    super.queue(new TypesUpdate(project, document));
  }

  class TypesUpdate extends Update {
    private final @NotNull Project m_project;
    private final @NotNull Document m_document;
    private final long m_docTimestamp;

    public TypesUpdate(@NotNull Project project, @NotNull Document document) {
      super(document);
      m_project = project;
      m_document = document;
      m_docTimestamp = m_document.getModificationStamp();
    }

    @Override
    public boolean isExpired() {
      boolean diff = m_lastModificationStamp.get() > m_docTimestamp;
      if (diff && LOG.isDebugEnabled()) {
        LOG.debug("Execution expired", m_docTimestamp);
      }
      return diff;
    }

    @Override
    public void run() {
      VirtualFile sourceFile = FileDocumentManager.getInstance().getFile(m_document);

      if (m_libRoot == null) {
        m_libRoot = m_contentRoot == null ? null : m_contentRoot.findFileByRelativePath("lib/bs");
      }

      if (sourceFile != null && m_libRoot != null && !m_project.isDisposed()) {
        Application application = ApplicationManager.getApplication();
        ErrorsManager errorsManager = ServiceManager.getService(m_project, ErrorsManager.class);
        application.executeOnPooledThread(
            () -> {
              long tstart = System.currentTimeMillis();

              errorsManager.clearErrors(sourceFile.getName());

              File tempFile = null;
              try {
                // Creates a temporary file on disk with a copy of the current document.
                // It'll be used by bsc for a temporary compilation

                String nameWithoutExtension = sourceFile.getNameWithoutExtension();
                tempFile =
                    FileUtil.createTempFile(
                        m_tempDirectory, nameWithoutExtension, "." + sourceFile.getExtension());
                Files.write(tempFile.toPath(), m_document.getText().getBytes());
                LOG.debug("Created temporary file", tempFile);

                // Compile temporary file
                Optional<VirtualFile> bscPath = BsPlatform.findBscExecutable(m_project, sourceFile);
                if (bscPath.isPresent()) {
                  File cmtFile = new File(m_tempDirectory, nameWithoutExtension + ".cmt");
                  if (isExpired()) {
                    return;
                  }

                  List<String> arguments = new ArrayList<>();
                  arguments.add(bscPath.get().getPath());
                  arguments.add("-bs-super-errors");
                  arguments.add("-color");
                  arguments.add("never");
                  arguments.addAll(m_ninja.getPkgFlags());
                  arguments.addAll(m_ninja.getBscFlags());
                  for (String ppxPath : m_ninja.getPpxIncludes()) {
                    arguments.add("-ppx");
                    arguments.add(ppxPath);
                  }
                  if (!m_namespace.isEmpty()) {
                    arguments.add("-bs-ns");
                    arguments.add(m_namespace);
                  }
                  if (m_jsxVersion != null) {
                    arguments.add("-bs-jsx");
                    arguments.add(m_jsxVersion);
                  }
                  for (String bscInclude : m_ninja.getIncludes()) {
                    arguments.add("-I");
                    arguments.add(bscInclude);
                  }
                  arguments.add("-o");
                  arguments.add(cmtFile.getAbsolutePath());
                  arguments.add("-bin-annot");
                  arguments.add(tempFile.getAbsolutePath());

                  GeneralCommandLine bscCli =
                      new GeneralCommandLine(arguments)
                          .withWorkDirectory(m_libRoot.getPath())
                          .withEnvironment("NINJA_ANSI_FORCED", "0");
                  OSProcessHandler bscProcessHandler = new ColoredProcessHandler(bscCli);
                  BscProcessListener bscListener = new BscProcessListener();
                  bscProcessHandler.addProcessListener(bscListener);
                  bscProcessHandler.startNotify();
                  bscProcessHandler.waitFor();
                  if (isExpired()) {
                    return;
                  }

                  Integer exitCode = bscProcessHandler.getExitCode();
                  LOG.debug("Exit code", exitCode);

                  String name = sourceFile.getName();
                  List<OutputInfo> info = bscListener.getInfo();
                  for (OutputInfo outputInfo : info) { // zzz
                    outputInfo.path = name;
                  }

                  if (LOG.isDebugEnabled()) {
                    LOG.debug("Found info", info);
                    for (OutputInfo outputInfo : info) {
                      LOG.trace("  -> " + outputInfo);
                    }
                  }

                  errorsManager.addAllInfo(info);
                  if (isExpired()) {
                    return;
                  }

                  if (exitCode != null && exitCode == 0 && cmtFile.exists()) {
                    // Call rincewind on the generated cmt file !
                    ReadAction.run(
                        () -> {
                          InsightManager insightManager =
                              ServiceManager.getService(m_project, InsightManager.class);
                          insightManager.queryTypes(
                              sourceFile,
                              cmtFile.toPath(),
                              types -> {
                                if (isExpired()) {
                                  return;
                                }

                                PsiFile psiFile =
                                    PsiManager.getInstance(m_project).findFile(sourceFile);

                                LOG.debug("Annotate types");
                                InferredTypesService.annotatePsiFile(
                                    m_project, RmlLanguage.INSTANCE, psiFile, types);

                                if (isExpired()) {
                                  return;
                                }

                                if (LOG.isTraceEnabled()) {
                                  LOG.trace("Restart daemon code analyzer for " + psiFile);
                                }
                                if (psiFile != null) {
                                  DaemonCodeAnalyzer.getInstance(m_project).restart(psiFile);
                                }
                              });
                        });
                  } else {
                    if (exitCode == null) {
                      LOG.debug(
                          "Something wrong happened during compilation, enable trace to see more details");
                    }
                    ApplicationManager.getApplication()
                        .invokeLater(
                            () -> {
                              if (!m_project.isDisposed()) {
                                PsiFile psiFile =
                                    PsiManager.getInstance(m_project).findFile(sourceFile);
                                if (psiFile != null) {
                                  if (LOG.isTraceEnabled()) {
                                    LOG.trace("Restart daemon code analyzer for " + psiFile);
                                  }
                                  DaemonCodeAnalyzer.getInstance(m_project).restart(psiFile);
                                }
                              }
                            });
                  }
                }
              } catch (IOException | ExecutionException e) {
                throw new RuntimeException(e); // TODO handle exception
              } finally {
                if (tempFile != null) {
                  FileUtil.delete(tempFile);
                }
                if (LOG.isDebugEnabled()) {
                  LOG.debug("Compilation done in " + (System.currentTimeMillis() - tstart) + "ms");
                }
              }
            });
      }
    }
  }

  private static class BscProcessListener implements ProcessListener {
    final BsLineProcessor m_lineProcessor = new BsLineProcessor(LOG);
    final StringBuilder m_builder = new StringBuilder();

    @Override
    public void startNotified(@NotNull ProcessEvent event) {
      LOG.trace("start " + event);
    }

    @Override
    public void processTerminated(@NotNull ProcessEvent event) {}

    @Override
    public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {}

    @Override
    public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
      String text = event.getText();
      if (text != null) {
        m_builder.append(text);
        if (text.endsWith("\n")) {
          m_lineProcessor.onRawTextAvailable(m_builder.toString());
          if (LOG.isTraceEnabled()) {
            LOG.trace(m_builder.toString().replace("\n", ""));
          }
          m_builder.setLength(0);
        }
      }
    }

    public @NotNull List<OutputInfo> getInfo() {
      return m_lineProcessor.getInfo();
    }
  }
}
