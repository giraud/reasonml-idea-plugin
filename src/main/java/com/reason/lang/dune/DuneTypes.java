package com.reason.lang.dune;

import com.intellij.psi.tree.IElementType;

public class DuneTypes {

    public static final IElementType LPAREN = new DuneElementType("LPAREN");
    public static final IElementType RPAREN = new DuneElementType("RPAREN");
    public static final IElementType STRING = new DuneElementType("String");
    public static final IElementType ATOM = new DuneElementType("ATOM");

    public static final IElementType SEXPR = new DuneElementType("s-expr");

    public static final IElementType VERSION = new DuneElementType("VERSION");

    public static final IElementType LIBRARY = new DuneElementType("LIBRARY");
    public static final IElementType NAME = new DuneElementType("NAME");
    public static final IElementType PUBLIC_NAME = new DuneElementType("PUBLIC_NAME");
    public static final IElementType SYNOPSIS = new DuneElementType("SYNOPSIS");

    public static final IElementType EXECUTABLE = new DuneElementType("EXECUTABLE");
    public static final IElementType KIND = new DuneElementType("KIND");
    public static final IElementType MODES = new DuneElementType("MODES");
    public static final IElementType FLAGS = new DuneElementType("FLAGS");
    public static final IElementType C_NAMES = new DuneElementType("C_NAMES");
    public static final IElementType C_FLAGS = new DuneElementType("C_FLAGS");
    public static final IElementType MODULES = new DuneElementType("MODULES");
    public static final IElementType WRAPPED = new DuneElementType("WRAPPED");
    public static final IElementType OPTIONAL = new DuneElementType("OPTIONAL");
    public static final IElementType LIBRARIES = new DuneElementType("LIBRARIES");
    public static final IElementType CXX_NAMES = new DuneElementType("CXX_NAMES");
    public static final IElementType CXX_FLAGS = new DuneElementType("CXX_FLAGS");
    public static final IElementType NO_DYNLINK = new DuneElementType("NO_DYNLINK");
    public static final IElementType PREPROCESS = new DuneElementType("PREPROCESS");
    public static final IElementType JS_OF_OCAML = new DuneElementType("JS_OF_OCAML");
    public static final IElementType VIRTUAL_DEPS = new DuneElementType("VIRTUAL_DEPS");
    public static final IElementType OCAMLC_FLAGS = new DuneElementType("OCAMLC_FLAGS");
    public static final IElementType LIBRARY_FLAGS = new DuneElementType("LIBRARY_FLAGS");
    public static final IElementType OCAMLOPT_FLAGS = new DuneElementType("OCAMLOPT_FLAGS");
    public static final IElementType C_LIBRARY_FLAGS = new DuneElementType("C_LIBRARY_FLAGS");
    public static final IElementType INSTALL_C_HEADERS = new DuneElementType("INSTALL_C_HEADERS");
    public static final IElementType PREPROCESSOR_DEPS = new DuneElementType("PREPROCESSOR_DEPS");
    public static final IElementType PPX_RUNTIME_LIBRARIES = new DuneElementType("PPX_RUNTIME_LIBRARIES");
    public static final IElementType SELF_BUILD_STUBS_ARCHIVE = new DuneElementType("SELF_BUILD_STUBS_ARCHIVE");
    public static final IElementType ALLOW_OVERLAPPING_DEPENDENCIES = new DuneElementType("ALLOW_OVERLAPPING_DEPENDENCIES");
    public static final IElementType MODULES_WITHOUT_IMPLEMENTATION = new DuneElementType("MODULES_WITHOUT_IMPLEMENTATION");
    public static final IElementType COMMENT = new DuneElementType("COMMENT");
    public static final IElementType EXE = new DuneElementType("EXE");
    public static final IElementType BEST = new DuneElementType("BEST");
    public static final IElementType BYTE = new DuneElementType("BYTE");
    public static final IElementType NAMES = new DuneElementType("NAMES");
    public static final IElementType OBJECT = new DuneElementType("OBJECT");
    public static final IElementType NATIVE = new DuneElementType("NATIVE");
    public static final IElementType LINK_FLAGS = new DuneElementType("LINK_FLAGS");
    public static final IElementType EXECUTABLES = new DuneElementType("EXECUTABLES");
    public static final IElementType PUBLIC_NAMES = new DuneElementType("PUBLIC_NAMES");
    public static final IElementType SHARED_OBJECT = new DuneElementType("SHARED_OBJECT");
    public static final IElementType DEPS = new DuneElementType("DEPS");
    public static final IElementType RULE = new DuneElementType("RULE");
    public static final IElementType MODE = new DuneElementType("MODE");
    public static final IElementType LOCKS = new DuneElementType("LOCKS");
    public static final IElementType ACTION = new DuneElementType("ACTION");
    public static final IElementType TARGETS = new DuneElementType("TARGETS");
    public static final IElementType PROMOTE = new DuneElementType("PROMOTE");
    public static final IElementType STANDARD = new DuneElementType("STANDARD");
    public static final IElementType FALLBACK = new DuneElementType("FALLBACK");
    public static final IElementType PROMOTE_UNTIL_CLEAN = new DuneElementType("PROMOTE_UNTIL_CLEAN");
    public static final IElementType ALIAS = new DuneElementType("ALIAS");
    public static final IElementType MENHIR = new DuneElementType("MENHIR");
    public static final IElementType INSTALL = new DuneElementType("INSTALL");
    public static final IElementType INCLUDE = new DuneElementType("INCLUDE");
    public static final IElementType OCAML_LEX = new DuneElementType("OCAML_LEX");
    public static final IElementType OCAML_YACC = new DuneElementType("OCAML_YACC");
    public static final IElementType COPY_FILES = new DuneElementType("COPY_FILES");
    public static final IElementType COPY_FILES_SHARP = new DuneElementType("COPY_FILES_SHARP");
}
