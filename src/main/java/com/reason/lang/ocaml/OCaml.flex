package com.reason.lang.ocaml;

import com.intellij.psi.tree.IElementType;
import com.reason.lang.core.type.ORLangTypes;
import com.intellij.lexer.FlexLexer;

import static com.intellij.psi.TokenType.*;

@SuppressWarnings("ALL")
%%

%{
    public OCamlLexer() {
        this.types = OclTypes.INSTANCE;
    }

    private ORLangTypes types;
    private int tokenStartIndex;
    private CharSequence quotedStringId;
    private int commentDepth;
    private boolean inCommentString = false;

    // Store the start index of a token
    private void tokenStart() {
        tokenStartIndex = zzStartRead;
    }

    // Set the start index of the token to the stored index
    private void tokenEnd() {
        zzStartRead = tokenStartIndex;
    }
%}

%public
%class OCamlLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType

EOL=\n|\r|\r\n
WHITE_SPACE_CHAR=[\ \t\f]|{EOL}
WHITE_SPACE={WHITE_SPACE_CHAR}+

NEWLINE=("\r"* "\n")
LOWERCASE=[a-z_]
UPPERCASE=[A-Z]
IDENTCHAR=[A-Za-z_0-9']

DECIMAL=[0-9]
DECIMAL_SEP=[0-9_]
HEXA=[0-9A-Fa-f]
HEXA_SEP=[0-9A-Fa-f_]
OCTAL=[0-7]
OCTAL_SEP=[0-7_]

DECIMAL_LITERAL={DECIMAL} {DECIMAL_SEP}*
HEXA_LITERAL="0" [xX] {HEXA} {HEXA_SEP}*
OCT_LITERAL="0" [oO] {OCTAL} {OCTAL_SEP}*
BIN_LITERAL="0" [bB] [0-1] [0-1_]*
INT_LITERAL= {DECIMAL_LITERAL} | {HEXA_LITERAL} | {OCT_LITERAL} | {BIN_LITERAL}
FLOAT_LITERAL={DECIMAL} {DECIMAL_SEP}* ("." {DECIMAL_SEP}* )? ([eE] [+-]? {DECIMAL} {DECIMAL_SEP}* )?
HEXA_FLOAT_LITERAL="0" [xX] {HEXA} {HEXA_SEP}* ("." {HEXA_SEP}* )? ([pP] [+-]? {DECIMAL} {DECIMAL_SEP}* )?
LITERAL_MODIFIER=[G-Zg-z]

ESCAPE_BACKSLASH="\\\\"
ESCAPE_SINGLE_QUOTE="\\'"
ESCAPE_LF="\\n"
ESCAPE_TAB="\\t"
ESCAPE_BACKSPACE="\\b"
ESCAPE_CR="\\r"
ESCAPE_QUOTE="\\\""
ESCAPE_DECIMAL="\\" {DECIMAL} {DECIMAL} {DECIMAL}
ESCAPE_HEXA="\\x" {HEXA} {HEXA}
ESCAPE_OCTAL="\\o" [0-3] {OCTAL} {OCTAL}
ESCAPE_CHAR= {ESCAPE_BACKSLASH} | {ESCAPE_SINGLE_QUOTE} | {ESCAPE_LF} | {ESCAPE_TAB} | {ESCAPE_BACKSPACE } | { ESCAPE_CR } | { ESCAPE_QUOTE } | {ESCAPE_DECIMAL} | {ESCAPE_HEXA} | {ESCAPE_OCTAL}

%state INITIAL
%state IN_STRING
%state IN_OCAML_ML_COMMENT

%%

<YYINITIAL>  {
      [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    {WHITE_SPACE} { return WHITE_SPACE; }

    // OCaml reserved keywords
    // https://v2.ocaml.org/manual/lex.html#sss:keywords
    "assert"      { return types.ASSERT; }
    "and"         { return types.AND; }
    "asr"         { return types.ASR; }
    "as"          { return types.AS; }
    "begin"       { return types.BEGIN; }
    "class"       { return types.CLASS; }
    "constraint"  { return types.CONSTRAINT; }
    "downto"      { return types.DOWNTO; }
    "done"        { return types.DONE; }
    "do"          { return types.DO; }
    "exception"   { return types.EXCEPTION; }
    "external"    { return types.EXTERNAL; }
    "else"        { return types.ELSE; }
    "end"         { return types.END; }
    "function"    { return types.FUNCTION; }
    "functor"     { return types.FUNCTOR; }
    "false"       { return types.BOOL_VALUE; }
    "fun"         { return types.FUN; }
    "for"         { return types.FOR; }
    "initializer" { return types.INITIALIZER; }
    "include"     { return types.INCLUDE; }
    "inherit"     { return types.INHERIT; }
    "if"          { return types.IF; }
    "in"          { return types.IN; }
    "land"        { return types.LAND; }
    "lazy"        { return types.LAZY; }
    "lxor"        { return types.LXOR; }
    "let"         { return types.LET; }
    "lor"         { return types.LOR; }
    "lsl"         { return types.LSL; }
    "lsr"         { return types.LSR; }
    "mutable"     { return types.MUTABLE; }
    "method"      { return types.METHOD; }
    "module"      { return types.MODULE;}
    "match"       { return types.MATCH; }
    "mod"         { return types.MOD; }
    "nonrec"      { return types.NONREC; }
    "new"         { return types.NEW; }
    "object"      { return types.OBJECT; }
    "open"        { return types.OPEN; }
    "of"          { return types.OF; }
    "or"          { return types.OR; }
    "private"     { return types.PRIVATE; }
    "rec"         { return types.REC; }
    "struct"      { return types.STRUCT; }
    "sig"         { return types.SIG; }
    "then"        { return types.THEN; }
    "true"        { return types.BOOL_VALUE; }
    "type"        { return types.TYPE; }
    "try"         { return types.TRY; }
    "to"          { return types.TO; }
    "virtual"     { return types.VIRTUAL; }
    "val"         { return types.VAL; }
    "when"        { return types.WHEN; }

    // ??
    "pub"         { return types.PUB; }
    "pri"         { return types.PRI; }
    "while"       { return types.WHILE; }
    "with"        { return types.WITH; }
    "raw"         { return types.RAW; }
    "unit"        { return types.UNIT; }
    "ref"         { return types.REF; }
    "raise"       { return types.RAISE; }
    "option"      { return types.OPTION; }
    "None"        { return types.NONE; }
    "Some"        { return types.SOME; }

    "_"   { return types.UNDERSCORE; }

    "'" ( {ESCAPE_CHAR} | . ) "'"    { return types.CHAR_VALUE; }
    {LOWERCASE}{IDENTCHAR}*          { return types.LIDENT; }
    {UPPERCASE}{IDENTCHAR}*          { return types.UIDENT; }
    {INT_LITERAL}{LITERAL_MODIFIER}? { return types.INT_VALUE; }
    ({FLOAT_LITERAL} | {HEXA_FLOAT_LITERAL}){LITERAL_MODIFIER}? { return types.FLOAT_VALUE; }
    "'"{LOWERCASE}{IDENTCHAR}*       { return types.TYPE_ARGUMENT; }
    "`"{UPPERCASE}{IDENTCHAR}*       { return types.POLY_VARIANT; }
    "`"{LOWERCASE}{IDENTCHAR}*       { return types.POLY_VARIANT; }

    "\"" { yybegin(IN_STRING); tokenStart(); }
    "(*" { yybegin(IN_OCAML_ML_COMMENT); inCommentString = false; commentDepth = 1; tokenStart(); }

    "#if"     { return types.DIRECTIVE_IF; }
    "#else"   { return types.DIRECTIVE_ELSE; }
    "#elif"   { return types.DIRECTIVE_ELIF; }
    "#endif"  { return types.DIRECTIVE_ENDIF; }
    "#end"    { return types.DIRECTIVE_END; }

    "##"  { return types.SHARPSHARP; }
    "@@"  { return types.ARROBASE_2; }
    "@@@" { return types.ARROBASE_3; }

    "::"  { return types.SHORTCUT; }
    "=>"  { return types.ARROW; }
    "->"  { return types.RIGHT_ARROW; }
    "<-"  { return types.LEFT_ARROW; }
    "|>"  { return types.PIPE_FORWARD; }
    "</"  { return types.TAG_LT_SLASH; }
    "/>"  { return types.TAG_AUTO_CLOSE; }
    "[|"  { return types.LARRAY; }
    "|]"  { return types.RARRAY; }

    "===" { return types.EQEQEQ; }
    "=="  { return types.EQEQ; }
    "="   { return types.EQ; }
    "!==" { return types.NOT_EQEQ; }
    "!="  { return types.NOT_EQ; }
    ":="  { return types.COLON_EQ; }
    ":>"  { return types.COLON_GT; }
    "<="  { return types.LT_OR_EQUAL; }
    ">="  { return types.GT_OR_EQUAL; }
    ";;"  { return types.SEMISEMI; }
    "||"  { return types.L_OR; }
    "&&"  { return types.L_AND; }
    "<>"  { return types.OP_STRUCT_DIFF; }

    ","   { return types.COMMA; }
    ":"   { return types.COLON; }
    ";"   { return types.SEMI; }
    "'"   { return types.SINGLE_QUOTE; }
    "..." { return types.DOTDOTDOT; }
    ".."  { return types.DOTDOT; }
    "."   { return types.DOT; }
    "|"   { return types.PIPE; }
    "("   { return types.LPAREN; }
    ")"   { return types.RPAREN; }
    "{"   { return types.LBRACE; }
    "}"   { return types.RBRACE; }
    "["   { return types.LBRACKET; }
    "]"   { return types.RBRACKET; }
    "@"   { return types.ARROBASE; }
    "#"   { return types.SHARP; }
    "?"   { return types.QUESTION_MARK; }
    "!"   { return types.EXCLAMATION_MARK; }
    "$"   { return types.DOLLAR; }
    "`"   { return types.BACKTICK; }
    "~"   { return types.TILDE; }
    "&"   { return types.AMPERSAND; }

    "<"  { return types.LT; }
    ">"  { return types.GT; }

    "\^"  { return types.CARRET; }
    "+."  { return types.PLUSDOT; }
    "-."  { return types.MINUSDOT; }
    "/."  { return types.SLASHDOT; }
    "*."  { return types.STARDOT; }
    "++"  { return types.STRING_CONCAT; }
    "+"   { return types.PLUS; }
    "-"   { return types.MINUS; }
    "/"   { return types.SLASH; }
    "*"   { return types.STAR; }
    "%"   { return types.PERCENT; }
}

<IN_STRING> {
    "\"" { yybegin(INITIAL); tokenEnd(); return types.STRING_VALUE; }
    "\\" { NEWLINE } ([ \t] *) { }
    "\\" [\\\'\"ntbr ] { }
    "\\" [0-9] [0-9] [0-9] { }
    "\\" "o" [0-3] [0-7] [0-7] { }
    "\\" "x" [0-9a-fA-F] [0-9a-fA-F] { }
    "\\" . { }
    { NEWLINE } { }
    . { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return types.STRING_VALUE; }
}

<IN_OCAML_ML_COMMENT> {
    "(*" { if (!inCommentString) commentDepth += 1; }
    "*)" { if (!inCommentString) { commentDepth -= 1; if(commentDepth == 0) { yybegin(INITIAL); tokenEnd(); return types.MULTI_COMMENT; } } }
    "\"" { inCommentString = !inCommentString; }
     . | {NEWLINE} { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return types.MULTI_COMMENT; }
}

[^] { return BAD_CHARACTER; }
