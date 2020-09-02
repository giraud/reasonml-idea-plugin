package com.reason.ide.settings;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;

@State(name = "ReasonSettings", storages = {@Storage("reason.xml")})
public class ORSettings implements PersistentStateComponent<ORSettings.ReasonSettingsState> {

    public static final boolean IS_FORMAT_ON_SAVE_DEFAULT = true;
    public static final String FORMAT_WIDTH_COLUMNS_DEFAULT = "80";
    public static final boolean IS_BS_ENABLED_DEFAULT = true;

    private final Project m_project;

    // General
    private boolean m_isFormatOnSaveEnabled = IS_FORMAT_ON_SAVE_DEFAULT;
    private String m_formatColumnWidth;
    private String m_ocamlformatExecutable = "";

    // BuckleScript
    private boolean m_isBsEnabled = IS_BS_ENABLED_DEFAULT;
    private String m_bsPlatformLocation = "";

    // Dune
    private String m_duneExecutable = "";

    // Esy
    private String m_esyExecutable = "";

    public static ORSettings getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ORSettings.class);
    }

    private ORSettings(@NotNull Project project) {
        m_project = project;
    }

    @NotNull
    @Override
    public ReasonSettingsState getState() {
        ReasonSettingsState state = new ReasonSettingsState();
        state.isFormatOnSaveEnabled = m_isFormatOnSaveEnabled;
        state.formatColumnWidth = m_formatColumnWidth;
        state.ocamlformatExecutable = m_ocamlformatExecutable;
        state.isBsEnabled = m_isBsEnabled;
        state.bsPlatformLocation = m_bsPlatformLocation;
        state.duneExecutable = m_duneExecutable;
        state.esyExecutable = m_esyExecutable;
        return state;
    }

    @Override
    public void loadState(@NotNull ReasonSettingsState state) {
        m_isFormatOnSaveEnabled = state.isFormatOnSaveEnabled;
        m_formatColumnWidth = state.formatColumnWidth;
        m_ocamlformatExecutable = state.ocamlformatExecutable;
        m_isBsEnabled = state.isBsEnabled;
        m_bsPlatformLocation = state.bsPlatformLocation;
        m_duneExecutable = state.duneExecutable;
        m_esyExecutable = state.esyExecutable;
    }

    public Project getProject() {
        return m_project;
    }

    public boolean isFormatOnSaveEnabled() {
        return m_isBsEnabled && m_isFormatOnSaveEnabled;
    }

    public void setFormatOnSaveEnabled(boolean isFormatOnSaveEnabled) {
        m_isFormatOnSaveEnabled = isFormatOnSaveEnabled;
    }

    @NotNull
    public String getFormatColumnWidth() {
        if (m_formatColumnWidth == null) {
            String systemRefmtWidth = System.getProperties().getProperty("refmtWidth");
            return systemRefmtWidth == null ? FORMAT_WIDTH_COLUMNS_DEFAULT : systemRefmtWidth;
        }

        return m_formatColumnWidth;
    }

    public void setFormatColumnWidth(@Nullable String formatColumnWidth) {
        m_formatColumnWidth = formatColumnWidth != null && formatColumnWidth.isEmpty() ? null : formatColumnWidth;
    }

    public String getOcamlformatExecutable() {
        return m_ocamlformatExecutable == null ? "" : m_ocamlformatExecutable;
    }

    public void setOcamlformatExecutable(String ocamlformatExecutable) {
        m_ocamlformatExecutable = ocamlformatExecutable;
    }

    public boolean isBsEnabled() {
        return m_isBsEnabled;
    }

    public void setBsEnabled(boolean isBsEnabled) {
        m_isBsEnabled = isBsEnabled;
    }

    public String getBsPlatformLocation() {
        return m_bsPlatformLocation == null ? "" : m_bsPlatformLocation;
    }

    public void setBsPlatformLocation(String bsPlatformLocation) {
        m_bsPlatformLocation = bsPlatformLocation;
    }

    @NotNull
    public String getDuneExecutable() {
        return m_duneExecutable == null ? "" : m_duneExecutable;
    }

    public void setDuneExecutable(String duneExecutable) {
        m_duneExecutable = duneExecutable;
    }

    public String getEsyExecutable() {
        return m_esyExecutable == null ? "" : m_esyExecutable;
    }

    public void setEsyExecutable(String esyExecutable) {
        m_esyExecutable = esyExecutable;
    }

    @SuppressWarnings("WeakerAccess")
    public static class ReasonSettingsState {
        // General
        public boolean isFormatOnSaveEnabled = IS_FORMAT_ON_SAVE_DEFAULT;
        public String formatColumnWidth;
        public String ocamlformatExecutable = "";
        // BuckleScript
        public boolean isBsEnabled = IS_BS_ENABLED_DEFAULT;
        public String bsPlatformLocation = "";
        // Dune
        public String duneExecutable = "";
        // Esy
        public String esyExecutable = "";
    }
}
