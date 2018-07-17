package com.reason.lang.dune;

import com.reason.lang.core.psi.type.MlCompositeElementType;
import com.reason.lang.core.psi.type.MlTokenElementType;

public class DuneTypes {

    // Composite element types

    public static final MlCompositeElementType SEXPR = new MlCompositeElementType("s-expr", DuneLanguage.INSTANCE);

    // Token element types

    public static final MlTokenElementType LPAREN = new MlTokenElementType("LPAREN", DuneLanguage.INSTANCE);
    public static final MlTokenElementType RPAREN = new MlTokenElementType("RPAREN", DuneLanguage.INSTANCE);
    public static final MlTokenElementType STRING = new MlTokenElementType("String", DuneLanguage.INSTANCE);
    public static final MlTokenElementType ATOM = new MlTokenElementType("ATOM", DuneLanguage.INSTANCE);

    public static final MlTokenElementType VERSION = new MlTokenElementType("VERSION", DuneLanguage.INSTANCE);

    public static final MlTokenElementType LIBRARY = new MlTokenElementType("LIBRARY", DuneLanguage.INSTANCE);
    public static final MlTokenElementType NAME = new MlTokenElementType("NAME", DuneLanguage.INSTANCE);
    public static final MlTokenElementType PUBLIC_NAME = new MlTokenElementType("PUBLIC_NAME", DuneLanguage.INSTANCE);
    public static final MlTokenElementType SYNOPSIS = new MlTokenElementType("SYNOPSIS", DuneLanguage.INSTANCE);

    public static final MlTokenElementType EXECUTABLE = new MlTokenElementType("EXECUTABLE", DuneLanguage.INSTANCE);
    public static final MlTokenElementType KIND = new MlTokenElementType("KIND", DuneLanguage.INSTANCE);
    public static final MlTokenElementType MODES = new MlTokenElementType("MODES", DuneLanguage.INSTANCE);
    public static final MlTokenElementType FLAGS = new MlTokenElementType("FLAGS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType C_NAMES = new MlTokenElementType("C_NAMES", DuneLanguage.INSTANCE);
    public static final MlTokenElementType C_FLAGS = new MlTokenElementType("C_FLAGS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType MODULES = new MlTokenElementType("MODULES", DuneLanguage.INSTANCE);
    public static final MlTokenElementType WRAPPED = new MlTokenElementType("WRAPPED", DuneLanguage.INSTANCE);
    public static final MlTokenElementType OPTIONAL = new MlTokenElementType("OPTIONAL", DuneLanguage.INSTANCE);
    public static final MlTokenElementType LIBRARIES = new MlTokenElementType("LIBRARIES", DuneLanguage.INSTANCE);
    public static final MlTokenElementType CXX_NAMES = new MlTokenElementType("CXX_NAMES", DuneLanguage.INSTANCE);
    public static final MlTokenElementType CXX_FLAGS = new MlTokenElementType("CXX_FLAGS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType NO_DYNLINK = new MlTokenElementType("NO_DYNLINK", DuneLanguage.INSTANCE);
    public static final MlTokenElementType PREPROCESS = new MlTokenElementType("PREPROCESS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType JS_OF_OCAML = new MlTokenElementType("JS_OF_OCAML", DuneLanguage.INSTANCE);
    public static final MlTokenElementType VIRTUAL_DEPS = new MlTokenElementType("VIRTUAL_DEPS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType OCAMLC_FLAGS = new MlTokenElementType("OCAMLC_FLAGS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType LIBRARY_FLAGS = new MlTokenElementType("LIBRARY_FLAGS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType OCAMLOPT_FLAGS = new MlTokenElementType("OCAMLOPT_FLAGS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType C_LIBRARY_FLAGS = new MlTokenElementType("C_LIBRARY_FLAGS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType INSTALL_C_HEADERS = new MlTokenElementType("INSTALL_C_HEADERS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType PREPROCESSOR_DEPS = new MlTokenElementType("PREPROCESSOR_DEPS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType PPX_RUNTIME_LIBRARIES = new MlTokenElementType("PPX_RUNTIME_LIBRARIES", DuneLanguage.INSTANCE);
    public static final MlTokenElementType SELF_BUILD_STUBS_ARCHIVE = new MlTokenElementType("SELF_BUILD_STUBS_ARCHIVE", DuneLanguage.INSTANCE);
    public static final MlTokenElementType ALLOW_OVERLAPPING_DEPENDENCIES = new MlTokenElementType("ALLOW_OVERLAPPING_DEPENDENCIES", DuneLanguage.INSTANCE);
    public static final MlTokenElementType MODULES_WITHOUT_IMPLEMENTATION = new MlTokenElementType("MODULES_WITHOUT_IMPLEMENTATION", DuneLanguage.INSTANCE);
    public static final MlTokenElementType COMMENT = new MlTokenElementType("COMMENT", DuneLanguage.INSTANCE);
    public static final MlTokenElementType EXE = new MlTokenElementType("EXE", DuneLanguage.INSTANCE);
    public static final MlTokenElementType BEST = new MlTokenElementType("BEST", DuneLanguage.INSTANCE);
    public static final MlTokenElementType BYTE = new MlTokenElementType("BYTE", DuneLanguage.INSTANCE);
    public static final MlTokenElementType NAMES = new MlTokenElementType("NAMES", DuneLanguage.INSTANCE);
    public static final MlTokenElementType OBJECT = new MlTokenElementType("OBJECT", DuneLanguage.INSTANCE);
    public static final MlTokenElementType NATIVE = new MlTokenElementType("NATIVE", DuneLanguage.INSTANCE);
    public static final MlTokenElementType LINK_FLAGS = new MlTokenElementType("LINK_FLAGS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType EXECUTABLES = new MlTokenElementType("EXECUTABLES", DuneLanguage.INSTANCE);
    public static final MlTokenElementType PUBLIC_NAMES = new MlTokenElementType("PUBLIC_NAMES", DuneLanguage.INSTANCE);
    public static final MlTokenElementType SHARED_OBJECT = new MlTokenElementType("SHARED_OBJECT", DuneLanguage.INSTANCE);
    public static final MlTokenElementType DEPS = new MlTokenElementType("DEPS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType RULE = new MlTokenElementType("RULE", DuneLanguage.INSTANCE);
    public static final MlTokenElementType MODE = new MlTokenElementType("MODE", DuneLanguage.INSTANCE);
    public static final MlTokenElementType LOCKS = new MlTokenElementType("LOCKS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType ACTION = new MlTokenElementType("ACTION", DuneLanguage.INSTANCE);
    public static final MlTokenElementType TARGETS = new MlTokenElementType("TARGETS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType PROMOTE = new MlTokenElementType("PROMOTE", DuneLanguage.INSTANCE);
    public static final MlTokenElementType STANDARD = new MlTokenElementType("STANDARD", DuneLanguage.INSTANCE);
    public static final MlTokenElementType FALLBACK = new MlTokenElementType("FALLBACK", DuneLanguage.INSTANCE);
    public static final MlTokenElementType PROMOTE_UNTIL_CLEAN = new MlTokenElementType("PROMOTE_UNTIL_CLEAN", DuneLanguage.INSTANCE);
    public static final MlTokenElementType ALIAS = new MlTokenElementType("ALIAS", DuneLanguage.INSTANCE);
    public static final MlTokenElementType MENHIR = new MlTokenElementType("MENHIR", DuneLanguage.INSTANCE);
    public static final MlTokenElementType INSTALL = new MlTokenElementType("INSTALL", DuneLanguage.INSTANCE);
    public static final MlTokenElementType INCLUDE = new MlTokenElementType("INCLUDE", DuneLanguage.INSTANCE);
    public static final MlTokenElementType OCAML_LEX = new MlTokenElementType("OCAML_LEX", DuneLanguage.INSTANCE);
    public static final MlTokenElementType OCAML_YACC = new MlTokenElementType("OCAML_YACC", DuneLanguage.INSTANCE);
    public static final MlTokenElementType COPY_FILES = new MlTokenElementType("COPY_FILES", DuneLanguage.INSTANCE);
    public static final MlTokenElementType COPY_FILES_SHARP = new MlTokenElementType("COPY_FILES_SHARP", DuneLanguage.INSTANCE);
}
