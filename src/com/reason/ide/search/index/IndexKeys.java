package com.reason.ide.search.index;

import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.util.indexing.ID;
import com.reason.ide.search.FileModuleData;
import com.reason.lang.core.psi.PsiException;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.psi.PsiVariantDeclaration;

public class IndexKeys {

    public static final ID<String, FileModuleData> FILE_MODULE = ID.create("reason.index.fileModule");

    public static final StubIndexKey<String, PsiModule> MODULES = StubIndexKey.createIndexKey("reason.module");
    public static final StubIndexKey<String, PsiModule> MODULES_COMP = StubIndexKey.createIndexKey("reason.module.comp");
    public static final StubIndexKey<Integer, PsiModule> MODULES_FQN = StubIndexKey.createIndexKey("reason.module.fqn");
    public static final StubIndexKey<String, PsiVariantDeclaration> VARIANTS = StubIndexKey.createIndexKey("reason.variant");
    public static final StubIndexKey<Integer, PsiVariantDeclaration> VARIANTS_FQN = StubIndexKey.createIndexKey("reason.variant.fqn");
    public static final StubIndexKey<String, PsiLet> LETS = StubIndexKey.createIndexKey("reason.let");
    public static final StubIndexKey<Integer, PsiLet> LETS_FQN = StubIndexKey.createIndexKey("reason.let.fqn");
    public static final StubIndexKey<String, PsiVal> VALS = StubIndexKey.createIndexKey("reason.val");
    public static final StubIndexKey<Integer, PsiVal> VALS_FQN = StubIndexKey.createIndexKey("reason.val.fqn");
    public static final StubIndexKey<String, PsiExternal> EXTERNALS = StubIndexKey.createIndexKey("reason.external");
    public static final StubIndexKey<String, PsiType> TYPES = StubIndexKey.createIndexKey("reason.type");
    public static final StubIndexKey<String, PsiRecordField> RECORD_FIELDS = StubIndexKey.createIndexKey("reason.record_field");
    public static final StubIndexKey<String, PsiParameter> PARAMETERS = StubIndexKey.createIndexKey("reason.parameter");
    public static final StubIndexKey<Integer, PsiParameter> PARAMETERS_FQN = StubIndexKey.createIndexKey("reason.parameter.fqn");
    public static final StubIndexKey<String, PsiException> EXCEPTIONS = StubIndexKey.createIndexKey("reason.exception");
    public static final StubIndexKey<Integer, PsiException> EXCEPTIONS_FQN = StubIndexKey.createIndexKey("reason.exception.fqn");

    private IndexKeys() {
    }
}
