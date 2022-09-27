package com.reason.lang.dune;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;

import static com.intellij.psi.TokenType.*;

@SuppressWarnings("ALL")
%%

%{
  private DuneTypes types;
  private int tokenStartIndex;
  private CharSequence quotedStringId;
  private int commentDepth;
  private int parenDepth;

  public DuneLexer(DuneTypes types) { this.types = types; }

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
%class DuneLexer
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
ATOM=[A-Za-z_0-9'@&\^!\.\-/+\\]

%state WAITING_VALUE
%state INITIAL
%state IN_STRING
%state IN_ML_COMMENT
%state IN_SEXPR_COMMENT
%state IN_SL_COMMENT

%%

<YYINITIAL>  {
      [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    {WHITE_SPACE}    { return WHITE_SPACE; }

    "\""        { yybegin(IN_STRING); tokenStart(); }
    "#|"        { yybegin(IN_ML_COMMENT); commentDepth = 1; tokenStart(); }
    "#;"        { yybegin(IN_SEXPR_COMMENT); parenDepth = 0; tokenStart(); }
    ";"         { yybegin(IN_SL_COMMENT); tokenStart(); }

    "%{"        { return types.VAR_START; }
    ">="        { return types.GTE; }
    "<="        { return types.LTE; }
    "}"         { return types.VAR_END; }
    ":"         { return types.COLON; }
    "("         { return types.LPAREN; }
    ")"         { return types.RPAREN; }
    "="         { return types.EQUAL; }
    "<"         { return types.LT; }
    ">"         { return types.GT; }
    "#"         { return types.SHARP; }

    {ATOM}+     { return types.ATOM; }
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

<IN_ML_COMMENT> {
    "#|" { commentDepth += 1; }
    "|#" { commentDepth -= 1; if(commentDepth == 0) { yybegin(INITIAL); tokenEnd(); return types.COMMENT; } }
    . | {NEWLINE} { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return types.COMMENT; }
}

<IN_SEXPR_COMMENT> {
    "(" { parenDepth += 1; }
    ")" { parenDepth -= 1; if(parenDepth == 0) { yybegin(INITIAL); tokenEnd(); return types.COMMENT; } }
    . | {NEWLINE} { }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return types.COMMENT; }
}

<IN_SL_COMMENT> {
    .         {}
    {NEWLINE} { yybegin(INITIAL); tokenEnd(); return types.COMMENT; }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return types.COMMENT; }
}

[^] { return BAD_CHARACTER; }
