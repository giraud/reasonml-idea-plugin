package com.reason;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.project.ProjectBundle;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.ui.SdkPathEditor;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.libraries.LibraryRootType;
import com.intellij.openapi.roots.libraries.ui.DetectedLibraryRoot;
import com.intellij.openapi.roots.libraries.ui.LibraryRootsDetector;
import com.intellij.openapi.roots.libraries.ui.OrderRoot;
import com.intellij.openapi.roots.libraries.ui.impl.RootDetectionUtil;
import com.intellij.openapi.roots.ui.OrderRootTypeUIFactory;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileVisitor;
import com.intellij.util.containers.ContainerUtil;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;

public class OCamlSourcesOrderRootTypeUIFactory implements OrderRootTypeUIFactory {
  @Override
  public SdkPathEditor createPathEditor(final Sdk sdk) {
    FileChooserDescriptor descriptor =
        new FileChooserDescriptor(true, true, false, false, false, true);
    return new OCamlSourcesOrderRootTypeUIFactory.SourcesPathEditor(descriptor);
  }

  @Override
  public @NotNull Icon getIcon() {
    return AllIcons.Nodes.Package;
  }

  @Override
  public @NotNull String getNodeText() {
    return ProjectBundle.message("library.sources.node");
  }

  private static class SourcesPathEditor extends SdkPathEditor {

    SourcesPathEditor(@NotNull FileChooserDescriptor descriptor) {
      super(
          ProjectBundle.message("sdk.configure.sourcepath.tab"),
          OCamlSourcesOrderRootType.getInstance(),
          descriptor);
    }

    @NotNull
    @Override
    protected VirtualFile[] adjustAddedFileSet(
        final Component component, final VirtualFile[] files) {
      java.util.List<OrderRoot> orderRoots =
          RootDetectionUtil.detectRoots(
              Arrays.asList(files),
              component,
              null,
              new OCamlRootsDetector(),
              new OrderRootType[] {OCamlSourcesOrderRootType.getInstance()});

      List<VirtualFile> result = new ArrayList<>();
      for (OrderRoot root : orderRoots) {
        result.add(root.getFile());
      }

      return VfsUtil.toVirtualFileArray(result);
    }

    private static class OCamlRootsDetector extends LibraryRootsDetector {
      @NotNull
      @Override
      public Collection<DetectedLibraryRoot> detectRoots(
          @NotNull VirtualFile rootCandidate, @NotNull ProgressIndicator progressIndicator) {
        List<DetectedLibraryRoot> result = new ArrayList<>();
        OrderRootType OCAML_SOURCES = OCamlSourcesOrderRootType.getInstance();
        Collection<VirtualFile> files = suggestOCamlRoots(rootCandidate, progressIndicator);
        for (VirtualFile file : files) {
          result.add(new DetectedLibraryRoot(file, OCAML_SOURCES, false));
        }
        return result;
      }

      @NotNull
      List<VirtualFile> suggestOCamlRoots(
          @NotNull VirtualFile dir, @NotNull final ProgressIndicator progressIndicator) {
        if (!dir.isDirectory()) {
          return ContainerUtil.emptyList();
        }

        final FileTypeManager typeManager = FileTypeManager.getInstance();
        final ArrayList<VirtualFile> foundDirectories = new ArrayList<>();
        try {
          VfsUtilCore.visitChildrenRecursively(
              dir,
              new VirtualFileVisitor() {
                @NotNull
                @Override
                public Result visitFileEx(@NotNull VirtualFile file) {
                  progressIndicator.checkCanceled();

                  if (!file.isDirectory()) {
                    FileType type = typeManager.getFileTypeByFileName(file.getName());

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

      @Override
      public String getRootTypeName(@NotNull LibraryRootType rootType) {
        if (OCamlSourcesOrderRootType.getInstance().equals(rootType.getType())
            && !rootType.isJarDirectory()) {
          return "sources";
        }
        return null;
      }
    }
  }
}
