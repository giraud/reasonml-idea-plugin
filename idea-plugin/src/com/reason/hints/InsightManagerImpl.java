package com.reason.hints;

import static com.reason.Platform.getOsPrefix;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Log;
import com.reason.Platform;
import com.reason.bs.BsProcess;
import java.io.File;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InsightManagerImpl implements InsightManager {

  private static final Log LOG = Log.create("hints");

  @NotNull final AtomicBoolean isDownloading = new AtomicBoolean(false);

  @NotNull private final Project m_project;

  private InsightManagerImpl(@NotNull Project project) {
    m_project = project;
  }

  @Override
  public void downloadRincewindIfNeeded(@NotNull VirtualFile sourceFile) {
    File rincewind = getRincewindFile(sourceFile);
    if (rincewind == null || !rincewind.exists()) {
      ProgressManager.getInstance().run(new RincewindDownloader(m_project, sourceFile));
    }
  }

  @Override
  public void queryTypes(
      @NotNull VirtualFile sourceFile, @NotNull Path cmtPath, @NotNull ProcessTerminated runAfter) {
    File rincewindFile = getRincewindFile(sourceFile);
    if (rincewindFile != null) {
      RincewindProcess.getInstance(m_project)
          .types(sourceFile, rincewindFile.getPath(), cmtPath.toString(), runAfter);
    }
  }

  @NotNull
  @Override
  public List<String> dumpMeta(@NotNull VirtualFile cmtFile) {
    RincewindProcess rincewindProcess = RincewindProcess.getInstance(m_project);

    File rincewindFile = getRincewindFileExcludingVersion(cmtFile, "0.4");
    return rincewindFile == null
        ? Collections.emptyList()
        : rincewindProcess.dumpMeta(rincewindFile.getPath(), cmtFile);
  }

  @NotNull
  @Override
  public String dumpTree(@NotNull VirtualFile cmtFile) {
    RincewindProcess rincewindProcess = RincewindProcess.getInstance(m_project);

    File rincewindFile = getRincewindFile(cmtFile);
    return rincewindFile == null
        ? "<unknown/>"
        : rincewindProcess.dumpTree(cmtFile, rincewindFile.getPath());
  }

  @NotNull
  @Override
  public List<String> dumpInferredTypes(@NotNull VirtualFile cmtFile) {
    RincewindProcess rincewindProcess = RincewindProcess.getInstance(m_project);

    File rincewindFile = getRincewindFile(cmtFile);
    return rincewindFile == null
        ? Collections.emptyList()
        : rincewindProcess.dumpTypes(rincewindFile.getPath(), cmtFile);
  }

  @Nullable
  @Override
  public File getRincewindFile(@NotNull VirtualFile sourceFile) {
    return getRincewindFileExcludingVersion(sourceFile, "");
  }

  @Nullable
  public File getRincewindFileExcludingVersion(
      @NotNull VirtualFile sourceFile, @NotNull String excludedVersion) {
    String filename = getRincewindFilenameExcludingVersion(sourceFile, excludedVersion);
    if (filename == null) {
      if (LOG.isTraceEnabled()) {
        LOG.trace(
            "No rincewind file found for " + sourceFile + " (excluded: " + excludedVersion + ")");
      }
      return null;
    }

    Path pluginLocation = Platform.getPluginLocation();
    String pluginPath =
        pluginLocation == null
            ? System.getProperty("java.io.tmpdir")
            : pluginLocation.toFile().getPath();
    if (LOG.isTraceEnabled()) {
      LOG.trace("Rincewind filename: " + filename + " at " + pluginPath);
    }
    return new File(pluginPath, filename);
  }

  @Nullable
  @Override
  public String getRincewindFilename(@NotNull VirtualFile sourceFile) {
    return getRincewindFilenameExcludingVersion(sourceFile, "");
  }

  @Nullable
  public String getRincewindFilenameExcludingVersion(
      @NotNull VirtualFile sourceFile, @NotNull String excludedVersion) {
    String ocamlVersion =
        ServiceManager.getService(m_project, BsProcess.class).getOCamlVersion(sourceFile);
    String rincewindVersion = getRincewindVersion(ocamlVersion);

    if (ocamlVersion != null && !rincewindVersion.equals(excludedVersion)) {
      return "rincewind_" + getOsPrefix() + ocamlVersion + "-" + rincewindVersion + ".exe";
    }

    return null;
  }

  @NotNull
  private String getRincewindVersion(@Nullable String ocamlVersion) {
    if ("4.02".equals(ocamlVersion)) {
      return "0.4";
    }

    return "0.9.1";
  }
}
