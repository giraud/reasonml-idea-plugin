package com.reason.lang.dune;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import static com.intellij.psi.TokenType.*;
%%

%{
  private DuneTypes types;
  private int tokenStartIndex;
  private CharSequence quotedStringId;
  private int commentDepth;

  public DuneLexer() {}

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
IDENTCHAR=[A-Za-z_0-9']

%state WAITING_VALUE
%state INITIAL
%state IN_STRING

%%

<YYINITIAL>  {
      [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    {WHITE_SPACE}    { return WHITE_SPACE; }

    "("              { return types.LPAREN; }
    ")"              { return types.RPAREN; }

    "jbuild_version" { return types.VERSION; }
    "library"        { return types.LIBRARY; }
    "name"           { return types.NAME; }
    "public_name"    { return types.PUBLIC_NAME; }
    "synopsis"       { return types.SYNOPSIS; }
    "executable"     { return types.EXECUTABLE; }

    {IDENTCHAR}+     { return types.IDENT; }
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

[^] { return BAD_CHARACTER; }
