package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Stack;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.reason.lang.ParserScopeEnum.file;
import static com.reason.lang.ParserScopeType.groupExpression;
import static com.reason.lang.ParserScopeType.scopeExpression;
import static com.reason.lang.ParserScopeType.startExpression;
import static com.reason.lang.RmlTypes.*;

public abstract class CommonParser  implements PsiParser, LightPsiParser {

    @Override
    @NotNull
    public ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder) {
        parseLight(elementType, builder);
        return builder.getTreeBuilt();
    }

    @Override
    public void parseLight(IElementType elementType, PsiBuilder builder) {
        //builder.setDebugMode(true);
        builder = adapt_builder_(elementType, builder, this, null);
        PsiBuilder.Marker m = enter_section_(builder, 0, _COLLAPSE_, null);

        Stack<ParserScope> scopes = new Stack<>();
        ParserScope fileScope = new ParserScope(file, FILE_MODULE, builder.mark());

        parseFile(builder, scopes, fileScope);

        // if we have a scope at last position in file, wihtout SEMI, we need to handle it here
        if (!scopes.empty()) {
            ParserScope scope = scopes.pop();
            while (scope != null) {
                scope.end();
                scope = scopes.empty() ? null : scopes.pop();
            }
        }

        fileScope.end();

        exit_section_(builder, 0, m, elementType, true, true, TRUE_CONDITION);
    }

    protected abstract void parseFile(PsiBuilder builder, Stack<ParserScope> scopes, ParserScope fileScope);

    @Nullable
    protected ParserScope endScopes(Stack<ParserScope> scopes) {
        ParserScope scope = null;

        if (!scopes.empty()) {
            scope = scopes.peek();
            while (scope != null && scope.scopeType != scopeExpression && scope.scopeType != startExpression && scope.scopeType != groupExpression) {
                scopes.pop().end();
                scope = getLatestScope(scopes);
            }
        }

        return scope;
    }

    @Nullable
    ParserScope endScopesUntilStartExpression(Stack<ParserScope> scopes) {
        ParserScope scope = null;

        if (!scopes.empty()) {
            scope = scopes.peek();
            while (scope != null && scope.scopeType != startExpression) {
                scopes.pop().end();
                scope = getLatestScope(scopes);
            }
        }

        return scope;
    }

    @Nullable
    ParserScope getLatestScope(Stack<ParserScope> scopes) {
        return scopes.empty() ? null : scopes.peek();
    }

    ParserScope markScope(PsiBuilder builder, Stack<ParserScope> scopes, ParserScopeEnum resolution, IElementType tokenType) {
        ParserScope scope = new ParserScope(resolution, tokenType, builder.mark());
        scopes.push(scope);
        return scope;
    }

    ParserScope markScope(PsiBuilder builder, Stack<ParserScope> scopes, ParserScopeEnum resolution, IElementType tokenType, ParserScopeType scopeType) {
        ParserScope scope = new ParserScope(resolution, tokenType, builder.mark());
        scope.scopeType = scopeType;
        scopes.push(scope);
        return scope;
    }

}
