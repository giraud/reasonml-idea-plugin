package com.reason.lang.doc.ocaml;

import com.intellij.lang.documentation.*;
import com.intellij.openapi.util.text.*;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.*;
import com.intellij.psi.impl.*;
import com.intellij.psi.tree.*;
import com.intellij.util.containers.*;
import com.reason.lang.doc.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;

/**
 * Syntax: {@link https://ocaml.org/manual/5.3/ocamldoc.html#s%3Aocamldoc-comments}
 */
public class OclDocConverter extends ORDocConverter {
    private static final Log LOG = Log.create("odoc");
    private final ODocLexer myLexer = new ODocLexer();

    @NotNull
    public HtmlBuilder convert(@Nullable PsiElement element, @NotNull String text) {
        HtmlBuilder html = new HtmlBuilder();

        try {
            Stack<String> endTags = new Stack<>();
            boolean advanced = false;
            boolean inSection = false;

            myLexer.reset(text, 0, text.length(), ODocLexer.YYINITIAL);
            IElementType tokenType = myLexer.advance();

            while (tokenType != null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace(tokenType + " : " + myLexer.yytext());
                }

                if (tokenType == OclDocTypes.CODE) {
                    String yyValue = extract(1, 1, myLexer.yytext());
                    html.append(HtmlChunk.raw(DocumentationMarkup.GRAYED_START + yyValue + DocumentationMarkup.GRAYED_END).wrapWith("code"));
                } else if (tokenType == OclDocTypes.BOLD) {
                    String yyValue = extract(2, 1, myLexer.yytext());
                    html.append(HtmlChunk.tag("b").addText(yyValue));
                } else if (tokenType == OclDocTypes.ITALIC) {
                    String yyValue = extract(2, 1, myLexer.yytext());
                    html.append(HtmlChunk.tag("i").addText(yyValue));
                } else if (tokenType == OclDocTypes.EMPHASIS) {
                    String yyValue = extract(2, 1, myLexer.yytext());
                    html.append(HtmlChunk.tag("em").addText(yyValue));
                } else if (tokenType == OclDocTypes.PRE) {
                    String yyValue = extractRaw(2, 2, myLexer.yytext());
                    html.append(HtmlChunk.raw(yyValue).wrapWith("code").wrapWith("pre"));
                } else if (tokenType == OclDocTypes.O_LIST) {
                    html.append(HtmlChunk.raw("<ol>"));
                    endTags.push("</ol>");
                } else if (tokenType == OclDocTypes.U_LIST) {
                    html.append(HtmlChunk.raw("<ul>"));
                    endTags.push("</ul>");
                } else if (tokenType == OclDocTypes.LIST_ITEM_START) {
                    html.append(HtmlChunk.raw("<li>"));
                    endTags.push("</li>");
                } else if (tokenType == OclDocTypes.SECTION) {
                    String header = "h" + extract(1, 0, myLexer.yytext());
                    html.append(HtmlChunk.raw("<" + header + ">"));
                    endTags.push("</" + header + ">");
                } else if (tokenType == OclDocTypes.RBRACE) {
                    if (!endTags.isEmpty()) {
                        html.append(HtmlChunk.raw(endTags.pop()));
                    }
                } else if (tokenType == OclDocTypes.LINK_START) {
                    html.append(HtmlChunk.raw("<a href=\""));
                    endTags.push("</a>");
                    endTags.push("\">");
                } else if (tokenType == OclDocTypes.TAG) {
                    String tag = extract(1, 0, myLexer.yytext());
                    if (inSection) {
                        html.appendRaw(endTags.pop()).appendRaw(endTags.pop()).appendRaw(endTags.pop());
                    } else {
                        inSection = true;
                        html.appendRaw(DocumentationMarkup.SECTIONS_START);
                        endTags.push(DocumentationMarkup.SECTIONS_END);
                    }
                    html.appendRaw(DocumentationMarkup.SECTION_HEADER_START);
                    endTags.push("</tr>");
                    endTags.push(DocumentationMarkup.SECTION_END);
                    html.append(HtmlChunk.text(StringUtil.toTitleCase(tag)))
                            .appendRaw(":</p>")
                            .appendRaw(DocumentationMarkup.SECTION_SEPARATOR)
                            .appendRaw("<p>");
                    endTags.push("</p>");
                    tokenType = skipWhiteSpace(myLexer);
                    advanced = true;
                } else if (tokenType == OclDocTypes.NEW_LINE) {
                    // \n\n is a new line
                    tokenType = myLexer.advance();
                    advanced = true;

                    boolean spaceAfterNewLine = tokenType == TokenType.WHITE_SPACE;
                    if (spaceAfterNewLine) {
                        tokenType = skipWhiteSpace(myLexer);
                    }

                    if (tokenType == OclDocTypes.NEW_LINE) {
                        html.append(HtmlChunk.p());
                    } else {
                        html.append(SPACE_CHUNK);
                    }
                } else if (tokenType != OclDocTypes.COMMENT_START && tokenType != OclDocTypes.COMMENT_END) {
                    String yyValue = myLexer.yytext().toString();
                    html.append(HtmlChunk.text(yyValue));
                }

                if (advanced) {
                    advanced = false;
                } else {
                    tokenType = myLexer.advance();
                }
            }

            while (!endTags.isEmpty()) {
                html.append(HtmlChunk.raw(endTags.pop()));
            }
        } catch (IOException e) {
            LOG.error("Error during ODoc parsing", e);
        }

        if (LOG.isTraceEnabled()) {
            LOG.trace("HTML: " + html);
        }

        return html;
    }
}
