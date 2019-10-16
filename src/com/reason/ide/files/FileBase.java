package com.reason.ide.files;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.ModuleHelper;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.PsiFileHelper;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public abstract class FileBase extends PsiFileBase implements PsiModule {

    @NotNull
    private final String m_moduleName;

    FileBase(@NotNull FileViewProvider viewProvider, @NotNull Language language) {
        super(viewProvider, language);
        m_moduleName = ORUtil.fileNameToModuleName(getName());
    }

    @NotNull
    public String asModuleName() {
        return m_moduleName;
    }

    public boolean isComponent() {
        if (FileHelper.isOCaml(getFileType())) {
            return false;
        }

        return ModuleHelper.isComponent(this);
    }

    @NotNull
    public String shortLocation(@NotNull Project project) {
        return FileHelper.shortLocation(project, getVirtualFile().getPath());
    }

    @NotNull
    public Collection<PsiNameIdentifierOwner> getExpressions() {
        return PsiFileHelper.getExpressions(this);
    }

    @NotNull
    public Collection<PsiNameIdentifierOwner> getExpressions(@Nullable String name) {
        return PsiFileHelper.getExpressions(this, name);
    }

    @NotNull
    public <T extends PsiNameIdentifierOwner> Collection<T> getExpressions(@Nullable String name, @NotNull Class<T> clazz) {
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
    public PsiModule getModuleExpression(@NotNull String name) {
        Collection<PsiInnerModule> modules = getExpressions(name, PsiInnerModule.class);
        for (PsiInnerModule module : modules) {
            if (name.equals(module.getName())) {
                return module;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public String getQualifiedName() {
        return asModuleName();
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
        FileType fileType = getFileType();
        return fileType instanceof RmlInterfaceFileType || fileType instanceof OclInterfaceFileType;
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
        return m_moduleName.equals(fileBase.m_moduleName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_moduleName);
    }
}
