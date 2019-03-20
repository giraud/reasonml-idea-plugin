package com.reason.lang;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ArrayUtil;
import com.intellij.util.containers.Stack;
import com.reason.lang.core.type.ORTokenElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParserState {

    private final PsiBuilder m_builder;
    private final ParserScope m_rootScope;
    private final Stack<ParserScope> m_scopes = new Stack<>();

    private ParserScope m_currentScope;
    @Nullable
    public IElementType previousTokenElementType;
    public boolean dontMove = false;

    public ParserState(PsiBuilder builder, ParserScope rootScope) {
        m_builder = builder;
        m_rootScope = rootScope;
        m_currentScope = rootScope;
    }

    // End everything
    public void endAny() {
        if (!m_scopes.isEmpty()) {
            ParserScope scope = m_scopes.peek();
            while (scope != null) {
                popEnd();
                scope = getLatestScope();
            }
        }
    }

    @NotNull
    public ParserScope popEndUntilStartScope() {
        ParserScope latestKnownScope = null;

        if (!m_scopes.isEmpty()) {
            latestKnownScope = m_scopes.peek();
            ParserScope scope = latestKnownScope;
            while (scope != null && !scope.isScopeStart()) {
                popEnd();
                scope = getLatestScope();
                if (scope != null) {
                    latestKnownScope = scope;
                }
            }
        }

        return latestKnownScope == null ? m_rootScope : latestKnownScope;
    }

    @NotNull
    public ParserState popEndUntilContext(@NotNull ParserScopeEnum context) {
        if (!m_scopes.isEmpty()) {
            ParserScope scope = m_scopes.peek();
            while (scope != null && !scope.isContext(context)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return this;
    }

    public void endUntilResolution(@NotNull ParserScopeEnum resolution) {
        if (!m_scopes.isEmpty()) {
            ParserScope scope = m_scopes.peek();
            while (scope != null && !scope.isResolution(resolution)) {
                popEnd();
                scope = getLatestScope();
            }
        }

    }

    @Nullable
    public ParserScope endUntilScopeToken(@NotNull ORTokenElementType scopeElementType) {
        ParserScope scope = null;

        if (!m_scopes.isEmpty()) {
            scope = m_scopes.peek();
            while (scope != null && scope.scopeTokenElementType != scopeElementType) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope getLatestScope() {
        return m_scopes.isEmpty() ? null : m_scopes.peek();
    }

    public void updateCurrentScope() {
        m_currentScope = m_scopes.isEmpty() ? m_rootScope : m_scopes.peek();
    }

    public boolean isCurrentResolution(ParserScopeEnum scope) {
        return m_currentScope.isResolution(scope);
    }

    @NotNull
    public ParserState complete() {
        m_currentScope.complete();
        return this;
    }

    public void setPreviousComplete() {
        m_scopes.get(m_scopes.size() - 2).complete();
    }

    @NotNull
    public ParserState add(@NotNull ParserScope scope) {
        m_scopes.add(scope);
        m_currentScope = scope;
        return this;
    }

    boolean empty() {
        return m_scopes.isEmpty();
    }

    void clear() {
        ParserScope scope = m_scopes.tryPop();
        while (scope != null) {
            scope.end();
            scope = m_scopes.tryPop();
        }
        m_currentScope = m_rootScope;
    }

    @Nullable
    public ParserScope pop() {
        ParserScope scope = m_scopes.tryPop();
        updateCurrentScope();
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

        if (!m_scopes.isEmpty()) {
            scope = m_scopes.peek();
            while (scope != null && !ArrayUtil.contains(scope.scopeTokenElementType, scopeElementTypes)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope popEndWhileContext(ParserScopeEnum context) {
        ParserScope scope = null;

        if (!m_scopes.isEmpty()) {
            scope = m_scopes.peek();
            while (scope != null && scope.isContext(context)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @NotNull
    public ParserState popEndUntilResolution(@NotNull ParserScopeEnum resolution) {
        if (!m_scopes.isEmpty()) {
            ParserScope scope = m_scopes.peek();
            while (scope != null && !scope.isResolution(resolution)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return this;
    }

    @NotNull
    public ParserState popEndUnlessFirstContext(@NotNull ParserScopeEnum context) {
        if (!m_scopes.isEmpty()) {
            ParserScope scope = m_scopes.pop();
            ParserScope previousScope = m_scopes.isEmpty() ? null : m_scopes.peek();
            while (scope != null && previousScope != null && previousScope.isContext(context)) {
                scope.end();
                scope = m_scopes.pop();
                previousScope = m_scopes.isEmpty() ? null : m_scopes.peek();
            }
            m_scopes.push(scope);
        }

        return this;
    }

    public boolean isCurrentCompositeElementType(IElementType compositeElementType) {
        return m_currentScope.isCompositeEqualTo(compositeElementType);
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

    public void setTokenElementType(@NotNull ORTokenElementType tokenType) {
        m_currentScope.setScopeTokenType(tokenType);
    }

    @NotNull
    public ParserState updateCurrentCompositeElementType(@NotNull IElementType compositeElementType) {
        m_currentScope.compositeElementType(compositeElementType);
        return this;
    }

    public boolean isCurrentContext(ParserScopeEnum context) {
        return m_currentScope.isContext(context);
    }

    @NotNull
    public ParserState updateCurrentContext(@NotNull ParserScopeEnum context) {
        m_currentScope.context(context);
        return this;
    }

    public ParserScopeEnum currentContext() {
        return m_currentScope.getContext();
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
    public ParserState wrapWith(@NotNull IElementType elementType) {
        PsiBuilder.Marker mark = m_builder.mark();
        m_builder.advanceLexer();
        mark.done(elementType);
        dontMove = true;
        return this;
    }

    public ParserState setStart() {
        m_currentScope.setIsStart(true);
        return this;
    }

}
