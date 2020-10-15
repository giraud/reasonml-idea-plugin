package com.reason.ide.search.index;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.impl.PsiFakeModule;
import org.jetbrains.annotations.NotNull;

public class ModuleTopLevelIndex extends StringStubIndexExtension<PsiFakeModule> {
  private static final int VERSION = 2;
  private static final ModuleTopLevelIndex INSTANCE = new ModuleTopLevelIndex();

  @NotNull
  public static ModuleTopLevelIndex getInstance() {
    return INSTANCE;
  }

  @Override
  public int getVersion() {
    return super.getVersion() + VERSION;
  }

  @NotNull
  @Override
  public StubIndexKey<String, PsiFakeModule> getKey() {
    return IndexKeys.MODULES_TOP_LEVEL;
  }
}
