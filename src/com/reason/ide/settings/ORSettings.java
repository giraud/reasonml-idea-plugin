package com.reason.ide.settings;

import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.BsPlatform;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;

@State(name = "ORSettings", storages = {@Storage("reason.xml")})
public class ORSettings implements PersistentStateComponent<ORSettings.ORSettingsState> {

    public static final boolean IS_FORMAT_ON_SAVE_DEFAULT = true;
    public static final String FORMAT_WIDTH_COLUMNS_DEFAULT = "80";
    public static final boolean IS_BS_ENABLED_DEFAULT = true;

    private final Project m_project;

    // General
    private boolean m_isFormatOnSaveEnabled = IS_FORMAT_ON_SAVE_DEFAULT;
    private String m_formatColumnWidth = FORMAT_WIDTH_COLUMNS_DEFAULT;
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
    public ORSettingsState getState() {
        ORSettingsState state = new ORSettingsState();
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
    public void loadState(@NotNull ORSettingsState state) {
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

    public String getFormatColumnWidth() {
        if (m_formatColumnWidth != null) {
            return m_formatColumnWidth;
        }
        String systemRefmtWidth = System.getProperties().getProperty("refmtWidth");
        if (systemRefmtWidth != null) {
           return systemRefmtWidth;
        }
        return FORMAT_WIDTH_COLUMNS_DEFAULT;
    }

    public void setFormatColumnWidth(String formatColumnWidth) {
        m_formatColumnWidth = formatColumnWidth;
    }

    public String getOcamlformatExecutable() {
        return StringUtils.defaultString(m_ocamlformatExecutable);
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
        if (StringUtils.isNotBlank(m_bsPlatformLocation)) {
           return m_bsPlatformLocation;
        }
        return BsPlatform.findFirstBsPlatformDirectory(m_project)
                .map(VirtualFile::getPath)
                .orElse("");
    }

    public void setBsPlatformLocation(String bsPlatformLocation) {
        m_bsPlatformLocation = bsPlatformLocation;
    }

    public String getDuneExecutable() {
        return StringUtils.defaultString(m_duneExecutable);
    }

    public void setDuneExecutable(String duneExecutable) {
        m_duneExecutable = duneExecutable;
    }

    public String getEsyExecutable() {
        return StringUtils.defaultString(m_esyExecutable);
    }

    public void setEsyExecutable(String esyExecutable) {
        m_esyExecutable = esyExecutable;
    }

    @SuppressWarnings("WeakerAccess")
    public static class ORSettingsState {
        // General
        public boolean isFormatOnSaveEnabled = IS_FORMAT_ON_SAVE_DEFAULT;
        public String formatColumnWidth = FORMAT_WIDTH_COLUMNS_DEFAULT;
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
