package com.reason.comp;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.*;
import com.reason.comp.Compiler.*;
import com.reason.comp.bs.*;
import com.reason.comp.dune.*;
import com.reason.comp.esy.*;
import com.reason.comp.rescript.*;
import com.reason.ide.files.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORCompilerManager {
    private static final Log LOG = Log.create("compiler.manager");
    private final @NotNull Project myProject;

    public ORCompilerManager(@NotNull Project project) {
        myProject = project;
    }

    public @Nullable Compiler getCompiler(@NotNull CliType cliType) {
        return getCompiler(cliType.getCompilerType());
    }

    public @Nullable Compiler getCompiler(@NotNull CompilerType compilerType) {
        Compiler compiler = myProject.getService(getCompilerClass(compilerType));
        return compiler != null && compiler.isConfigured(myProject) ? compiler : null;
    }

    public @Nullable <T extends Compiler> T getCompiler(@NotNull Class<T> clazz) {
        T compiler = myProject.getService(clazz);
        return compiler != null && compiler.isConfigured(myProject) ? compiler : null;
    }

    public @Nullable ORResolvedCompiler<? extends Compiler> getCompiler(@Nullable VirtualFile editorFile) {
        boolean shouldTraverse = editorFile != null && (editorFile.isDirectory() || FileHelper.isCompilable(editorFile.getFileType()));
        return shouldTraverse ? traverseAncestorsForCompiler(editorFile.getParent(), new HashMap<>()) : null;
    }

    private @Nullable ORResolvedCompiler<? extends Compiler> traverseAncestorsForCompiler(@Nullable VirtualFile currentDir, @NotNull Map<String, VirtualFile> visited) {
        // hit filesystem root, give up
        if (currentDir == null) {
            return null;
        }

        // we've already visited this directory, must have hit a symlink
        if (visited.get(currentDir.getPath()) != null) {
            return null;
        }
        visited.put(currentDir.getPath(), currentDir);

        // look for a compiler configuration file in the current directory
        CompilerVisitor compilerVisitor = new CompilerVisitor();
        VfsUtil.visitChildrenRecursively(currentDir, compilerVisitor);
        if (compilerVisitor.myCompiler != null) {
            return compilerVisitor.myCompiler;
        }

        // we just checked the project root, we're done
        if (currentDir.getPath().equals(myProject.getBasePath())) {
            return null;
        }

        // move up a directory and try again
        return traverseAncestorsForCompiler(currentDir.getParent(), visited);
    }

    private static @NotNull Class<? extends Compiler> getCompilerClass(@NotNull CompilerType compilerType) {
        return switch (compilerType) {
            case BS -> BsCompiler.class;
            case RESCRIPT -> ResCompiler.class;
            case DUNE -> DuneCompiler.class;
            case ESY -> EsyCompiler.class;
        };
    }

    private class CompilerVisitor extends VirtualFileVisitor<VirtualFile> {
        ORResolvedCompiler<? extends Compiler> myCompiler = null;

        CompilerVisitor() {
            super(SKIP_ROOT, NO_FOLLOW_SYMLINKS, ONE_LEVEL_DEEP);
        }

        @Override
        public boolean visitFile(@NotNull VirtualFile file) {
            if (myCompiler != null || file.isDirectory()) {
                return false;
            }

            if (EsyPackageJson.isEsyPackageJson(file)) {
                EsyCompiler compiler = getCompiler(EsyCompiler.class);
                myCompiler = compiler != null ? new ORResolvedCompiler<>(compiler, file, null) : null;
            } else if (DuneFileType.isDuneFile(file)) {
                // Will be empty if dune isn't configured, might be an esy project
                DuneCompiler compiler = getCompiler(DuneCompiler.class);
                myCompiler = compiler != null ? new ORResolvedCompiler<>(compiler, file, null) : null;
            } else if (FileHelper.isRescriptConfigJson(file)) {
                LOG.debug("Detected rescript(only) config", file);
                ResCompiler rescript = getCompiler(ResCompiler.class);
                if (rescript != null) {
                    VirtualFile binFile = ResPlatform.findBscExecutable(myProject, file);
                    myCompiler = binFile != null ? new ResResolvedCompiler(rescript, file, binFile) : null;
                }
            } else if (FileHelper.isBsConfigJson(file)) {
                // Could be either a Rescript or a Bucklescript installation
                ResCompiler rescript = getCompiler(ResCompiler.class);
                VirtualFile binFile = ResPlatform.findBscExecutable(myProject, file);
                if (rescript != null && binFile != null) {
                    myCompiler = new ResResolvedCompiler(rescript, file, binFile);
                } else {
                    BsCompiler bucklescript = getCompiler(BsCompiler.class);
                    binFile = BsPlatform.findBscExecutable(myProject, file);
                    if (bucklescript != null && binFile != null) {
                        myCompiler = new BsResolvedCompiler(bucklescript, file, binFile);
                    }
                }
            }

            return true;
        }
    }
}
