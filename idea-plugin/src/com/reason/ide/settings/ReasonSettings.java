package com.reason.ide.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "ReasonSettings", storages = {@Storage("reason.xml")})
public class ReasonSettings implements PersistentStateComponent<ReasonSettings.ReasonSettingsState> {

    private Project m_project;
    private boolean m_enabled = true;
    private boolean m_refmtOnSave = true;
    @NotNull
    private String m_location = "";
    @Nullable
    private String m_refmtWidth;

    public static ReasonSettings getInstance(@NotNull Project project) {
        ReasonSettings settings = ServiceManager.getService(project, ReasonSettings.class);
        settings.m_project = project;
        return settings;
    }

    @NotNull
    @Override
    public ReasonSettingsState getState() {
        ReasonSettingsState state = new ReasonSettingsState();
        state.enabled = m_enabled;
        state.refmtOnSave = m_refmtOnSave;
        state.location = m_location;
        state.refmtWidth = m_refmtWidth;
        return state;
    }

    @Override
    public void loadState(@NotNull ReasonSettingsState state) {
        m_enabled = state.enabled;
        m_refmtOnSave = state.refmtOnSave;
        m_location = state.location;
        m_refmtWidth = state.refmtWidth;
    }

    public Project getProject() {
        return m_project;
    }

    @NotNull
    public String getRefmtWidth() {
        if (m_refmtWidth == null) {
            String sysWidth = System.getProperties().getProperty("refmtWidth");
            return sysWidth == null ? "80" : sysWidth;
        }

        return m_refmtWidth;
    }

    public boolean isEnabled() {
        return m_enabled;
    }

    public boolean isRefmtOnSaveEnabled() {
        return m_enabled && m_refmtOnSave;
    }

    @NotNull
    public String getLocation() {
        return m_location;
    }

    public boolean isRefmtOnSave() {
        return m_refmtOnSave;
    }

    public void setEnabled(boolean enabled) {
        m_enabled = enabled;
    }

    public void setLocation(@NotNull String value) {
        m_location = value;
    }

    public void setRefmtOnSave(boolean value) {
        m_refmtOnSave = value;
    }

    public void setRefmtWidth(@Nullable String value) {
        m_refmtWidth = value;
    }

    @SuppressWarnings("WeakerAccess")
    public static class ReasonSettingsState {
        public boolean enabled = true;
        public boolean refmtOnSave = true;

        @NotNull
        public String location = "";
        @Nullable
        public String refmtWidth;
    }
}
