package com.reason.lang.doc.ocaml;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;

import static com.intellij.psi.TokenType.*;

@SuppressWarnings("ALL")
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
TAG_CHARACTER = [a-zA-Z]
INPUT_CHARACTER = [^\r\n\ \t\f\[\{\}\*]

%state INITIAL
%state IN_CODE
%state IN_PRE
%state IN_MARKUP

%%

<YYINITIAL>  {
    [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    "(**"                         { return OclDocTypes.COMMENT_START; }
    "*)"                          { return OclDocTypes.COMMENT_END; }
    "["                           { yybegin(IN_CODE); codeDepth = 1; tokenStart(); }
    "{{:"                         { return OclDocTypes.LINK_START; }
    "{["                          { yybegin(IN_PRE); tokenStart(); }
    "{b"                          { yybegin(IN_MARKUP); tag = OclDocTypes.BOLD; tokenStart(); }
    "{i"                          { yybegin(IN_MARKUP); tag = OclDocTypes.ITALIC; tokenStart(); }
    "{e"                          { yybegin(IN_MARKUP); tag = OclDocTypes.EMPHASIS; tokenStart(); }
    "{!"                          { yybegin(IN_MARKUP); tag = OclDocTypes.CROSS_REF; tokenStart(); }
    "{ol" {WHITE_SPACE}*          { return OclDocTypes.O_LIST; }
    "{ul" {WHITE_SPACE}*          { return OclDocTypes.U_LIST; }
    "{-" {WHITE_SPACE}*           { return OclDocTypes.LIST_ITEM_START; }
    "{" {DIGITS} {WHITE_SPACE}*   { return OclDocTypes.SECTION; }
    ":"                           { return OclDocTypes.COLON; }
    "}"                           { return OclDocTypes.RBRACE; }
    "@" {TAG_CHARACTER}+          { return OclDocTypes.TAG; }
    {WHITE_SPACE}                 { return WHITE_SPACE; }
    {EOL}                         { return OclDocTypes.NEW_LINE; }
    {INPUT_CHARACTER}+            { return OclDocTypes.ATOM; }
}

<IN_CODE> {
   "["           { codeDepth += 1; }
   "]"           { codeDepth -= 1; if (codeDepth == 0) { yybegin(INITIAL); tokenEnd(); return OclDocTypes.CODE; } }
   . | {EOL}     { }
   <<EOF>>       { yybegin(INITIAL); tokenEnd(); return OclDocTypes.CODE; }
}

<IN_PRE> {
   "]}"          { yybegin(INITIAL); tokenEnd(); return OclDocTypes.PRE; }
   . | {EOL}     { }
   <<EOF>>       { yybegin(INITIAL); tokenEnd(); return OclDocTypes.PRE; }
}

<IN_MARKUP> {
   "}"           { yybegin(INITIAL); tokenEnd(); return tag; }
   . | {EOL}     { }
   <<EOF>>       { yybegin(INITIAL); tokenEnd(); return tag; }
}

[^] { return BAD_CHARACTER; }
