package com.reason.lang.odoc;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;

import static com.intellij.psi.TokenType.*;
%%

%unicode
%class ODocLexer
%implements FlexLexer
%function advance
%type IElementType
%eof{  return;
%eof}

%{
  private int tokenStartIndex;
  private int codeDepth;

   public ODocLexer() {
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

EOL=\n|\r|\r\n
WHITE_SPACE_CHAR=[\ \t\f]
WHITE_SPACE={WHITE_SPACE_CHAR}+
INPUT_CHARACTER = [^\r\n\ \t\f\[]

%state INITIAL
%state IN_CODE

%%

<YYINITIAL>  {
    [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    "(**"               { return ODocTypes.START; }
    "*)"                { return ODocTypes.END; }
    "["                 { yybegin(IN_CODE); codeDepth = 1; tokenStart(); }
    {WHITE_SPACE}       { return WHITE_SPACE; }
    {EOL}               { return ODocTypes.NEW_LINE; }
    {INPUT_CHARACTER}*  { return ODocTypes.ATOM; }
}

<IN_CODE> {
   "["           { codeDepth += 1; }
   "]"           { codeDepth -= 1; if (codeDepth == 0) { yybegin(INITIAL); tokenEnd(); return ODocTypes.CODE; } }
   . | {EOL}     { }
   <<EOF>>       { yybegin(INITIAL); tokenEnd(); return ODocTypes.CODE; }

}

[^] { return BAD_CHARACTER; }
