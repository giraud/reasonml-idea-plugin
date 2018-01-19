package com.reason.lang.core;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.reason.Joiner;
import com.reason.ide.files.FileBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ModulePath {
    private String[] m_names;

    public ModulePath(@NotNull String name) {
        m_names = new String[]{name};
    }

    public ModulePath(@NotNull String[] names) {
        m_names = names; // fragile, should copy names
    }

    public ModulePath(@NotNull ModulePath modulePath, @Nullable String name) {
        if (name == null) {
            m_names = modulePath.getNames();
        } else {
            String[] names = modulePath.getNames();
            m_names = new String[names.length + 1];
            System.arraycopy(names, 0, m_names, 0, names.length);
            m_names[m_names.length - 1] = name;
        }
    }

    public ModulePath(@NotNull PsiElement element) {
        this(Collections.singletonList(element));
    }

    public ModulePath(@NotNull List<PsiElement> elements) {
        m_names = new String[elements.size()];
        for (int i = 0; i < elements.size(); i++) {
            PsiElement element = elements.get(i);
            m_names[i] = element instanceof FileBase ? ((FileBase) element).asModuleName() : ((PsiNamedElement) element).getName();
        }
    }

    public String[] getNames() {
        return m_names;
    }

    @Override
    public String toString() {
        return Joiner.join(".", m_names);
    }
}
