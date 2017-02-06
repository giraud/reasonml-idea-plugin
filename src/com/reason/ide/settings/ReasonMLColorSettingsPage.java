package com.reason.ide.settings;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.reason.icons.ReasonMLIcons;
import com.reason.ide.highlight.ReasonMLSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class ReasonMLColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comment", ReasonMLSyntaxHighlighter.COMMENT_),
            new AttributesDescriptor("Module name", ReasonMLSyntaxHighlighter.MODULE_NAME_),
            new AttributesDescriptor("Option", ReasonMLSyntaxHighlighter.OPTION_),
            new AttributesDescriptor("Tag", ReasonMLSyntaxHighlighter.TAG_),
            new AttributesDescriptor("Keyword", ReasonMLSyntaxHighlighter.KEYWORD_),
            new AttributesDescriptor("Operation", ReasonMLSyntaxHighlighter.OPERATION_SIGN_),
            new AttributesDescriptor("String", ReasonMLSyntaxHighlighter.STRING_),
            new AttributesDescriptor("Semicolon", ReasonMLSyntaxHighlighter.SEMICOLON_),
            new AttributesDescriptor("Braces", ReasonMLSyntaxHighlighter.BRACES_),
            new AttributesDescriptor("Brackets", ReasonMLSyntaxHighlighter.BRACKETS_),
            new AttributesDescriptor("Parenthesis", ReasonMLSyntaxHighlighter.PARENS_),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return ReasonMLIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new ReasonMLSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "/* This is a comment */\n\n" +
                "type t = { key: int };\n" +
                "let add x y => x + y;\n" +
                "let constant = \"My constant\";\n" +
                "module ModuleName = {\n" +
                "};\n\n" +
                "React.createElement <div prop=value/> <Button> (ReactElement.toString \"ok\") </Button>\n";
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
        return "ReasonML";
    }
}
