package com.reason.ide;

import java.util.*;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.diagnostic.Logger;
import com.reason.Joiner;
import com.reason.lang.core.psi.PsiModule;

public class Debug {

    private static final String SEP = ": ";
    private final Logger m_log;

    public Debug(Logger log) {
        m_log = log;
    }

    public boolean isDebugEnabled() {
        return m_log.isDebugEnabled();
    }

    public void debug(String comment) {
        if (m_log.isDebugEnabled()) {
            m_log.debug(comment);
        }
    }

    public void debug(String comment, int t) {
        if (m_log.isDebugEnabled()) {
            m_log.debug(comment + SEP + t);
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
            m_log.debug(comment + SEP + Boolean.toString(t));
        }
    }

    public void debug(String comment, @Nullable List<String> t) {
        if (m_log.isDebugEnabled()) {
            m_log.debug(comment + SEP + (t == null ? "" : t.size() + " ") + "[" + Joiner.join(", ", t) + "]");
        }
    }

    public void debug(String comment, PsiModule module) {
        if (m_log.isDebugEnabled()) {
            m_log.debug(comment + SEP + module.getQualifiedName() + " (" + module.getContainingFile().getVirtualFile().getPath() + ")");
        }
    }

    public void debug(String comment, String t, boolean t1) {
        if (m_log.isDebugEnabled()) {
            m_log.debug(comment + SEP + t + " " + Boolean.toString(t1));
        }
    }
}
