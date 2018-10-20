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
            new AttributesDescriptor("Comment", DuneSyntaxHighlighter.COMMENT_),
            new AttributesDescriptor("Stanza", DuneSyntaxHighlighter.STANZAS_),
            new AttributesDescriptor("Fields", DuneSyntaxHighlighter.FIELDS_),
            new AttributesDescriptor("Options", DuneSyntaxHighlighter.OPTIONS_),
            new AttributesDescriptor("Atoms", DuneSyntaxHighlighter.ATOM_),
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
        return "; A single line comment\n" +
                "(jbuild_version 1)\n\n" +
                "#| Block comments #| can be \"nested\" |# |#\n\n" +

                "(executable\n" +
                " ((names (main))\n" +
                "  #; (this S-expression\n" +
                "         (has been commented out)\n" +
                "       )\n" +
                "  (libraries (hello_world))))\n\n" +

                "(install\n" +
                " ((section bin)\n" +
                "  (files ((main.exe as hello_world)))))\n\n" +

                "(rule\n" +
                " ((targets (config.full))\n" +
                "  (deps    (config_common.ml config))\n" +
                "  (action  (run ${OCAML} ${path:real_configure.ml}))))";
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
