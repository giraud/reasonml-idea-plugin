package com.reason.ide.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import org.jetbrains.annotations.*;

@State(name = "ReasonSettings", storages = {@Storage("reason.xml")})
public class ORSettings implements PersistentStateComponent<ORSettings.ReasonSettingsState> {
    private static final boolean IS_FORMAT_ON_SAVE_DEFAULT = true;
    private static final String FORMAT_WIDTH_COLUMNS_DEFAULT = "80";
    private static final boolean IS_BS_ENABLED_DEFAULT = true;

    private final @NotNull Project m_project;

    // General
    private boolean m_isFormatOnSaveEnabled = IS_FORMAT_ON_SAVE_DEFAULT;
    private @Nullable String m_formatColumnWidth;
    private boolean myUseSuperErrors = false;

    // BuckleScript
    private boolean m_isBsEnabled = IS_BS_ENABLED_DEFAULT;
    private String m_bsPlatformLocation = "";

    // Esy
    private String m_esyExecutable = "";

    private ORSettings(@NotNull Project project) {
        m_project = project;
    }

    @NotNull
    @Override
    public ReasonSettingsState getState() {
        ReasonSettingsState state = new ReasonSettingsState();
        state.isFormatOnSaveEnabled = m_isFormatOnSaveEnabled;
        state.formatColumnWidth = m_formatColumnWidth;
        state.isUseSuperErrors = myUseSuperErrors;
        state.isBsEnabled = m_isBsEnabled;
        state.bsPlatformLocation = m_bsPlatformLocation;
        state.esyExecutable = m_esyExecutable;
        return state;
    }

    @Override
    public void loadState(@NotNull ReasonSettingsState state) {
        m_isFormatOnSaveEnabled = state.isFormatOnSaveEnabled;
        m_formatColumnWidth = state.formatColumnWidth;
        myUseSuperErrors = state.isUseSuperErrors;
        m_isBsEnabled = state.isBsEnabled;
        m_bsPlatformLocation = state.bsPlatformLocation;
        m_esyExecutable = state.esyExecutable;
    }

    public @NotNull Project getProject() {
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
        m_formatColumnWidth =
                formatColumnWidth != null && formatColumnWidth.isEmpty() ? null : formatColumnWidth;
    }

    public boolean isUseSuperErrors() {
        return myUseSuperErrors;
    }

    public void setUseSuperErrors(boolean useSuperErrors) {
        myUseSuperErrors = useSuperErrors;
    }

    public boolean isBsEnabled() {
        return m_isBsEnabled;
    }

    public void setBsEnabled(boolean isBsEnabled) {
        m_isBsEnabled = isBsEnabled;
    }

    public @NotNull String getBsPlatformLocation() {
        return m_bsPlatformLocation == null ? "" : m_bsPlatformLocation;
    }

    public void setBsPlatformLocation(String bsPlatformLocation) {
        m_bsPlatformLocation = bsPlatformLocation;
    }

    public @NotNull String getEsyExecutable() {
        return m_esyExecutable == null ? "" : m_esyExecutable;
    }

    public void setEsyExecutable(String esyExecutable) {
        m_esyExecutable = esyExecutable;
    }

    @SuppressWarnings("WeakerAccess")
    public static class ReasonSettingsState {
        // General
        public boolean isFormatOnSaveEnabled = IS_FORMAT_ON_SAVE_DEFAULT;
        public @Nullable String formatColumnWidth;
        public boolean isUseSuperErrors = false;
        // BuckleScript
        public boolean isBsEnabled = IS_BS_ENABLED_DEFAULT;
        public String bsPlatformLocation = "";
        // Esy
        public String esyExecutable = "";
    }
}
