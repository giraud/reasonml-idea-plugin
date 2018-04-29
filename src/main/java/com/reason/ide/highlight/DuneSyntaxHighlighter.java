package com.reason.ide.highlight;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.tree.IElementType;
import com.reason.lang.dune.DuneLexer;
import com.reason.lang.dune.DuneTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;
import static com.intellij.psi.TokenType.BAD_CHARACTER;

public class DuneSyntaxHighlighter extends SyntaxHighlighterBase {

    private static final Set<IElementType> STANZAS_TYPES = of(
            DuneTypes.VERSION, DuneTypes.EXECUTABLE, DuneTypes.LIBRARY
    );

    private static final Set<IElementType> FIELDS_TYPES = of(
            DuneTypes.NAME, DuneTypes.PUBLIC_NAME
    );

    private static final TextAttributesKey STANZAS_ = createTextAttributesKey("DUNE_STANZAS", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    private static final TextAttributesKey FIELDS_ = createTextAttributesKey("DUNE_FIELDS", DefaultLanguageHighlighterColors.KEYWORD);
    private static final TextAttributesKey BAD_CHAR_ = createTextAttributesKey("DUNE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] STANZAS_KEYS = new TextAttributesKey[]{STANZAS_};
    private static final TextAttributesKey[] FIELDS_KEYS = new TextAttributesKey[]{FIELDS_};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHAR_};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new FlexAdapter(new DuneLexer());
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (STANZAS_TYPES.contains(tokenType)) {
            return STANZAS_KEYS;
        } else if (FIELDS_TYPES.contains(tokenType)) {
            return FIELDS_KEYS;
        } else if (BAD_CHARACTER.equals(tokenType)) {
            return BAD_CHAR_KEYS;
        }

        return EMPTY_KEYS;
    }

    private static Set<IElementType> of(IElementType... types) {
        Set<IElementType> result = new HashSet<>();
        Collections.addAll(result, types);
        return result;
    }
}