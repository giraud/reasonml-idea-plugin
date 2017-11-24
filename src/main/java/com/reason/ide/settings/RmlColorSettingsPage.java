package com.reason.ide.settings;

import java.io.*;
import java.util.*;
import javax.swing.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.reason.icons.Icons;
import com.reason.ide.highlight.RmlSyntaxHighlighter;

import static com.intellij.openapi.util.io.FileUtil.loadTextAndClose;

public class RmlColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[] {
        new AttributesDescriptor("Comment", RmlSyntaxHighlighter.RML_COMMENT_),
        new AttributesDescriptor("Module name", RmlSyntaxHighlighter.MODULE_NAME_),
        new AttributesDescriptor("Option", RmlSyntaxHighlighter.OPTION_),
        new AttributesDescriptor("Tag", RmlSyntaxHighlighter.TAG_),
        new AttributesDescriptor("Keyword", RmlSyntaxHighlighter.KEYWORD_),
        new AttributesDescriptor("Operation", RmlSyntaxHighlighter.OPERATION_SIGN_),
        new AttributesDescriptor("String", RmlSyntaxHighlighter.STRING_),
        new AttributesDescriptor("Number", RmlSyntaxHighlighter.NUMBER_),
        new AttributesDescriptor("Semicolon", RmlSyntaxHighlighter.SEMICOLON_),
        new AttributesDescriptor("Braces", RmlSyntaxHighlighter.BRACES_),
        new AttributesDescriptor("Brackets", RmlSyntaxHighlighter.BRACKETS_),
        new AttributesDescriptor("Parenthesis", RmlSyntaxHighlighter.PARENS_),
        new AttributesDescriptor("Type argument", RmlSyntaxHighlighter.TYPE_ARGUMENT_),
        new AttributesDescriptor("Polymorphic variants", RmlSyntaxHighlighter.POLY_VARIANT_),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.RML_FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new RmlSyntaxHighlighter();
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

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
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
