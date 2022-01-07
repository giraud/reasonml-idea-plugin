package com.reason.comp;

import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.Compiler.*;
import com.reason.comp.bs.*;
import com.reason.comp.dune.*;
import com.reason.comp.esy.*;
import com.reason.comp.rescript.*;
import com.reason.ide.files.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.comp.ORConstants.*;

public class ORCompilerManager {
    private static final Log LOG = Log.create("manager.compiler");
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

    public @Nullable ORResolvedCompiler<? extends Compiler> getCompiler(@NotNull VirtualFile editorFile) {
        boolean shouldTraverse = editorFile.isDirectory() || FileHelper.isCompilable(editorFile.getFileType());
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
        switch (compilerType) {
            case BS:
                return BsCompiler.class;
            case RESCRIPT:
                return ResCompiler.class;
            case DUNE:
                return DuneCompiler.class;
            case ESY:
                return EsyCompiler.class;
            default:
                // this shouldn't happen. fall back to BuckleScript
                LOG.error("Unsupported or null compilerType. compilerType = " + compilerType);
                return BsCompiler.class;
        }
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
                Compiler compiler = getCompiler(CompilerType.ESY);
                myCompiler = compiler == null ? null : new ORResolvedCompiler<>(compiler, file, null);
            } else if (DuneFileType.isDuneFile(file)) {
                // will be empty if dune isn't configured, might be an esy project
                Compiler compiler = getCompiler(CompilerType.DUNE);
                myCompiler = compiler == null ? null : new ORResolvedCompiler<>(compiler, file, null);
            } else if (BsConfigJsonFileType.isBsConfigFile(file)) {
                // could be a rescript or a bucklescript installation
                Compiler rescript = getCompiler(CompilerType.RESCRIPT);
                VirtualFile binDir = ResPlatform.findBinaryPathForConfigFile(myProject, file);
                VirtualFile binFile = binDir == null ? null : ORPlatform.findBinary(binDir, BSC_EXE_NAME);
                if (rescript instanceof ResCompiler && binFile != null) {
                    myCompiler = new ResResolvedCompiler((ResCompiler) rescript, file, binFile);
                } else {
                    Compiler bucklescript = getCompiler(CompilerType.BS);
                    binDir = BsPlatform.findBinaryPathForConfigFile(myProject, file);
                    binFile = binDir == null ? null : ORPlatform.findBinary(binDir, BSC_EXE_NAME);
                    if (bucklescript instanceof BsCompiler && binFile != null) {
                        myCompiler = new BsResolvedCompiler((BsCompiler) bucklescript, file, binFile);
                    }
                }
            }

            return true;
        }
    }
}
