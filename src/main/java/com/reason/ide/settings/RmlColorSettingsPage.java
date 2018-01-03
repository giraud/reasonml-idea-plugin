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
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static com.intellij.openapi.util.io.FileUtil.loadTextAndClose;

public class RmlColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Annotation", MlSyntaxHighlighter.ANNOTATION_),
            new AttributesDescriptor("Comment", MlSyntaxHighlighter.RML_COMMENT_),
            new AttributesDescriptor("Module name", MlSyntaxHighlighter.MODULE_NAME_),
            new AttributesDescriptor("Option", MlSyntaxHighlighter.OPTION_),
            new AttributesDescriptor("Markup tag", MlSyntaxHighlighter.MARKUP_TAG_),
            new AttributesDescriptor("Keyword", MlSyntaxHighlighter.KEYWORD_),
            new AttributesDescriptor("Operation", MlSyntaxHighlighter.OPERATION_SIGN_),
            new AttributesDescriptor("String", MlSyntaxHighlighter.STRING_),
            new AttributesDescriptor("Number", MlSyntaxHighlighter.NUMBER_),
            new AttributesDescriptor("Semicolon", MlSyntaxHighlighter.SEMICOLON_),
            new AttributesDescriptor("Braces", MlSyntaxHighlighter.BRACES_),
            new AttributesDescriptor("Brackets", MlSyntaxHighlighter.BRACKETS_),
            new AttributesDescriptor("Parenthesis", MlSyntaxHighlighter.PARENS_),
            new AttributesDescriptor("Type argument", MlSyntaxHighlighter.TYPE_ARGUMENT_),
            new AttributesDescriptor("Polymorphic variants", MlSyntaxHighlighter.POLY_VARIANT_),
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
        InputStream colorsStream = getClass().getResourceAsStream("/ColorsExemple.re");

        String exempleSourceCode;
        try {
            exempleSourceCode = loadTextAndClose(colorsStream);
        } catch (IOException e) {
            exempleSourceCode = "Error when loading the demo text";
        }

        return exempleSourceCode;
    }

    static Map<String, TextAttributesKey> additionalTags = new HashMap<>();

    static {
        additionalTags.put("ANNOTATION_NAME", MlSyntaxHighlighter.ANNOTATION_);
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
        return "Reason";
    }
}
