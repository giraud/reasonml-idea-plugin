package com.reason.lang.ocamlgrammar;

import com.intellij.psi.tree.IElementType;
import com.intellij.lexer.FlexLexer;

import static com.intellij.psi.TokenType.*;

@SuppressWarnings("ALL")
%%

%public
%unicode
%class OclGrammarLexer
%implements FlexLexer
%function advance
%type IElementType
%eof{  return;
%eof}

%{
  private OclGrammarTypes types;
  private int tokenStartIndex;
  private CharSequence quotedStringId;
  private int braceDepth;
  private boolean rulesDone = false;
  private boolean zzEOFDone = false;
  private boolean zzAtBOL = false;

   public OclGrammarLexer() {
      this((java.io.Reader)null);
      this.types = OclGrammarTypes.INSTANCE;
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
%state IN_TEMPLATE
%state IN_STRING
%state IN_ML_COMMENT
%state IN_SL_COMMENT

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

    "DECLARE"      { return types.DECLARE; }
    "PLUGIN"       { return types.PLUGIN; }
    "GRAMMAR"      { return types.GRAMMAR; }
    "VERNAC"       { return types.VERNAC; }
    "ARGUMENT"     { return types.ARGUMENT; }
    "EXTEND"       { return types.EXTEND; }
    "COMMAND"      { return types.COMMAND; }
    "TACTIC"       { return types.TACTIC; }
    "END"          { return types.END; }

    "{"            { yybegin(IN_TEMPLATE); braceDepth = 1; tokenStart(); return types.LBRACE; }
    "}"            { return types.RBRACE; }

    "->"           { return types.ARROW; }
    "|"            { return types.PIPE; }
    "("            { return types.LPAREN; }
    ")"            { return types.RPAREN; }
    "["            { return types.LBRACKET; }
    "]"            { return types.RBRACKET; }

    "/*"           { yybegin(IN_ML_COMMENT); tokenStart(); }
    "(*"           { yybegin(IN_ML_COMMENT); tokenStart(); }
    "//"           { yybegin(IN_SL_COMMENT); tokenStart(); }

    "\"" { yybegin(IN_STRING); tokenStart(); }

    {IDENT}+       { return types.IDENT; }
    [^\ \t\f]      { return types.ATOM; }
}

<IN_TEMPLATE> {
    "{"           { braceDepth += 1; }
    "}"           { braceDepth -= 1; if(braceDepth == 0) { yypushback(1); tokenEnd(); yybegin(INITIAL); return types.TEMPLATE_OCAML_TEXT; } }
    . | {NEWLINE} { }
    <<EOF>>       { yybegin(INITIAL); tokenEnd(); return types.TEMPLATE_OCAML_TEXT; }
}

<IN_STRING> {
    "\"" { yybegin(INITIAL); tokenEnd(); return types.STRING_VALUE; }
    . | {NEWLINE} { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return types.STRING_VALUE; }
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

[^] { return BAD_CHARACTER; }
