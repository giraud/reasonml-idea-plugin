package com.reason.ide.settings;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.reason.icons.Icons;
import com.reason.ide.highlight.DuneSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Map;

public class DuneColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comment", DuneSyntaxHighlighter.DUNE_COMMENT_),
            new AttributesDescriptor("Stanza", DuneSyntaxHighlighter.STANZAS_),
            new AttributesDescriptor("Fields", DuneSyntaxHighlighter.FIELDS_),
            new AttributesDescriptor("Parenthesis", DuneSyntaxHighlighter.PARENS_),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return Icons.DUNE_FILE;
    }

    @NotNull
    @Override
    public com.intellij.openapi.fileTypes.SyntaxHighlighter getHighlighter() {
        return new DuneSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "" +
                "this_is_an_atom_123'&^%!  ; this is a comment\n" +
                "\"another atom in an OCaml-string \\\"string in a string\\\" \\123\"\n" +
                "\n" +
                "; empty list follows below\n" +
                "()\n" +
                "\n" +
                "; a more complex example\n" +
                "(\n" +
                "  (\n" +
                "    list in a list  ; comment within a list\n" +
                "    (list in a list in a list)\n" +
                "    42 is the answer to all questions\n" +
                "    #; (this S-expression\n" +
                "         (has been commented out)\n" +
                "       )\n" +
                "    #| Block comments #| can be \"nested\" |# |#\n" +
                "  )\n" +
                ")";
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
        return "Dune";
    }
}
