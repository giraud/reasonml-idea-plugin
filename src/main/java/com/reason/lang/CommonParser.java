package com.reason.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.LightPsiParser;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.psi.type.MlTokenElementType;
import com.reason.lang.core.psi.type.MlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import static com.reason.lang.ParserScopeEnum.*;
import static com.reason.lang.ParserScopeType.groupExpression;

public abstract class CommonParser implements PsiParser, LightPsiParser {

    protected MlTypes m_types;

    protected CommonParser(MlTypes types) {
        m_types = types;
    }

    @Override
    @NotNull
    public ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder) {
        builder.setDebugMode(false);

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

        ParserScope fileScope = new ParserScope(file, file, null, null, builder.mark());

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

    protected ParserScope mark(PsiBuilder builder, ParserScopeEnum context) {
        return new ParserScope(context, context, m_types.GENERIC_EXPR, null, builder.mark());
    }

    protected ParserScope mark(PsiBuilder builder, ParserScopeEnum context, IElementType compositeElementType) {
        return new ParserScope(context, context, compositeElementType, null, builder.mark());
    }

    protected ParserScope mark(PsiBuilder builder, ParserScopeEnum context, ParserScopeEnum resolution, IElementType compositeElementType) {
        return new ParserScope(context, resolution, compositeElementType, null, builder.mark());
    }

    protected ParserScope markComplete(PsiBuilder builder, ParserScopeEnum resolution, IElementType compositeElementType) {
        ParserScope scope = mark(builder, resolution, compositeElementType);
        scope.complete = true;
        return scope;
    }

    protected ParserScope markScope(PsiBuilder builder, ParserScopeEnum context, ParserScopeEnum resolution, IElementType compositeElementType, ParserScopeType scopeType, MlTokenElementType scopeTokenElementType) {
        ParserScope scope = new ParserScope(context, resolution, compositeElementType, scopeTokenElementType, builder.mark());
        scope.scopeType = scopeType;
        return scope;
    }

    protected ParserScope markScope(PsiBuilder builder, ParserScopeEnum resolution, IElementType compositeElementType, ParserScopeType scopeType, MlTokenElementType scopeTokenElementType) {
        return markScope(builder, resolution, resolution, compositeElementType, scopeType, scopeTokenElementType);
    }

    protected ParserScope markCompleteScope(PsiBuilder builder, ParserScopeEnum context, ParserScopeEnum resolution, IElementType compositeElementType, ParserScopeType scopeType, MlTokenElementType scopeTokenElement) {
        ParserScope scope = markScope(builder, context, resolution, compositeElementType, scopeType, scopeTokenElement);
        scope.complete = true;
        return scope;
    }

    protected ParserScope markCompleteScope(PsiBuilder builder, ParserScopeEnum resolution, IElementType compositeElementType, ParserScopeType scopeType, MlTokenElementType scopeTokenElement) {
        return markCompleteScope(builder, resolution, resolution, compositeElementType, scopeType, scopeTokenElement);
    }

    protected ParserScope markGroup(@NotNull PsiBuilder builder, @NotNull ParserScopeEnum resolution, @NotNull IElementType compositeElementType, @Nullable MlTokenElementType scopeTokenElementType) {
        return markScope(builder, resolution, compositeElementType, groupExpression, scopeTokenElementType);
    }

    protected ParserScope markCompleteGroup(@NotNull PsiBuilder builder, @NotNull ParserScopeEnum resolution, @NotNull IElementType compositeElementType) {
        ParserScope scope = markGroup(builder, resolution, compositeElementType, null);
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
        return state.isCurrentResolution(typeNamed) || state.isCurrentResolution(typeNamedEq) || state.isCurrentResolution(typeNamedEqVariant);
    }

    protected boolean isModuleResolution(ParserState state) {
        return state.isCurrentResolution(moduleNamed) || state.isCurrentResolution(moduleNamedSignature) || state.isCurrentResolution(moduleNamedColon);
    }

    protected boolean isLetResolution(ParserState state) {
        return state.isCurrentResolution(letNamed) || state.isCurrentResolution(letNamedEq);
    }
}
