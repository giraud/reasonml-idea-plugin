package com.reason.lang.odoc;

import static com.reason.lang.odoc.ODocMarkup.CODE_END;
import static com.reason.lang.odoc.ODocMarkup.CODE_START;

import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.containers.Stack;
import com.reason.Log;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class ODocConverter {
  private static final Log LOG = Log.create("odoc");

  private final ODocLexer m_lexer;
  private final StringBuilder m_builder = new StringBuilder();

  private boolean m_paragraphStarted = true;

  public ODocConverter(ODocLexer lexer) {
    m_lexer = lexer;
  }

  @NotNull
  public String convert(@NotNull String text) {
    m_lexer.reset(text, 0, text.length() - 1, ODocLexer.YYINITIAL);
    m_builder.append("<p>");

    Stack<String> scopes = new Stack<>();
    boolean inPre = false;
    boolean inLink = false;

    try {
      IElementType previousVisibleTokenType = null;
      IElementType tokenType = m_lexer.advance();
      while (tokenType != null) {
        // System.out.println(tokenType.toString() + " : " + m_lexer.yytext());

        if (!m_paragraphStarted
            && tokenType != ODocTypes.NEW_LINE
            && tokenType != TokenType.WHITE_SPACE) {
          m_paragraphStarted = true;
          m_builder.append("<p>");
        }

        //noinspection StatementWithEmptyBody
        if (tokenType == ODocTypes.OCL_START
            || tokenType == ODocTypes.OCL_END
            || tokenType == ODocTypes.RML_START
            || tokenType == ODocTypes.RML_END) {
          // skip
        } else if (tokenType == ODocTypes.CODE) {
          m_builder.append(CODE_START).append(extract(m_lexer.yytext(), 1)).append(CODE_END);
        } else if (tokenType == ODocTypes.BOLD) {
          m_builder.append("<b>").append(extract(m_lexer.yytext(), 2)).append("</b>");
        } else if (tokenType == ODocTypes.ITALIC) {
          m_builder.append("<i>").append(extract(m_lexer.yytext(), 2)).append("</i>");
        } else if (tokenType == ODocTypes.EMPHASIS) {
          m_builder.append("<em>").append(extract(m_lexer.yytext(), 2)).append("</em>");
        } else if (tokenType == ODocTypes.CROSS_REF) {
          String link = extract(m_lexer.yytext(), 2);
          m_builder.append("<a href=\")").append(link).append("\">").append(link).append("</a>");
        } else if (tokenType == ODocTypes.O_LIST) {
          m_builder.append("<ol>");
          scopes.add("</ol>");
        } else if (tokenType == ODocTypes.U_LIST) {
          m_builder.append("<ul>");
          scopes.add("</ul>");
        } else if (tokenType == ODocTypes.LIST_ITEM) {
          m_builder.append("<li>");
          scopes.add("</li>");
        } else if (tokenType == ODocTypes.PRE_START) {
          m_builder.append("<pre>");
          inPre = true;
        } else if (tokenType == ODocTypes.PRE_END) {
          inPre = false;
          m_builder.append("</pre>");
        } else if (tokenType == ODocTypes.SECTION) {
          String section = "h" + extract(m_lexer.yytext());
          m_builder.append("<").append(section).append(">");
          scopes.add("</" + section + ">");
        } else if (tokenType == ODocTypes.LINK) {
          m_builder.append("<a href=\"");
          inLink = true;
        } else if (tokenType == ODocTypes.RBRACE) {
          if (inLink) {
            inLink = false;
            m_builder.append("\">");
            scopes.add("</a>");
          } else if (!scopes.empty()) {
            m_builder.append(scopes.pop());
          }
        } else if (tokenType == ODocTypes.NEW_LINE) {
          if (inPre) {
            m_builder.append("\n");
          } else if (previousVisibleTokenType == ODocTypes.NEW_LINE && m_paragraphStarted) {
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
      LOG.error("Error during ODoc parsing", e);
    }

    if (m_paragraphStarted) {
      m_builder.append("</p>");
    }

    return m_builder.toString();
  }

  @NotNull
  private String extract(@NotNull CharSequence text) {
    return ((String) text).substring(1, text.length()).trim();
  }

  @NotNull
  private String extract(@NotNull CharSequence text, int start) {
    return ((String) text).substring(start, text.length() - 1).trim();
  }
}
