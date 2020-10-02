package com.reason.dune;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Platform;
import com.reason.sdk.OCamlSdkType;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import org.jetbrains.annotations.NotNull;

public class Dune {

  private Dune() {}

  public static Optional<VirtualFile> findDuneExecutable(@NotNull Project project) {
    Sdk ocamlSdk = OCamlSdkType.getSDK(project);
    if (ocamlSdk == null) {
      return Optional.empty();
    }
    String ocamlSdkPath = ocamlSdk.getHomePath();
    if (ocamlSdkPath == null) {
      return Optional.empty();
    }
    String extension = Platform.isWindows() ? ".exe" : "";
    Path binaryPath = Paths.get(ocamlSdkPath, "bin", "dune" + extension);
    LocalFileSystem fileSystem = LocalFileSystem.getInstance();
    VirtualFile duneBinary = fileSystem.findFileByPath(binaryPath.toString());
    if (duneBinary == null) {
      return Optional.empty();
    }
    return Optional.of(duneBinary);
  }
}
