package com.reason.ide.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.BsPlatform;
import com.reason.dune.Dune;
import com.reason.esy.Esy;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

@State(name = "ReasonSettings", storages = {@Storage("reason.xml")})
public class ReasonSettings implements PersistentStateComponent<ReasonSettings.ReasonSettingsState> {

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

    public static ReasonSettings getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, ReasonSettings.class);
    }

    private ReasonSettings(@NotNull Project project) {
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

    public String getFormatColumnWidth() {
        if (!StringUtils.isBlank(m_formatColumnWidth)) {
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
        return StringUtils.defaultString(m_bsPlatformLocation);
    }

    public Optional<VirtualFile> getOrFindBsPlatformLocation() {
        if (StringUtils.isBlank(m_bsPlatformLocation)) {
            return BsPlatform.findFirstBsPlatformDirectory(m_project);
        }
        VirtualFile bsPlatformDirectory = LocalFileSystem.getInstance().findFileByPath(m_bsPlatformLocation);
        return Optional.ofNullable(bsPlatformDirectory);
    }

    public String getOrFindBsPlatformLocationAsString() {
        return getOrFindBsPlatformLocation()
                .map(VirtualFile::getPath)
                .orElse("");
    }

    public void setBsPlatformLocation(String bsPlatformLocation) {
        m_bsPlatformLocation = bsPlatformLocation;
    }

    public Optional<VirtualFile> findBsbExecutable() {
        return getOrFindBsPlatformLocation()
                .flatMap(BsPlatform::findBsbExecutable);
    }

    public Optional<VirtualFile> findBscExecutable() {
        return getOrFindBsPlatformLocation()
                .flatMap(BsPlatform::findBscExecutable);
    }

    public String getDuneExecutable() {
        return StringUtils.defaultString(m_duneExecutable);
    }

    public Optional<VirtualFile> getOrFindDuneExecutable() {
        if (StringUtils.isBlank(m_duneExecutable)) {
            return Dune.findDuneExecutable(m_project);
        }
        VirtualFile duneExecutable = LocalFileSystem.getInstance().findFileByPath(m_duneExecutable);
        return Optional.ofNullable(duneExecutable);
    }

    public String getOrFindDuneExecutableAsString() {
        return getOrFindDuneExecutable()
                .map(VirtualFile::getPath)
                .orElse("");
    }

    public void setDuneExecutable(String duneExecutable) {
        m_duneExecutable = duneExecutable;
    }

    public String getEsyExecutable() {
        return StringUtils.defaultString(m_esyExecutable);
    }

    public Optional<VirtualFile> getOrFindEsyExecutable() {
        if (StringUtils.isBlank(m_esyExecutable)) {
            return Esy.findEsyExecutable();
        }
        VirtualFile esyExecutable = LocalFileSystem.getInstance().findFileByPath(m_esyExecutable);
        return Optional.ofNullable(esyExecutable);
    }

    public String getOrFindEsyExecutableAsString() {
        return getOrFindEsyExecutable()
                .map(VirtualFile::getPath)
                .orElse("");
    }

    public void setEsyExecutable(String esyExecutable) {
        m_esyExecutable = esyExecutable;
    }

    @SuppressWarnings("WeakerAccess")
    public static class ReasonSettingsState {
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
