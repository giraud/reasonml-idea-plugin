package com.reason.lang;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

import static com.reason.lang.ParserScopeType.*;

public class ParserState {

    public boolean dontMove = false;
    public IElementType previousTokenType;

    private ParserScope currentScope;
    private final ParserScope m_rootScope;
    private final Stack<ParserScope> m_scopes = new Stack<>();

    ParserState(ParserScope rootScope) {
        m_rootScope = rootScope;
        currentScope = rootScope;
    }

    @Nullable
    public ParserScope end() {
        ParserScope scope = null;

        if (!m_scopes.empty()) {
            scope = m_scopes.peek();
            while (scope != null && scope.scopeType == any) {
                m_scopes.pop().end();
                scope = getLatestScope();
            }
            updateCurrentScope();
        }

        return scope;
    }

    @Nullable
    public ParserScope endUntilStartForced() {
        ParserScope scope = null;

        if (!m_scopes.empty()) {
            scope = m_scopes.peek();
            while (scope != null && scope.scopeType != startExpression) {
                m_scopes.pop().end();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope endUntilStart() {
        ParserScope scope = null;

        if (!m_scopes.empty()) {
            scope = m_scopes.peek();
            while (scope != null && scope.scopeType != startExpression) {
                m_scopes.pop().end();
                scope = getLatestScope();
            }
        }

        updateCurrentScope();

        return scope;
    }

    @Nullable
    public ParserScope endUntilScopeExpression(IElementType scopeElementType) {
        ParserScope scope = null;

        if (!m_scopes.empty()) {
            scope = m_scopes.peek();
            while (scope != null && scope.scopeType != scopeExpression && (scopeElementType == null || scope.scopeElementType != scopeElementType)) {
                m_scopes.pop().end();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope getLatestScope() {
        return m_scopes.empty() ? null : m_scopes.peek();
    }

    public void updateCurrentScope() {
        currentScope = m_scopes.empty() ? m_rootScope : m_scopes.peek();
    }

    public boolean isResolution(ParserScopeEnum scope) {
        return currentScope.resolution == scope;
    }

    public boolean notResolution(ParserScopeEnum scope) {
        return currentScope.resolution != scope;
    }

    public void complete() {
        currentScope.complete = true;
    }

    public void add(ParserScope scope) {
        m_scopes.add(scope);
        currentScope = scope;
    }

    public boolean empty() {
        return m_scopes.empty();
    }

    public void clear() {
        ParserScope scope = m_scopes.pop();
        while (scope != null) {
            scope.end();
            scope = m_scopes.empty() ? null : m_scopes.pop();
        }
        currentScope = m_rootScope;
    }

    @Nullable
    public ParserScope pop() {
        ParserScope scope = m_scopes.pop();
        updateCurrentScope();
        return scope;
    }

    public void popEnd() {
        ParserScope scope = pop();
        if (scope != null) {
            scope.end();
        }
    }

    public void setComplete() {
        currentScope.complete = true;
    }

    public boolean isCurrentTokenType(IElementType elementType) {
        return currentScope.tokenType == elementType;
    }

    public void setResolution(ParserScopeEnum resolution) {
        currentScope.resolution = resolution;
    }

    public boolean notInScopeExpression() {
        return currentScope.scopeType != ParserScopeType.scopeExpression && currentScope.scopeType != ParserScopeType.groupExpression;
    }
}
