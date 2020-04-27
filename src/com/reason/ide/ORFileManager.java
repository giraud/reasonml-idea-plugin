package com.reason.ide;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.reason.Joiner;
import com.reason.Log;
import com.reason.StringUtil;
import com.reason.bs.Bucklescript;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

public class ORFileManager {

    private static final Log LOG = Log.create("manager.file");

    private ORFileManager() {}

    @Nullable
    public static PsiFile findCmtFileFromSource(@NotNull Project project, @NotNull String filenameWithoutExtension) {
        if (!DumbService.isDumb(project)) {
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            String filename = filenameWithoutExtension + ".cmt";

            PsiFile[] cmtFiles = FilenameIndex.getFilesByName(project, filename, scope);
            if (cmtFiles.length == 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("File module for " + filename + " is NOT FOUND, files found: [" + Joiner.join(", ", cmtFiles) + "]");
                }
                return null;
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("Found cmt " + filename + " (" + cmtFiles[0].getVirtualFile().getPath() + ")");
            }

            return cmtFiles[0];
        } else {
            LOG.info("Cant find cmt while reindexing");
        }

        return null;
    }

    @NotNull
    public static String toRelativeSourceName(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull Path relativePath) {
        String sourcePath = relativePath.toString();
        String namespace = ServiceManager.getService(project, Bucklescript.class).getNamespace(sourceFile);
        if (!namespace.isEmpty()) {
            sourcePath = sourcePath.replace("-" + StringUtil.toFirstUpper(namespace), "");
        }
        int dotPos = sourcePath.lastIndexOf(".");
        return 0 <= dotPos ? sourcePath.substring(0, dotPos) + ".re" : sourcePath;
    }
}
