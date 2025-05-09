package com.reason.ide.js;

import com.intellij.lang.javascript.library.*;
import com.intellij.openapi.*;
import com.intellij.openapi.application.*;
import com.intellij.openapi.command.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.startup.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.vfs.pointers.*;
import com.intellij.util.concurrency.*;
import com.intellij.webcore.libraries.*;
import com.reason.comp.bs.*;
import jpsplugin.com.reason.*;
import kotlin.*;
import kotlin.coroutines.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.intellij.openapi.vfs.VirtualFile.*;
import static com.intellij.util.ArrayUtilRt.*;
import static com.intellij.webcore.libraries.ScriptingLibraryModel.LibraryLevel.*;

public class ORJsLibraryManager implements ProjectActivity, DumbAware {
    private static final Log LOG = Log.create("activity.js.lib");
    private static final String LIB_NAME = "Bucklescript";

    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        DumbService.getInstance(project).smartInvokeLater(() -> runActivityLater(project));
        return null;
    }

    private void runActivityLater(@NotNull Project project) {
        LOG.info("run Js library manager");

        ReadAction.nonBlocking(() ->
                        BsPlatform.findConfigFiles(project).stream().findFirst())
                .finishOnUiThread(ModalityState.defaultModalityState(), bsConfigFileOptional -> {
                    if (bsConfigFileOptional.isPresent()) {
                        VirtualFile bsConfigFile = bsConfigFileOptional.get();
                        LOG.debug("bucklescript config file", bsConfigFile);

                        String baseDir = "file://" + bsConfigFile.getParent().getPath() + "/node_modules/";
                        List<VirtualFile> sources = new ArrayList<>(readBsConfigDependencies(project, baseDir, bsConfigFile));

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

                        WriteCommandAction.runWriteCommandAction(project, () -> jsLibraryManager.commitChanges(RootsChangeRescanningInfo.TOTAL_RESCAN));
                    }
                })
                .submit(AppExecutorUtil.getAppExecutorService());
    }

    private @NotNull List<VirtualFile> readBsConfigDependencies(@NotNull Project project, @NotNull String nodeModulesDir, @NotNull VirtualFile bsConfigFile) {
        List<VirtualFile> result = new ArrayList<>();

        LOG.debug("Read deps from", bsConfigFile);

        VirtualFilePointerManager vFilePointerManager = VirtualFilePointerManager.getInstance();
        BsConfig bsConfig = BsConfigReader.read(bsConfigFile);

        JSLibraryManager jsLibraryManager = JSLibraryManager.getInstance(project);
        Disposable disposable = Disposer.newDisposable(jsLibraryManager, "ORJsLibraryManager");
        try {
            for (String dependency : bsConfig.getDependencies()) {
                String depDirUrl = nodeModulesDir + dependency;
                VirtualFilePointer dirPointer = vFilePointerManager.create(depDirUrl, disposable,
                        new VirtualFilePointerListener() {
                            @Override
                            public void validityChanged(@NotNull VirtualFilePointer[] pointers) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("validity changed for " + pointers[0]);
                                }
                                ScriptingLibraryModel bucklescriptModel = jsLibraryManager.getLibraryByName(LIB_NAME);
                                if (bucklescriptModel != null) {
                                    List<VirtualFile> changedSources = new ArrayList<>(bucklescriptModel.getSourceFiles());
                                    if (pointers[0].isValid()) {
                                        VirtualFile dirFile = pointers[0].getFile();
                                        changedSources.add(dirFile);
                                        if (dirFile != null) {
                                            VirtualFile bsConfigDepFile = dirFile.findChild("bsconfig.json");
                                            if (bsConfigDepFile != null) {
                                                changedSources.addAll(readBsConfigDependencies(project, nodeModulesDir, bsConfigDepFile));
                                            }
                                        }
                                        jsLibraryManager.updateLibrary(
                                                LIB_NAME,
                                                LIB_NAME,
                                                changedSources.toArray(new VirtualFile[0]),
                                                EMPTY_ARRAY,
                                                EMPTY_STRING_ARRAY);
                                        WriteCommandAction.runWriteCommandAction(project, () -> jsLibraryManager.commitChanges(RootsChangeRescanningInfo.TOTAL_RESCAN));
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
