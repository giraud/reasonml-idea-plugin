package com.reason.lang.ocamllex;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

public class OclLexParser extends CommonPsiParser {
    protected OclLexParser(boolean isSafe) {
        super(isSafe);
    }

    @Override protected ORParser<OclLexTypes> getORParser(@NotNull PsiBuilder builder) {
        return new OclLexParserState(builder, !myIsSafe);
    }

    static class OclLexParserState extends ORParser<OclLexTypes> {
        protected OclLexParserState(@NotNull PsiBuilder builder, boolean verbose) {
            super(OclLexTypes.INSTANCE, builder, verbose);
        }

        @Override
        public void parse() {
            while (!myBuilder.eof()) {
                IElementType tokenType = myBuilder.getTokenType();

                if (tokenType == myTypes.LET) {
                    popEndUntilScope();
                    mark(myTypes.C_LET);
                } else if (tokenType == myTypes.RULE) {
                    popEndUntilScope();
                    mark(myTypes.C_RULE);
                } else if (tokenType == myTypes.PIPE) {
                    if (strictlyIn(myTypes.C_RULE)) {
                        popEndUntilFoundIndex();
                        mark(myTypes.C_PATTERN);
                    }
                } else if (tokenType == myTypes.LBRACE) {
                    if (!isCurrent(myTypes.C_PATTERN)) {
                        popEndUntilScope();
                    }
                    markScope(myTypes.C_INJECTION, myTypes.LBRACE);
                } else if (tokenType == myTypes.RBRACE) {
                    popEndUntilScope();
                    advance().popEnd();
                    if (isCurrent(myTypes.C_PATTERN)) {
                        popEnd();
                    }
                } else if (tokenType == myTypes.AND) {
                    if (strictlyIn(myTypes.C_RULE)) {
                        popEndUntilScope();
                        advance().mark(myTypes.C_RULE);
                    }
                }

                if (dontMove) {
                    dontMove = false;
                } else {
                    myBuilder.advanceLexer();
                }
            }
        }
    }
}
