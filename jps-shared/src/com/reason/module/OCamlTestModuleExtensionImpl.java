package com.reason.module;

import com.intellij.openapi.components.PersistentStateComponentWithModificationTracker;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.LanguageLevelProjectExtension;
import com.intellij.openapi.roots.ModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OCamlTestModuleExtensionImpl extends ModuleExtension implements PersistentStateComponentWithModificationTracker<TestModuleState>, OCamlTestModuleExtension {

    private final OCamlTestModuleExtensionImpl m_testExtension;
    private Module m_module;
    private TestModuleState m_state = new TestModuleState();

    @Override
    public long getStateModificationCount() {
        return m_state.getModificationCount();
    }

    public static OCamlTestModuleExtensionImpl getInstance(final Module module) {
        return ModuleRootManager.getInstance(module).getModuleExtension(OCamlTestModuleExtensionImpl.class);
    }

    public OCamlTestModuleExtensionImpl(Module module) {
        m_module = module;
        m_testExtension = null;
    }

    public OCamlTestModuleExtensionImpl(final OCamlTestModuleExtensionImpl source, boolean writable) {
        m_module = source.m_module;
        m_testExtension = source;
        // setter must be used instead of creating new state with constructor param because in any case default language level for module is null (i.e. project language level)
        m_state.setLevel(source.getLevel());
    }

    @Override
    public void setLevel(final String languageLevel) {
        if (m_state.getLevel() == languageLevel) {
            return;
        }

        //LOG.assertTrue(myWritable, "Writable model can be retrieved from writable ModifiableRootModel");
        m_state.setLevel(languageLevel);
    }

    @Nullable
    @Override
    public TestModuleState getState() {
        return m_state;
    }

    @Override
    public void loadState(@NotNull TestModuleState state) {
        m_state = state;
    }

    @Override
    public ModuleExtension getModifiableModel(final boolean writable) {
        return new OCamlTestModuleExtensionImpl(this, writable);
    }

    @Override
    public void commit() {
        if (isChanged()) {
            m_testExtension.m_state.setLevel(m_state.getLevel());
            LanguageLevelProjectExtension.getInstance(m_module.getProject()).languageLevelsChanged();
        }
    }

    @Override
    public boolean isChanged() {
        return m_testExtension != null && !m_testExtension.m_state.equals(m_state);
    }

    @Override
    public void dispose() {
        m_module = null;
        m_state = null;
    }

    @Nullable
    @Override
    public String getLevel() {
        return m_state.getLevel();
    }
}
