package com.reason.ide.search.index;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

public class IndexKeys {
    @FunctionalInterface
    public
    interface ProcessElement<T> {
        void process(@NotNull T element);
    }

    public static final StubIndexKey<String, RPsiFakeModule> MODULES_TOP_LEVEL = StubIndexKey.createIndexKey("reason.module.toplevel");
    public static final StubIndexKey<String, RPsiModule> MODULES_COMP = StubIndexKey.createIndexKey("reason.module.comp");
    public static final StubIndexKey<Integer, RPsiModule> MODULES_COMP_FQN = StubIndexKey.createIndexKey("reason.module.comp.fqn");
    public static final StubIndexKey<String, RPsiModule> MODULES = StubIndexKey.createIndexKey("reason.module");
    public static final StubIndexKey<String, RPsiModule> MODULES_ALIASED = StubIndexKey.createIndexKey("reason.module.aliased");
    public static final StubIndexKey<String, RPsiModule> MODULES_ALIASES = StubIndexKey.createIndexKey("reason.module.aliases");
    public static final StubIndexKey<Integer, RPsiModule> MODULES_FQN = StubIndexKey.createIndexKey("reason.module.fqn");
    public static final StubIndexKey<Integer, RPsiClass> CLASSES_FQN = StubIndexKey.createIndexKey("reason.class.fqn");
    public static final StubIndexKey<Integer, RPsiClassMethod> CLASS_METHODS_FQN = StubIndexKey.createIndexKey("reason.method.fqn");
    public static final StubIndexKey<String, RPsiVariantDeclaration> VARIANTS = StubIndexKey.createIndexKey("reason.variant");
    public static final StubIndexKey<Integer, RPsiVariantDeclaration> VARIANTS_FQN = StubIndexKey.createIndexKey("reason.variant.fqn");
    public static final StubIndexKey<String, RPsiLet> LETS = StubIndexKey.createIndexKey("reason.let");
    public static final StubIndexKey<Integer, RPsiLet> LETS_FQN = StubIndexKey.createIndexKey("reason.let.fqn");
    public static final StubIndexKey<String, RPsiVal> VALS = StubIndexKey.createIndexKey("reason.val");
    public static final StubIndexKey<Integer, RPsiVal> VALS_FQN = StubIndexKey.createIndexKey("reason.val.fqn");
    public static final StubIndexKey<String, RPsiExternal> EXTERNALS = StubIndexKey.createIndexKey("reason.external");
    public static final StubIndexKey<Integer, RPsiExternal> EXTERNALS_FQN = StubIndexKey.createIndexKey("reason.external.fqn");
    public static final StubIndexKey<String, RPsiType> TYPES = StubIndexKey.createIndexKey("reason.type");
    public static final StubIndexKey<Integer, RPsiType> TYPES_FQN = StubIndexKey.createIndexKey("reason.type.fqn");
    public static final StubIndexKey<String, RPsiObjectField> OBJECT_FIELDS = StubIndexKey.createIndexKey("reason.object_field");
    public static final StubIndexKey<String, RPsiRecordField> RECORD_FIELDS = StubIndexKey.createIndexKey("reason.record_field");
    public static final StubIndexKey<String, RPsiException> EXCEPTIONS = StubIndexKey.createIndexKey("reason.exception");
    public static final StubIndexKey<Integer, RPsiException> EXCEPTIONS_FQN = StubIndexKey.createIndexKey("reason.exception.fqn");
    public static final StubIndexKey<String, RPsiParameterDeclaration> PARAMETERS = StubIndexKey.createIndexKey("reason.parameter");
    public static final StubIndexKey<Integer, RPsiParameterDeclaration> PARAMETERS_FQN = StubIndexKey.createIndexKey("reason.parameter.fqn");
    public static final StubIndexKey<Integer, RPsiInnerModule> FUNCTORS_CALL_FQN = StubIndexKey.createIndexKey("reason.functorcall.fqn");
    public static final StubIndexKey<String, RPsiInclude> INCLUDES = StubIndexKey.createIndexKey("reason.include");
    public static final StubIndexKey<String, RPsiOpen> OPENS = StubIndexKey.createIndexKey("reason.open");

    private IndexKeys() {
    }
}
