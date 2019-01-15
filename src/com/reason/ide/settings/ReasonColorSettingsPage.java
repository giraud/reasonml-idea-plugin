package com.reason.ide.settings;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.reason.Icons;
import com.reason.ide.highlight.ORSyntaxHighlighter;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class ReasonColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Annotation", ORSyntaxHighlighter.ANNOTATION_),
            new AttributesDescriptor("Comment", ORSyntaxHighlighter.RML_COMMENT_),
            new AttributesDescriptor("Code lens", ORSyntaxHighlighter.CODE_LENS_),
            new AttributesDescriptor("Module name", ORSyntaxHighlighter.MODULE_NAME_),
            new AttributesDescriptor("Option", ORSyntaxHighlighter.OPTION_),
            new AttributesDescriptor("Markup tag", ORSyntaxHighlighter.MARKUP_TAG_),
            new AttributesDescriptor("Markup attribute", ORSyntaxHighlighter.MARKUP_ATTRIBUTE_),
            new AttributesDescriptor("Keyword", ORSyntaxHighlighter.KEYWORD_),
            new AttributesDescriptor("Operation", ORSyntaxHighlighter.OPERATION_SIGN_),
            new AttributesDescriptor("String", ORSyntaxHighlighter.STRING_),
            new AttributesDescriptor("Number", ORSyntaxHighlighter.NUMBER_),
            new AttributesDescriptor("Semicolon", ORSyntaxHighlighter.SEMICOLON_),
            new AttributesDescriptor("Braces", ORSyntaxHighlighter.BRACES_),
            new AttributesDescriptor("Brackets", ORSyntaxHighlighter.BRACKETS_),
            new AttributesDescriptor("Parenthesis", ORSyntaxHighlighter.PARENS_),
            new AttributesDescriptor("Type argument", ORSyntaxHighlighter.TYPE_ARGUMENT_),
            new AttributesDescriptor("Variant name", ORSyntaxHighlighter.VARIANT_NAME_),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.RML_FILE;
    }

    @NotNull
    @Override
    public com.intellij.openapi.fileTypes.SyntaxHighlighter getHighlighter() {
        return new ORSyntaxHighlighter(RmlTypes.INSTANCE);
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
        additionalTags.put("csAnnotation", ORSyntaxHighlighter.ANNOTATION_);
        additionalTags.put("csCodeLens", ORSyntaxHighlighter.CODE_LENS_);
        additionalTags.put("csMarkupAttribute", ORSyntaxHighlighter.MARKUP_ATTRIBUTE_);
        additionalTags.put("csMarkupTag", ORSyntaxHighlighter.MARKUP_TAG_);
        additionalTags.put("csModuleName", ORSyntaxHighlighter.MODULE_NAME_);
        additionalTags.put("csVariantName", ORSyntaxHighlighter.VARIANT_NAME_);
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
