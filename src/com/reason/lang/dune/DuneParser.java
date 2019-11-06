package com.reason.lang.dune;

import com.intellij.lang.PsiBuilder;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.CommonParser;
import com.reason.lang.ParserState;
import org.jetbrains.annotations.NotNull;

import static com.intellij.lang.parser.GeneratedParserUtilBase.current_position_;
import static com.intellij.lang.parser.GeneratedParserUtilBase.empty_element_parsed_guard_;
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

            if (tokenType == m_types.LPAREN) {
                state.add(markScope(builder, sexpr, m_types.SEXPR, m_types.LPAREN));
            } else if (tokenType == m_types.RPAREN) {
                if (state.isInScopeExpression()) {
                    state.complete();
                    state.advance();
                    state.popEnd();
                } else {
                    builder.error("Unbalanced parenthesis");
                }
            } else if (tokenType == m_types.VERSION) {
                state.wrapWith(m_types.VERSION);
            } else if (tokenType == m_types.EXECUTABLE) {
                parseExecutable(builder, state);
            } else if (tokenType == m_types.LIBRARY) {
                parseLibrary(builder, state);
            } else if (tokenType == m_types.NAME) {
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
    private void parseExecutable(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        state.updateCurrentResolution(executable);
        state.setTokenElementType(m_types.EXECUTABLE);
        state.advance();
        if (builder.getTokenType() == m_types.LPAREN) {
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
    private void parseLibrary(@NotNull PsiBuilder builder, @NotNull ParserState state) {
        state.updateCurrentResolution(library);
        state.setTokenElementType(m_types.LIBRARY);
        state.advance();
        if (builder.getTokenType() == m_types.LPAREN) {
            state.complete();
        } else {
            builder.error("( expected");
        }
    }

    /* (name id) */
    private void parseName(PsiBuilder builder, @NotNull ParserState state) {
        if (state.previousElementType1 == m_types.LPAREN) {
            state.updateCurrentResolution(name);
            state.setTokenElementType(m_types.NAME);
            state.complete();
        }
    }

}
