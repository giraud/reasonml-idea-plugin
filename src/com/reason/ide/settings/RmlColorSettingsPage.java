package com.reason.ide.settings;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.reason.icons.ReasonMLIcons;
import com.reason.ide.highlight.RmlSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class RmlColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comment", RmlSyntaxHighlighter.COMMENT_),
            new AttributesDescriptor("Module name", RmlSyntaxHighlighter.MODULE_NAME_),
            new AttributesDescriptor("Option", RmlSyntaxHighlighter.OPTION_),
            new AttributesDescriptor("Tag", RmlSyntaxHighlighter.TAG_),
            new AttributesDescriptor("Keyword", RmlSyntaxHighlighter.KEYWORD_),
            new AttributesDescriptor("Operation", RmlSyntaxHighlighter.OPERATION_SIGN_),
            new AttributesDescriptor("String", RmlSyntaxHighlighter.STRING_),
            new AttributesDescriptor("Semicolon", RmlSyntaxHighlighter.SEMICOLON_),
            new AttributesDescriptor("Braces", RmlSyntaxHighlighter.BRACES_),
            new AttributesDescriptor("Brackets", RmlSyntaxHighlighter.BRACKETS_),
            new AttributesDescriptor("Parenthesis", RmlSyntaxHighlighter.PARENS_),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return ReasonMLIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new RmlSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "/* This is a comment */\n\n" +
                "module ModuleName = {\n" +
                "  type t = { key: int };\n" +
                "  let add x y => x + y;\n" +
                "  let myList = [ 1, 2, 3 ];\n" +
                "  let array = [| 1, 2, 3 |];\n" +
                "  let choice x = switch (myOption)\n" +
                "      | None => \"nok\"\n" +
                "      | Some value => \"ok\"\n" +
                "  let constant = \"My constant\";\n" +
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
