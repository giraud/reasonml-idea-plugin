package com.reason.ide;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.reason.Joiner;
import com.reason.ide.files.FileBase;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

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

    public void debug(String comment, int t, @Nullable Collection t1) {
        if (m_log.isDebugEnabled()) {
            m_log.debug(comment + SEP + t + (t1 != null && 1 == t1.size() ? " " + t1.iterator().next() : ""));
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

    public void debug(String comment, @Nullable PsiFile[] t) {
        if (m_log.isDebugEnabled()) {
            m_log.debug(comment + SEP + (t == null ? "" : t.length + " "));
        }
    }

    public void debug(String comment, @Nullable FileBase t) {
        if (m_log.isDebugEnabled()) {
            m_log.debug(comment + SEP + (t == null ? "<NULL>" : t.getVirtualFile().getCanonicalPath() + " "));
        }
    }

    public void debug(String comment, @Nullable Collection<? extends Object> t) {
        if (m_log.isDebugEnabled()) {
            m_log.debug(comment + SEP + (t == null ? "" : t.size() + " ") + "[" + Joiner.join(", ", t) + "]");
        }
    }

    public void debug(String comment, PsiQualifiedNamedElement element) {
        if (m_log.isDebugEnabled()) {
            m_log.debug(comment + SEP + element.getQualifiedName() + " (" + element.getContainingFile().getVirtualFile().getPath() + ")");
        }
    }

    public void debug(String comment, PsiQualifiedNamedElement element, int position) {
        if (m_log.isDebugEnabled()) {
            m_log.debug(comment + SEP + element.getQualifiedName() + " (" + element.getContainingFile().getVirtualFile().getPath() + ") pos=" + position);
        }
    }

    public void debug(String comment, String t, boolean t1) {
        if (m_log.isDebugEnabled()) {
            m_log.debug(comment + SEP + t + " " + Boolean.toString(t1));
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
                sb.append(entry.getKey() + ":" + entry.getValue());
                start = false;

            }
            m_log.debug(comment + SEP + "[" + sb.toString() + "]");
        }
    }
}
