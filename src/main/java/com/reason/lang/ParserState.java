package com.reason.lang;

import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.Stack;
import com.reason.lang.core.psi.type.MlTokenElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ParserState {

    public boolean dontMove = false;
    public IElementType previousTokenElementType;

    private final ParserScope m_rootScope;
    private final Stack<ParserScope> m_scopes = new Stack<>();

    private ParserScope currentScope;

    ParserState(ParserScope rootScope) {
        m_rootScope = rootScope;
        currentScope = rootScope;
    }

    // End everything
    public void endAny() {
        ParserScope scope = null;

        if (!m_scopes.isEmpty()) {
            scope = m_scopes.peek();
            while (scope != null) {
                popEnd();
                scope = getLatestScope();
            }
        }
    }

    @NotNull
    public ParserScope endUntilStartScope() {
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

    @Nullable
    public ParserScope endUntilContext(@NotNull ParserScopeEnum context) {
        ParserScope scope = null;

        if (!m_scopes.isEmpty()) {
            scope = m_scopes.peek();
            while (scope != null && !scope.isContext(context)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope endUntilResolution(@NotNull ParserScopeEnum resolution) {
        ParserScope scope = null;

        if (!m_scopes.isEmpty()) {
            scope = m_scopes.peek();
            while (scope != null && !scope.isResolution(resolution)) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope endUntilScopeToken(@NotNull MlTokenElementType scopeElementType) {
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
        currentScope = m_scopes.isEmpty() ? m_rootScope : m_scopes.peek();
    }

    public boolean isCurrentResolution(ParserScopeEnum scope) {
        return currentScope.isResolution(scope);
    }

    public void complete() {
        currentScope.complete = true;
    }

    public void setPreviousComplete() {
        m_scopes.get(m_scopes.size() - 2).complete = true;
    }

    public void add(ParserScope scope) {
        m_scopes.add(scope);
        currentScope = scope;
    }

    public void add(ParserScope scope, boolean start) {
        add(scope);
        scope.setStart(start);
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
        currentScope = m_rootScope;
    }

    @Nullable
    public ParserScope pop() {
        ParserScope scope = m_scopes.tryPop();
        updateCurrentScope();
        return scope;
    }

    public void popEnd() {
        ParserScope scope = pop();
        if (scope != null) {
            scope.end();
        }
    }

    public boolean isCurrentCompositeElementType(IElementType compositeElementType) {
        return currentScope.isCompositeEqualTo(compositeElementType);
    }

    public boolean isScopeTokenElementType(MlTokenElementType scopeTokenElementType) {
        return currentScope.isScopeTokenEqualTo(scopeTokenElementType);
    }

    @NotNull
    public ParserState updateCurrentResolution(@NotNull ParserScopeEnum resolution) {
        currentScope.resolution(resolution);
        return this;
    }

    public boolean notInScopeExpression() {
        return !currentScope.scope;
    }

    public boolean isInScopeExpression() {
        return currentScope.scope;
    }

    public void setTokenElementType(MlTokenElementType tokenType) {
        currentScope.setScopeTokenType(tokenType);
    }

    @NotNull
    public ParserState updateCurrentCompositeElementType(@NotNull IElementType compositeElementType) {
        currentScope.compositeElementType(compositeElementType);
        return this;
    }

    public boolean isCurrentTokenType(MlTokenElementType tokenElementType) {
        return currentScope.isScopeTokenEqualTo(tokenElementType);
    }

    public boolean isCurrentContext(ParserScopeEnum context) {
        return currentScope.isContext(context);
    }

    public ParserState updateCurrentContext(ParserScopeEnum context) {
        currentScope.context(context);
        return this;
    }

    public ParserScopeEnum currentContext() {
        return currentScope.getContext();
    }

}
