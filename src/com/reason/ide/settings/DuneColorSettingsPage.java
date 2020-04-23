package com.reason.ide.settings;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import icons.ORIcons;
import com.reason.ide.highlight.DuneSyntaxHighlighter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class DuneColorSettingsPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comment", DuneSyntaxHighlighter.COMMENT_),
            new AttributesDescriptor("Stanza", DuneSyntaxHighlighter.STANZAS_),
            new AttributesDescriptor("Fields", DuneSyntaxHighlighter.FIELDS_),
            new AttributesDescriptor("Options", DuneSyntaxHighlighter.OPTIONS_),
            new AttributesDescriptor("Atoms", DuneSyntaxHighlighter.ATOM_),
            new AttributesDescriptor("Variables", DuneSyntaxHighlighter.VAR_),
            new AttributesDescriptor("Parenthesis", DuneSyntaxHighlighter.PARENS_),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return ORIcons.DUNE_FILE;
    }

    @NotNull
    @Override
    public com.intellij.openapi.fileTypes.SyntaxHighlighter getHighlighter() {
        return new DuneSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "; A single line comment\n\n" +
                "#| Block comments #| can be \"nested\" |# |#\n\n" +

                "(<csStanza>executable</csStanza>\n" +
                "  (<csField>names</csField> (main))\n" +
                "  #; (this S-expression\n" +
                "         (has been commented out)\n" +
                "       )\n" +
                "  (<csField>libraries</csField> (hello_world)))\n\n" +

                "(<csStanza>install</csStanza>\n" +
                "  (<csField>section</csField> bin)\n" +
                "  (<csField>files</csField> ((main.exe as hello_world))))\n\n" +

                "(<csStanza>rule</csStanza>\n" +
                "  (<csField>targets</csField> (config.full)\n" +
                "  (<csField>deps</csField>    (config_common.ml config))\n" +
                "  (<csField>action</csField>  (run <csVar>%{OCAML}</csVar> <csVar>%{path:real_configure.ml}</csVar>)))";
    }

    private static final Map<String, TextAttributesKey> additionalTags = new HashMap<>();

    static {
        additionalTags.put("csField", DuneSyntaxHighlighter.FIELDS_);
        additionalTags.put("csStanza", DuneSyntaxHighlighter.STANZAS_);
        additionalTags.put("csVar", DuneSyntaxHighlighter.VAR_);
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
        return "Dune";
    }
}
