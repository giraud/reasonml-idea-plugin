package com.reason.ide.settings;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.reason.icons.Icons;
import com.reason.ide.highlight.MlSyntaxHighlighter;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ReasonColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Annotation", MlSyntaxHighlighter.ANNOTATION_),
            new AttributesDescriptor("Comment", MlSyntaxHighlighter.RML_COMMENT_),
            new AttributesDescriptor("Code lens", MlSyntaxHighlighter.CODE_LENS_),
            new AttributesDescriptor("Module name", MlSyntaxHighlighter.MODULE_NAME_),
            new AttributesDescriptor("Option", MlSyntaxHighlighter.OPTION_),
            new AttributesDescriptor("Markup tag", MlSyntaxHighlighter.MARKUP_TAG_),
            new AttributesDescriptor("Markup attribute", MlSyntaxHighlighter.MARKUP_ATTRIBUTE_),
            new AttributesDescriptor("Keyword", MlSyntaxHighlighter.KEYWORD_),
            new AttributesDescriptor("Operation", MlSyntaxHighlighter.OPERATION_SIGN_),
            new AttributesDescriptor("String", MlSyntaxHighlighter.STRING_),
            new AttributesDescriptor("Number", MlSyntaxHighlighter.NUMBER_),
            new AttributesDescriptor("Semicolon", MlSyntaxHighlighter.SEMICOLON_),
            new AttributesDescriptor("Braces", MlSyntaxHighlighter.BRACES_),
            new AttributesDescriptor("Brackets", MlSyntaxHighlighter.BRACKETS_),
            new AttributesDescriptor("Parenthesis", MlSyntaxHighlighter.PARENS_),
            new AttributesDescriptor("Type argument", MlSyntaxHighlighter.TYPE_ARGUMENT_),
            new AttributesDescriptor("Variant name", MlSyntaxHighlighter.VARIANT_NAME_),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.RML_FILE;
    }

    @NotNull
    @Override
    public com.intellij.openapi.fileTypes.SyntaxHighlighter getHighlighter() {
        return new MlSyntaxHighlighter(RmlTypes.INSTANCE);
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "" +
                "/* This is a comment */\n\n" +

                "module <csModuleName>ModuleName</csModuleName> = {\n" +
                "  type t = { key: int };\n" +
                "  type tree 'a =\n" +
                "    | <csVariantName>Node</csVariantName> (tree 'a) (tree 'a)\n" +
                "    | <csVariantName>Leaf</csVariantName>;\n\n" +

                "  [<csAnnotation>@bs.deriving</csAnnotation> {accessors: accessors}]\n" +
                "  type t = [`Up | `Down | `Left | `Right];\n\n" +

                "  let add = (x y) => x + y;  <csCodeLens>int -> int</csCodeLens>\n" +
                "  let myList = [ 1.0, 2.0, 3. ];\n" +
                "  let array = [| 1, 2, 3 |];\n" +
                "  let choice x = switch (myOption)\n" +
                "    | None => \"nok\"\n" +
                "    | Some(value) => \"ok\";\n" +
                "  let constant = \"My constant\";  <csCodeLens>string</csCodeLens>\n" +
                "  let numericConstant = 123;  <csCodeLens>int</csCodeLens>\n" +
                "};\n\n" +

                "React.createElement <csMarkupTag><div</csMarkupTag> <csMarkupAttribute>prop</csMarkupAttribute>=value<csMarkupTag>/></csMarkupTag> <csMarkupTag><Button></csMarkupTag> (ReasonReact.stringToElement(\"ok\") <csMarkupTag></Button></csMarkupTag>;";
    }

    private static final Map<String, TextAttributesKey> additionalTags = new HashMap<>();

    static {
        additionalTags.put("csAnnotation", MlSyntaxHighlighter.ANNOTATION_);
        additionalTags.put("csCodeLens", MlSyntaxHighlighter.CODE_LENS_);
        additionalTags.put("csMarkupAttribute", MlSyntaxHighlighter.MARKUP_ATTRIBUTE_);
        additionalTags.put("csMarkupTag", MlSyntaxHighlighter.MARKUP_TAG_);
        additionalTags.put("csModuleName", MlSyntaxHighlighter.MODULE_NAME_);
        additionalTags.put("csVariantName", MlSyntaxHighlighter.VARIANT_NAME_);
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return additionalTags;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Reason (OCaml)";
    }
}
