package com.reason.ide.docs;

import com.intellij.lang.documentation.*;
import com.intellij.openapi.editor.richcopy.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.util.text.*;
import com.intellij.psi.*;
import com.reason.*;
import com.reason.ide.files.*;
import com.reason.ide.highlight.*;
import com.reason.lang.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.doc.*;
import com.reason.lang.doc.ocaml.*;
import com.reason.lang.doc.reason.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import static com.intellij.lang.documentation.DocumentationMarkup.*;

/**
 * See {@link com.intellij.codeInsight.documentation.DocumentationManagerProtocol} for link protocol.
 * and also {@link com.intellij.codeInsight.documentation.DocumentationManagerUtil}
 */
class DocFormatter {
    private static final Log LOG = Log.create("doc.formatter");

    private DocFormatter() {
    }

    static @NotNull String format(@NotNull PsiFile file, @NotNull PsiElement element, @Nullable ORLanguageProperties lang, @NotNull String text) {
        HtmlBuilder builder = new HtmlBuilder();
        builder.appendRaw("<html><body>");

        if (file instanceof FileBase source) {
            FileType fileType = source.getFileType();
            boolean isReasonLikeComment = FileHelper.isReason(fileType) || FileHelper.isRescript(fileType);

            // Definition

            builder.append(HtmlChunk.raw(DocumentationMarkup.DEFINITION_START));
            if (element instanceof FileBase fileElement) {
                formatTopModule(fileElement, builder);
            } else if (element instanceof PsiNamedElement namedElement) {
                String path = source.getModuleName();
                if (element instanceof RPsiQualifiedPathElement qualifiedPathElement) {
                    path = Joiner.join(".", qualifiedPathElement.getPath());
                }

                float highlightingSaturation = DocumentationSettings.getHighlightingSaturation(false);

                builder.append(TOP_ELEMENT.children(
                        HtmlChunk.tag("icon").attr("src", "AllIcons.Nodes.Package"),
                        HtmlChunk.nbsp(),
                        HtmlChunk.raw(Platform.getRelativePathToModule(file)).wrapWith("code")
                ));

                String name = namedElement.getName();
                if (name != null) {
                    String className = element.getClass().getSimpleName().substring(4).replace("Impl", "").toLowerCase();
                    //builder.append(HtmlChunk.text(className + " " + name));
                    StringBuilder styleBuilder = new StringBuilder();
                    HtmlSyntaxInfoUtil.appendStyledSpan(styleBuilder, ORSyntaxHighlighter.KEYWORD_, className, highlightingSaturation);

                    builder.append(BOTTOM_ELEMENT.children(
                            HtmlChunk.raw(styleBuilder.toString()),
                            HtmlChunk.nbsp(),
                            HtmlChunk.raw(name)
                    ));

                    /*
                    if (element instanceof RPsiSignatureElement) {
                        RPsiSignature signature = ((RPsiSignatureElement) element).getSignature();
                        if (signature != null) {
                            builder.append(HtmlChunk.text(" : ")).append(HtmlChunk.text(signature.asText(lang)).wrapWith("code"));
                        }
                    }
                     */
                }
            }
            builder.append(HtmlChunk.raw(DocumentationMarkup.DEFINITION_END));

            // Content

            ORDocConverter converter = isReasonLikeComment ? new RmlDocConverter() : new OclDocConverter();
            builder.append(HtmlChunk.raw(DocumentationMarkup.CONTENT_START));
            builder.append(converter.convert(element, text));
            builder.append(HtmlChunk.raw(DocumentationMarkup.CONTENT_END));

            if (LOG.isDebugEnabled()) {
                LOG.debug(builder.toString());
            }

        } else {
            builder.append(HtmlChunk.text(text));
        }

        builder.appendRaw("</body></html>");
        return builder.toString();
    }

    private static void formatTopModule(FileBase fileElement, HtmlBuilder builder) {
        float highlightingSaturation = DocumentationSettings.getHighlightingSaturation(false);

        builder.append(TOP_ELEMENT.children(
                HtmlChunk.tag("icon").attr("src", "AllIcons.Nodes.Package"),
                HtmlChunk.nbsp(),
                HtmlChunk.raw(Platform.getRelativePathToModule(fileElement)).wrapWith("code")
        ));

        StringBuilder styleBuilder = new StringBuilder();
        HtmlSyntaxInfoUtil.appendStyledSpan(styleBuilder, ORSyntaxHighlighter.KEYWORD_, "module", highlightingSaturation);

        builder.append(BOTTOM_ELEMENT.children(
                HtmlChunk.raw(styleBuilder.toString()),
                HtmlChunk.nbsp(),
                HtmlChunk.raw(fileElement.getModuleName())
        ));
    }


    static @NotNull String escapeCodeForHtml(@Nullable PsiElement code) {
        if (code == null) {
            return "";
        }

        return escapeCodeForHtml(code.getText());
    }

    @Nullable
    public static String escapeCodeForHtml(@Nullable String code) {
        return code == null ? null : code.
                replaceAll("<", "&lt;").
                replaceAll(">", "&gt;");
    }
}
