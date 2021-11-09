package com.reason.lang.doc.ocaml;

import com.intellij.lang.documentation.*;
import com.intellij.openapi.util.text.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.util.containers.*;
import com.reason.lang.doc.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;

/**
 * see:
 * {@link  com.intellij.codeInsight.documentation.DocumentationManagerUtil}
 * {@link com.intellij.lang.documentation.DocumentationMarkup}
 * {@link com.intellij.codeInsight.documentation.DocumentationManagerProtocol}
 */
public class OclDocConverter extends ORDocConverter {
    private static final Log LOG = Log.create("odoc");
    private final ODocLexer myLexer = new ODocLexer();

    @NotNull
    public HtmlBuilder convert(@Nullable PsiElement element, @NotNull String text) {
        myLexer.reset(text, 0, text.length(), ODocLexer.YYINITIAL);

        Stack<ORDocHtmlBuilder> builders = new Stack<>();
        builders.add(new ORDocHtmlBuilder());
        ORDocHtmlBuilder currentBuilder = builders.peek();
        boolean advanced = false;

        try {
            IElementType tokenType = myLexer.advance();
            while (tokenType != null) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace(tokenType + " : " + myLexer.yytext());
                }

                // We have not produced the sections table, and it's the end of the comment
                if (currentBuilder instanceof ORDocSectionsBuilder && tokenType == OclDocTypes.COMMENT_END) {
                    ORDocSectionsBuilder sectionsBuilder = (ORDocSectionsBuilder) builders.pop();
                    currentBuilder = builders.peek();

                    trimEndChildren(sectionsBuilder.myChildren);
                    sectionsBuilder.myChildren.add(HtmlChunk.raw("</p>"));
                    sectionsBuilder.addSection();

                    currentBuilder.myBuilder.append(sectionsBuilder.myBuilder.wrapWith(DocumentationMarkup.SECTIONS_TABLE));
                    currentBuilder.myChildren.clear();
                }

                //noinspection StatementWithEmptyBody
                if (tokenType == OclDocTypes.COMMENT_START || tokenType == OclDocTypes.COMMENT_END) {
                    // skip
                } else if (tokenType == OclDocTypes.CODE) {
                    String yyValue = extract(1, 1, myLexer.yytext());
                    currentBuilder.addChild(HtmlChunk.raw(DocumentationMarkup.GRAYED_START + yyValue + DocumentationMarkup.GRAYED_END).wrapWith("code"));
                } else if (tokenType == OclDocTypes.BOLD) {
                    String yyValue = extract(2, 1, myLexer.yytext());
                    currentBuilder.addChild(HtmlChunk.text(yyValue).wrapWith("b"));
                } else if (tokenType == OclDocTypes.ITALIC) {
                    String yyValue = extract(2, 1, myLexer.yytext());
                    currentBuilder.addChild(HtmlChunk.text(yyValue).italic());
                } else if (tokenType == OclDocTypes.EMPHASIS) {
                    String yyValue = extract(2, 1, myLexer.yytext());
                    currentBuilder.addChild(HtmlChunk.text(yyValue).wrapWith("em"));
                } else if (tokenType == OclDocTypes.PRE) {
                    String yyValue = extractRaw(2, 2, myLexer.yytext());
                    currentBuilder.addChild(HtmlChunk.raw(yyValue).wrapWith("code").wrapWith("pre"));
                } else if (tokenType == OclDocTypes.O_LIST) {
                    currentBuilder.appendChildren(true);
                    TagHtmlBuilder listBuilder = new TagHtmlBuilder("ol");
                    builders.add(listBuilder);
                    currentBuilder = listBuilder;
                } else if (tokenType == OclDocTypes.U_LIST) {
                    currentBuilder.appendChildren(true);
                    TagHtmlBuilder listBuilder = new TagHtmlBuilder("ul");
                    builders.add(listBuilder);
                    currentBuilder = listBuilder;
                } else if (tokenType == OclDocTypes.LIST_ITEM_START) {
                    currentBuilder.appendChildren(false);
                    TagHtmlBuilder listBuilder = new TagHtmlBuilder("li");
                    builders.add(listBuilder);
                    currentBuilder = listBuilder;
                } else if (tokenType == OclDocTypes.SECTION) {
                    currentBuilder.appendChildren(true);
                    String tag = "h" + extract(1, 0, myLexer.yytext());
                    TagHtmlBuilder listBuilder = new TagHtmlBuilder(tag);
                    builders.add(listBuilder);
                    currentBuilder = listBuilder;
                } else if (tokenType == OclDocTypes.RBRACE) {
                    ORDocHtmlBuilder builder = builders.empty() ? null : builders.pop();
                    currentBuilder = builders.peek();
                    if (builder instanceof TagHtmlBuilder) {
                        TagHtmlBuilder tagBuilder = (TagHtmlBuilder) builder;
                        tagBuilder.appendChildren(false);
                        currentBuilder.addChild(tagBuilder.myBuilder.wrapWith(tagBuilder.myTag));
                        if (tagBuilder.myTag.startsWith("h")) {
                            // a title
                            currentBuilder.appendChildren(false);
                        }
                    }
                } else if (tokenType == OclDocTypes.LINK_START) {
                    // consume url
                    StringBuilder sbUrl = new StringBuilder();
                    tokenType = myLexer.advance();
                    while (tokenType != null && tokenType != OclDocTypes.RBRACE) {
                        sbUrl.append(myLexer.yytext());
                        tokenType = myLexer.advance();
                    }
                    if (tokenType == OclDocTypes.RBRACE) {
                        tokenType = myLexer.advance();
                        // consume text
                        StringBuilder sbText = new StringBuilder();
                        while (tokenType != null && tokenType != OclDocTypes.RBRACE) {
                            if (tokenType != OclDocTypes.NEW_LINE) {
                                sbText.append(myLexer.yytext());
                            }
                            tokenType = myLexer.advance();
                        }
                        if (tokenType == OclDocTypes.RBRACE) {
                            currentBuilder.addChild(HtmlChunk.link(sbUrl.toString(), sbText.toString()));
                        }
                    }
                } else if (tokenType == OclDocTypes.TAG) {
                    String yyValue = extract(1, 0, myLexer.yytext());
                    if (currentBuilder instanceof ORDocSectionsBuilder) {
                        ORDocSectionsBuilder sectionsBuilder = (ORDocSectionsBuilder) currentBuilder;
                        if (sectionsBuilder.myTag.equals(yyValue)) {
                            trimEndChildren(sectionsBuilder.myChildren);
                            sectionsBuilder.myChildren.add(HtmlChunk.raw("</p><p>"));
                            tokenType = skipWhiteSpace(myLexer);
                            advanced = true;
                        } else {
                            trimEndChildren(sectionsBuilder.myChildren);
                            sectionsBuilder.myChildren.add(HtmlChunk.raw("</p>"));
                            sectionsBuilder.addSection();

                            sectionsBuilder.addHeaderCell(yyValue);
                            tokenType = skipWhiteSpace(myLexer);
                            advanced = true;
                        }
                    } else {
                        currentBuilder.appendChildren(true);

                        ORDocSectionsBuilder sectionsBuilder = new ORDocSectionsBuilder();
                        sectionsBuilder.addHeaderCell(yyValue);
                        tokenType = skipWhiteSpace(myLexer);
                        advanced = true;

                        builders.add(sectionsBuilder);
                        currentBuilder = sectionsBuilder;
                    }
                } else if (tokenType == OclDocTypes.NEW_LINE) {
                    if (!(currentBuilder instanceof ORDocSectionsBuilder)) {
                        // \n\n is a new line
                        tokenType = myLexer.advance();
                        boolean spaceAfterNewLine = tokenType == TokenType.WHITE_SPACE;
                        if (spaceAfterNewLine) {
                            tokenType = skipWhiteSpace(myLexer);
                        }

                        if (tokenType == OclDocTypes.NEW_LINE) {
                            currentBuilder.appendChildren(true);
                        } else {
                            currentBuilder.addSpace();
                        }

                        advanced = true;
                    }
                } else {
                    String yyValue = myLexer.yytext().toString();

                    boolean isSpace = tokenType == TokenType.WHITE_SPACE;
                    if (!(isSpace && currentBuilder.myChildren.isEmpty())) {
                        currentBuilder.myChildren.add(isSpace ? SPACE_CHUNK : HtmlChunk.text(yyValue));
                    }
                }

                if (advanced) {
                    advanced = false;
                } else {
                    tokenType = myLexer.advance();
                }
            }
        } catch (IOException e) {
            LOG.error("Error during ODoc parsing", e);
        }

        currentBuilder.appendChildren(true);

        if (LOG.isTraceEnabled()) {
            LOG.trace("HTML: " + currentBuilder.myBuilder);
        }

        return currentBuilder.myBuilder;
    }

    static class TagHtmlBuilder extends ORDocHtmlBuilder {
        final String myTag;

        TagHtmlBuilder(String tag) {
            myTag = tag;
        }
    }
}
