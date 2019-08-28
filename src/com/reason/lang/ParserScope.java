package com.reason.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.type.ORTokenElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParserScope {

    @NotNull
    private final PsiBuilder m_builder;
    private final int m_offset;

    private ParserScopeEnum m_resolution;
    private IElementType m_compositeElementType;
    private ORTokenElementType m_scopeTokenElementType;
    private ParserScopeEnum m_context;
    private boolean m_isComplete = false;
    private boolean m_isDummy = false; // Always drop
    private boolean m_isScope = false;
    private boolean m_scopeStart = false;
    @Nullable
    private PsiBuilder.Marker m_mark;

    private ParserScope(PsiBuilder builder, ParserScopeEnum context, ParserScopeEnum resolution, IElementType compositeElementType, ORTokenElementType scopeTokenElementType) {
        m_builder = builder;
        m_mark = builder.mark();
        m_offset = builder.getCurrentOffset();
        m_context = context;
        m_resolution = resolution;
        m_compositeElementType = compositeElementType;
        m_scopeTokenElementType = scopeTokenElementType;
    }

    public static ParserScope mark(@NotNull PsiBuilder builder, @NotNull ParserScopeEnum context, @NotNull IElementType compositeElementType) {
        return new ParserScope(builder, context, context, compositeElementType, null);
    }

    public static ParserScope mark(@NotNull PsiBuilder builder, @NotNull ParserScopeEnum context, @NotNull ParserScopeEnum resolution, @NotNull IElementType compositeElementType) {
        return new ParserScope(builder, context, resolution, compositeElementType, null);
    }

    @NotNull
    public static ParserScope markScope(@NotNull PsiBuilder builder, @NotNull ParserScopeEnum context, @NotNull ParserScopeEnum resolution, @NotNull IElementType compositeElementType, @NotNull ORTokenElementType scopeTokenElementType) {
        ParserScope parserScope = new ParserScope(builder, context, resolution, compositeElementType, scopeTokenElementType).setIsStart(true);
        parserScope.m_isScope = true;
        return parserScope;
    }

    @NotNull
    public static ParserScope markScope(@NotNull PsiBuilder builder, @NotNull ParserScopeEnum resolution, @NotNull IElementType compositeElementType, @NotNull ORTokenElementType scopeTokenElementType) {
        return markScope(builder, resolution, resolution, compositeElementType, scopeTokenElementType);
    }

    public static ParserScope markRoot(@NotNull PsiBuilder builder) {
        return new ParserScope(builder, ParserScopeEnum.file, ParserScopeEnum.file, null, null);
    }

    public boolean isEmpty() {
        return m_builder.getCurrentOffset() - m_offset == 0;
    }

    public void end() {
        if (m_isDummy) {
            drop();
        } else if (m_isComplete) {
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

    public void drop() {
        if (m_mark != null) {
            m_mark.drop();
            m_mark = null;
        }
    }

    @NotNull
    public ParserScope complete() {
        m_isComplete = true;
        return this;
    }

    @NotNull
    public ParserScope dummy() {
        m_isDummy = true;
        return this;
    }

    public boolean isResolution(ParserScopeEnum resolution) {
        return m_resolution == resolution;
    }

    @NotNull
    public ParserScope resolution(ParserScopeEnum resolution) {
        m_resolution = resolution;
        return this;
    }

    boolean isCompositeEqualTo(IElementType compositeElementType) {
        return m_compositeElementType == compositeElementType;
    }

    boolean isScopeTokenEqualTo(ORTokenElementType tokenElementType) {
        return m_scopeTokenElementType == tokenElementType;
    }

    void setScopeTokenType(@NotNull ORTokenElementType tokenElementType) {
        m_scopeTokenElementType = tokenElementType;
        m_isScope = true;
    }

    @NotNull
    public ParserScope compositeElementType(IElementType compositeElementType) {
        m_compositeElementType = compositeElementType;
        return this;
    }

    public boolean isContext(ParserScopeEnum context) {
        return m_context == context;
    }

    @NotNull
    public ParserScope context(@NotNull ParserScopeEnum context) {
        m_context = context;
        return this;
    }

    public ParserScopeEnum getContext() {
        return m_context;
    }

    public boolean isScopeStart() {
        return m_scopeStart;
    }

    @NotNull
    public ParserScope setIsStart(boolean isStart) {
        m_scopeStart = isStart;
        return this;
    }

    ParserScopeEnum getResolution() {
        return m_resolution;
    }

    public boolean isScope() {
        return m_isScope;
    }

    public ORTokenElementType getScopeTokenElementType() {
        return m_scopeTokenElementType;
    }
}
