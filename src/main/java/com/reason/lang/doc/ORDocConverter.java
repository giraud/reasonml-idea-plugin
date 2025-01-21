package com.reason.lang.doc;

import com.intellij.codeInsight.documentation.*;
import com.intellij.lang.documentation.*;
import com.intellij.lang.java.*;
import com.intellij.lexer.*;
import com.intellij.openapi.util.text.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

/**
 * see:
 * {@link com.intellij.codeInsight.documentation.DocumentationManagerUtil}
 * {@link com.intellij.lang.documentation.DocumentationMarkup}
 * {@link com.intellij.codeInsight.documentation.DocumentationManagerProtocol}
 *
 * {@link com.intellij.lang.java.JavaDocumentationProvider}
 */
public abstract class ORDocConverter {
    protected static final HtmlChunk SPACE_CHUNK = HtmlChunk.text(" ");

    public abstract @NotNull HtmlBuilder convert(@Nullable PsiElement element, @NotNull String text);

    protected IElementType skipWhiteSpace(@NotNull FlexLexer lexer) throws IOException {
        IElementType elementType = lexer.advance();
        while (elementType != null && elementType == TokenType.WHITE_SPACE) {
            elementType = lexer.advance();
        }
        return elementType;
    }

    @NotNull
    protected String extractRaw(int startOffset, int endOffset, @NotNull CharSequence text) {
        return ((String) text).substring(startOffset, text.length() - endOffset);
    }

    @NotNull
    protected String extract(int startOffset, int endOffset, @NotNull CharSequence text) {
        return ((String) text).substring(startOffset, text.length() - endOffset).trim();
    }

    protected static @NotNull List<HtmlChunk> trimEndChildren(@NotNull List<HtmlChunk> children) {
        if (!children.isEmpty()) {
            int lastIndex = children.size() - 1;
            HtmlChunk lastChunk = children.get(lastIndex);
            if (lastChunk == SPACE_CHUNK) {
                children.remove(lastIndex);
                return trimEndChildren(children);
            }
        }
        return children;
    }

    public static class ORDocHtmlBuilder {
        public final HtmlBuilder myBuilder = new HtmlBuilder();
        public final List<HtmlChunk> myChildren = new ArrayList<>();

        public void appendChildren(boolean wrap) {
            if (!myChildren.isEmpty()) {
                trimEndChildren(myChildren);
                if (!myChildren.isEmpty()) {
                    if (wrap) {
                        myBuilder.append(HtmlChunk.p().children(myChildren));
                    } else {
                        for (HtmlChunk chunk : myChildren) {
                            myBuilder.append(chunk);
                        }
                    }
                    myChildren.clear();
                }
            }
        }

        public void addSpace() {
            if (!myChildren.isEmpty()) {
                int lastIndex = myChildren.size() - 1;
                HtmlChunk lastChunk = myChildren.get(lastIndex);
                if (lastChunk != SPACE_CHUNK) {
                    myChildren.add(SPACE_CHUNK);
                }
            }
        }

        public void addChild(HtmlChunk.Element element) {
            myChildren.add(element);
        }
    }

    public static class ORDocSectionsBuilder extends ORDocHtmlBuilder {
        public String myTag = "";
        public HtmlChunk.Element myHeaderCell = null;

        public void addHeaderCell(@NotNull String tag) {
            myHeaderCell = DocumentationMarkup.SECTION_HEADER_CELL.child(HtmlChunk.text(StringUtil.toTitleCase(tag) + ":").wrapWith("p"));
            myChildren.add(HtmlChunk.raw("<p>"));
            myTag = tag;
        }

        public void addSection() {
            HtmlChunk contentCell = DocumentationMarkup.SECTION_CONTENT_CELL.children(trimEndChildren(myChildren));
            myBuilder.append(HtmlChunk.tag("tr").children(myHeaderCell, contentCell));
            myHeaderCell = null;
            myTag = "";
            myChildren.clear();
        }
    }

}
