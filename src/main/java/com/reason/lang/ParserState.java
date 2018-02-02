package com.reason.lang;

import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

import static com.reason.lang.ParserScopeType.*;

public class ParserState {
    public final ParserScope fileScope;
    public ParserScope currentScope;
    public boolean dontMove = false;
    public Stack<ParserScope> scopes;
    public IElementType previousTokenType;

    ParserState(ParserScope fileScope) {
        this.fileScope = fileScope;
        currentScope = fileScope;
        scopes = new Stack<>();
    }


    @Nullable
    public ParserScope end() {
        ParserScope scope = null;

        if (!scopes.empty()) {
            scope = scopes.peek();
            while (scope != null && scope.scopeType == any) {
                scopes.pop().end();
                scope = getLatestScope();
            }
            updateCurrentScope();
        }

        return scope;
    }

    @Nullable
    public ParserScope endUntilStartForced() {
        ParserScope scope = null;

        if (!scopes.empty()) {
            scope = scopes.peek();
            while (scope != null && scope.scopeType != startExpression) {
                scopes.pop().end();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope endUntilStart() {
        ParserScope scope = null;

        if (!scopes.empty()) {
            scope = scopes.peek();
            while (scope != null && scope.scopeType != startExpression && scope.scopeType != scopeExpression) {
                scopes.pop().end();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope endUntilScopeExpression(IElementType scopeElementType) {
        ParserScope scope = null;

        if (!scopes.empty()) {
            scope = scopes.peek();
            while (scope != null && scope.scopeType != scopeExpression && (scopeElementType == null || scope.scopeElementType != scopeElementType)) {
                scopes.pop().end();
                scope = getLatestScope();
            }
        }

        return scope;
    }

    @Nullable
    public ParserScope getLatestScope() {
        return scopes.empty() ? null : scopes.peek();
    }

    public void updateCurrentScope() {
        currentScope = scopes.empty() ? fileScope : scopes.peek();
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
        scopes.add(scope);
        currentScope = scope;
    }

    public boolean empty() {
        return scopes.empty();
    }

    public void clear() {
        ParserScope scope = scopes.pop();
        while (scope != null) {
            scope.end();
            scope = scopes.empty() ? null : scopes.pop();
        }
        currentScope = fileScope;
    }

    @Nullable
    public ParserScope pop() {
        ParserScope scope = scopes.pop();
        updateCurrentScope();
        return scope;
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
}
