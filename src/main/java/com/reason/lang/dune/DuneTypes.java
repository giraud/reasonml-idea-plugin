package com.reason.lang.dune;

import com.reason.lang.core.type.ORCompositeElementType;
import com.reason.lang.core.type.ORTokenElementType;

public class DuneTypes {

    public static final DuneTypes INSTANCE = new DuneTypes();

    private DuneTypes() {
    }

    // Composite element types

    public final ORCompositeElementType SEXPR = new ORCompositeElementType("s-expr", DuneLanguage.INSTANCE);

    // Token element types

    public final ORTokenElementType LPAREN = new ORTokenElementType("LPAREN", DuneLanguage.INSTANCE);
    public final ORTokenElementType RPAREN = new ORTokenElementType("RPAREN", DuneLanguage.INSTANCE);
    public final ORTokenElementType STRING = new ORTokenElementType("String", DuneLanguage.INSTANCE);
    public final ORTokenElementType ATOM = new ORTokenElementType("ATOM", DuneLanguage.INSTANCE);

    public final ORTokenElementType VERSION = new ORTokenElementType("VERSION", DuneLanguage.INSTANCE);

    public final ORTokenElementType LIBRARY = new ORTokenElementType("LIBRARY", DuneLanguage.INSTANCE);
    public final ORTokenElementType NAME = new ORTokenElementType("NAME", DuneLanguage.INSTANCE);
    public final ORTokenElementType PUBLIC_NAME = new ORTokenElementType("PUBLIC_NAME", DuneLanguage.INSTANCE);
    public final ORTokenElementType SYNOPSIS = new ORTokenElementType("SYNOPSIS", DuneLanguage.INSTANCE);

    public final ORTokenElementType EXECUTABLE = new ORTokenElementType("EXECUTABLE", DuneLanguage.INSTANCE);
    public final ORTokenElementType KIND = new ORTokenElementType("KIND", DuneLanguage.INSTANCE);
    public final ORTokenElementType MODES = new ORTokenElementType("MODES", DuneLanguage.INSTANCE);
    public final ORTokenElementType FLAGS = new ORTokenElementType("FLAGS", DuneLanguage.INSTANCE);
    public final ORTokenElementType C_NAMES = new ORTokenElementType("C_NAMES", DuneLanguage.INSTANCE);
    public final ORTokenElementType C_FLAGS = new ORTokenElementType("C_FLAGS", DuneLanguage.INSTANCE);
    public final ORTokenElementType MODULES = new ORTokenElementType("MODULES", DuneLanguage.INSTANCE);
    public final ORTokenElementType WRAPPED = new ORTokenElementType("WRAPPED", DuneLanguage.INSTANCE);
    public final ORTokenElementType OPTIONAL = new ORTokenElementType("OPTIONAL", DuneLanguage.INSTANCE);
    public final ORTokenElementType LIBRARIES = new ORTokenElementType("LIBRARIES", DuneLanguage.INSTANCE);
    public final ORTokenElementType CXX_NAMES = new ORTokenElementType("CXX_NAMES", DuneLanguage.INSTANCE);
    public final ORTokenElementType CXX_FLAGS = new ORTokenElementType("CXX_FLAGS", DuneLanguage.INSTANCE);
    public final ORTokenElementType NO_DYNLINK = new ORTokenElementType("NO_DYNLINK", DuneLanguage.INSTANCE);
    public final ORTokenElementType PREPROCESS = new ORTokenElementType("PREPROCESS", DuneLanguage.INSTANCE);
    public final ORTokenElementType JS_OF_OCAML = new ORTokenElementType("JS_OF_OCAML", DuneLanguage.INSTANCE);
    public final ORTokenElementType VIRTUAL_DEPS = new ORTokenElementType("VIRTUAL_DEPS", DuneLanguage.INSTANCE);
    public final ORTokenElementType OCAMLC_FLAGS = new ORTokenElementType("OCAMLC_FLAGS", DuneLanguage.INSTANCE);
    public final ORTokenElementType LIBRARY_FLAGS = new ORTokenElementType("LIBRARY_FLAGS", DuneLanguage.INSTANCE);
    public final ORTokenElementType OCAMLOPT_FLAGS = new ORTokenElementType("OCAMLOPT_FLAGS", DuneLanguage.INSTANCE);
    public final ORTokenElementType C_LIBRARY_FLAGS = new ORTokenElementType("C_LIBRARY_FLAGS", DuneLanguage.INSTANCE);
    public final ORTokenElementType INSTALL_C_HEADERS = new ORTokenElementType("INSTALL_C_HEADERS", DuneLanguage.INSTANCE);
    public final ORTokenElementType PREPROCESSOR_DEPS = new ORTokenElementType("PREPROCESSOR_DEPS", DuneLanguage.INSTANCE);
    public final ORTokenElementType PPX_RUNTIME_LIBRARIES = new ORTokenElementType("PPX_RUNTIME_LIBRARIES", DuneLanguage.INSTANCE);
    public final ORTokenElementType SELF_BUILD_STUBS_ARCHIVE = new ORTokenElementType("SELF_BUILD_STUBS_ARCHIVE", DuneLanguage.INSTANCE);
    public final ORTokenElementType ALLOW_OVERLAPPING_DEPENDENCIES = new ORTokenElementType("ALLOW_OVERLAPPING_DEPENDENCIES", DuneLanguage.INSTANCE);
    public final ORTokenElementType MODULES_WITHOUT_IMPLEMENTATION = new ORTokenElementType("MODULES_WITHOUT_IMPLEMENTATION", DuneLanguage.INSTANCE);
    public final ORTokenElementType COMMENT = new ORTokenElementType("COMMENT", DuneLanguage.INSTANCE);
    public final ORTokenElementType EXE = new ORTokenElementType("EXE", DuneLanguage.INSTANCE);
    public final ORTokenElementType BEST = new ORTokenElementType("BEST", DuneLanguage.INSTANCE);
    public final ORTokenElementType BYTE = new ORTokenElementType("BYTE", DuneLanguage.INSTANCE);
    public final ORTokenElementType NAMES = new ORTokenElementType("NAMES", DuneLanguage.INSTANCE);
    public final ORTokenElementType OBJECT = new ORTokenElementType("OBJECT", DuneLanguage.INSTANCE);
    public final ORTokenElementType NATIVE = new ORTokenElementType("NATIVE", DuneLanguage.INSTANCE);
    public final ORTokenElementType LINK_FLAGS = new ORTokenElementType("LINK_FLAGS", DuneLanguage.INSTANCE);
    public final ORTokenElementType EXECUTABLES = new ORTokenElementType("EXECUTABLES", DuneLanguage.INSTANCE);
    public final ORTokenElementType PUBLIC_NAMES = new ORTokenElementType("PUBLIC_NAMES", DuneLanguage.INSTANCE);
    public final ORTokenElementType SHARED_OBJECT = new ORTokenElementType("SHARED_OBJECT", DuneLanguage.INSTANCE);
    public final ORTokenElementType DEPS = new ORTokenElementType("DEPS", DuneLanguage.INSTANCE);
    public final ORTokenElementType RULE = new ORTokenElementType("RULE", DuneLanguage.INSTANCE);
    public final ORTokenElementType MODE = new ORTokenElementType("MODE", DuneLanguage.INSTANCE);
    public final ORTokenElementType LOCKS = new ORTokenElementType("LOCKS", DuneLanguage.INSTANCE);
    public final ORTokenElementType ACTION = new ORTokenElementType("ACTION", DuneLanguage.INSTANCE);
    public final ORTokenElementType TARGETS = new ORTokenElementType("TARGETS", DuneLanguage.INSTANCE);
    public final ORTokenElementType PROMOTE = new ORTokenElementType("PROMOTE", DuneLanguage.INSTANCE);
    public final ORTokenElementType STANDARD = new ORTokenElementType("STANDARD", DuneLanguage.INSTANCE);
    public final ORTokenElementType FALLBACK = new ORTokenElementType("FALLBACK", DuneLanguage.INSTANCE);
    public final ORTokenElementType PROMOTE_UNTIL_CLEAN = new ORTokenElementType("PROMOTE_UNTIL_CLEAN", DuneLanguage.INSTANCE);
    public final ORTokenElementType ALIAS = new ORTokenElementType("ALIAS", DuneLanguage.INSTANCE);
    public final ORTokenElementType MENHIR = new ORTokenElementType("MENHIR", DuneLanguage.INSTANCE);
    public final ORTokenElementType INSTALL = new ORTokenElementType("INSTALL", DuneLanguage.INSTANCE);
    public final ORTokenElementType INCLUDE = new ORTokenElementType("INCLUDE", DuneLanguage.INSTANCE);
    public final ORTokenElementType OCAML_LEX = new ORTokenElementType("OCAML_LEX", DuneLanguage.INSTANCE);
    public final ORTokenElementType OCAML_YACC = new ORTokenElementType("OCAML_YACC", DuneLanguage.INSTANCE);
    public final ORTokenElementType COPY_FILES = new ORTokenElementType("COPY_FILES", DuneLanguage.INSTANCE);
    public final ORTokenElementType COPY_FILES_SHARP = new ORTokenElementType("COPY_FILES_SHARP", DuneLanguage.INSTANCE);
}
