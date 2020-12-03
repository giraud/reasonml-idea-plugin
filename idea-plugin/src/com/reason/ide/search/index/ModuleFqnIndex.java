package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ModuleFqnIndex extends IntStubIndexExtension<PsiModule> {
  private static final int VERSION = 8;

  public static @Nullable ModuleFqnIndex getInstance() {
    return EP_NAME.findExtension(ModuleFqnIndex.class);
  }

  @Override
  public int getVersion() {
    return super.getVersion() + VERSION;
  }

  @NotNull
  @Override
  public StubIndexKey<Integer, PsiModule> getKey() {
    return IndexKeys.MODULES_FQN;
  }

  @NotNull
  @Override
  public Collection<PsiModule> get(@NotNull final Integer integer, @NotNull final Project project, @NotNull final GlobalSearchScope scope) {
    return StubIndex.getElements(getKey(), integer, project, scope, PsiModule.class);
  }
}
