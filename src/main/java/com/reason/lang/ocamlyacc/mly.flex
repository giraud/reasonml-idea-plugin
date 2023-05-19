package com.reason.lang.ocamlyacc;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;

import static com.intellij.psi.TokenType.*;

@SuppressWarnings("ALL")
%%

%unicode
%class YaccLexer
%implements FlexLexer
%function advance
%type IElementType
%eof{  return;
%eof}

%{
  private int tokenStartIndex;
  private CharSequence quotedStringId;
  private int braceDepth;
  private boolean rulesDone = false;
  private boolean zzEOFDone = false;
  private boolean zzAtBOL = false;

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

%state INITIAL
%state IN_HEADER
%state IN_SEMANTIC_ACTION
%state IN_COMMENT
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

    "%{"           { yybegin(IN_HEADER); tokenStart(); return OclYaccTypes.INSTANCE.HEADER_START; }
    "%}"           { return OclYaccTypes.INSTANCE.HEADER_STOP; }
    "{"            { yybegin(IN_SEMANTIC_ACTION); braceDepth = 1; tokenStart(); return OclYaccTypes.INSTANCE.LBRACE; }
    "}"            { return OclYaccTypes.INSTANCE.RBRACE; }

    "%%"           { if (rulesDone) { yybegin(IN_TRAILER); } rulesDone = true; return OclYaccTypes.INSTANCE.SECTION_SEPARATOR; }

    "%token"       {return OclYaccTypes.INSTANCE.TOKEN; }
    "%start"       {return OclYaccTypes.INSTANCE.START; }
    "%type"        {return OclYaccTypes.INSTANCE.TYPE; }
    "%left"        {return OclYaccTypes.INSTANCE.LEFT; }
    "%right"       {return OclYaccTypes.INSTANCE.RIGHT; }

    "."            { return OclYaccTypes.INSTANCE.DOT; }
    ":"            { return OclYaccTypes.INSTANCE.COLON; }
    ";"            { return OclYaccTypes.INSTANCE.SEMI; }
    "|"            { return OclYaccTypes.INSTANCE.PIPE; }
    "<"            { return OclYaccTypes.INSTANCE.LT; }
    ">"            { return OclYaccTypes.INSTANCE.GT; }

    "/*"           { yybegin(IN_COMMENT); tokenStart(); }
    "(*"           { yybegin(IN_COMMENT); tokenStart(); }

    {IDENT}+       { return OclYaccTypes.INSTANCE.IDENT; }
    /*[^\ \t\f]     { return OclYaccTypes.INSTANCE.ATOM; }*/
}

<IN_HEADER> {
    "%}"      { yypushback(2); tokenEnd(); yybegin(INITIAL); return OclYaccTypes.INSTANCE.OCAML_LAZY_NODE; }
    .         { }
    {NEWLINE} { }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return OclYaccTypes.INSTANCE.OCAML_LAZY_NODE; }
}

<IN_SEMANTIC_ACTION> {
    "{"       { braceDepth += 1; }
    "}"       { braceDepth -= 1; if(braceDepth == 0) { yypushback(1); tokenEnd(); yybegin(INITIAL); return OclYaccTypes.INSTANCE.OCAML_LAZY_NODE; } }
    .         { }
    {NEWLINE} { }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return OclYaccTypes.INSTANCE.OCAML_LAZY_NODE; }
}

<IN_COMMENT> {
    .         { }
    {NEWLINE} { yybegin(INITIAL); tokenEnd(); return OclYaccTypes.INSTANCE.SINGLE_COMMENT; }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return OclYaccTypes.INSTANCE.SINGLE_COMMENT; }
}

<IN_TRAILER> {
    .         { }
    {NEWLINE} { }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return OclYaccTypes.INSTANCE.OCAML_LAZY_NODE; }
}

[^] { return BAD_CHARACTER; }
