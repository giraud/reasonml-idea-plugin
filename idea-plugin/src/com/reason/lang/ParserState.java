package com.reason.lang;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.WhitespaceSkippedCallback;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.reason.lang.core.type.ORCompositeType;
import com.reason.lang.core.type.ORTokenElementType;

public class ParserState {

    public final PsiBuilder m_builder;
    private final ParserScope m_rootComposite;
    private final LinkedList<ParserScope> m_composites = new LinkedList<>();

    private ParserScope m_currentScope;
    @Nullable
    public IElementType previousElementType2;
    @Nullable
    public IElementType previousElementType1;

    public boolean dontMove = false;

    public ParserState(PsiBuilder builder, ParserScope rootCompositeMarker) {
        m_builder = builder;
        m_rootComposite = rootCompositeMarker;
        m_currentScope = rootCompositeMarker;
    }

    // End everything
    public void endAny() {
        if (!m_composites.isEmpty()) {
            ParserScope scope = m_composites.peek();
            while (scope != null) {
                popEnd();
                scope = getLatestScope();
            }
        }
    }

    @NotNull
    public ParserScope popEndUntilStart() {
        ParserScope latestKnownScope = null;

        if (!m_composites.isEmpty()) {
            latestKnownScope = m_composites.peek();
            ParserScope scope = latestKnownScope;
            while (scope != null && !scope.isStart()) {
                scope = pop();
                if (scope != null) {
                    if (scope.isEmpty()) {
                        scope.drop();
                    } else {
                        scope.end();
                    }
                    latestKnownScope = scope;
                }
                scope = getLatestScope();
            }
        }

        return latestKnownScope == null ? m_rootComposite : latestKnownScope;
    }

    @NotNull
    public ParserScope popEndUntilScope() {
        ParserScope latestKnownScope = null;

        if (!m_composites.isEmpty()) {
            latestKnownScope = m_composites.peek();
            ParserScope marker = latestKnownScope;
            while (marker != null && !marker.isScope()) {
                marker = pop();
                if (marker != null) {
                    if (marker.isEmpty()) {
                        marker.drop();
                    } else {
                        marker.end();
                    }
                    latestKnownScope = marker;
                }
                marker = getLatestScope();
            }
        }

        return latestKnownScope == null ? m_rootComposite : latestKnownScope;
    }

    @NotNull
    public ParserState endUntilResolution(@NotNull ParserScopeEnum resolution) {
        if (!m_composites.isEmpty()) {
            ParserScope scope = m_composites.peek();
            while (scope != null && !scope.isResolution(resolution)) {
                popEnd();
                scope = getLatestScope();
            }
        }
        return this;
    }

    @Nullable
    public ParserScope endUntilScopeToken(@NotNull ORTokenElementType scopeElementType) {
        ParserScope scope = null;

        if (!m_composites.isEmpty()) {
            scope = m_composites.peek();
            while (scope != null && scope.getScopeTokenElementType() != scopeElementType) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope getLatestScope() {
        return m_composites.isEmpty() ? null : m_composites.peek();
    }

    public boolean isCurrentResolution(ParserScopeEnum scope) {
        return m_currentScope.isResolution(scope);
    }

    public boolean isPreviousResolution(ParserScopeEnum scope) {
        if (m_composites.size() >= 2) {
            return m_composites.get(1).isResolution(scope);
        }
        return false;
    }

    public boolean isGrandPreviousResolution(ParserScopeEnum scope) {
        if (m_composites.size() >= 3) {
            return m_composites.get(2).isResolution(scope);
        }
        return false;
    }

    @NotNull
    public ParserState complete() {
        m_currentScope.complete();
        return this;
    }

    public void setPreviousCompleteOLD() {
        m_composites.get(1).complete();
    }

    @NotNull
    public ParserState add(@NotNull ParserScope scope) {
        m_composites.push(scope);
        m_currentScope = scope;
        return this;
    }

    // is resolution needed ?
    public ParserState markScope(ParserScopeEnum resolution, ORCompositeType compositeType, ORTokenElementType scopeType) {
        add(ParserScope.markScope(m_builder, resolution, resolution, compositeType, scopeType));
        m_currentScope.complete();
        return this;
    }

    public ParserState markOptional(ParserScopeEnum resolution, ORCompositeType compositeElementType) {
        add(ParserScope.mark(m_builder, resolution, resolution, compositeElementType));
        return this;
    }

    public ParserState markOLD(ParserScopeEnum scope, ParserScopeEnum resolution, ORCompositeType compositeType) {
        add(ParserScope.mark(m_builder, scope, resolution, compositeType));
        m_currentScope.complete();
        return this;
    }

    public ParserState mark(ParserScopeEnum resolution, ORCompositeType compositeElementType) {
        return markOLD(resolution, resolution, compositeElementType);
    }

    boolean empty() {
        return m_composites.isEmpty();
    }

    public ParserScope tryPop(LinkedList<ParserScope> scopes) {
        return empty() ? null : scopes.pop();
    }

    void clear() {
        ParserScope scope = tryPop(m_composites);
        while (scope != null) {
            scope.end();
            scope = tryPop(m_composites);
        }
        m_currentScope = m_rootComposite;
    }

    @Nullable
    public ParserScope pop() {
        ParserScope scope = tryPop(m_composites);
        updateCurrentScopeRml();
        return scope;
    }

    @NotNull
    public ParserState popEnd() {
        ParserScope scope = pop();
        if (scope != null) {
            scope.end();
        }
        return this;
    }

    @NotNull
    public ParserState popCancel() {
        ParserScope scope = pop();
        if (scope != null) {
            scope.drop();
        }
        return this;
    }

    @Nullable
    public ParserScope popEndUntilOneOfElementType(@NotNull ORTokenElementType... scopeElementTypes) {
        ParserScope scope = null;

        if (!m_composites.isEmpty()) {
            scope = m_composites.peek();
            while (scope != null && !ArrayUtil.contains(scope.getScopeTokenElementType(), scopeElementTypes)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope popEndWhileContextOLD(ParserScopeEnum context) {
        ParserScope scope = null;

        if (!m_composites.isEmpty()) {
            scope = m_composites.peek();
            while (scope != null && scope.isContext(context)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @NotNull
    public ParserState popEndUntilResolution(@NotNull ParserScopeEnum resolution) {
        if (!m_composites.isEmpty()) {
            ParserScope scope = m_composites.peek();
            while (scope != null && !scope.isResolution(resolution)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return this;
    }

    public boolean isCurrentCompositeElementType(ORCompositeType compositeType) {
        return m_currentScope.isCompositeEqualTo(compositeType);
    }

    public boolean isScopeTokenElementType(ORTokenElementType scopeTokenElementType) {
        return m_currentScope.isScopeTokenEqualTo(scopeTokenElementType);
    }

    @NotNull
    public ParserState updateCurrentResolution(@NotNull ParserScopeEnum resolution) {
        m_currentScope.resolution(resolution);
        return this;
    }

    public boolean notInScopeExpression() {
        return !m_currentScope.isScope();
    }

    public boolean isInScopeExpression() {
        return m_currentScope.isScope();
    }

    @NotNull
    public ParserState updateCurrentCompositeElementType(@NotNull ORCompositeType compositeElementType) {
        m_currentScope.updateCompositeElementType(compositeElementType);
        return this;
    }

    public ParserScopeEnum currentResolution() {
        return m_currentScope.getResolution();
    }

    @NotNull
    public ParserState advance() {
        m_builder.advanceLexer();
        dontMove = true;
        return this;
    }

    @NotNull
    public ParserState updateScopeToken(@NotNull ORTokenElementType token) {
        m_currentScope.setScopeTokenType(token);
        return this;
    }

    public boolean isCurrentEmpty() {
        return m_currentScope.isEmpty();
    }

    @NotNull
    public ParserState wrapWith(@NotNull ORCompositeType compositeType) {
        if (compositeType instanceof IElementType) {
            IElementType elementType = (IElementType) compositeType;
            PsiBuilder.Marker mark = m_builder.mark();
            m_builder.advanceLexer();
            mark.done(elementType);
            dontMove = true;
        }
        return this;
    }

    @NotNull
    public ParserState setStart() {
        m_currentScope.setIsStart(true);
        return this;
    }

    @NotNull
    public ParserState setStart(boolean isStart) {
        m_currentScope.setIsStart(isStart);
        return this;
    }

    public void error(String message) {
        m_builder.error(message);
    }

    public ParserState remapCurrentToken(ORTokenElementType elementType) {
        m_builder.remapCurrentToken(elementType);
        return this;
    }

    public ParserState setWhitespaceSkippedCallback(@Nullable WhitespaceSkippedCallback callback) {
        m_builder.setWhitespaceSkippedCallback(callback);
        return this;
    }

    public String getTokenText() {
        return m_builder.getTokenText();
    }

    public IElementType rawLookup(int steps) {
        return m_builder.rawLookup(steps);
    }

    public IElementType lookAhead(int steps) {
        return m_builder.lookAhead(steps);
    }

    public IElementType getTokenType() {
        return m_builder.getTokenType();
    }

    public ParserState dummy() {
        m_currentScope.dummy();
        return this;
    }

    // is start useful ?
    public ParserState markStart(ParserScopeEnum scope, ORCompositeType compositeType) {
        mark(scope, compositeType);
        setStart();
        return this;
    }

    public boolean isCurrentContextRml(ParserScopeEnum context) {
        return m_currentScope.isContext(context);
    }

    @NotNull
    public ParserState updateCurrentContextRml(@NotNull ParserScopeEnum context) {
        m_currentScope.context(context);
        return this;
    }

    public ParserScopeEnum currentContextRml() {
        return m_currentScope.getContext();
    }

    @NotNull
    public ParserState popEndUntilContextRml(@NotNull ParserScopeEnum context) {
        if (!m_composites.isEmpty()) {
            ParserScope scope = m_composites.peek();
            while (scope != null && !scope.isContext(context)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return this;
    }

    @NotNull
    public ParserState popEndUnlessFirstContextRml(@NotNull ParserScopeEnum context) {
        if (!m_composites.isEmpty()) {
            ParserScope scope = m_composites.pop();
            ParserScope previousScope = m_composites.isEmpty() ? null : m_composites.peek();
            while (scope != null && previousScope != null && previousScope.isContext(context)) {
                scope.end();
                scope = m_composites.pop();
                previousScope = m_composites.isEmpty() ? null : m_composites.peek();
            }
            m_composites.push(scope);
        }

        return this;
    }

    public ParserState markScopeRml(ParserScopeEnum scope, ParserScopeEnum resolution, ORCompositeType compositeType, ORTokenElementType scopeType) {
        add(ParserScope.markScope(m_builder, scope, resolution, compositeType, scopeType));
        //m_currentScope.complete();
        return this;
    }

    public void updateCurrentScopeRml() {
        m_currentScope = m_composites.isEmpty() ? m_rootComposite : m_composites.peek();
    }
}
