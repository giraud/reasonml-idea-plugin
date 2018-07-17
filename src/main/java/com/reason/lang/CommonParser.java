package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.type.MlTypes;
import org.jetbrains.annotations.NotNull;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.reason.lang.ParserScopeEnum.*;

public abstract class CommonParser implements PsiParser, LightPsiParser {

    protected MlTypes m_types;

    protected CommonParser(MlTypes types) {
        m_types = types;
    }

    @Override
    @NotNull
    public ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder) {
        builder.setDebugMode(true);

        //System.out.println("start parsing");
        //long start = System.currentTimeMillis();

        parseLight(elementType, builder);
        //noinspection UnnecessaryLocalVariable
        ASTNode treeBuilt = builder.getTreeBuilt();

        //long end = System.currentTimeMillis();
        //System.out.println("end parsing in " + (end - start) + "ms");

        return treeBuilt;
    }

    @Override
    public void parseLight(IElementType elementType, PsiBuilder builder) {
        builder = adapt_builder_(elementType, builder, this, null);
        PsiBuilder.Marker m = enter_section_(builder, 0, _COLLAPSE_, null);

        ParserScope fileScope = new ParserScope(file, null, builder.mark());

        ParserState state = new ParserState(fileScope);
        parseFile(builder, state);

        // if we have a scope at last position in file, without SEMI, we need to handle it here
        if (!state.empty()) {
            state.clear();
        }

        fileScope.end();

        exit_section_(builder, 0, m, elementType, true, true, TRUE_CONDITION);
    }

    protected abstract void parseFile(PsiBuilder builder, ParserState parserState);

    protected ParserScope mark(PsiBuilder builder, ParserScopeEnum resolution, IElementType tokenType) {
        return new ParserScope(resolution, tokenType, builder.mark());
    }

    protected ParserScope markComplete(PsiBuilder builder, ParserScopeEnum resolution, IElementType tokenType) {
        ParserScope scope = mark(builder, resolution, tokenType);
        scope.complete = true;
        return scope;
    }

    protected ParserScope markScope(PsiBuilder builder, ParserScopeEnum resolution, IElementType tokenType, ParserScopeType scopeType, IElementType scopeElementType) {
        ParserScope scope = mark(builder, resolution, tokenType);
        scope.scopeType = scopeType;
        scope.scopeElementType = scopeElementType;
        return scope;
    }

    protected ParserScope markCompleteScope(PsiBuilder builder, ParserScopeEnum resolution, IElementType tokenType, ParserScopeType scopeType, IElementType scopeElementType) {
        ParserScope scope = markScope(builder, resolution, tokenType, scopeType, scopeElementType);
        scope.complete = true;
        return scope;
    }

    protected boolean advance(PsiBuilder builder) {
        builder.advanceLexer();
        return true;
    }

    protected boolean wrapWith(IElementType elementType, PsiBuilder builder) {
        PsiBuilder.Marker mark = builder.mark();
        advance(builder);
        mark.done(elementType);
        return true;
    }

    protected boolean isTypeResolution(ParserState state) {
        return state.isResolution(typeNamed) || state.isResolution(typeNamedEq) || state.isResolution(typeNamedEqVariant);
    }

}
