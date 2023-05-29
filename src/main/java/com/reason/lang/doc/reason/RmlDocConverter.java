package com.reason.lang.doc.reason;

import com.intellij.lang.documentation.*;
import com.intellij.openapi.util.text.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.intellij.util.containers.Stack;
import com.reason.lang.doc.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class RmlDocConverter extends ORDocConverter {
    private static final Log LOG = Log.create("rdoc");
    private final RDocLexer myLexer = new RDocLexer();

    public @NotNull HtmlBuilder convert(@Nullable PsiElement element, @NotNull String text) {
        myLexer.reset(text, 0, text.length(), RDocLexer.YYINITIAL);

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
                if (currentBuilder instanceof ORDocSectionsBuilder && tokenType == RmlDocTypes.COMMENT_END) {
                    ORDocSectionsBuilder sectionsBuilder = (ORDocSectionsBuilder) builders.pop();
                    currentBuilder = builders.peek();

                    trimEndChildren(sectionsBuilder.myChildren);
                    sectionsBuilder.myChildren.add(HtmlChunk.raw("</p>"));
                    sectionsBuilder.addSection();

                    currentBuilder.myBuilder.append(sectionsBuilder.myBuilder.wrapWith(DocumentationMarkup.SECTIONS_TABLE));
                    currentBuilder.myChildren.clear();
                }

                //noinspection StatementWithEmptyBody
                if (tokenType == RmlDocTypes.COMMENT_START || tokenType == RmlDocTypes.COMMENT_END) {
                    // skip
                } else if (tokenType == RmlDocTypes.TAG) {
                    String yyValue = extract(1, 0, myLexer.yytext());
                    if (currentBuilder instanceof ORDocSectionsBuilder sectionsBuilder) {
                        if (sectionsBuilder.myTag.equals(yyValue)) {
                            trimEndChildren(sectionsBuilder.myChildren);
                            sectionsBuilder.myChildren.add(HtmlChunk.raw("</p><p>"));
                            tokenType = skipWhiteSpace(myLexer);
                            if ("param".equals(yyValue)) {
                                // add dash:: paramName - paramValue
                                sectionsBuilder.myChildren.add(HtmlChunk.text(myLexer.yytext() + " -"));
                            } else {
                                advanced = true;
                            }
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
                        if ("param".equals(yyValue)) {
                            // add dash:: paramName - paramValue
                            sectionsBuilder.myChildren.add(HtmlChunk.text(myLexer.yytext() + " -"));
                        } else {
                            advanced = true;
                        }

                        builders.add(sectionsBuilder);
                        currentBuilder = sectionsBuilder;
                    }
                } else if (tokenType == RmlDocTypes.NEW_LINE) {
                    if (!(currentBuilder instanceof ORDocSectionsBuilder)) {
                        // \n\n is a new line
                        tokenType = myLexer.advance();
                        boolean spaceAfterNewLine = tokenType == TokenType.WHITE_SPACE;
                        if (spaceAfterNewLine) {
                            tokenType = skipWhiteSpace(myLexer);
                        }

                        if (tokenType == RmlDocTypes.NEW_LINE) {
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
                        if (currentBuilder instanceof ORDocSectionsBuilder && ((ORDocSectionsBuilder) currentBuilder).myHeaderCell == null) {
                            // @param |>xxx<|  yyyy
                            ((ORDocSectionsBuilder) currentBuilder).myHeaderCell = DocumentationMarkup.SECTION_HEADER_CELL.child(HtmlChunk.text(yyValue));
                        } else {
                            currentBuilder.myChildren.add(isSpace ? SPACE_CHUNK : HtmlChunk.text(yyValue));
                        }
                    }
                }

                if (advanced) {
                    advanced = false;
                } else {
                    tokenType = myLexer.advance();
                }
            }
        } catch (IOException e) {
            LOG.error("Error during RDoc parsing", e);
        }

        currentBuilder.appendChildren(true);

        if (LOG.isTraceEnabled()) {
            LOG.trace("HTML: " + currentBuilder.myBuilder);
        }

        return currentBuilder.myBuilder;
    }
}
