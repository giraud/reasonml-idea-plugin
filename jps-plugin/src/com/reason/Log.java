package com.reason;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.ResolveResult;
import com.reason.lang.core.psi.PsiQualifiedElement;
import java.io.*;
import java.nio.file.Path;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Log {

  private static final String SEP = ": ";
  @NotNull private final Logger m_log;

  private Log(String name) {
    m_log = Logger.getInstance("ReasonML." + name);
  }

  @NotNull
  public static Log create(String name) {
    return new Log(name);
  }

  public boolean isDebugEnabled() {
    return m_log.isDebugEnabled();
  }

  public boolean isTraceEnabled() {
    return m_log.isTraceEnabled();
  }

  public void debug(String comment) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment);
    }
  }

  public void debug(String msg, Project t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(msg + SEP + t);
    }
  }

  public void debug(String comment, Integer t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + t);
    }
  }

  public void debug(String comment, int t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + t);
    }
  }

  public void debug(String comment, long t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + t);
    }
  }

  public void debug(String comment, int t, @Nullable Collection<?> t1) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(
          comment + SEP + t + (t1 != null && 1 == t1.size() ? " " + t1.iterator().next() : ""));
    }
  }

  public void debug(String comment, String t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + t);
    }
  }

  public void debug(String comment, int t, String t1) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + t + " " + t1);
    }
  }

  public void debug(String comment, String t, String t1) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + t + " " + t1);
    }
  }

  public void debug(String comment, boolean t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + t);
    }
  }

  public void debug(String comment, @Nullable PsiFile @Nullable [] t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + (t == null ? "" : t.length + " "));
    }
  }

  public void debug(String comment, @Nullable Path t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + (t == null ? "" : t + " "));
    }
  }

  public void debug(String comment, @Nullable ResolveResult @Nullable [] t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + (t == null ? "" : t.length + " [" + Joiner.join(", ", t) + "]"));
    }
  }

  public void debug(@NotNull String comment, @Nullable PsiFile t) {
    if (m_log.isDebugEnabled()) {
      debug(comment, t == null ? null : t.getVirtualFile());
    }
  }

  public void debug(@NotNull String comment, @NotNull String t, @Nullable PsiFile t1) {
    if (m_log.isDebugEnabled()) {
      debug(comment + SEP + t, t1 == null ? null : t1.getVirtualFile());
    }
  }

  public void debug(@NotNull String comment, @NotNull String t, @Nullable VirtualFile t1) {
    if (m_log.isDebugEnabled()) {
      debug(comment + SEP + t, t1);
    }
  }

  public void debug(@NotNull String comment, @Nullable VirtualFile t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + (t == null ? "<NULL>" : t.getCanonicalPath() + " "));
    }
  }

  public void debug(@NotNull String comment, @Nullable File t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + (t == null ? "<NULL>" : t.getPath() + " "));
    }
  }

  public void debug(String comment, @Nullable Collection<?> t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(
          comment + SEP + (t == null ? "" : t.size() + " ") + "[" + Joiner.join(", ", t) + "]");
    }
  }

  public void trace(String comment, @Nullable Collection<?> t) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(
          comment + SEP + (t == null ? "" : t.size() + " ") + "[" + Joiner.join(", ", t) + "]");
    }
  }

  public void debug(String comment, @NotNull PsiQualifiedElement element) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(
          comment
              + SEP
              + element.getQualifiedName()
              + " ("
              + element.getContainingFile().getVirtualFile().getPath()
              + ")");
    }
  }

  public void debug(String comment, @NotNull PsiQualifiedElement element, int position) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(
          comment
              + SEP
              + element.getQualifiedName()
              + " ("
              + element.getContainingFile().getVirtualFile().getPath()
              + ") pos="
              + position);
    }
  }

  public void debug(String comment, String t, boolean t1) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + t + " " + t1);
    }
  }

  public void debug(@NotNull String comment, @NotNull Map<String, Integer> map) {
    if (m_log.isDebugEnabled()) {
      StringBuilder sb = new StringBuilder();
      boolean start = true;
      for (Map.Entry<String, Integer> entry : map.entrySet()) {
        if (!start) {
          sb.append(", ");
        }
        sb.append(entry.getKey()).append(":").append(entry.getValue());
        start = false;
      }
      m_log.debug(comment + SEP + "[" + sb.toString() + "]");
    }
  }

  public void debug(String comment, String[] values) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(comment + SEP + "[" + Joiner.join(", ", values) + "]");
    }
  }

  public void debug(String msg, VirtualFile[] files) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(msg + SEP + "[" + Joiner.join(", ", files) + "]");
    }
  }

  public void debug(String msg, PsiElement element) {
    if (m_log.isDebugEnabled()) {
      m_log.debug(
          msg
              + SEP
              + " "
              + element
              + SEP
              + (element instanceof PsiNamedElement
                  ? " [" + ((PsiNamedElement) element).getName() + "]"
                  : ""));
    }
  }

  public void error(String message, Exception e) {
    m_log.error(message, e);
  }

  public void error(String msg) {
    m_log.error(msg);
  }

  public void info(String msg) {
    m_log.info(msg);
  }

  public void warn(String msg) {
    m_log.warn(msg);
  }

  public void warn(@NotNull Exception e) {
    m_log.warn(e);
  }

  public void info(String msg, @NotNull Map<Module, VirtualFile> rootContents) {
    StringBuilder sb = new StringBuilder();
    for (Map.Entry<Module, VirtualFile> entry : rootContents.entrySet()) {
      sb.append("(module ")
          .append(entry.getKey())
          .append(", file ")
          .append(entry.getValue())
          .append(")");
    }
    m_log.info(msg + SEP + sb.toString());
  }

  public void trace(String msg) {
    if (m_log.isTraceEnabled()) {
      m_log.trace(msg);
    }
  }

  public void trace(String msg, VirtualFile sourceFile) {
    if (m_log.isTraceEnabled()) {
      m_log.trace(msg + SEP + "file: " + sourceFile);
    }
  }

  public void trace(String msg, String t) {
    if (m_log.isTraceEnabled()) {
      m_log.trace(msg + SEP + " " + t);
    }
  }
}
