package com.reason.sdk;

import com.intellij.openapi.projectRoots.SdkAdditionalData;
import org.jetbrains.annotations.NotNull;

public class OCamlSdkAdditionalData implements SdkAdditionalData {

  private String m_major = "";
  private String m_minor = "";
  private String m_patch;
  private Boolean m_forced = false;

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
        // case "03":
        //    return "0";
      case "04":
        return "2";
        // case "05":
        //    return "0";
      case "06":
      case "07":
      case "08":
      case "09":
        return "1";
        // case "10":
        //    return "0";
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

  @Override
  public @NotNull String toString() {
    return getMajor() + '.' + getMinor() + '.' + getPatch();
  }
}
