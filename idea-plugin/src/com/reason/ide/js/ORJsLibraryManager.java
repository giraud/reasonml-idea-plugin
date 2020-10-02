package com.reason.ide.js;

import static com.intellij.openapi.vfs.VirtualFile.EMPTY_ARRAY;
import static com.intellij.util.ArrayUtilRt.EMPTY_STRING_ARRAY;
import static com.intellij.webcore.libraries.ScriptingLibraryModel.LibraryLevel.PROJECT;

import com.intellij.lang.javascript.library.JSLibraryManager;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerListener;
import com.intellij.openapi.vfs.pointers.VirtualFilePointerManager;
import com.intellij.webcore.libraries.ScriptingLibraryModel;
import com.reason.Log;
import com.reason.bs.BsConfig;
import com.reason.bs.BsConfigReader;
import com.reason.ide.ORProjectManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class ORJsLibraryManager implements StartupActivity, DumbAware {

  private static final Log LOG = Log.create("activity.js.lib");
  private static final String LIB_NAME = "Bucklescript";

  @Override
  public void runActivity(@NotNull Project project) {
    DumbService.getInstance(project).smartInvokeLater(() -> runActivityLater(project));
  }

  private void runActivityLater(Project project) {
    LOG.info("run Js library manager");

    Optional<VirtualFile> bsConfigFileOptional =
        ORProjectManager.findFirstBsConfigurationFile(project);
    if (bsConfigFileOptional.isPresent()) {
      VirtualFile bsConfigFile = bsConfigFileOptional.get();
      LOG.debug("bucklescript config file", bsConfigFile);

      String baseDir = "file://" + bsConfigFile.getParent().getPath() + "/node_modules/";
      List<VirtualFile> sources =
          new ArrayList<>(readBsConfigDependencies(project, baseDir, bsConfigFile));

      JSLibraryManager jsLibraryManager = JSLibraryManager.getInstance(project);

      ScriptingLibraryModel bucklescriptModel = jsLibraryManager.getLibraryByName(LIB_NAME);
      if (bucklescriptModel == null) {
        LOG.debug("Creating js library", LIB_NAME);
        jsLibraryManager.createLibrary(
            LIB_NAME,
            sources.toArray(new VirtualFile[0]),
            EMPTY_ARRAY,
            EMPTY_STRING_ARRAY,
            PROJECT,
            true);
      } else {
        LOG.debug("Updating js library", LIB_NAME);
        jsLibraryManager.updateLibrary(
            LIB_NAME,
            LIB_NAME,
            sources.toArray(new VirtualFile[0]),
            EMPTY_ARRAY,
            EMPTY_STRING_ARRAY);
      }

      WriteCommandAction.runWriteCommandAction(project, (Runnable) jsLibraryManager::commitChanges);
    }
  }

  private List<VirtualFile> readBsConfigDependencies(
      @NotNull Project project, @NotNull String nodeModulesDir, @NotNull VirtualFile bsConfigFile) {
    List<VirtualFile> result = new ArrayList<>();

    LOG.debug("Read deps from", bsConfigFile);

    VirtualFilePointerManager vFilePointerManager = VirtualFilePointerManager.getInstance();
    BsConfig bsConfig = BsConfigReader.read(bsConfigFile);

    JSLibraryManager jsLibraryManager = JSLibraryManager.getInstance(project);
    Disposable disposable = Disposer.newDisposable(jsLibraryManager, "ORJsLibraryManager");
    try {
      for (String dependency : bsConfig.getDependencies()) {
        String depDirUrl = nodeModulesDir + dependency;
        VirtualFilePointer dirPointer =
            vFilePointerManager.create(
                depDirUrl,
                disposable,
                new VirtualFilePointerListener() {
                  @Override
                  public void validityChanged(@NotNull VirtualFilePointer[] pointers) {
                    if (LOG.isDebugEnabled()) {
                      LOG.debug("validity changed for " + pointers[0]);
                    }
                    ScriptingLibraryModel bucklescriptModel =
                        jsLibraryManager.getLibraryByName(LIB_NAME);
                    if (bucklescriptModel != null) {
                      List<VirtualFile> changedSources =
                          new ArrayList<>(bucklescriptModel.getSourceFiles());
                      if (pointers[0].isValid()) {
                        VirtualFile dirFile = pointers[0].getFile();
                        changedSources.add(dirFile);
                        if (dirFile != null) {
                          VirtualFile bsConfigDepFile = dirFile.findChild("bsconfig.json");
                          if (bsConfigDepFile != null) {
                            changedSources.addAll(
                                readBsConfigDependencies(project, nodeModulesDir, bsConfigDepFile));
                          }
                        }
                        jsLibraryManager.updateLibrary(
                            LIB_NAME,
                            LIB_NAME,
                            changedSources.toArray(new VirtualFile[0]),
                            EMPTY_ARRAY,
                            EMPTY_STRING_ARRAY);
                        WriteCommandAction.runWriteCommandAction(
                            project, (Runnable) jsLibraryManager::commitChanges);
                      } else {
                        System.out.println("removed " + pointers[0]);
                      }
                    }
                  }
                });

        VirtualFile dirFile = dirPointer.getFile();
        if (dirFile != null) {
          result.add(dirFile);

          if (dirFile.isValid()) {
            VirtualFile bsConfigDepFile = dirFile.findChild("bsconfig.json");
            if (bsConfigDepFile != null) {
              result.addAll(readBsConfigDependencies(project, nodeModulesDir, bsConfigDepFile));
            }
          }
        }
      }
    } finally {
      disposable.dispose();
    }

    return result;
  }
}
