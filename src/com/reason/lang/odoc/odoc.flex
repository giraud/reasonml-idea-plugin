package com.reason.lang.odoc;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;

import static com.intellij.psi.TokenType.*;
%%

%unicode
%public
%class ODocLexer
%implements FlexLexer
%function advance
%type IElementType
%eof{  return;
%eof}

%{
  private int tokenStartIndex;
  private int codeDepth;
  private IElementType tag;

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
DIGIT=[0-9]
DIGITS={DIGIT}+
INPUT_CHARACTER = [^\r\n\ \t\f\[\{\}\*]

%state INITIAL
%state IN_CODE
%state IN_MARKUP

%%

<YYINITIAL>  {
    [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    "(**"                 { return ODocTypes.START; }
    "*)"                  { return ODocTypes.END; }
    "["                   { yybegin(IN_CODE); codeDepth = 1; tokenStart(); }
    "{{:"                 { return ODocTypes.LINK; }
    "{[" {WHITE_SPACE}* {EOL}?                  { return ODocTypes.PRE_START; }
    "]}"                  { return ODocTypes.PRE_END; }
    "{b"                  { yybegin(IN_MARKUP); tag = ODocTypes.BOLD; tokenStart(); }
    "{i"                  { yybegin(IN_MARKUP); tag = ODocTypes.ITALIC; tokenStart(); }
    "{e"                  { yybegin(IN_MARKUP); tag = ODocTypes.EMPHASIS; tokenStart(); }
    "{!"                  { yybegin(IN_MARKUP); tag = ODocTypes.CROSS_REF; tokenStart(); }
    "{ol" {WHITE_SPACE}*  { return ODocTypes.O_LIST; }
    "{ul" {WHITE_SPACE}*  { return ODocTypes.U_LIST; }
    "{-" {WHITE_SPACE}*   { return ODocTypes.LIST_ITEM; }
    "{" {DIGITS} {WHITE_SPACE}* { return ODocTypes.SECTION; }
    ":"                   { return ODocTypes.COLON; }
    "}"                   { return ODocTypes.RBRACE; }
    {WHITE_SPACE}         { return WHITE_SPACE; }
    {EOL}                 { return ODocTypes.NEW_LINE; }
    {INPUT_CHARACTER}+    { return ODocTypes.ATOM; }
}

<IN_CODE> {
   "["           { codeDepth += 1; }
   "]"           { codeDepth -= 1; if (codeDepth == 0) { yybegin(INITIAL); tokenEnd(); return ODocTypes.CODE; } }
   . | {EOL}     { }
   <<EOF>>       { yybegin(INITIAL); tokenEnd(); return ODocTypes.CODE; }
}

<IN_MARKUP> {
   "}"           { yybegin(INITIAL); tokenEnd(); return tag; }
   . | {EOL}     { }
   <<EOF>>       { yybegin(INITIAL); tokenEnd(); return tag; }
}

[^] { return BAD_CHARACTER; }
