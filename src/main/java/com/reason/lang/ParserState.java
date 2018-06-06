package com.reason.lang;

import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.Stack;
import org.jetbrains.annotations.Nullable;

import static com.reason.lang.ParserScopeType.any;
import static com.reason.lang.ParserScopeType.scopeExpression;

public class ParserState {

    public boolean dontMove = false;
    public IElementType previousTokenType;

    private final ParserScope m_rootScope;
    private final Stack<ParserScope> m_scopes = new Stack<>();

    private ParserScope currentScope;

    ParserState(ParserScope rootScope) {
        m_rootScope = rootScope;
        currentScope = rootScope;
    }

    // End any scope that is not a scope/group and that is not a start expression
    @Nullable
    public ParserScope endAny() {
        ParserScope scope = null;

        if (!m_scopes.isEmpty()) {
            scope = m_scopes.peek();
            while (scope != null && !scope.start && scope.scopeType == any) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope endUntilStart() {
        ParserScope scope = null;

        if (!m_scopes.isEmpty()) {
            scope = m_scopes.peek();
            while (scope != null && !scope.start) {
                popEnd();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope endUntilScopeExpression(IElementType scopeElementType) {
        ParserScope scope = null;

        if (!m_scopes.isEmpty()) {
            scope = m_scopes.peek();
            while (scope != null && scope.scopeType != scopeExpression && (scopeElementType == null || scope.scopeElementType != scopeElementType)) {
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

    public boolean isResolution(ParserScopeEnum scope) {
        return currentScope.resolution == scope;
    }

    public boolean notResolution(ParserScopeEnum scope) {
        return currentScope.resolution != scope;
    }

    public void setComplete() {
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
        scope.start = start;
    }

    public void addStart(ParserScope scope) {
        add(scope, true);
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

    public boolean isCurrentTokenType(IElementType elementType) {
        return currentScope.tokenType == elementType;
    }

    public boolean isScopeElementType(IElementType scopeElementType) {
        return currentScope.scopeElementType == scopeElementType;
    }

    public void setResolution(ParserScopeEnum resolution) {
        currentScope.resolution = resolution;
    }

    public boolean notInScopeExpression() {
        return currentScope.scopeType != scopeExpression && currentScope.scopeType != ParserScopeType.groupExpression;
    }

    public boolean isInScopeExpression() {
        return currentScope.scopeType == scopeExpression || currentScope.scopeType == ParserScopeType.groupExpression;
    }

    public void setTokenType(IElementType tokenType) {
        currentScope.tokenType = tokenType;
    }

    public void setScopeType(ParserScopeType scopeType) {
        currentScope.scopeType = scopeType;
    }

    public boolean isStart(@Nullable ParserScope scope) {
        return scope != null && scope.start;
    }

    public IElementType getTokenType() {
        return currentScope.tokenType;
    }
}
