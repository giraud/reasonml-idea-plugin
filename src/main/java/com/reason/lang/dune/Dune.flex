package com.reason.lang.dune;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.TokenType;

import static com.intellij.psi.TokenType.*;
%%

%{
  private DuneTypes types;
  private int tokenStartIndex;
  private CharSequence quotedStringId;
  private int commentDepth;

  public DuneLexer() {}

  //Store the start index of a token
  private void tokenStart() {
    tokenStartIndex = zzStartRead;
  }

  //Set the start index of the token to the stored index
  private void tokenEnd() {
    zzStartRead = tokenStartIndex;
  }
%}

%public
%class DuneLexer
%implements FlexLexer
%unicode
%function advance
%type IElementType
%eof{  return;
%eof}

EOL=\n|\r|\r\n
WHITE_SPACE_CHAR=[\ \t\f]|{EOL}
WHITE_SPACE={WHITE_SPACE_CHAR}+

NEWLINE=("\r"* "\n")
IDENTCHAR=[A-Za-z_0-9']

%state WAITING_VALUE
%state INITIAL
%state IN_STRING

%%

<YYINITIAL>  {
      [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    {WHITE_SPACE}    { return WHITE_SPACE; }

    "("              { return types.LPAREN; }
    ")"              { return types.RPAREN; }

    "jbuild_version" { return types.VERSION; }
    "library"        { return types.LIBRARY; }
    "executable"     { return types.EXECUTABLE; }

    "name"                     { return types.NAME; }
    "public_name"              { return types.PUBLIC_NAME; }
    "synopsis"                 { return types.SYNOPSIS; }
    "modules"                  { return types.MODULES; }
    "libraries"                { return types.LIBRARIES; }
    "wrapped"                  { return types.WRAPPED; }
    "preprocess"               { return types.PREPROCESS; }
    "preprocessor_deps"        { return types.PREPROCESSOR_DEPS; }
    "optional"                 { return types.OPTIONAL; }
    "c_names"                  { return types.C_NAMES; }
    "cxx_names"                { return types.CXX_NAMES; }
    "install_c_headers"        { return types.INSTALL_C_HEADERS; }
    "modes"                    { return types.MODES; }
    "no_dynlink"               { return types.NO_DYNLINK; }
    "kind"                     { return types.KIND; }
    "ppx_runtime_libraries"    { return types.PPX_RUNTIME_LIBRARIES; }
    "virtual_deps"             { return types.VIRTUAL_DEPS; }
    "js_of_ocaml"              { return types.JS_OF_OCAML; }
    "flags"                    { return types.FLAGS; }
    "ocamlc_flags"             { return types.OCAMLC_FLAGS; }
    "ocamlopt_flags"           { return types.OCAMLOPT_FLAGS; }
    "library_flags"            { return types.LIBRARY_FLAGS; }
    "c_flags"                  { return types.C_FLAGS; }
    "cxx_flags"                { return types.CXX_FLAGS; }
    "c_library_flags"          { return types.C_LIBRARY_FLAGS; }
    "self_build_stubs_archive"        { return types.SELF_BUILD_STUBS_ARCHIVE; }
    "modules_without_implementation"  { return types.MODULES_WITHOUT_IMPLEMENTATION; }
    "allow_overlapping_dependencies"  { return types.ALLOW_OVERLAPPING_DEPENDENCIES; }

    {IDENTCHAR}+     { return types.ATOM; }
}

<IN_STRING> {
    "\"" { yybegin(INITIAL); tokenEnd(); return types.STRING; }
    "\\" { NEWLINE } ([ \t] *) { }
    "\\" [\\\'\"ntbr ] { }
    "\\" [0-9] [0-9] [0-9] { }
    "\\" "o" [0-3] [0-7] [0-7] { }
    "\\" "x" [0-9a-fA-F] [0-9a-fA-F] { }
    "\\" . { }
    { NEWLINE } { }
    . { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return types.STRING; }
}

[^] { return BAD_CHARACTER; }
