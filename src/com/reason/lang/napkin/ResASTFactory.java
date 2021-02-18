package com.reason.lang.napkin;

import com.reason.lang.core.psi.impl.ORASTFactory;

public class ResASTFactory extends ORASTFactory<ResTypes> {
  public ResASTFactory() {
    super(ResTypes.INSTANCE);
  }
}
