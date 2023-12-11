package com.reason.lang;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import org.jetbrains.annotations.*;

public abstract class CommonPsiParser implements PsiParser {
    public static final int PARSE_MAX_TIME = 1_000; // 1s

    protected final boolean myIsSafe;

    protected CommonPsiParser(boolean isSafe) {
        myIsSafe = isSafe;
    }

    @Override
    public @NotNull ASTNode parse(@NotNull IElementType elementType, @NotNull PsiBuilder builder) {
        //builder.setDebugMode(false); // RELEASE: debug mode is false
        PsiBuilder.Marker r = builder.mark();

        ORParser<?> state = getORParser(builder);
        state.parse();

        // if we have a scope at last position in a file, without SEMI, we need to handle it here
        if (!state.empty()) {
            state.clear();
        }

        // end stream
        if (!builder.eof()) {
            builder.mark().error("Unexpected token");
            while (!builder.eof()) {
                builder.advanceLexer();
            }
        }

        r.done(elementType);

        return builder.getTreeBuilt();
    }

    protected abstract ORParser<?> getORParser(@NotNull PsiBuilder builder);
}
