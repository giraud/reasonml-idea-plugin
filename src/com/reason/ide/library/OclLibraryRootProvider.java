package com.reason.ide.library;

import com.intellij.navigation.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;
import com.reason.ide.settings.*;
import icons.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import java.nio.file.*;
import java.util.*;

/**
 * Manage external library based on opam settings.
 * It is updated automatically when module entries are changed.
 */
public class OclLibraryRootProvider extends AdditionalLibraryRootsProvider {
    private static final Log LOG = Log.create("library.rootProvider");

    @Override
    public @NotNull Collection<SyntheticLibrary> getAdditionalProjectLibraries(@NotNull Project project) {
        // checkReadAccessAllowed
        LOG.debug("Get additional project libraries");

        ORSettings settings = project.getService(ORSettings.class);
        String opamLocation = settings.getOpamLocation();
        String opamSwitch = settings.getSwitchName();
        if (opamLocation.isEmpty() && opamSwitch.isEmpty()) {
            return Collections.emptyList();
        }

        return List.of(new OpamLibrary(opamLocation, opamSwitch));
    }

    @Override
    public @NotNull Collection<VirtualFile> getRootsToWatch(@NotNull Project project) {
        Collection<SyntheticLibrary> libraries = getAdditionalProjectLibraries(project);
        LOG.debug("Roots to watch", libraries);

        return libraries.stream()
                .map(lib -> Collections.singleton(((OpamLibrary) lib).getOpamSwitchLocation()))
                .collect(ArrayList::new, Collection::addAll, Collection::addAll);
    }

    static final class OpamLibrary extends SyntheticLibrary implements ItemPresentation {
        private final Collection<VirtualFile> mySourceRoots = new ArrayList<>();
        private final String myOpamRoot;
        private final String myOpamSwitch;

        private @Nullable VirtualFile getOpamSwitchLocation() {
            return VirtualFileManager.getInstance().findFileByNioPath(Path.of(myOpamRoot, myOpamSwitch));
        }

        public OpamLibrary(@NotNull String opamRoot, @NotNull String opamSwitch) {
            myOpamRoot = opamRoot;
            myOpamSwitch = opamSwitch;

            VirtualFile opamFile = getOpamSwitchLocation();
            if (opamFile != null) {
                VfsUtilCore.visitChildrenRecursively(opamFile, new VirtualFileVisitor<VirtualFile>() {
                    @Override
                    public @NotNull Result visitFileEx(@NotNull VirtualFile file) {
                        if (file.isDirectory()) {
                            String fileName = file.getName();
                            if (fileName.startsWith(".") || fileName.equals("doc")) {
                                return SKIP_CHILDREN;
                            }

                            List<VirtualFile> children = VfsUtil.getChildren(file, child -> {
                                String childName = child.getName();
                                return childName.endsWith(".ml") || childName.endsWith(".mli");
                            });

                            if (!children.isEmpty()) {
                                mySourceRoots.add(file);
                                return SKIP_CHILDREN;
                            }
                        }

                        return CONTINUE;
                    }
                });
            }
        }

        @Override
        public @NotNull Collection<VirtualFile> getSourceRoots() {
            return mySourceRoots;
        }

        @Override
        public @NotNull String getPresentableText() {
            return "Opam switch <" + myOpamSwitch + ">";
        }

        @Override
        public @NotNull String getLocationString() {
            return Path.of(myOpamRoot, myOpamSwitch).toString();
        }

        @Override
        public @NotNull Icon getIcon(boolean unused) {
            return ORIcons.OCL_SDK;
        }

        @Override
        public boolean equals(Object other) {
            return (other instanceof OpamLibrary); // only one opam lib authorized
        }

        @Override
        public int hashCode() {
            return mySourceRoots.hashCode();
        }
    }
}
