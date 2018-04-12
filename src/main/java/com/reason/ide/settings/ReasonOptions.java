package com.reason.ide.settings;

import com.intellij.util.xmlb.annotations.Tag;

public class ReasonOptions {
    @Tag("refmtWidth")
    public String m_refmtWidth = "80";

    public ReasonOptions() {
    }

    public ReasonOptions(ReasonOptions options) {
        m_refmtWidth = options.m_refmtWidth;
    }
}
