package com.reason.lang.ocamlyacc;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

public class OclYaccParser extends CommonPsiParser {
    protected OclYaccParser(boolean isSafe) {
        super(isSafe);
    }

    @Override
    protected ORParser<OclYaccTypes> getORParser(@NotNull PsiBuilder builder) {
        return new OclYaccParserState(builder, !myIsSafe);
    }

    static class OclYaccParserState extends ORParser<OclYaccTypes> {
        protected OclYaccParserState(@NotNull PsiBuilder builder, boolean verbose) {
            super(OclYaccTypes.INSTANCE, builder, verbose);
        }

        @Override
        public void parse() {
            boolean inRules = false;

            while (!myBuilder.eof()) {
                IElementType tokenType = myBuilder.getTokenType();

                if (tokenType == myTypes.HEADER_START) {
                    markScope(myTypes.C_HEADER, myTypes.HEADER_START);
                } else if (tokenType == myTypes.HEADER_STOP) {
                    advance().popEnd();
                } else if (tokenType == myTypes.TOKEN || tokenType == myTypes.START || tokenType == myTypes.TYPE || tokenType == myTypes.LEFT
                        || tokenType == myTypes.RIGHT || tokenType == myTypes.NON_ASSOC) {
                    popEndUntilScope();
                    mark(myTypes.C_DECLARATION);
                } else if (tokenType == myTypes.SECTION_SEPARATOR) {
                    popEndUntilScope();
                    if (!inRules) {
                        inRules = true;
                    }
                } else if (inRules) {
                    if (tokenType == myTypes.IDENT) {
                        if (rawLookup(1) == myTypes.COLON) {
                            popEndUntilScope();
                            mark(myTypes.C_RULE).advance()
                                    .mark(myTypes.C_RULE_BODY);
                        }
                    } else if (tokenType == myTypes.SEMI) {
                        if (strictlyIn(myTypes.C_RULE)) {
                            advance();
                            popEndUntilFoundIndex();
                            popEnd();
                        }
                    } else if (tokenType == myTypes.LBRACE) {
                        markScope(myTypes.C_INJECTION, myTypes.LBRACE);
                    } else if (tokenType == myTypes.RBRACE) {
                        popEndUntilScopeToken(myTypes.LBRACE);
                        advance().popEnd();
                    } else if (tokenType == myTypes.INLINE) {
                        popEndUntilScope();
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
