package com.reason.lang.reason;

import com.reason.lang.core.psi.impl.ORASTFactory;

public class RmlASTFactory extends ORASTFactory<RmlTypes> {
  public RmlASTFactory() {
    super(RmlTypes.INSTANCE);
  }
}
