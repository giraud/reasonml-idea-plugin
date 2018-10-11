package com.reason.lang.dune;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserState;

import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
import static com.reason.lang.ParserScope.markScope;
import static com.reason.lang.ParserScopeEnum.*;

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

            state.previousTokenElementType = tokenType;
            tokenType = builder.getTokenType();
            if (tokenType == null) {
                break;
            }

            if (tokenType == DuneTypes.INSTANCE.LPAREN) {
                state.add(markScope(builder, sexpr, DuneTypes.INSTANCE.SEXPR, m_types.LPAREN));
            } else if (tokenType == DuneTypes.INSTANCE.RPAREN) {
                if (state.isInScopeExpression()) {
                    state.complete();
                    state.advance(builder);
                    state.popEnd();
                } else {
                    builder.error("Unbalanced parenthesis");
                }
            } else if (tokenType == DuneTypes.INSTANCE.VERSION) {
                wrapWith(DuneTypes.INSTANCE.VERSION, builder);
            } else if (tokenType == DuneTypes.INSTANCE.EXECUTABLE) {
                parseExecutable(builder, state);
            } else if (tokenType == DuneTypes.INSTANCE.LIBRARY) {
                parseLibrary(builder, state);
            } else if (tokenType == DuneTypes.INSTANCE.NAME) {
                parseName(builder, state);
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
    (executable (
        (name <name>)
        <optional-fields>
    ))
    */
    private void parseExecutable(PsiBuilder builder, ParserState state) {
        state.updateCurrentResolution(executable);
        state.setTokenElementType(DuneTypes.INSTANCE.EXECUTABLE);
        state.advance(builder);
        if (builder.getTokenType() == DuneTypes.INSTANCE.LPAREN) {
            state.complete();
        } else {
            builder.error("( expected");
        }
    }

    /*
    (library (
        (name <library-name>)
        <optional-fields>
    ))
    */
    private void parseLibrary(PsiBuilder builder, ParserState state) {
        state.updateCurrentResolution(library);
        state.setTokenElementType(DuneTypes.INSTANCE.LIBRARY);
        state.advance(builder);
        if (builder.getTokenType() == DuneTypes.INSTANCE.LPAREN) {
            state.complete();
        } else {
            builder.error("( expected");
        }
    }

    /* (name id) */
    private void parseName(PsiBuilder builder, ParserState state) {
        if (state.previousTokenElementType == DuneTypes.INSTANCE.LPAREN) {
            state.updateCurrentResolution(name);
            state.setTokenElementType(DuneTypes.INSTANCE.NAME);
            state.complete();
        }
    }

}
