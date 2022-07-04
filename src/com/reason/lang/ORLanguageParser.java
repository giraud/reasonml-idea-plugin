package com.reason.lang;

import com.intellij.lang.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

public abstract class ORLanguageParser<T extends ORTypes> extends ORParser<T> {
    protected ORLanguageParser(@NotNull T types, @NotNull PsiBuilder builder, boolean verbose) {
        super(types, builder, verbose);
    }

    public @NotNull ORLanguageParser<T> markDummyParenthesisScope() {
        if (getTokenType() == myTypes.LPAREN) {
            markDummyScope(myTypes.H_PLACE_HOLDER, myTypes.LPAREN).advance();
        }
        return this;
    }

    public @NotNull ORLanguageParser<T> markParenthesisScope() {
        if (getTokenType() == myTypes.LPAREN) {
            markScope(myTypes.C_SCOPED_EXPR, myTypes.LPAREN)
                    .advance().markHolder(myTypes.H_COLLECTION_ITEM);
        }
        return this;
    }

    @Nullable protected WhitespaceSkippedCallback endJsxPropertyIfWhitespace() {
        return (type, start, end) -> {
            if (is(myTypes.C_TAG_PROPERTY) || (strictlyIn(myTypes.C_TAG_PROP_VALUE))) {
                if (isFound(myTypes.C_TAG_PROP_VALUE)) {
                    popEndUntilFoundIndex().popEnd();
                }
                popEnd();
                setWhitespaceSkippedCallback(null);
            }
        };
    }

    public void eof() {
        mark(myTypes.C_FAKE_MODULE).popEnd();
    }
}
