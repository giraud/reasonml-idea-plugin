package com.reason.ide.files;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.ModuleHelper;
import com.reason.lang.PsiFileHelper;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiQualifiedElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FileBase extends PsiFileBase implements PsiQualifiedElement {

  @NotNull private final String m_moduleName;

  FileBase(@NotNull FileViewProvider viewProvider, @NotNull Language language) {
    super(viewProvider, language);
    m_moduleName = ORUtil.fileNameToModuleName(getName());
  }

  @NotNull
  public String getModuleName() {
    return m_moduleName;
  }

  @NotNull
  @Override
  public String getPath() {
    return getModuleName();
  }

  @NotNull
  @Override
  public String getQualifiedName() {
    return getModuleName();
  }

  public boolean isComponent() {
    if (FileHelper.isOCaml(getFileType())) {
      return false;
    }

    return ModuleHelper.isComponent(this);
  }

  @Override
  public PsiElement getNavigationElement() {
    /* ClassCastException ??
    if (isComponent()) {
        PsiLet make = getLetExpression("make");
        if (make != null) {
            return make;
        }
    }
    */
    return super.getNavigationElement();
  }

  @NotNull
  public String shortLocation(@NotNull Project project) {
    return FileHelper.shortLocation(this);
  }

  @NotNull
  public Collection<PsiNamedElement> getExpressions(@Nullable String name) {
    return PsiFileHelper.getExpressions(this, name);
  }

  @SafeVarargs
  @NotNull
  public final <T extends PsiQualifiedElement> List<T> getQualifiedExpressions(
      @Nullable String name, @NotNull Class<? extends T>... clazz) {
    List<T> result = new ArrayList<>();

    if (name != null) {
      Collection<T> children = PsiTreeUtil.findChildrenOfAnyType(this, clazz);
      for (T child : children) {
        if (name.equals(child.getQualifiedName())) {
          result.add(child);
        }
      }
    }

    return result;
  }

  public boolean isInterface() {
    return FileHelper.isInterface(getFileType());
  }

  @Override
  public boolean equals(@Nullable Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    FileBase fileBase = (FileBase) o;
    return m_moduleName.equals(fileBase.m_moduleName) && isInterface() == fileBase.isInterface();
  }

  @Override
  public int hashCode() {
    return Objects.hash(m_moduleName, isInterface());
  }
}
