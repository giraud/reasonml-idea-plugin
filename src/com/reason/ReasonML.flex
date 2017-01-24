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

UPPERCASE=[A-Z]
IDENTCHAR=[A-Za-z_0-9']

%state WAITING_VALUE
%state INITIAL
%state IN_STRING
%state IN_QUOTED_STRING
%state IN_COMMENT

%%


<YYINITIAL>  {
      [^]                             { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
        {WHITE_SPACE} { return WHITE_SPACE; }

        "module" { return MODULE;}
        "include" { return INCLUDE; }
        "type" { return TYPE; }

        "=" { return EQUAL; }
        ";" { return SEMI; }
        "." { return DOT; }
        "{" { return LBRACE; }
        "}" { return RBRACE; }

        {UPPERCASE}{IDENTCHAR}* { return UIDENT; }
}

[^] { System.out.println("Bad char:" + yytext()); return BAD_CHARACTER; } // Copied this need to know how it works