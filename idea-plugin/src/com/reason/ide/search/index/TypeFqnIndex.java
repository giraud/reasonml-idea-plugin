package com.reason.ide.search.index;

import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class TypeFqnIndex extends IntStubIndexExtension<PsiType> {
  private static final int VERSION = 1;
  private static final StubIndexKey<Integer, PsiType> KEY = StubIndexKey.createIndexKey("reason.type.fqn");
  private static final TypeFqnIndex INSTANCE = new TypeFqnIndex();

  public static @NotNull TypeFqnIndex getInstance() {
    return INSTANCE;
  }

  @Override
  public int getVersion() {
    return super.getVersion() + VERSION;
  }

  @Override
  public @NotNull StubIndexKey<Integer, PsiType> getKey() {
    return KEY;
  }

  @Override
  public @NotNull Collection<PsiType> get(@NotNull final Integer integer, @NotNull final Project project, @NotNull final GlobalSearchScope scope) {
    return StubIndex.getElements(getKey(), integer, project, scope, PsiType.class);
  }
}
