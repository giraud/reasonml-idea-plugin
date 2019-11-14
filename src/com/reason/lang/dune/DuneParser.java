package com.reason.lang.dune;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserState;
import org.jetbrains.annotations.NotNull;

import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScope.mark;
import static com.reason.lang.ParserScope.markScope;
import static com.reason.lang.ParserScopeEnum.*;

public class DuneParser extends CommonParser<DuneTypes> {
    DuneParser() {
        super(DuneTypes.INSTANCE);
    }

    @Override
    protected void parseFile(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        IElementType tokenType = null;

        //long parseStart = System.currentTimeMillis();

        int c = current_position_(builder);
        while (true) {
            //long parseTime = System.currentTimeMillis();
            //if (5 < parseTime - parseStart) {
            // Protection: abort the parsing if too much time spent
            //break;
            //}

            state.previousElementType1 = tokenType;
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            // ( .. )
            if (tokenType == m_types.LPAREN) {
                parseLParen(builder, state);
            } else if (tokenType == m_types.RPAREN) {
                if (state.isCurrentResolution(stanzaNamedFields)) {
                    state.popEnd();
                }

                if (state.isInScopeExpression()) {
                    state.complete();
                    state.advance();
                    state.popEnd();
                } else {
                    builder.error("Unbalanced parenthesis");
                }
            }

            // %{ .. }
            else if (tokenType == m_types.VAR_START) {
                parseVarStart(builder, state);
            } else if (tokenType == m_types.VAR_END) {
                parseVarEnd(builder, state);
            } else if (tokenType == m_types.ATOM) {
                parseAtom(builder, state);
            }

            if (state.dontMove) {
                state.dontMove = false;
            } else {
                builder.advanceLexer();
            }

            if (!empty_element_parsed_guard_(builder, "duneFile", c)) {
                break;
            }

            c = builder.rawTokenIndex();
        }
    }

    private void parseAtom(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentResolution(stanza)) {
            state.updateCurrentResolution(stanzaNamed).
                    advance().
                    add(mark(builder, state.currentContext(), stanzaNamedFields, m_types.C_FIELDS).complete());
        }
    }

    private void parseLParen(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentContext(file)) {
            state.add(markScope(builder, stanza, m_types.C_STANZA, m_types.LPAREN));
        } else if (state.isCurrentResolution(stanzaNamedFields)) {
            state.add(markScope(builder, stanzaFields, field, m_types.C_FIELD, m_types.LPAREN));
        } else {
            state.add(markScope(builder, sexpr, m_types.C_SEXPR, m_types.LPAREN));
        }
    }

    /* |>%{<| .. } */
    private void parseVarStart(PsiBuilder builder, @NotNull ParserState state) {
        state.add(markScope(builder, duneVariable, m_types.C_VAR, m_types.VAR_START));
    }

    /* %{ .. |>}<| */
    private void parseVarEnd(PsiBuilder builder, @NotNull ParserState state) {
        if (state.isCurrentContext(duneVariable)) {
            state.complete().advance().popEnd();
        }
    }

}
