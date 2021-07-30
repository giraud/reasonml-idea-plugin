package com.reason.ide;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.reason.comp.bs.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.nio.file.*;

public class ORFileManager {
    private static final Log LOG = Log.create("manager.file");

    private ORFileManager() {
    }

    public static @Nullable PsiFile findCmtFileFromSource(@NotNull Project project, @NotNull String filenameWithoutExtension) {
        if (!DumbService.isDumb(project)) {
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            String filename = filenameWithoutExtension + ".cmt";

            PsiFile[] cmtFiles = FilenameIndex.getFilesByName(project, filename, scope);
            if (cmtFiles.length == 0) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("File module for " + filename + " is NOT FOUND, files found: [" + Joiner.join(", ", cmtFiles) + "]");
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

    public static @NotNull String toRelativeSourceName(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull Path relativePath) {
        String sourcePath = relativePath.toString();
        String namespace = project.getService(BsCompiler.class).getNamespace(sourceFile);
        if (!namespace.isEmpty()) {
            sourcePath = sourcePath.replace("-" + StringUtil.toFirstUpper(namespace), "");
        }
        int dotPos = sourcePath.lastIndexOf(".");
        return 0 <= dotPos ? sourcePath.substring(0, dotPos) + ".re" : sourcePath;
    }
}
