package com.reason.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.type.ORTokenElementType;

public class ParserScope {
    private ParserScopeEnum m_resolution;
    private IElementType m_compositeElementType;
    private ParserScopeEnum m_context;
    ORTokenElementType scopeTokenElementType;

    boolean complete = false;
    boolean scope = false;
    private boolean m_scopeStart = false;

    private PsiBuilder.Marker m_mark;

    ParserScope(ParserScopeEnum context, ParserScopeEnum resolution, IElementType compositeElementType, ORTokenElementType scopeTokenElementType, PsiBuilder.Marker mark) {
        m_context = context;
        m_resolution = resolution;
        m_compositeElementType = compositeElementType;
        m_mark = mark;
        this.scopeTokenElementType = scopeTokenElementType;
    }

    public void end() {
        if (complete) {
            done();
        } else {
            drop();
        }
    }

    private void done() {
        if (m_mark != null) {
            if (m_compositeElementType != null) {
                m_mark.done(m_compositeElementType);
            } else {
                m_mark.drop();
            }
            m_mark = null;
        }
    }

    private void drop() {
        if (m_mark != null) {
            m_mark.drop();
            m_mark = null;
        }
    }

    public ParserScope complete() {
        complete = true;
        return this;
    }

    public boolean isResolution(ParserScopeEnum resolution) {
        return m_resolution == resolution;
    }

    public ParserScope resolution(ParserScopeEnum resolution) {
        m_resolution = resolution;
        return this;
    }

    boolean isCompositeEqualTo(IElementType compositeElementType) {
        return m_compositeElementType == compositeElementType;
    }

    boolean isScopeTokenEqualTo(ORTokenElementType tokenElementType) {
        return this.scopeTokenElementType == tokenElementType;
    }

    void setScopeTokenType(ORTokenElementType tokenElementType) {
        this.scopeTokenElementType = tokenElementType;
    }

    public ParserScope compositeElementType(IElementType compositeElementType) {
        m_compositeElementType = compositeElementType;
        return this;
    }

    public boolean isContext(ParserScopeEnum context) {
        return m_context == context;
    }

    public void context(ParserScopeEnum context) {
        m_context = context;
    }

    public ParserScopeEnum getContext() {
        return m_context;
    }

    public boolean isScopeStart() {
        return m_scopeStart;
    }

    public void setStart(boolean start) {
        m_scopeStart = start;
    }

    public ParserScopeEnum getResolution() {
        return m_resolution;
    }
}
