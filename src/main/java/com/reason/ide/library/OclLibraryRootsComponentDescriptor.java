package com.reason.ide.library;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.ui.*;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.*;
import com.intellij.openapi.vfs.*;
import com.intellij.util.containers.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class OclLibraryRootsComponentDescriptor extends LibraryRootsComponentDescriptor {
    @Override
    public @Nullable OrderRootTypePresentation getRootTypePresentation(@NotNull OrderRootType type) {
        return DefaultLibraryRootsComponentDescriptor.getDefaultPresentation(type);
    }

    @Override
    public @NotNull List<? extends RootDetector> getRootDetectors() {
        return List.of(new OclSourceRootDetector());
    }

    @Override
    public @NotNull List<? extends AttachRootButtonDescriptor> createAttachButtons() {
        return Collections.emptyList();
    }

    static class OclSourceRootDetector extends RootDetector {
        protected OclSourceRootDetector() {
            super(OclSourcesOrderRootType.getInstance(), false, "OCaml sources");
        }

        @Override public @NotNull Collection<VirtualFile> detectRoots(@NotNull VirtualFile rootCandidate, @NotNull ProgressIndicator progressIndicator) {
            if (!rootCandidate.isDirectory()) {
                return ContainerUtil.emptyList();
            }

            FileTypeManager typeManager = FileTypeManager.getInstance();
            List<VirtualFile> foundDirectories = new ArrayList<>();

            try {
                VfsUtilCore.visitChildrenRecursively(rootCandidate, new VirtualFileVisitor<VirtualFile>() {
                    @Override
                    public @NotNull Result visitFileEx(@NotNull VirtualFile file) {
                        progressIndicator.checkCanceled();
                        String fileName = file.getName();

                        if (file.isDirectory()) {
                            if (fileName.startsWith(".") || "testsuite".equals(fileName) || "test".equals(fileName) || "example".equals(fileName)) {
                                return SKIP_CHILDREN;
                            }
                        } else {
                            FileType type = typeManager.getFileTypeByFileName(fileName);

                            if (type.getDefaultExtension().equals("ml")) {
                                VirtualFile root = file.getParent();
                                if (root != null) {
                                    foundDirectories.add(root);
                                    return skipTo(root);
                                }
                            }
                        }

                        return CONTINUE;
                    }
                });
            } catch (ProcessCanceledException ignore) {
            }

            return foundDirectories;
        }
    }
}
