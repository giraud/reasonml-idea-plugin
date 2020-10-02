package com.reason.ide.search.index;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IntStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.stubs.StubIndexKey;
import com.reason.lang.core.psi.PsiParameter;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;

public class ParameterFqnIndex extends IntStubIndexExtension<PsiParameter> {
  private static final int VERSION = 2;
  private static final ParameterFqnIndex INSTANCE = new ParameterFqnIndex();

  @NotNull
  public static ParameterFqnIndex getInstance() {
    return INSTANCE;
  }

  @Override
  public int getVersion() {
    return super.getVersion() + VERSION;
  }

  @NotNull
  @Override
  public StubIndexKey<Integer, PsiParameter> getKey() {
    return IndexKeys.PARAMETERS_FQN;
  }

  @NotNull
  @Override
  public Collection<PsiParameter> get(
      @NotNull final Integer integer,
      @NotNull final Project project,
      @NotNull final GlobalSearchScope scope) {
    return StubIndex.getElements(
        getKey(),
        integer,
        project, /*new JavaSourceFilterScope(scope) TODO*/
        scope,
        PsiParameter.class);
  }
}
