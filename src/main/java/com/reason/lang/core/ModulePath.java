package com.reason.lang.core;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.reason.Joiner;
import com.reason.ide.files.FileBase;

import java.util.List;

public class ModulePath {
    String[] m_names;

    public ModulePath(String[] names) {
        m_names = names; // fragile, should copy names
    }

    public ModulePath(ModulePath modulePath, String name) {
        String[] names = modulePath.getNames();
        m_names = new String[names.length + 1];
        System.arraycopy(names, 0, m_names, 0, names.length);
        m_names[m_names.length - 1] = name;
    }

    public ModulePath(List<PsiElement> elements) {
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
