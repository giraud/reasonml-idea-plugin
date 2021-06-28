package com.reason.comp.esy;

import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class Esy {

  private Esy() {}

  public static Optional<VirtualFile> findEsyExecutable() {
    Optional<Path> esyExecutablePath = findEsyExecutableInPath();
    if (esyExecutablePath.isPresent()) {
      LocalFileSystem fileSystem = LocalFileSystem.getInstance();
      return Optional.ofNullable(fileSystem.findFileByPath(esyExecutablePath.get().toString()));
    }
    return findEsyExecutableInstalledWithN();
  }

  private static Optional<VirtualFile> findEsyExecutableInstalledWithN() {
    String homeDirectory = System.getProperty("user.home");
    LocalFileSystem fileSystem = LocalFileSystem.getInstance();
    Path executablePath = Paths.get(homeDirectory, ".n", "bin", EsyConstants.ESY_EXECUTABLE_NAME);
    return Optional.ofNullable(fileSystem.findFileByPath(executablePath.toString()));
  }

  private static @NotNull Optional<Path> findEsyExecutableInPath() {
    String systemPath = System.getenv("PATH");
    return Platform.findExecutableInPath(EsyConstants.ESY_EXECUTABLE_NAME, systemPath);
  }
}
