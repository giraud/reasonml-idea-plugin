package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Stack;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.reason.lang.ParserScopeEnum.file;
import static com.reason.lang.ParserScopeType.any;

public abstract class CommonParser implements PsiParser, LightPsiParser {

    protected MlTypes m_types;

    protected CommonParser(MlTypes types) {
        m_types = types;
    }

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


        ParserState parserState = new ParserState(new ParserScope(file, m_types.FILE_MODULE, null));
        parseFile(builder, parserState);

        // if we have a scope at last position in file, without SEMI, we need to handle it here
        if (!parserState.scopes.empty()) {
            ParserScope scope = parserState.scopes.pop();
            while (scope != null) {
                scope.end();
                scope = parserState.scopes.empty() ? null : parserState.scopes.pop();
            }
        }

        new ParserScope(file, m_types.FILE_MODULE, null).end();

        exit_section_(builder, 0, m, elementType, true, true, TRUE_CONDITION);
    }

    protected abstract void parseFile(PsiBuilder builder, ParserState parserState);

    private ParserScope mark(PsiBuilder builder, Stack<ParserScope> scopes, ParserScopeEnum resolution, IElementType tokenType) {
        ParserScope scope = new ParserScope(resolution, tokenType, builder.mark());
        scope.scopeType = any;
        scopes.push(scope);
        return scope;
    }

    protected ParserScope markComplete(PsiBuilder builder, Stack<ParserScope> scopes, ParserScopeEnum resolution, IElementType tokenType) {
        ParserScope scope = mark(builder, scopes, resolution, tokenType);
        scope.complete = true;
        return scope;
    }

    protected ParserScope markScope(PsiBuilder builder, Stack<ParserScope> scopes, ParserScopeEnum resolution, IElementType tokenType, ParserScopeType scopeType, IElementType scopeElementType) {
        ParserScope scope = mark(builder, scopes, resolution, tokenType);
        scope.scopeType = scopeType;
        scope.scopeElementType = scopeElementType;
        return scope;
    }

    protected ParserScope markCompleteScope(PsiBuilder builder, Stack<ParserScope> scopes, ParserScopeEnum resolution, IElementType tokenType, ParserScopeType scopeType, IElementType scopeElementType) {
        ParserScope scope = markScope(builder, scopes, resolution, tokenType, scopeType, scopeElementType);
        scope.complete = true;
        return scope;
    }

    protected boolean advance(PsiBuilder builder) {
        builder.advanceLexer();
        return true;
    }

    protected boolean advance(PsiBuilder builder, IElementType elementType) {
        PsiBuilder.Marker mark = builder.mark();
        advance(builder);
        mark.done(elementType);
        return true;
    }

}
