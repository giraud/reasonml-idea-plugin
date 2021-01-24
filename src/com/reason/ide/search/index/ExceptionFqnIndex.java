package com.reason.ide.search.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IntStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiException;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class ExceptionFqnIndex extends IntStubIndexExtension<PsiException> {
  private static final int VERSION = 3;
  private static final ExceptionFqnIndex INSTANCE = new ExceptionFqnIndex();

  @NotNull
  public static ExceptionFqnIndex getInstance() {
    return INSTANCE;
  }

  @Override
  public int getVersion() {
    return super.getVersion() + VERSION;
  }

  @NotNull
  @Override
  public StubIndexKey<Integer, PsiException> getKey() {
    return IndexKeys.EXCEPTIONS_FQN;
  }

  @NotNull
  @Override
  public Collection<PsiException> get(
      @NotNull final Integer integer,
      @NotNull final Project project,
      @NotNull final GlobalSearchScope scope) {
    return StubIndex.getElements(getKey(), integer, project, scope, PsiException.class);
  }
}
