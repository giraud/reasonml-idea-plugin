package com.reason.ide.files;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.ModuleHelper;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.PsiFileHelper;
import com.reason.lang.core.psi.ExpressionScope;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVal;

public abstract class FileBase extends PsiFileBase implements PsiModule {

    @NotNull
    private final String m_moduleName;

    FileBase(@NotNull FileViewProvider viewProvider, @NotNull Language language) {
        super(viewProvider, language);
        m_moduleName = ORUtil.fileNameToModuleName(getName());
    }

    @Nullable
    @Override
    public String getModuleName() {
        return m_moduleName;
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
        return FileHelper.shortLocation(project, getVirtualFile().getPath());
    }

    @Override
    @NotNull
    public Collection<PsiNameIdentifierOwner> getExpressions(ExpressionScope eScope) {
        return PsiFileHelper.getExpressions(this, eScope);
    }

    @NotNull
    public Collection<PsiNameIdentifierOwner> getExpressions(@Nullable String name) {
        return PsiFileHelper.getExpressions(this, name);
    }

    @NotNull
    public <T extends PsiNameIdentifierOwner> List<T> getExpressions(@Nullable String name, @NotNull Class<T> clazz) {
        List<T> result = new ArrayList<>();

        if (name != null) {
            Collection<T> children = PsiTreeUtil.findChildrenOfType(this, clazz);
            for (T child : children) {
                if (name.equals(child.getName())) {
                    result.add(child);
                }
            }
        }

        return result;
    }

    @Nullable
    @Override
    public PsiModule getModuleExpression(@Nullable String name) {
        if (name == null) {
            return null;
        }

        Collection<PsiInnerModule> modules = getExpressions(name, PsiInnerModule.class);
        for (PsiInnerModule module : modules) {
            if (name.equals(module.getName())) {
                return module;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public List<PsiLet> getLetExpressions() {
        return PsiFileHelper.getLetExpressions(this);
    }

    @Nullable
    @Override
    public PsiLet getLetExpression(@Nullable String name) {
        Collection<PsiLet> expressions = getExpressions(name, PsiLet.class);
        return expressions.isEmpty() ? null : expressions.iterator().next();
    }

    @Nullable
    @Override
    public PsiVal getValExpression(@Nullable String name) {
        Collection<PsiVal> expressions = getExpressions(name, PsiVal.class);
        return expressions.isEmpty() ? null : expressions.iterator().next();
    }

    @Nullable
    @Override
    public PsiType getTypeExpression(@Nullable String name) {
        List<PsiType> expressions = getExpressions(name, PsiType.class);
        return expressions.isEmpty() ? null : expressions.iterator().next();
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

    @NotNull
    public Collection<PsiInnerModule> getModules() {
        return PsiFileHelper.getModuleExpressions(this);
    }

    @Nullable
    @Override
    public String getAlias() {
        return null;
    }

    public boolean isInterface() {
        return FileHelper.isInterface(getFileType());
    }

    @Override
    public boolean equals(Object o) {
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
