package com.reason.lang.dune;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserScopeEnum;
import com.reason.lang.ParserState;

import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScopeEnum.library;
import static com.reason.lang.ParserScopeEnum.sexpr;
import static com.reason.lang.ParserScopeType.scopeExpression;

public class DuneParser extends CommonParser {
    public DuneParser() {
        super(null);
    }

    @Override
    protected void parseFile(PsiBuilder builder, ParserState state) {
        IElementType tokenType = null;

        //long parseStart = System.currentTimeMillis();

        int c = current_position_(builder);
        while (true) {
            //long parseTime = System.currentTimeMillis();
            //if (5 < parseTime - parseStart) {
            // Protection: abort the parsing if too much time spent
            //break;
            //}

            state.previousTokenType = tokenType;
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            if (tokenType == DuneTypes.LPAREN) {
                state.add(markScope(builder, sexpr, DuneTypes.SEXPR, scopeExpression, null));
            } else if (tokenType == DuneTypes.RPAREN) {
                if (state.isInScopeExpression()) {
                    state.setComplete();
                    state.dontMove = advance(builder);
                    state.popEnd();
                } else {
                    builder.error("Unbalanced parenthesis");
                }
            } else if (tokenType == DuneTypes.VERSION) {
                wrapWith(DuneTypes.VERSION, builder);
            } else if (tokenType == DuneTypes.LIBRARY) {
                parseLibrary(builder, state);
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

    /*
    (library (
        (name <library-name>)
        <optional-fields>
    ))
    */
    private void parseLibrary(PsiBuilder builder, ParserState state) {
        state.add(mark(builder, library, DuneTypes.LIBRARY));
        state.dontMove = advance(builder);
        if (builder.getTokenType() == DuneTypes.LPAREN) {
            state.setComplete();
        }
        else {
            builder.error("need a (");
        }
    }

}
