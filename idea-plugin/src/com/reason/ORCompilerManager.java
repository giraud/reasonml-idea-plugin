package com.reason;

import com.intellij.openapi.components.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.bs.*;
import com.reason.dune.*;
import com.reason.esy.*;
import com.reason.ide.console.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

import java.util.*;

@Service
public class ORCompilerManager {
  private static final Log LOG = Log.create("manager.compiler");

  private final @NotNull Project m_project;

  public ORCompilerManager(@NotNull Project project) {
    m_project = project;
  }

  public @NotNull Optional<Compiler> getCompiler(@NotNull CliType cliType) {
    return getCompiler(cliType.getCompilerType());
  }

  public @NotNull Optional<Compiler> getCompiler(@NotNull CompilerType compilerType) {
    Compiler compiler = ServiceManager.getService(m_project, getCompilerClass(compilerType));
    if (compiler.isConfigured(m_project)) {
      return Optional.of(compiler);
    }
    return Optional.empty();
  }

  public @NotNull Optional<Compiler> getCompiler(@NotNull VirtualFile editorFile) {
    FileType fileType = editorFile.getFileType();
    if (FileHelper.isCompilable(fileType)) {
      return traverseAncestorsForCompiler(editorFile.getParent(), new HashMap<>());
    }
    return Optional.empty();
  }

  private class CompilerVisitor extends VirtualFileVisitor<VirtualFile> {
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    Optional<Compiler> compiler = Optional.empty();

    CompilerVisitor() {
      super(SKIP_ROOT, NO_FOLLOW_SYMLINKS, ONE_LEVEL_DEEP);
    }

    @Override
    public boolean visitFile(@NotNull VirtualFile file) {
      if (compiler.isPresent()) {
        return false;
      }

      if (EsyPackageJson.isEsyPackageJson(file)) {
        compiler = getCompiler(CompilerType.ESY);
      } else if (DuneFileType.isDuneFile(file)) {
        // will be empty if dune isn't configured, might be an esy project
        compiler = getCompiler(CompilerType.DUNE);
      } else if (BsConfigJsonFileType.isBsConfigFile(file)) {
        compiler = getCompiler(CompilerType.BS);
      }

      return true;
    }
  }

  private @NotNull Optional<Compiler> traverseAncestorsForCompiler(
      @Nullable VirtualFile currentDir, @NotNull Map<String, VirtualFile> visited) {
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

    CompilerVisitor compilerVisitor = new CompilerVisitor();
    VfsUtil.visitChildrenRecursively(currentDir, compilerVisitor);

    if (compilerVisitor.compiler.isPresent()) {
      return compilerVisitor.compiler;
    }

    // we just checked the project root, we're done
    if (currentDir.getPath().equals(m_project.getBasePath())) {
      return Optional.empty();
    }
    // move up a directory and try again
    return traverseAncestorsForCompiler(currentDir.getParent(), visited);
  }

  private static @NotNull Class<? extends Compiler> getCompilerClass(
      @NotNull CompilerType compilerType) {
    switch (compilerType) {
      case BS:
        return BsCompiler.class;
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
}
