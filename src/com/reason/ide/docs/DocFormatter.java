package com.reason.ide.docs;

import com.intellij.lang.documentation.*;
import com.intellij.openapi.fileTypes.*;
import com.intellij.openapi.util.text.*;
import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.doc.*;
import com.reason.lang.doc.ocaml.*;
import com.reason.lang.doc.reason.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

class DocFormatter {
    private static final Log LOG = Log.create("doc.formatter");

    private DocFormatter() {
    }

    static @NotNull String format(@NotNull PsiFile file, @NotNull PsiElement element, @Nullable ORLanguageProperties lang, @NotNull String text) {
        if (file instanceof FileBase) {
            FileBase source = (FileBase) file;

            // Definition

            HtmlBuilder definitionBuilder = new HtmlBuilder();

            String path = source.getModuleName();
            if (element instanceof PsiQualifiedPathElement) {
                path = Joiner.join(".", ((PsiQualifiedPathElement) element).getPath());
            }
            definitionBuilder.append(HtmlChunk.text(path).bold());

            if (element instanceof PsiNamedElement) {
                String className = element.getClass().getSimpleName().substring(3).replace("Impl", "").toLowerCase();
                String name = ((PsiNamedElement) element).getName();
                if (name != null) {
                    definitionBuilder.append(HtmlChunk.raw("<p><i>"));
                    definitionBuilder.append(HtmlChunk.text(className + " " + name));

                    if (element instanceof PsiSignatureElement) {
                        PsiSignature signature = ((PsiSignatureElement) element).getSignature();
                        if (signature != null) {
                            definitionBuilder.append(HtmlChunk.text(" : ")).append(HtmlChunk.text(signature.asText(lang)).wrapWith("code"));
                        }
                    }
                }
                definitionBuilder.append(HtmlChunk.raw("</i></p>"));
            }

            // Content

            HtmlBuilder contentBuilder = new HtmlBuilder();
            FileType fileType = source.getFileType();
            boolean isReasonLikeComment = FileHelper.isReason(fileType) || FileHelper.isRescript(fileType);
            ORDocConverter converter = isReasonLikeComment ? new RmlDocConverter() : new OclDocConverter();
            contentBuilder.append(converter.convert(element, text));

            // final render

            HtmlBuilder builder = new HtmlBuilder();
            builder.append(definitionBuilder.wrapWith(DocumentationMarkup.DEFINITION_ELEMENT));
            builder.append(contentBuilder.wrapWith(DocumentationMarkup.CONTENT_ELEMENT));

            if (LOG.isDebugEnabled()) {
                LOG.debug(builder.toString());
            }

            return builder.toString();
        }
        return text;
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
