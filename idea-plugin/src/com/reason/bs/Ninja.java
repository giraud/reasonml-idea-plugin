package com.reason.bs;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Ninja {
  private final List<String> m_includes;
  private final List<String> m_ppxIncludes;
  private final List<String> m_pkgFlags;
  private final List<String> m_bscFlags;

  public Ninja(@Nullable String contents) {
    m_includes = readIncludes(contents);
    m_ppxIncludes = readPpxIncludes(contents);
    m_pkgFlags = readPkgFlags(contents);
    m_bscFlags = readBscFlags(contents);
  }

  private List<String> readIncludes(@Nullable String contents) {
    List<String> result = new ArrayList<>();
    if (contents == null) {
      return result;
    }

    int g_lib_incls = contents.indexOf("g_lib_incls");
    if (g_lib_incls >= 0) {
      int end_of_g_lib_incls = contents.indexOf("\n", g_lib_incls);
      String lineContents = contents.substring(g_lib_incls + 13, end_of_g_lib_incls).trim();
      for (String token : lineContents.split("-I\\s")) {
        String trimmedToken = token.trim();
        if (!trimmedToken.isEmpty()) {
          if (trimmedToken.startsWith("\"")) {
            result.add(trimmedToken.substring(1, trimmedToken.length() - 1));
          } else {
            result.add(trimmedToken);
          }
        }
      }
    }

    int g_dpkg_incls = contents.indexOf("g_dpkg_incls");
    if (g_dpkg_incls >= 0) {
      int end_of_g_dpkg_incls = contents.indexOf("\n", g_dpkg_incls);
      String lineContents = contents.substring(g_dpkg_incls + 14, end_of_g_dpkg_incls).trim();
      for (String token : lineContents.split("-I\\s")) {
        String trimmedToken = token.trim();
        if (!trimmedToken.isEmpty()) {
          if (trimmedToken.startsWith("\"")) {
            result.add(trimmedToken.substring(1, trimmedToken.length() - 1));
          } else {
            result.add(trimmedToken);
          }
        }
      }
    }

    return result;
  }

  public List<String> readPpxIncludes(@Nullable String contents) {
    List<String> result = new ArrayList<>();
    if (contents == null) {
      return result;
    }

    int ppx_flags = contents.indexOf("ppx_flags");
    if (ppx_flags >= 0) {
      int end_of_ppx_flags = contents.indexOf("\n", ppx_flags);
      String ppxLine = contents.substring(ppx_flags + 11, end_of_ppx_flags).trim();
      String[] split = ppxLine.split("-ppx\\s");
      for (String include : split) {
        String trimmedInclude = include.trim();
        if (!trimmedInclude.isEmpty()) {
          if (trimmedInclude.startsWith("\"")) {
            result.add(trimmedInclude.substring(1, trimmedInclude.length() - 1));
          } else {
            result.add(trimmedInclude);
          }
        }
      }
    }

    return result;
  }

  private List<String> readPkgFlags(@Nullable String contents) {
    List<String> result = new ArrayList<>();
    if (contents == null) {
      return result;
    }

    int property = contents.indexOf("g_pkg_flg");
    if (property >= 0) {
      int end_of_property = contents.indexOf("\n", property);
      String lineContents = contents.substring(property + 11, end_of_property).trim();
      for (String tokens : lineContents.split("\\s")) {
        String trimmedToken = tokens.trim();
        if (!trimmedToken.isEmpty()) {
          result.add(trimmedToken);
        }
      }
    }

    return result;
  }

  private List<String> readBscFlags(@Nullable String contents) {
    List<String> result = new ArrayList<>();
    if (contents == null) {
      return result;
    }

    int property = contents.indexOf("bsc_flags");
    if (property >= 0) {
      int end_of_property = contents.indexOf("\n", property);
      String lineContents = contents.substring(property + 11, end_of_property).trim();
      for (String token : lineContents.split("\\s")) {
        String trimmedToken = token.trim();
        if (!trimmedToken.isEmpty()) {
          result.add(trimmedToken);
        }
      }
    }

    return result;
  }

  public void addInclude(@NotNull String source) {
    m_includes.add(source);
  }

  public List<String> getPkgFlags() {
    return m_pkgFlags;
  }

  public List<String> getBscFlags() {
    return m_bscFlags;
  }

  public List<String> getPpxIncludes() {
    return m_ppxIncludes;
  }

  public List<String> getIncludes() {
    return m_includes;
  }
}
