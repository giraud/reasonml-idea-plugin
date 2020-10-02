package com.reason.module;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.model.ex.JpsElementTypeBase;
import org.jetbrains.jps.model.module.JpsModuleSourceRootType;

/*

     ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
     List<VirtualFile> resourceRoots = rootManager.getSourceRoots(OCamlBinaryRootType.BINARY);

*/
public class OCamlBinaryRootType extends JpsElementTypeBase<OCamlBinaryRootProperties>
    implements JpsModuleSourceRootType<OCamlBinaryRootProperties> {
  public static final OCamlBinaryRootType BINARY = new OCamlBinaryRootType();

  private OCamlBinaryRootType() {}

  @NotNull
  @Override
  public OCamlBinaryRootProperties createDefaultProperties() {
    return new OCamlBinaryRootProperties("", true);
  }
}
