package jpsplugin.com.reason.sdk;

import com.intellij.openapi.projectRoots.SdkAdditionalData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class OCamlSdkAdditionalData implements SdkAdditionalData {

    private String m_major = "";
    private String m_minor = "";
    private String m_patch;
    private boolean m_forced = false;
    private boolean m_cygwin = false;
    private @NotNull String m_cygwinBash = "";

    public void setVersionFromHome(@NotNull String versionString) {
        String[] split = versionString.split("\\.");
        if (split.length > 1) {
            m_major = split[0];
            m_minor = split[1];
            if (split.length > 2) {
                m_patch = split[2];
            }
        }
    }

    @NotNull
    public String getMajor() {
        return m_major.isEmpty() ? "4" : m_major;
    }

    public void setMajor(@NotNull String major) {
        m_major = major;
    }

    @NotNull
    public String getMinor() {
        return m_minor.isEmpty() ? "06" : m_minor;
    }

    public void setMinor(@NotNull String minor) {
        m_minor = minor;
    }

    @NotNull
    public String getPatch() {
        if (m_patch != null) {
            return m_patch;
        }

        switch (getMinor()) {
            case "02":
                return "3";
            case "04":
                return "2";
            case "06":
            case "07":
            case "08":
            case "09":
            case "10":
            case "11":
                return "1";
            default:
                return "0";
        }
    }

    public void setPatch(@NotNull String patch) {
        m_patch = patch;
    }

    public Boolean isForced() {
        return m_forced;
    }

    public void setForced(Boolean forced) {
        m_forced = forced;
    }

    public Boolean isCygwin() {
        return m_cygwin;
    }

    public void setCygwin(Boolean cygwin) {
        m_cygwin = cygwin;
    }

    public void setCygwinBash(@Nullable String path) {
        m_cygwinBash = path == null ? "" : path;
    }

    public @NotNull String getCygwinBash() {
        return m_cygwinBash;
    }

    @Override
    public @NotNull String toString() {
        return getMajor() + '.' + getMinor() + '.' + getPatch();
    }
}
