package com.reason.lang;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import static com.reason.lang.RmlTypes.*;
import static com.intellij.psi.TokenType.*;
%%

%{
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

    "mutable"    { return MUTABLE;}
    "module"     { return MODULE;}
    "open"       { return OPEN; }
    "include"    { return INCLUDE; }
    "type"       { return TYPE; }
    "let"        { return LET; }
    "fun"        { return FUN; }
    "external"   { return EXTERNAL; }
    "unit"       { return UNIT; }
    "if"         { return IF; }
    "else"       { return ELSE; }
    "switch"     { return SWITCH; }
    "as"         { return AS; }
    "rec"        { return REC; }
    "ref"        { return REF; }
    "try"        { return TRY; }
    "raise"      { return RAISE; }
    "for"        { return FOR; }
    "in"         { return IN; }
    "exception"  { return EXCEPTION; }
    "when"       { return WHEN; }
    "and"        { return AND; }
    "while"      { return WHILE; }

    // OCaml
    "of"         { return OF; }
    "to"         { return TO; }
    "do"         { return DO; }
    "done"       { return DONE; }
    "object"     { return OBJECT; }
    "begin"      { return BEGIN; }
    "end"        { return END; }
    "assert"     { return ASSERT; }
    "lazy"       { return LAZY; }
    "match"      { return MATCH; }
    "with"       { return WITH; }
    "then"       { return THEN; }
    "function"   { return FUNCTION; }
    "sig"        { return SIG; }
    "struct"     { return STRUCT; }
    "val"        { return VAL; }
    //

    "option"    { return OPTION; }
    "None"      { return NONE; }
    "Some"      { return SOME; }

    "list"      { return LIST; }

    "false"     { return FALSE; }
    "true"      { return TRUE; }

    "'" ( {ESCAPE_CHAR} | . ) "'"    { return CHAR; }
    {LOWERCASE}{IDENTCHAR}*          { return LIDENT; }
    {UPPERCASE}{IDENTCHAR}*          { return UIDENT; }
    {INT_LITERAL}{LITERAL_MODIFIER}? { return INT; }
    ({FLOAT_LITERAL} | {HEXA_FLOAT_LITERAL}){LITERAL_MODIFIER}? { return FLOAT; }
    "'"{LOWERCASE}{IDENTCHAR}*       { return TYPE_ARGUMENT; }
    "`"{UPPERCASE}{IDENTCHAR}*       { return POLY_VARIANT; }

    "\"" { yybegin(IN_STRING); tokenStart(); }
    "/*" { yybegin(IN_RML_COMMENT); commentDepth = 1; tokenStart(); }
    "(*" { yybegin(IN_OCL_COMMENT); commentDepth = 1; tokenStart(); }

    "&&"   { return ANDAND; }
    "::"   { return SHORTCUT; }
    "=>"   { return ARROW; }
    "->"   { return SIMPLE_ARROW; }
    "|>"   { return PIPE_FORWARD; }
    "/>"   { return TAG_AUTO_CLOSE; }
    "[|"   { return LARRAY; }
    "|]"   { return RARRAY; }

    "===" { return EQEQEQ; }
    "=="  { return EQEQ; }
    "="   { return EQ; }
    "!==" { return NOT_EQEQ; }
    "!="  { return NOT_EQ; }
    "<>"  { return DIFF; }
    ","   { return COMMA; }
    ":"   { return COLON; }
    ";"   { return SEMI; }
    "'"   { return QUOTE; }
    "..." { return DOTDOTDOT; }
    "."   { return DOT; }
    "|"   { return PIPE; }
    "("   { return LPAREN; }
    ")"   { return RPAREN; }
    "{"   { return LBRACE; }
    "}"   { return RBRACE; }
    "["   { return LBRACKET; }
    "]"   { return RBRACKET; }
    "@"   { return ARROBASE; }
    "#"   { return SHARP; }
    "?"   { return QUESTION_MARK; }
    "!"   { return EXCLAMATION_MARK; }
    "$"   { return DOLLAR; }
    "`"   { return BACKTICK; }
    "~"   { return TILDE; }

    "<"  { return LT; }
    ">"  { return GT; }

    "\^"  { return CARRET; }
    "+."  { return PLUSDOT; }
    "-."  { return MINUSDOT; }
    "/."  { return SLASHDOT; }
    "*."  { return STARDOT; }
    "+"   { return PLUS; }
    "-"   { return MINUS; }
    "/"   { return SLASH; }
    "*"   { return STAR; }
    "%"   { return PERCENT; }
}

<IN_STRING> {
    "\"" { yybegin(INITIAL); tokenEnd(); return STRING; }
    "\\" { NEWLINE } ([ \t] *) { }
    "\\" [\\\'\"ntbr ] { }
    "\\" [0-9] [0-9] [0-9] { }
    "\\" "o" [0-3] [0-7] [0-7] { }
    "\\" "x" [0-9a-fA-F] [0-9a-fA-F] { }
    "\\" . { }
    { NEWLINE } { }
    . { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return STRING; }
}

<IN_RML_COMMENT> {
    "/*" { commentDepth += 1; }
    "*/" { commentDepth -= 1; if(commentDepth == 0) { yybegin(INITIAL); tokenEnd(); return COMMENT; } }
    . | {NEWLINE} { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return COMMENT; }
}

<IN_OCL_COMMENT> {
    "(*" { commentDepth += 1; }
    "*)" { commentDepth -= 1; if(commentDepth == 0) { yybegin(INITIAL); tokenEnd(); return COMMENT; } }
    . | {NEWLINE} { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return COMMENT; }
}

[^] { return BAD_CHARACTER; }
