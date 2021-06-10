package com.reason.comp;

import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.*;
import com.reason.comp.bs.*;
import com.reason.comp.dune.*;
import com.reason.comp.esy.*;
import com.reason.comp.rescript.*;
import com.reason.ide.console.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

import java.lang.*;
import java.util.*;

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

    public @Nullable Compiler getCompiler(@NotNull VirtualFile editorFile) {
        FileType fileType = editorFile.getFileType();
        return FileHelper.isCompilable(fileType) ? traverseAncestorsForCompiler(editorFile.getParent(), new HashMap<>()) : null;
    }

    private @Nullable Compiler traverseAncestorsForCompiler(@Nullable VirtualFile currentDir, @NotNull Map<String, VirtualFile> visited) {
        // hit filesystem root, give up
        if (currentDir == null) {
            return null;
        }
        // we've already visited this directory, must have hit a symlink
        if (visited.get(currentDir.getPath()) != null) {
            return null;
        }
        visited.put(currentDir.getPath(), currentDir);
        // look for a compiler configuration file

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
        Compiler myCompiler = null;

        CompilerVisitor() {
            super(SKIP_ROOT, NO_FOLLOW_SYMLINKS, ONE_LEVEL_DEEP);
        }

        @Override
        public boolean visitFile(@NotNull VirtualFile file) {
            if (myCompiler != null) {
                return false;
            }

            if (EsyPackageJson.isEsyPackageJson(file)) {
                myCompiler = getCompiler(CompilerType.ESY);
            } else if (DuneFileType.isDuneFile(file)) {
                // will be empty if dune isn't configured, might be an esy project
                myCompiler = getCompiler(CompilerType.DUNE);
            } else if (BsConfigJsonFileType.isBsConfigFile(file)) {
                // could be a rescript or a bucklescript installation
                Compiler rescript = getCompiler(CompilerType.RESCRIPT);
                if (rescript != null && ResPlatform.findBinaryPathForConfigFile(myProject, file) != null) {
                    myCompiler = rescript;
                } else {
                    Compiler bucklescript = getCompiler(CompilerType.BS);
                    if (bucklescript != null && BsPlatform.findBinaryPathForConfigFile(myProject, file) != null) {
                        myCompiler = bucklescript;
                    }
                }
            }

            return true;
        }
    }
}
