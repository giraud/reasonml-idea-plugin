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
            DuneTypes.VERSION, DuneTypes.EXECUTABLE, DuneTypes.EXECUTABLES, DuneTypes.LIBRARY, DuneTypes.RULE,
            DuneTypes.ALIAS, DuneTypes.MENHIR, DuneTypes.INSTALL, DuneTypes.INCLUDE, DuneTypes.OCAML_LEX,
            DuneTypes.OCAML_YACC, DuneTypes.COPY_FILES, DuneTypes.COPY_FILES_SHARP
    );

    private static final Set<IElementType> FIELDS_TYPES = of(
            DuneTypes.NAME, DuneTypes.PUBLIC_NAME, DuneTypes.KIND, DuneTypes.MODES, DuneTypes.FLAGS, DuneTypes.C_NAMES,
            DuneTypes.C_FLAGS, DuneTypes.MODULES, DuneTypes.WRAPPED, DuneTypes.OPTIONAL, DuneTypes.LIBRARIES,
            DuneTypes.CXX_NAMES, DuneTypes.CXX_FLAGS, DuneTypes.NO_DYNLINK, DuneTypes.PREPROCESS, DuneTypes.JS_OF_OCAML,
            DuneTypes.VIRTUAL_DEPS, DuneTypes.OCAMLC_FLAGS, DuneTypes.LIBRARY_FLAGS, DuneTypes.OCAMLOPT_FLAGS,
            DuneTypes.C_LIBRARY_FLAGS, DuneTypes.INSTALL_C_HEADERS, DuneTypes.PREPROCESSOR_DEPS,
            DuneTypes.PPX_RUNTIME_LIBRARIES, DuneTypes.SELF_BUILD_STUBS_ARCHIVE, DuneTypes.LINK_FLAGS,
            DuneTypes.ALLOW_OVERLAPPING_DEPENDENCIES, DuneTypes.MODULES_WITHOUT_IMPLEMENTATION, DuneTypes.NAMES,
            DuneTypes.PUBLIC_NAMES, DuneTypes.SHARED_OBJECT, DuneTypes.DEPS, DuneTypes.MODE, DuneTypes.LOCKS,
            DuneTypes.ACTION, DuneTypes.TARGETS
    );

    private static final Set<IElementType> OPTIONS_TYPES = of(
            DuneTypes.EXE, DuneTypes.BEST, DuneTypes.BYTE, DuneTypes.OBJECT, DuneTypes.NATIVE, DuneTypes.PROMOTE,
            DuneTypes.STANDARD
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
        return new FlexAdapter(new DuneLexer());
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(DuneTypes.LPAREN) || tokenType.equals(DuneTypes.RPAREN)) {
            return PAREN_KEYS;
        } else if (tokenType.equals(DuneTypes.COMMENT)) {
            return COMMENT_KEYS;
        } else if (STANZAS_TYPES.contains(tokenType)) {
            return STANZAS_KEYS;
        } else if (FIELDS_TYPES.contains(tokenType)) {
            return FIELDS_KEYS;
        } else if (OPTIONS_TYPES.contains(tokenType)) {
            return OPTIONS_KEYS;
        } else if (tokenType.equals(DuneTypes.STRING)) {
            return STRING_KEYS;
        } else if (tokenType.equals(DuneTypes.ATOM)) {
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