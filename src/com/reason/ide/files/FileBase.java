package com.reason.ide.files;

import com.intellij.extapi.psi.*;
import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public abstract class FileBase extends PsiFileBase implements RPsiQualifiedPathElement {
    private final @NotNull String m_moduleName;

    FileBase(@NotNull FileViewProvider viewProvider, @NotNull Language language) {
        super(viewProvider, language);
        m_moduleName = ORUtil.fileNameToModuleName(getName());
    }

    public @NotNull String getModuleName() {
        return m_moduleName;
    }

    //region PsiQualifiedName
    @Override
    public String @Nullable [] getPath() {
        return null;
    }

    @Override
    public @NotNull String getQualifiedName() {
        return getModuleName();
    }
    //endregion

    public boolean isComponent() {
        if (FileHelper.isOCaml(getFileType())) {
            return false;
        }

        return ModuleHelper.isComponent(this);
    }

    public @Nullable PsiElement getComponentNavigationElement() {
        if (isComponent()) {
            List<RPsiLet> lets = PsiTreeUtil.getStubChildrenOfTypeAsList(this, RPsiLet.class);
            for (RPsiLet let : lets) {
                if ("make".equals(let.getName())) {
                    return let;
                }
            }
            List<RPsiExternal> externals = PsiTreeUtil.getStubChildrenOfTypeAsList(this, RPsiExternal.class);
            for (RPsiExternal external : externals) {
                if ("make".equals(external.getName())) {
                    return external;
                }
            }
        }
        return null;
    }

    @SafeVarargs
    public @NotNull final <T extends PsiQualifiedNamedElement> List<T> getQualifiedExpressions(@Nullable String name, @NotNull Class<? extends T>... clazz) {
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
