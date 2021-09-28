package com.reason.ide.sdk.sources;

import com.intellij.openapi.fileChooser.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.ui.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.roots.libraries.ui.*;
import com.intellij.openapi.roots.libraries.ui.impl.*;
import com.intellij.openapi.roots.ui.configuration.libraryEditor.*;
import com.intellij.openapi.vfs.*;
import com.reason.ide.sdk.*;
import org.jetbrains.annotations.*;

import java.awt.*;
import java.util.List;
import java.util.*;

/**
 * Menu to see the sources, and add more manually
 * IntelliJ-only (todo: add it inside the settings for others)
 */
public class OCamlSourcesOrderRootTypeUIFactory extends SourcesOrderRootTypeUIFactory {

    @Override public SdkPathEditor createPathEditor(Sdk sdk) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, true, true, false, true, true);
        return new SourcesPathEditor(descriptor, sdk);
    }

    private static class SourcesPathEditor extends SdkPathEditor {
        private final Sdk mySdk;

        SourcesPathEditor(@NotNull FileChooserDescriptor descriptor, Sdk sdk) {
            super("Sourcepath", OCamlSourcesOrderRootType.getInstance(), descriptor);
            mySdk = sdk;
        }

        @Override
        protected @NotNull VirtualFile[] adjustAddedFileSet(final Component component, final VirtualFile[] files) {
            List<OrderRoot> orderRoots =
                    RootDetectionUtil.detectRoots(
                            Arrays.asList(files),
                            component,
                            null,
                            new OCamlRootsDetector(),
                            new OrderRootType[]{OCamlSourcesOrderRootType.getInstance()});

            List<VirtualFile> result = new ArrayList<>();
            for (OrderRoot root : orderRoots) {
                result.add(root.getFile());
            }
            // special case, we are showing SOURCES as CLASSES for OCaml
            if (mySdk.getSdkType() instanceof OCamlSdkType) {
                OCamlSdkType sdk = (OCamlSdkType) mySdk.getSdkType();
                sdk.updateSdkPaths(mySdk, result);
            }

            return VfsUtil.toVirtualFileArray(result);
        }
    }
}
