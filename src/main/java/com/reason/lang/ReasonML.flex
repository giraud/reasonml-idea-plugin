package com.reason.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import static com.intellij.psi.TokenType.*;
%%

%{
  public ReasonMLLexer(MlTypes types) {
    this.types = types;
  }

  private MlTypes types;
  private int tokenStartIndex;
  private CharSequence quotedStringId;
  private int commentDepth;

  //Store the start index of a token
  private void tokenStart() {
    tokenStartIndex = zzStartRead;
  }

  //Set the start index of the token to the stored index
  private void tokenEnd() {
    zzStartRead = tokenStartIndex;
  }
%}

%public
%class ReasonMLLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

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

%state WAITING_VALUE
%state INITIAL
%state IN_STRING
%state IN_RML_COMMENT
%state IN_OCL_COMMENT

%%

<YYINITIAL>  {
      [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    {WHITE_SPACE} { return WHITE_SPACE; }

    "mutable"     { return types.MUTABLE;}
    "module"      { return types.MODULE;}
    "open"        { return types.OPEN; }
    "include"     { return types.INCLUDE; }
    "type"        { return types.TYPE; }
    "let"         { return types.LET; }
    "fun"         { return types.FUN; }
    "external"    { return types.EXTERNAL; }
    "unit"        { return types.UNIT; }
    "if"          { return types.IF; }
    "else"        { return types.ELSE; }
    "switch"      { return types.SWITCH; }
    "as"          { return types.AS; }
    "rec"         { return types.REC; }
    "ref"         { return types.REF; }
    "try"         { return types.TRY; }
    "raise"       { return types.RAISE; }
    "for"         { return types.FOR; }
    "in"          { return types.IN; }
    "exception"   { return types.EXCEPTION; }
    "when"        { return types.WHEN; }
    "and"         { return types.AND; }
    "while"       { return types.WHILE; }
    "as"          { return types.AS; }
    "asr"         { return types.ASR; }
    "class"       { return types.CLASS; }
    "constraint"  { return types.CONSTRAINT; }
    "downto"      { return types.DOWNTO; }
    "functor"     { return types.FUNCTOR; }
    "inherit"     { return types.INHERIT; }
    "initializer" { return types.INITIALIZER; }
    "land"        { return types.LAND; }
    "lor"         { return types.LOR; }
    "lsl"         { return types.LSL; }
    "lsr"         { return types.LSR; }
    "lxor"        { return types.LXOR; }
    "method"      { return types.METHOD; }
    "mod"         { return types.MOD; }
    "mutable"     { return types.MUTABLE; }
    "new"         { return types.NEW; }
    "nonrec"      { return types.NONREC; }
    "of"          { return types.OF; }
    "or"          { return types.OR; }
    "private"     { return types.PRIVATE; }
    "virtual"     { return types.VIRTUAL; }
    "val"         { return types.VAL; }
    "pub"         { return types.PUB; }

    // OCaml
    "of"         { return types.OF; }
    "to"         { return types.TO; }
    "do"         { return types.DO; }
    "done"       { return types.DONE; }
    "object"     { return types.OBJECT; }
    "begin"      { return types.BEGIN; }
    "end"        { return types.END; }
    "assert"     { return types.ASSERT; }
    "lazy"       { return types.LAZY; }
    "match"      { return types.MATCH; }
    "with"       { return types.WITH; }
    "then"       { return types.THEN; }
    "function"   { return types.FUNCTION; }
    "sig"        { return types.SIG; }
    "struct"     { return types.STRUCT; }
    //

    "option"    { return types.OPTION; }
    "None"      { return types.NONE; }
    "Some"      { return types.SOME; }

    "list"      { return types.LIST; }

    "false"     { return types.FALSE; }
    "true"      { return types.TRUE; }

    "'" ( {ESCAPE_CHAR} | . ) "'"    { return types.CHAR; }
    {LOWERCASE}{IDENTCHAR}*          { return types.LIDENT; }
    {UPPERCASE}{IDENTCHAR}*          { return types.UIDENT; }
    {INT_LITERAL}{LITERAL_MODIFIER}? { return types.INT; }
    ({FLOAT_LITERAL} | {HEXA_FLOAT_LITERAL}){LITERAL_MODIFIER}? { return types.FLOAT; }
    "'"{LOWERCASE}{IDENTCHAR}*       { return types.TYPE_ARGUMENT; }
    "`"{UPPERCASE}{IDENTCHAR}*       { return types.POLY_VARIANT; }

    "\"" { yybegin(IN_STRING); tokenStart(); }
    "/*" { yybegin(IN_RML_COMMENT); commentDepth = 1; tokenStart(); }
    "(*" { yybegin(IN_OCL_COMMENT); commentDepth = 1; tokenStart(); }

    "&&"  { return types.ANDAND; }
    "##"  { return types.SHARPSHARP; }
    "::"  { return types.SHORTCUT; }
    "=>"  { return types.ARROW; }
    "->"  { return types.RIGHT_ARROW; }
    "<-"  { return types.LEFT_ARROW; }
    "|>"  { return types.PIPE_FORWARD; }
    "</"  { return types.TAG_LT_SLASH; }
    "/>"  { return types.TAG_AUTO_CLOSE; }
    "[|"  { return types.LARRAY; }
    "|]"  { return types.RARRAY; }
    ">]"  { return types.GT_BRACKET; }
    ">}"  { return types.GT_BRACE; }
    "{<"  { return types.BRACE_LT; }
    "[<"  { return types.BRACKET_LT; }
    "[>"  { return types.BRACKET_GT; }
    "{|"  { return types.ML_STRING_OPEN; /*bs MultiLine*/ }
    "|}"  { return types.ML_STRING_CLOSE; /*bs MultiLine*/ }
    "{j|"  { return types.JS_STRING_OPEN; /*js interpolation*/ }
    "|j}"  { return types.JS_STRING_CLOSE; /*js interpolation*/ }

    "===" { return types.EQEQEQ; }
    "=="  { return types.EQEQ; }
    "="   { return types.EQ; }
    "!==" { return types.NOT_EQEQ; }
    "!="  { return types.NOT_EQ; }
    ":="  { return types.COLON_EQ; }
    ":>"  { return types.COLON_GT; }
    "<>"  { return types.DIFF; }
    ";;"  { return types.SEMISEMI; }

    ","   { return types.COMMA; }
    ":"   { return types.COLON; }
    ";"   { return types.SEMI; }
    "'"   { return types.QUOTE; }
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
    "_"   { return types.UNDERSCORE; }

    "<"  { return types.LT; }
    ">"  { return types.GT; }

    "\^"  { return types.CARRET; }
    "+."  { return types.PLUSDOT; }
    "-."  { return types.MINUSDOT; }
    "/."  { return types.SLASHDOT; }
    "*."  { return types.STARDOT; }
    "+"   { return types.PLUS; }
    "-"   { return types.MINUS; }
    "/"   { return types.SLASH; }
    "*"   { return types.STAR; }
    "%"   { return types.PERCENT; }
}

<IN_STRING> {
    "\"" { yybegin(INITIAL); tokenEnd(); return types.STRING; }
    "\\" { NEWLINE } ([ \t] *) { }
    "\\" [\\\'\"ntbr ] { }
    "\\" [0-9] [0-9] [0-9] { }
    "\\" "o" [0-3] [0-7] [0-7] { }
    "\\" "x" [0-9a-fA-F] [0-9a-fA-F] { }
    "\\" . { }
    { NEWLINE } { }
    . { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return types.STRING; }
}

<IN_RML_COMMENT> {
    "/*" { commentDepth += 1; }
    "*/" { commentDepth -= 1; if(commentDepth == 0) { yybegin(INITIAL); tokenEnd(); return types.COMMENT; } }
    . | {NEWLINE} { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return types.COMMENT; }
}

<IN_OCL_COMMENT> {
    "(*" { commentDepth += 1; }
    "*)" { commentDepth -= 1; if(commentDepth == 0) { yybegin(INITIAL); tokenEnd(); return types.COMMENT; } }
    . | {NEWLINE} { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return types.COMMENT; }
}

[^] { return BAD_CHARACTER; }
