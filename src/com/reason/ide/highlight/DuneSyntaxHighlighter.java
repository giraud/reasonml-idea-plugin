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
            DuneTypes.INSTANCE.VERSION, DuneTypes.INSTANCE.EXECUTABLE, DuneTypes.INSTANCE.EXECUTABLES, DuneTypes.INSTANCE.LIBRARY, DuneTypes.INSTANCE.RULE,
            DuneTypes.INSTANCE.ALIAS, DuneTypes.INSTANCE.MENHIR, DuneTypes.INSTANCE.INSTALL, DuneTypes.INSTANCE.INCLUDE, DuneTypes.INSTANCE.OCAML_LEX,
            DuneTypes.INSTANCE.OCAML_YACC, DuneTypes.INSTANCE.COPY_FILES, DuneTypes.INSTANCE.COPY_FILES_SHARP
    );

    private static final Set<IElementType> FIELDS_TYPES = of(
            DuneTypes.INSTANCE.NAME, DuneTypes.INSTANCE.PUBLIC_NAME, DuneTypes.INSTANCE.KIND, DuneTypes.INSTANCE.MODES, DuneTypes.INSTANCE.FLAGS, DuneTypes.INSTANCE.C_NAMES,
            DuneTypes.INSTANCE.C_FLAGS, DuneTypes.INSTANCE.MODULES, DuneTypes.INSTANCE.WRAPPED, DuneTypes.INSTANCE.OPTIONAL, DuneTypes.INSTANCE.LIBRARIES,
            DuneTypes.INSTANCE.CXX_NAMES, DuneTypes.INSTANCE.CXX_FLAGS, DuneTypes.INSTANCE.NO_DYNLINK, DuneTypes.INSTANCE.PREPROCESS, DuneTypes.INSTANCE.JS_OF_OCAML,
            DuneTypes.INSTANCE.VIRTUAL_DEPS, DuneTypes.INSTANCE.OCAMLC_FLAGS, DuneTypes.INSTANCE.LIBRARY_FLAGS, DuneTypes.INSTANCE.OCAMLOPT_FLAGS,
            DuneTypes.INSTANCE.C_LIBRARY_FLAGS, DuneTypes.INSTANCE.INSTALL_C_HEADERS, DuneTypes.INSTANCE.PREPROCESSOR_DEPS,
            DuneTypes.INSTANCE.PPX_RUNTIME_LIBRARIES, DuneTypes.INSTANCE.SELF_BUILD_STUBS_ARCHIVE, DuneTypes.INSTANCE.LINK_FLAGS,
            DuneTypes.INSTANCE.ALLOW_OVERLAPPING_DEPENDENCIES, DuneTypes.INSTANCE.MODULES_WITHOUT_IMPLEMENTATION, DuneTypes.INSTANCE.NAMES,
            DuneTypes.INSTANCE.PUBLIC_NAMES, DuneTypes.INSTANCE.SHARED_OBJECT, DuneTypes.INSTANCE.DEPS, DuneTypes.INSTANCE.MODE, DuneTypes.INSTANCE.LOCKS,
            DuneTypes.INSTANCE.ACTION, DuneTypes.INSTANCE.TARGETS
    );

    private static final Set<IElementType> OPTIONS_TYPES = of(
            DuneTypes.INSTANCE.EXE, DuneTypes.INSTANCE.BEST, DuneTypes.INSTANCE.BYTE, DuneTypes.INSTANCE.OBJECT, DuneTypes.INSTANCE.NATIVE, DuneTypes.INSTANCE.PROMOTE,
            DuneTypes.INSTANCE.STANDARD
    );

    public static final TextAttributesKey PARENS_ = createTextAttributesKey("DUNE_PAREN", DefaultLanguageHighlighterColors.PARENTHESES);
    public static final TextAttributesKey COMMENT_ = createTextAttributesKey("DUNE_COMMENT", DefaultLanguageHighlighterColors.BLOCK_COMMENT);
    public static final TextAttributesKey STANZAS_ = createTextAttributesKey("DUNE_STANZA", DefaultLanguageHighlighterColors.FUNCTION_DECLARATION);
    public static final TextAttributesKey FIELDS_ = createTextAttributesKey("DUNE_FIELD", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey OPTIONS_ = createTextAttributesKey("DUNE_OPTION", DefaultLanguageHighlighterColors.LABEL);
    public static final TextAttributesKey STRING_ = createTextAttributesKey("DUNE_STRING", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey ATOM_ = createTextAttributesKey("DUNE_ATOM", DefaultLanguageHighlighterColors.CONSTANT);
    private static final TextAttributesKey BAD_CHAR_ = createTextAttributesKey("DUNE_BAD_CHARACTER", HighlighterColors.BAD_CHARACTER);

    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT_};
    private static final TextAttributesKey[] PAREN_KEYS = new TextAttributesKey[]{PARENS_};
    private static final TextAttributesKey[] STANZAS_KEYS = new TextAttributesKey[]{STANZAS_};
    private static final TextAttributesKey[] FIELDS_KEYS = new TextAttributesKey[]{FIELDS_};
    private static final TextAttributesKey[] OPTIONS_KEYS = new TextAttributesKey[]{OPTIONS_};
    private static final TextAttributesKey[] STRING_KEYS = new TextAttributesKey[]{STRING_};
    private static final TextAttributesKey[] ATOM_KEYS = new TextAttributesKey[]{ATOM_};
    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHAR_};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new FlexAdapter(new DuneLexer(DuneTypes.INSTANCE));
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(DuneTypes.INSTANCE.LPAREN) || tokenType.equals(DuneTypes.INSTANCE.RPAREN)) {
            return PAREN_KEYS;
        } else if (tokenType.equals(DuneTypes.INSTANCE.COMMENT)) {
            return COMMENT_KEYS;
        } else if (STANZAS_TYPES.contains(tokenType)) {
            return STANZAS_KEYS;
        } else if (FIELDS_TYPES.contains(tokenType)) {
            return FIELDS_KEYS;
        } else if (OPTIONS_TYPES.contains(tokenType)) {
            return OPTIONS_KEYS;
        } else if (tokenType.equals(DuneTypes.INSTANCE.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(DuneTypes.INSTANCE.ATOM)) {
            return ATOM_KEYS;
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