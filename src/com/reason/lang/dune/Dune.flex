package com.reason.lang.dune;

import com.intellij.psi.tree.IElementType;

import static com.intellij.psi.TokenType.*;
%%

%{
  private DuneTypes types;
  private int tokenStartIndex;
  private CharSequence quotedStringId;
  private int commentDepth;
  private int parenDepth;

  public DuneLexer(DuneTypes types) { this.types = types; }

  // Store the start index of a token
  private void tokenStart() {
    tokenStartIndex = zzStartRead;
  }

  // Set the start index of the token to the stored index
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
ATOM=[A-Za-z_0-9'&\^%!\.-]

%state WAITING_VALUE
%state INITIAL
%state IN_STRING
%state IN_ML_COMMENT
%state IN_SEXPR_COMMENT
%state IN_SL_COMMENT

%%

<YYINITIAL>  {
      [^]   { yybegin(INITIAL); yypushback(1); }
}

<INITIAL> {
    {WHITE_SPACE}    { return WHITE_SPACE; }

    "("              { return types.LPAREN; }
    ")"              { return types.RPAREN; }

    "\""             { yybegin(IN_STRING); tokenStart(); }
    "#|"             { yybegin(IN_ML_COMMENT); commentDepth = 1; tokenStart(); }
    "#;"             { yybegin(IN_SEXPR_COMMENT); parenDepth = 0; tokenStart(); }
    ";"              { yybegin(IN_SL_COMMENT); tokenStart(); }

    "jbuild_version" { return types.VERSION; }
    "library"        { return types.LIBRARY; }
    "executable"     { return types.EXECUTABLE; }
    "executables"    { return types.EXECUTABLES; }
    "rule"           { return types.RULE; }
    "ocamllex"       { return types.OCAML_LEX; }
    "ocamlyacc"      { return types.OCAML_YACC; }
    "menhir"         { return types.MENHIR; }
    "alias"          { return types.ALIAS; }
    "install"        { return types.INSTALL; }
    "copy_files"     { return types.COPY_FILES; }
    "copy_files#"    { return types.COPY_FILES_SHARP; }
    "include"        { return types.INCLUDE; }

    "action"                          { return types.ACTION; }
    "allow_overlapping_dependencies"  { return types.ALLOW_OVERLAPPING_DEPENDENCIES; }
    "c_flags"                         { return types.C_FLAGS; }
    "c_library_flags"                 { return types.C_LIBRARY_FLAGS; }
    "c_names"                         { return types.C_NAMES; }
    "cxx_flags"                       { return types.CXX_FLAGS; }
    "cxx_names"                       { return types.CXX_NAMES; }
    "deps"                            { return types.DEPS; }
    "flags"                           { return types.FLAGS; }
    "install_c_headers"               { return types.INSTALL_C_HEADERS; }
    "js_of_ocaml"                     { return types.JS_OF_OCAML; }
    "kind"                            { return types.KIND; }
    "libraries"                       { return types.LIBRARIES; }
    "library_flags"                   { return types.LIBRARY_FLAGS; }
    "link_flags"                      { return types.LINK_FLAGS; }
    "locks"                           { return types.LOCKS; }
    "mode"                            { return types.MODE; }
    "modes"                           { return types.MODES; }
    "modules"                         { return types.MODULES; }
    "modules_without_implementation"  { return types.MODULES_WITHOUT_IMPLEMENTATION; }
    "name"                            { return types.NAME; }
    "names"                           { return types.NAMES; }
    "no_dynlink"                      { return types.NO_DYNLINK; }
    "ocamlc_flags"                    { return types.OCAMLC_FLAGS; }
    "ocamlopt_flags"                  { return types.OCAMLOPT_FLAGS; }
    "optional"                        { return types.OPTIONAL; }
    "ppx_runtime_libraries"           { return types.PPX_RUNTIME_LIBRARIES; }
    "preprocess"                      { return types.PREPROCESS; }
    "preprocessor_deps"               { return types.PREPROCESSOR_DEPS; }
    "public_name"                     { return types.PUBLIC_NAME; }
    "public_names"                    { return types.PUBLIC_NAMES; }
    "self_build_stubs_archive"        { return types.SELF_BUILD_STUBS_ARCHIVE; }
    "synopsis"                        { return types.SYNOPSIS; }
    "targets"                         { return types.TARGETS; }
    "virtual_deps"                    { return types.VIRTUAL_DEPS; }
    "wrapped"                         { return types.WRAPPED; }

    // Compilation mode
    "byte"                            { return types.BYTE; }
    "native"                          { return types.NATIVE; }
    "best"                            { return types.BEST; }

    // Binary kind
    "exe"                             { return types.EXE; }
    "object"                          { return types.OBJECT; }
    "shared_object"                   { return types.SHARED_OBJECT; }

    // Modes
    "standard"                        { return types.STANDARD; }
    "fallback"                        { return types.FALLBACK; }
    "promote"                         { return types.PROMOTE; }
    "promote-until-clean"             { return types.PROMOTE_UNTIL_CLEAN; }

    {ATOM}+     { return types.ATOM; }
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

<IN_ML_COMMENT> {
    "#|" { commentDepth += 1; }
    "|#" { commentDepth -= 1; if(commentDepth == 0) { yybegin(INITIAL); tokenEnd(); return types.COMMENT; } }
    . | {NEWLINE} { }
    <<EOF>> { yybegin(INITIAL); tokenEnd(); return types.COMMENT; }
}

<IN_SEXPR_COMMENT> {
    "(" { parenDepth += 1; }
    ")" { parenDepth -= 1; if(parenDepth == 0) { yybegin(INITIAL); tokenEnd(); return types.COMMENT; } }
    . | {NEWLINE} { }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return types.COMMENT; }
}

<IN_SL_COMMENT> {
    .         {}
    {NEWLINE} { yybegin(INITIAL); tokenEnd(); return types.COMMENT; }
    <<EOF>>   { yybegin(INITIAL); tokenEnd(); return types.COMMENT; }
}

[^] { return BAD_CHARACTER; }
