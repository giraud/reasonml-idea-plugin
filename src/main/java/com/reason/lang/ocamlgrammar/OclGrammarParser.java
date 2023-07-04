package com.reason.lang.ocamlgrammar;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

public class OclGrammarParser extends CommonPsiParser {
    protected OclGrammarParser(boolean isSafe) {
        super(isSafe);
    }

    @Override
    protected ORParser<OclGrammarTypes> getORParser(@NotNull PsiBuilder builder) {
        return new OclYaccParserState(builder, !myIsSafe);
    }

    static class OclYaccParserState extends ORParser<OclGrammarTypes> {
        protected OclYaccParserState(@NotNull PsiBuilder builder, boolean verbose) {
            super(OclGrammarTypes.INSTANCE, builder, verbose);
        }

        @Override
        public void parse() {
            while (!myBuilder.eof()) {
                IElementType tokenType = myBuilder.getTokenType();

                if (tokenType == myTypes.VERNAC) {
                    popEndUntilScope();
                    mark(myTypes.C_VERNAC);
                } else if (tokenType == myTypes.TACTIC) {
                    popEndUntilScope();
                    mark(myTypes.C_TACTIC);
                } else if (tokenType == myTypes.ARGUMENT) {
                    if (!isCurrent(myTypes.C_VERNAC)) {
                        popEndUntilScope();
                        mark(myTypes.C_ARGUMENT);
                    }
                } else if (tokenType == myTypes.GRAMMAR) {
                    popEndUntilScope();
                    mark(myTypes.C_GRAMMAR);
                } else if (tokenType == myTypes.END) {
                    advance().popEndUntilScope();
                } else if (tokenType == myTypes.LBRACE) {
                    markScope(myTypes.C_INJECTION, myTypes.LBRACE);
                } else if (tokenType == myTypes.RBRACE) {
                    popEndUntilScopeToken(myTypes.LBRACE);
                    advance().popEnd();
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
