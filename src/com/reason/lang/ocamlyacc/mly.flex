package com.reason.lang.ocamlyacc;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;

import static com.intellij.psi.TokenType.*;
%%

%unicode
%public
%class YaccLexer
%implements FlexLexer

%{
  private int tokenStartIndex;
  private CharSequence quotedStringId;
  private int commentDepth;
  private int parenDepth;

   public YaccLexer() {
      this((java.io.Reader)null);
  }

  // Store the start index of a token
  private void tokenStart() {
    tokenStartIndex = zzStartRead;
  }

  // Set the start index of the token to the stored index
  private void tokenEnd() {
    zzStartRead = tokenStartIndex;
  }
%}

%function advance
%type IElementType
%eof{  return;
%eof}

%state INITIAL
%state IN_HEADER

EOL=\n|\r|\r\n
WHITE_SPACE_CHAR=[\ \t\f]|{EOL}
WHITE_SPACE={WHITE_SPACE_CHAR}+

NEWLINE=("\r"* "\n")
ATOM=[A-Za-z_0-9'&\^%!\.-]

%%

<YYINITIAL>  {
      [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    {WHITE_SPACE}    { return WHITE_SPACE; }

    "%{"             { yybegin(IN_HEADER); tokenStart(); return HEADER_START; }
}

<IN_HEADER> {
    "}%" { yybegin(INITIAL); tokenEnd(); return HEADER_STOP; }
    . { return OCAML_NODE; }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return types.STRING; }
}

[^] { return BAD_CHARACTER; }
