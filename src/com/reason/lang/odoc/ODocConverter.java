package com.reason.lang.odoc;

import com.intellij.lang.documentation.DocumentationMarkup;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ODocConverter {
    private final ODocLexer m_lexer;
    private final StringBuilder m_builder = new StringBuilder();

    boolean m_paragraphStarted = true;

    public ODocConverter(ODocLexer lexer) {
        m_lexer = lexer;
    }

    public String convert(@NotNull String text) {
        m_lexer.reset(text, 0, text.length() - 1, ODocLexer.YYINITIAL);
        m_builder.append("<p>");

        try {
            IElementType previousVisibleTokenType = null;
            IElementType tokenType = m_lexer.advance();
            while (true) {
                if (tokenType == null) {
                    break;
                }

                System.out.println(tokenType.toString() + " : " + m_lexer.yytext());

                if (!m_paragraphStarted && tokenType != ODocTypes.NEW_LINE && tokenType != TokenType.WHITE_SPACE) {
                    m_paragraphStarted = true;
                    m_builder.append("<p>");
                }

                if (tokenType == ODocTypes.START || tokenType == ODocTypes.END) {
                    // skip
                } else if (tokenType == ODocTypes.CODE) {
                    CharSequence yytext = m_lexer.yytext();
                    m_builder.append("<span style=\"font-family: monospace; font-style: italic\">").append(yytext.subSequence(1, yytext.length() - 1)).append("</span>");
                } else if (tokenType == ODocTypes.NEW_LINE) {
                    if (previousVisibleTokenType == ODocTypes.NEW_LINE && m_paragraphStarted) {
                        m_paragraphStarted = false;
                        m_builder.append("</p>");
                    }
                } else {
                    m_builder.append(tokenType == TokenType.WHITE_SPACE ? " " : m_lexer.yytext());
                }

                if (tokenType != TokenType.WHITE_SPACE) {
                    previousVisibleTokenType = tokenType;
                }

                tokenType = m_lexer.advance();
            }
        } catch (IOException e) {
            System.out.println(e);
        }

        if (m_paragraphStarted) {
            m_builder.append("</p>");
        }

        return m_builder.toString();
    }

}
