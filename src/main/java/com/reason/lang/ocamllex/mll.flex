package com.reason.lang.ocamllex;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;

import static com.intellij.psi.TokenType.*;

@SuppressWarnings("ALL")
%%

%unicode
%public
%class OclLexLexer
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

   public OclLexLexer() {
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
%state IN_COMMENT
%state IN_OCAML_SCOPE
%state IN_STRING
%state IN_CHAR

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

    "(*"           { yybegin(IN_COMMENT); tokenStart(); }
    "\""           { yybegin(IN_STRING); tokenStart(); }
    "'"            { yybegin(IN_CHAR); tokenStart(); }
    "{"            { yybegin(IN_OCAML_SCOPE); braceDepth = 1; tokenStart(); return OclLexTypes.INSTANCE.LBRACE; }
    "}"            { return OclLexTypes.INSTANCE.RBRACE; }

    "="            { return OclLexTypes.INSTANCE.EQ; }
    "|"            { return OclLexTypes.INSTANCE.PIPE; }
    "["            { return OclLexTypes.INSTANCE.LBRACKET; }
    "]"            { return OclLexTypes.INSTANCE.RBRACKET; }
    "-"            { return OclLexTypes.INSTANCE.DASH; }

    "let"          { return OclLexTypes.INSTANCE.LET; }
    "rule"         { return OclLexTypes.INSTANCE.RULE; }
    "parse"        { return OclLexTypes.INSTANCE.PARSE; }
    "and"          { return OclLexTypes.INSTANCE.AND; }

    {IDENT}+       { return OclLexTypes.INSTANCE.IDENT; }
    [^\ \t\f]      { return OclLexTypes.INSTANCE.ATOM; }
}

<IN_COMMENT> {
    .         { }
    {NEWLINE} { yybegin(INITIAL); tokenEnd(); return OclLexTypes.INSTANCE.SINGLE_COMMENT; }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return OclLexTypes.INSTANCE.SINGLE_COMMENT; }
}

<IN_OCAML_SCOPE> {
    "{"       { braceDepth += 1; }
    "}"       { braceDepth -= 1; if(braceDepth == 0) { yypushback(1); tokenEnd(); yybegin(INITIAL); return OclLexTypes.INSTANCE.TEMPLATE_OCAML_TEXT; } }
    .         { }
    {NEWLINE} { }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return OclLexTypes.INSTANCE.TEMPLATE_OCAML_TEXT; }
}

<IN_STRING> {
    "\"" { yybegin(INITIAL); tokenEnd(); return OclLexTypes.INSTANCE.STRING_VALUE; }
    "\\" [\\\'\"ntbr ] { }
    { NEWLINE } { }
    . { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return OclLexTypes.INSTANCE.STRING_VALUE; }
}

<IN_CHAR> {
    "'" { yybegin(INITIAL); tokenEnd(); return OclLexTypes.INSTANCE.STRING_VALUE; }
    "\\" [\\\'\"ntbr ] { }
    { NEWLINE } { }
    . { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return OclLexTypes.INSTANCE.STRING_VALUE; }
}

[^] { return BAD_CHARACTER; }
