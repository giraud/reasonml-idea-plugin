package com.reason.lang.doc.reason;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;

import static com.intellij.psi.TokenType.*;

@SuppressWarnings("ALL")
%%

%unicode
%public
%class RDocLexer
%implements FlexLexer
%function advance
%type IElementType
%eof{  return;
%eof}

%{
  private int tokenStartIndex;
  private int codeDepth;
  private IElementType tag;

   public RDocLexer() {
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
TAG_CHARACTER = [a-zA-Z]
INPUT_CHARACTER = [^\r\n\ \t\f\*]

%state INITIAL

%%

<YYINITIAL>  {
    [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    "/**"                         { return RmlDocTypes.COMMENT_START; }
    "*/"                          { return RmlDocTypes.COMMENT_END; }
    "@" {TAG_CHARACTER}+          { return RmlDocTypes.TAG; }
    {WHITE_SPACE}                 { return WHITE_SPACE; }
    {EOL}                         { return RmlDocTypes.NEW_LINE; }
    {INPUT_CHARACTER}+            { return RmlDocTypes.ATOM; }
}

[^] { return BAD_CHARACTER; }
