package com.reason.ide.sdk.sources;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.progress.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.*;
import com.intellij.openapi.roots.libraries.ui.*;
import com.intellij.openapi.vfs.*;
import com.intellij.util.containers.*;
import org.jetbrains.annotations.*;

import java.util.*;

/**
 * Given a Path, find the roots.
 * A root is a folder in which we got sources.
 * For OCaml
 * => https://github.com/ocaml/ocaml/releases/tag/4.12.0
 * => source code
 * => take every folder aside "testsuite" and you can see what we call roots
 * <p>
 * This is detecting the roots given the rootCandidate (the folder that is supposed to
 * store the roots).
 */
public class OCamlRootsDetector extends LibraryRootsDetector {

    public static Collection<VirtualFile> suggestOCamlRoots(VirtualFile rootCandidate) {
        return suggestOCamlRoots(rootCandidate, new EmptyProgressIndicator());
    }

    @NotNull
    public static List<VirtualFile> suggestOCamlRoots(@NotNull VirtualFile dir, @NotNull ProgressIndicator progressIndicator) {
        if (!dir.isDirectory()) {
            return ContainerUtil.emptyList();
        }

        final FileTypeManager typeManager = FileTypeManager.getInstance();
        final ArrayList<VirtualFile> foundDirectories = new ArrayList<>();

        VfsUtilCore.visitChildrenRecursively(
                dir,
                new VirtualFileVisitor<VirtualFile>() {
                    @Override
                    public @NotNull Result visitFileEx(@NotNull VirtualFile file) {
                        progressIndicator.checkCanceled();

                        if (!file.isDirectory()) {
                            FileType type = typeManager.getFileTypeByFileName(file.getName());

                            if (type.getDefaultExtension().equals("ml")) {
                                VirtualFile root = file.getParent();
                                if (root != null) {
                                    // unneeded
                                    if (!root.getPath().contains("testsuite")) {
                                        foundDirectories.add(root);
                                    }
                                    return skipTo(root);
                                }
                            }
                        }
                        return CONTINUE;
                    }
                });

        return foundDirectories;
    }

    @Override
    public @NotNull Collection<DetectedLibraryRoot> detectRoots(@NotNull VirtualFile rootCandidate,
                                                                @NotNull ProgressIndicator progressIndicator) {
        List<DetectedLibraryRoot> result = new ArrayList<>();
        OrderRootType OCAML_SOURCES = OCamlSourcesOrderRootType.getInstance();
        Collection<VirtualFile> files = suggestOCamlRoots(rootCandidate, progressIndicator);
        for (VirtualFile file : files) {
            result.add(new DetectedLibraryRoot(file, OCAML_SOURCES, false));
        }
        return result;
    }

    @Override
    public String getRootTypeName(@NotNull LibraryRootType rootType) {
        if (OCamlSourcesOrderRootType.getInstance().equals(rootType.getType())
                && !rootType.isJarDirectory()) {
            return "sources";
        }
        return null;
    }
}
