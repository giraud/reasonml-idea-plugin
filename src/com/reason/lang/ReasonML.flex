package com.reason;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import static com.reason.psi.ReasonMLTypes.*;
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
DECIMAL_LITERAL=[0-9] [0-9_]*
HEX_LITERAL="0" [xX] [0-9A-Fa-f][0-9A-Fa-f_]*
OCT_LITERAL="0" [oO] [0-7] [0-7_]*
BIN_LITERAL="0" [bB] [0-1] [0-1_]*
INT_LITERAL= { DECIMAL_LITERAL } | { HEX_LITERAL } | { OCT_LITERAL } | { BIN_LITERAL }
FLOAT_LITERAL=[0-9] [0-9_]* ("." [0-9_]* )? ([eE] [+-]? [0-9] [0-9_]* )?
HEX_FLOAT_LITERAL="0" [xX] [0-9A-Fa-f] [0-9A-Fa-f_]* ("." [0-9A-Fa-f_]* )? ([pP] [+-]? [0-9] [0-9_]* )?
LITERAL_MODIFIER=[G-Zg-z]

%state WAITING_VALUE
%state INITIAL
%state IN_STRING
%state IN_COMMENT

%%

<YYINITIAL>  {
      [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    {WHITE_SPACE} { return WHITE_SPACE; }

    "module"    { return MODULE;}
    "open"      { return OPEN; }
    "include"   { return INCLUDE; }
    "type"      { return TYPE; }
    "let"       { return LET; }
    "fun"       { return FUN; }
    "external"  { return EXTERNAL; }

    "option"    { return OPTION; }
    "None"      { return NONE; }
    "Some"      { return SOME; }

    "false" { return FALSE; }
    "true"  { return TRUE; }

    {LOWERCASE}{IDENTCHAR}*          { return LIDENT; }
    {UPPERCASE}{IDENTCHAR}*          { return UIDENT; }
    {INT_LITERAL}{LITERAL_MODIFIER}? { return INT; }
    ({FLOAT_LITERAL} | {HEX_FLOAT_LITERAL}){LITERAL_MODIFIER}? { return FLOAT; }

    "\"" { yybegin(IN_STRING); tokenStart(); }
    "/*" { yybegin(IN_COMMENT); commentDepth = 1; tokenStart(); }

    "::"   { return SHORTCUT; }
    "=>"   { return ARROW; }
    "@@bs" { return BBS; }
    "@bs"  { return BS; }

    "="  { return EQUAL; }
    ","  { return COMMA; }
    ":"  { return COLON; }
    ";"  { return SEMI; }
    "'"  { return QUOTE; }
    "."  { return DOT; }
    "("  { return LPAREN; }
    ")"  { return RPAREN; }
    "{"  { return LBRACE; }
    "}"  { return RBRACE; }
    "["  { return LBRACKET; }
    "]"  { return RBRACKET; }
    "#"  { return SHARP; }
    "?"  { return QUESTION_MARK; }

    "/>" { return AUTO_CLOSE_TAG; }
    "</" { return CLOSE_TAG; }
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

<IN_COMMENT> {
    "/*" { commentDepth += 1; }
    "*/" { commentDepth -= 1; if(commentDepth == 0) { yybegin(INITIAL); tokenEnd(); return COMMENT; } }
    . | {NEWLINE} { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return COMMENT; }
}

[^] { return BAD_CHARACTER; }