package com.reason;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.BsCompiler;
import com.reason.dune.DuneCompiler;
import com.reason.esy.EsyCompiler;
import com.reason.esy.EsyPackageJson;
import com.reason.ide.console.CliType;
import com.reason.ide.files.*;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Optional;

public class ORCompilerManager {

    private final Project project;

    public static ORCompilerManager getInstance(@NotNull Project project) {
        return new ORCompilerManager(project);
    }

    private ORCompilerManager(@NotNull Project project) {
        this.project = project;
    }

    public Optional<Compiler> getCompiler(CliType cliType) {
        return getCompiler(cliType.getCompilerType());
    }

    public Optional<Compiler> getCompiler(CompilerType compilerType) {
        Compiler compiler = ServiceManager.getService(project, getCompilerClass(compilerType));
        if (compiler.isConfigured(project)) {
            return Optional.of(compiler);
        }
        return Optional.empty();
    }

    public Optional<Compiler> getCompiler(VirtualFile editorFile) {
        FileType fileType = editorFile.getFileType();
        if (FileHelper.isReason(fileType) || FileHelper.isOCaml(fileType)) {
            return traverseAncestorsForCompiler(editorFile.getParent(), new HashMap<>());
        }
        return Optional.empty();
    }

    private Optional<Compiler> traverseAncestorsForCompiler(VirtualFile currentDir,
            HashMap<String, VirtualFile> visited) {
        // hit filesystem root, give up
        if (currentDir == null) {
            return Optional.empty();
        }
        // we've already visited this directory, must have hit a symlink
        if (visited.get(currentDir.getPath()) != null) {
            return Optional.empty();
        }
        visited.put(currentDir.getPath(), currentDir);
        // look for a compiler configuration file
        for (VirtualFile child : currentDir.getChildren()) {
            if (EsyPackageJson.isEsyPackageJson(child)) {
                Optional<Compiler> compiler = getCompiler(CompilerType.ESY);
                if (compiler.isPresent()) {
                    return compiler;
                }
            }
            if (DuneFileType.isDuneFile(child)) {
                // will be empty if dune isn't configured, might be an esy project
                Optional<Compiler> compiler = getCompiler(CompilerType.DUNE);
                if (compiler.isPresent()) {
                    return compiler;
                }
            }
            if (BsConfigJsonFileType.isBsConfigFile(child)) {
                Optional<Compiler> compiler = getCompiler(CompilerType.BS);
                if (compiler.isPresent()) {
                    return compiler;
                }
            }
        }
        // we just checked the project root, we're done
        if (currentDir.getPath().equals(project.getBasePath())) {
            return Optional.empty();
        }
        // move up a directory and try again
        return traverseAncestorsForCompiler(currentDir.getParent(), visited);
    }

    private static Class<? extends Compiler> getCompilerClass(CompilerType compilerType) {
        switch (compilerType) {
            case BS:
                return BsCompiler.class;
            case DUNE:
                return DuneCompiler.class;
            case ESY:
                return EsyCompiler.class;
            default:
                throw new RuntimeException("Unsupported compiler type. Type = " + compilerType);
        }
    }
}
