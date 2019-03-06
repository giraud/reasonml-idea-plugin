package com.reason.lang.ocamlyacc;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;

import static com.intellij.psi.TokenType.*;
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

    "%{"           { yybegin(IN_HEADER); tokenStart(); return OclYaccTypes.HEADER_START; }
    "%}"           { return OclYaccTypes.HEADER_STOP; }
    "{"           { yybegin(IN_SEMANTIC_ACTION); braceDepth = 1; tokenStart(); return OclYaccTypes.LBRACE; }
    "}"           { return OclYaccTypes.RBRACE; }

    "%%"           {return OclYaccTypes.SECTION_SEPARATOR; }

    "%token"      {return OclYaccTypes.TOKEN; }
    "%start"      {return OclYaccTypes.START; }
    "%type"       {return OclYaccTypes.TYPE; }
    "%left"       {return OclYaccTypes.LEFT; }
    "%right"      {return OclYaccTypes.RIGHT; }

    "."           { return OclYaccTypes.DOT; }
    ":"           { return OclYaccTypes.COLON; }
    ";"           { return OclYaccTypes.SEMI; }
    "|"           { return OclYaccTypes.PIPE; }
    "<"           { return OclYaccTypes.LT; }
    ">"           { return OclYaccTypes.GT; }

    {IDENT}+      { return OclYaccTypes.IDENT; }
    /*[^\ \t\f]     { return OclYaccTypes.ATOM; }*/
}

<IN_HEADER> {
    "%}"      { yypushback(2); tokenEnd(); yybegin(INITIAL); return OclYaccTypes.OCAML_LAZY_NODE; }
    .         { }
    {NEWLINE} { }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return OclYaccTypes.OCAML_LAZY_NODE; }
}

<IN_SEMANTIC_ACTION> {
    "{"       { braceDepth += 1; }
    "}"       { braceDepth -= 1; if(braceDepth == 0) { yypushback(1); tokenEnd(); yybegin(INITIAL); return OclYaccTypes.OCAML_LAZY_NODE; } }
    .         { }
    {NEWLINE} { }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return OclYaccTypes.OCAML_LAZY_NODE; }
}

[^] { return BAD_CHARACTER; }
