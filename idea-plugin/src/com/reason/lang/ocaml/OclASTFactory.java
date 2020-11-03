package com.reason.lang.ocaml;

import com.reason.lang.core.psi.impl.ORASTFactory;

public class OclASTFactory extends ORASTFactory<OclTypes> {
  public OclASTFactory() {
    super(OclTypes.INSTANCE);
  }
}
