package com.reason.lang.ocamlyacc;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;

import static com.intellij.psi.TokenType.*;

@SuppressWarnings("ALL")
%%

%public
%unicode
%class OclYaccLexer
%implements FlexLexer
%function advance
%type IElementType
%eof{  return;
%eof}

%{
  private OclYaccTypes types;
  private int tokenStartIndex;
  private CharSequence quotedStringId;
  private int braceDepth;
  private boolean rulesDone = false;
  private boolean zzEOFDone = false;
  private boolean zzAtBOL = false;

   public OclYaccLexer() {
      this((java.io.Reader)null);
      this.types = OclYaccTypes.INSTANCE;
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

%state INITIAL
%state IN_HEADER
%state IN_SEMANTIC_ACTION
%state IN_ML_COMMENT
%state IN_SL_COMMENT
%state IN_TRAILER

EOL=\n|\r|\r\n
WHITE_SPACE_CHAR=[\ \t\f]|{EOL}
WHITE_SPACE={WHITE_SPACE_CHAR}+

NEWLINE=("\r"* "\n")
IDENT=[A-Za-z_0-9]

%%

<YYINITIAL>  {
      [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    {WHITE_SPACE}  { return WHITE_SPACE; }

    "%{"           { yybegin(IN_HEADER); tokenStart(); return types.HEADER_START; }
    "%}"           { return types.HEADER_STOP; }
    "{"            { yybegin(IN_SEMANTIC_ACTION); braceDepth = 1; tokenStart(); return types.LBRACE; }
    "}"            { return types.RBRACE; }

    "%%"           { if (rulesDone) { yybegin(IN_TRAILER); } rulesDone = true; return types.SECTION_SEPARATOR; }

    "%token"       { return types.TOKEN; }
    "%start"       { return types.START; }
    "%type"        { return types.TYPE; }
    "%left"        { return types.LEFT; }
    "%right"       { return types.RIGHT; }
    "%nonassoc"    { return types.NON_ASSOC; }
    "%inline"      { return types.INLINE; }

    "."            { return types.DOT; }
    ":"            { return types.COLON; }
    ";"            { return types.SEMI; }
    "|"            { return types.PIPE; }
    "<"            { return types.LT; }
    ">"            { return types.GT; }

    "/*"           { yybegin(IN_ML_COMMENT); tokenStart(); }
    "(*"           { yybegin(IN_ML_COMMENT); tokenStart(); }
    "//"           { yybegin(IN_SL_COMMENT); tokenStart(); }

    {IDENT}+       { return types.IDENT; }
    [^\ \t\f]      { return types.ATOM; }
}

<IN_HEADER> {
    "%}"      { yypushback(2); tokenEnd(); yybegin(INITIAL); return types.TEMPLATE_OCAML_TEXT; }
    .         { }
    {NEWLINE} { }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return types.TEMPLATE_OCAML_TEXT; }
}

<IN_SEMANTIC_ACTION> {
    "{"           { braceDepth += 1; }
    "}"           { braceDepth -= 1; if(braceDepth == 0) { yypushback(1); tokenEnd(); yybegin(INITIAL); return types.TEMPLATE_OCAML_TEXT; } }
    . | {NEWLINE} { }
    <<EOF>>       { yybegin(INITIAL); tokenEnd(); return types.TEMPLATE_OCAML_TEXT; }
}

<IN_ML_COMMENT> {
    "*/"          { yybegin(INITIAL); tokenEnd(); return types.MULTI_COMMENT; }
    "*)"          { yybegin(INITIAL); tokenEnd(); return types.MULTI_COMMENT; }
    . | {NEWLINE} { }
    <<EOF>>       { yybegin(INITIAL); tokenEnd(); return types.MULTI_COMMENT; }
}

<IN_SL_COMMENT> {
    .         { }
    {NEWLINE} { yybegin(INITIAL); tokenEnd(); return types.SINGLE_COMMENT; }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return types.SINGLE_COMMENT; }
}

<IN_TRAILER> {
    . | {NEWLINE} { }
    <<EOF>>       { yybegin(INITIAL); tokenEnd(); return types.TEMPLATE_OCAML_TEXT; }
}

[^] { return BAD_CHARACTER; }
