package com.reason.ide.settings;

import com.intellij.openapi.options.colors.*;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.reason.ide.ReasonMLIcons;
import com.reason.ide.ReasonMLSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class ReasonMLColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comment", ReasonMLSyntaxHighlighter.COMMENT_),
            new AttributesDescriptor("Tag", ReasonMLSyntaxHighlighter.TAG),
            new AttributesDescriptor("UIdentifier", ReasonMLSyntaxHighlighter.UIDENTIFIER),
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
               "module ModuleName = {\n" +
               "};\n\n" +
                "React.createElement <div prop=value/> <Button>ok</Button>\n";
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
