package com.reason.lang;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import static com.intellij.lang.parser.GeneratedParserUtilBase.*;

public abstract class CommonParser<T> implements PsiParser, LightPsiParser {
    protected static final Log LOG = Log.create("parser");

    protected final T myTypes;
    protected final boolean myIsSafe;

    protected CommonParser(boolean isSafe, T types) {
        myIsSafe = isSafe;
        myTypes = types;
    }

    @Override
    @NotNull
    public ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder) {
        //builder.setDebugMode(true);

        //System.out.println("start parsing ");
        //long start = System.currentTimeMillis();

        ASTNode treeBuilt;

        //try {
        parseLight(elementType, builder);
        treeBuilt = builder.getTreeBuilt();
        //} finally {
        //    long end = System.currentTimeMillis();
        //    System.out.println("end parsing in " + (end - start) + "ms");
        //}

        return treeBuilt;
    }

    @Override
    public void parseLight(IElementType elementType, PsiBuilder builder) {
        builder = adapt_builder_(elementType, builder, this);
        PsiBuilder.Marker m = enter_section_(builder, 0, _NONE_);

        ParserState state = new ParserState(!myIsSafe, builder);
        parseFile(builder, state);

        // if we have a scope at last position in a file, without SEMI, we need to handle it here
        if (!state.empty()) {
            state.clear();
        }

        if (myTypes instanceof ORTypes) {
            state.mark(((ORTypes) myTypes).C_FAKE_MODULE).popEnd();
        }

        exit_section_(builder, 0, m, elementType, true, true, TRUE_CONDITION);
    }

    protected abstract void parseFile(PsiBuilder builder, ParserState parserState);

    protected boolean isModuleResolution(@NotNull Marker scope) {
        if (myTypes instanceof ORTypes) {
            ORTypes m_types = (ORTypes) this.myTypes;
            return scope.isCompositeType(m_types.C_MODULE_DECLARATION) || scope.isCompositeType(m_types.C_MODULE_TYPE);
        }
        return false;
    }

    @Nullable
    protected WhitespaceSkippedCallback endJsxPropertyIfWhitespace(@NotNull ParserState state) {
        if (myTypes instanceof ORTypes) {
            ORTypes types = (ORTypes) myTypes;
            return (type, start, end) -> {
                if (state.is(types.C_TAG_PROPERTY)
                        || (state.strictlyIn(types.C_TAG_PROP_VALUE)/* && !state.hasScopeToken()*/)) {
                    if (state.isFound(types.C_TAG_PROP_VALUE)) {
                        state.popEndUntilFoundIndex().popEnd();
                    }
                    state.popEnd();
                    state.setWhitespaceSkippedCallback(null);
                }
            };
        }
        return null;
    }
}
