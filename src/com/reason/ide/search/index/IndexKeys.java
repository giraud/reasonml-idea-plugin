package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.intellij.util.indexing.*;
import com.reason.ide.search.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

public class IndexKeys {

    public static final ID<String, FileModuleData> FILE_MODULE = ID.create("reason.index.fileModule");

    public static final StubIndexKey<String, PsiFakeModule> MODULES_TOP_LEVEL = StubIndexKey.createIndexKey("reason.module.toplevel");
    public static final StubIndexKey<String, PsiModule> MODULES_COMP = StubIndexKey.createIndexKey("reason.module.comp");
    public static final StubIndexKey<Integer, PsiModule> MODULES_COMP_FQN = StubIndexKey.createIndexKey("reason.module.comp.fqn");
    public static final StubIndexKey<String, PsiModule> MODULES = StubIndexKey.createIndexKey("reason.module");
    public static final StubIndexKey<Integer, PsiModule> MODULES_FQN = StubIndexKey.createIndexKey("reason.module.fqn");
    public static final StubIndexKey<String, PsiVariantDeclaration> VARIANTS = StubIndexKey.createIndexKey("reason.variant");
    public static final StubIndexKey<Integer, PsiVariantDeclaration> VARIANTS_FQN = StubIndexKey.createIndexKey("reason.variant.fqn");
    public static final StubIndexKey<String, PsiLet> LETS = StubIndexKey.createIndexKey("reason.let");
    public static final StubIndexKey<Integer, PsiLet> LETS_FQN = StubIndexKey.createIndexKey("reason.let.fqn");
    public static final StubIndexKey<String, PsiVal> VALS = StubIndexKey.createIndexKey("reason.val");
    public static final StubIndexKey<Integer, PsiVal> VALS_FQN = StubIndexKey.createIndexKey("reason.val.fqn");
    public static final StubIndexKey<String, PsiExternal> EXTERNALS = StubIndexKey.createIndexKey("reason.external");
    public static final StubIndexKey<String, PsiType> TYPES = StubIndexKey.createIndexKey("reason.type");
    public static final StubIndexKey<Integer, PsiType> TYPES_FQN = StubIndexKey.createIndexKey("reason.type.fqn");
    public static final StubIndexKey<String, PsiRecordField> RECORD_FIELDS = StubIndexKey.createIndexKey("reason.record_field");
    public static final StubIndexKey<String, PsiException> EXCEPTIONS = StubIndexKey.createIndexKey("reason.exception");
    public static final StubIndexKey<Integer, PsiException> EXCEPTIONS_FQN = StubIndexKey.createIndexKey("reason.exception.fqn");
    public static final StubIndexKey<String, PsiParameter> PARAMETERS = StubIndexKey.createIndexKey("reason.parameter");
    public static final StubIndexKey<Integer, PsiParameter> PARAMETERS_FQN = StubIndexKey.createIndexKey("reason.parameter.fqn");

    private IndexKeys() {
    }
}
