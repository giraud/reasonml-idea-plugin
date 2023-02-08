package com.reason.lang.dune;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.lang.*;
import org.jetbrains.annotations.*;

public class DuneParser extends CommonPsiParser {
    DuneParser() {
        super(true);
    }

    @Override protected ORParser<DuneTypes> getORParser(@NotNull PsiBuilder builder) {
        return new DuneParserState(builder, !myIsSafe);
    }

    static class DuneParserState extends ORParser<DuneTypes> {
        protected DuneParserState(@NotNull PsiBuilder builder, boolean verbose) {
            super(DuneTypes.INSTANCE, builder, verbose);
        }

        @Override
        public void parse() {
            IElementType tokenType;

            while (!myBuilder.eof()) {
                tokenType = myBuilder.getTokenType();

                if (tokenType == myTypes.ATOM) {
                    parseAtom();
                }
                // ( ... )
                else if (tokenType == myTypes.LPAREN) {
                    parseLParen();
                } else if (tokenType == myTypes.RPAREN) {
                    parseRParen();
                }
                // %{ ... }
                else if (tokenType == myTypes.VAR_START) {
                    parseVarStart();
                } else if (tokenType == myTypes.VAR_END) {
                    parseVarEnd();
                }

                if (dontMove) {
                    dontMove = false;
                } else {
                    myBuilder.advanceLexer();
                }
            }
        }

        @Override
        public void eof() {
        }

        private void parseAtom() {
            if (is(myTypes.C_STANZA)) {
                advance().mark(myTypes.C_FIELDS);
            }
        }

        private void parseLParen() {
            if (isRoot()) {
                markScope(myTypes.C_STANZA, myTypes.LPAREN);
            } else if (is(myTypes.C_FIELDS)) {
                markScope(myTypes.C_FIELD, myTypes.LPAREN);
            } else {
                markScope(myTypes.C_SEXPR, myTypes.LPAREN);
            }
        }

        private void parseRParen() {
            if (is(myTypes.C_FIELDS)) {
                popEnd();
            }

            if (rawHasScope()) {
                advance().popEnd();
            } else {
                error("Unbalanced parenthesis");
            }
        }

        private void parseVarStart() {
            // |>%{<| ... }
            markScope(myTypes.C_VAR, myTypes.VAR_START);
        }

        private void parseVarEnd() {
            if (is(myTypes.C_VAR)) {
                // %{ ... |>}<|
                advance().popEnd();
            }
        }
    }
}
