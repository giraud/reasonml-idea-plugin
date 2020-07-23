package com.reason.hints;

import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.reason.Log;
import com.reason.WGet;
import com.reason.ide.ORFileManager;
import com.reason.ide.files.FileBase;
import com.reason.ide.hints.InferredTypesService;

public class RincewindDownloader extends Task.Backgroundable {

    private static final double TOTAL_BYTES = 10_000_000.0;
    private static final String DOWNLOAD_URL = "https://dl.bintray.com/giraud/ocaml/";
    private static final Log LOG = Log.create("hints");

    @NotNull
    private final VirtualFile m_sourceFile;

    RincewindDownloader(@Nullable Project project, @NotNull VirtualFile sourceFile) {
        super(project, "Downloading Rincewind binary");
        m_sourceFile = sourceFile;
    }

    @Override
    public void run(@NotNull ProgressIndicator indicator) {
        if (myProject.isDisposed()) {
            LOG.debug("Project is disposed, can't download rincewind");
            return;
        }

        InsightManagerImpl insightManager = (InsightManagerImpl) ServiceManager.getService(myProject, InsightManager.class);
        if (!insightManager.isDownloading.compareAndSet(false, true)) {
            // We are already in the process of downloading
            LOG.debug("Already downloading, abort");
            return;
        }

        try {
            File targetFile = insightManager.getRincewindFile(m_sourceFile);
            if (targetFile == null) {
                LOG.debug("No target file, abort downloading");
                return;
            }

            String rincewindFilename = insightManager.getRincewindFilename(m_sourceFile);
            if (rincewindFilename == null) {
                LOG.debug("No rincewind version found, abort downloading");
                return;
            }

            LOG.info("Downloading " + targetFile.getName() + "...");
            indicator.setIndeterminate(false);
            indicator.setFraction(0.0);

            boolean downloaded = WGet.apply(DOWNLOAD_URL + rincewindFilename, targetFile, indicator, TOTAL_BYTES);
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
                            PsiFile cmtFile = ORFileManager.findCmtFileFromSource(myProject, openedFile.getNameWithoutExtension());
                            if (cmtFile != null) {
                                Path cmtPath = FileSystems.getDefault().getPath(cmtFile.getVirtualFile().getPath());

                                application.invokeLater(() -> application.runReadAction(() -> {
                                    PsiFile psiFile = psiManager.findFile(openedFile);
                                    if (psiFile instanceof FileBase) {
                                        LOG.debug("Query types for " + openedFile);
                                        insightManager.queryTypes(openedFile, cmtPath, inferredTypes -> InferredTypesService
                                                .annotatePsiFile(myProject, psiFile.getLanguage(), openedFile, inferredTypes));
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
