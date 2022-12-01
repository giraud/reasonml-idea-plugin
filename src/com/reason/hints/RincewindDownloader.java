package com.reason.hints;

import com.intellij.openapi.application.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.ide.hints.*;
import com.reason.lang.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.nio.file.*;

public class RincewindDownloader extends Task.Backgroundable {
    private static final Log LOG = Log.create("hints");
    private static final double TOTAL_BYTES = 10_000_000.0;
    private static final String DOWNLOAD_URL = "https://rincewind.jfrog.io/artifactory/ocaml/";

    private final File myRincewindTarget;

    RincewindDownloader(@Nullable Project project, @NotNull File rincewindTarget) {
        super(project, "Downloading Rincewind binary");
        myRincewindTarget = rincewindTarget;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        if (myProject == null || myProject.isDisposed()) {
            LOG.debug("Project is disposed, can't download rincewind");
            return;
        }

        InsightManagerImpl insightManager = (InsightManagerImpl) myProject.getService(InsightManager.class);
        if (!insightManager.isDownloading.compareAndSet(false, true)) {
            // We are already in the process of downloading
            LOG.debug("Already downloading, abort");
            return;
        }

        try {
            String rincewindFilename = myRincewindTarget.getName();
            LOG.info("Downloading " + rincewindFilename + "...");
            indicator.setIndeterminate(false);
            indicator.setFraction(0.0);

            boolean downloaded = WGet.apply(DOWNLOAD_URL + rincewindFilename, myRincewindTarget, indicator, TOTAL_BYTES);
            if (downloaded) {
                Application application = ApplicationManager.getApplication();
                application.executeOnPooledThread(() -> {
                    DumbService dumbService = DumbService.getInstance(myProject);
                    dumbService.runReadActionInSmartMode(() -> {
                        LOG.info("Rincewind downloaded, query types for opened files");
                        PsiManager psiManager = PsiManager.getInstance(myProject);
                        VirtualFile[] openedFiles = FileEditorManager.getInstance(myProject).getOpenFiles();
                        for (VirtualFile openedFile : openedFiles) {
                            // Query types and update psi cache
                            PsiFile cmtFile = ORFileUtils.findCmtFileFromSource(myProject, openedFile.getNameWithoutExtension(), null);
                            if (cmtFile != null) {
                                Path cmtPath = FileSystems.getDefault().getPath(cmtFile.getVirtualFile().getPath());

                                application.invokeLater(() ->
                                        application.runReadAction(() -> {
                                            PsiFile psiFile = psiManager.findFile(openedFile);
                                            if (psiFile instanceof FileBase) {
                                                LOG.debug("Query types for " + openedFile);
                                                insightManager.queryTypes(openedFile, cmtPath, inferredTypes ->
                                                        InferredTypesService.annotatePsiFile(
                                                                myProject,
                                                                ORLanguageProperties.cast(psiFile.getLanguage()),
                                                                openedFile,
                                                                inferredTypes));
                                            }
                                        }));
                            }
                        }
                    });
                });
            }

            indicator.setFraction(1.0);
        } finally {
            insightManager.isDownloading.set(false);
        }
    }
}
