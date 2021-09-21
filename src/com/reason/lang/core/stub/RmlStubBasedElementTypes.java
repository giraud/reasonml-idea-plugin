package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.type.*;
import com.reason.lang.reason.*;

public interface RmlStubBasedElementTypes {
    IStubElementType<PsiModuleStub, PsiModule> C_FAKE_MODULE = new PsiFakeModuleStubElementType(RmlLanguage.INSTANCE);
    IStubElementType<PsiModuleStub, PsiModule> C_FUNCTOR_DECLARATION = new PsiFunctorModuleStubElementType(RmlLanguage.INSTANCE);
    IStubElementType<PsiModuleStub, PsiModule> C_MODULE_DECLARATION = new PsiInnerModuleStubElementType(RmlLanguage.INSTANCE);

    IStubElementType<PsiKlassStub, PsiKlass> C_CLASS_DECLARATION = new PsiKlassStubElementType(RmlLanguage.INSTANCE);
    IStubElementType<PsiExceptionStub, PsiException> C_EXCEPTION_DECLARATION = new PsiExceptionStubElementType(RmlLanguage.INSTANCE);
    IStubElementType<PsiTypeStub, PsiType> C_TYPE_DECLARATION = new PsiTypeStubElementType(RmlLanguage.INSTANCE);
    IStubElementType<PsiExternalStub, PsiExternal> C_EXTERNAL_DECLARATION = new PsiExternalStubElementType(RmlLanguage.INSTANCE);
    IStubElementType<PsiLetStub, PsiLet> C_LET_DECLARATION = new PsiLetStubElementType(RmlLanguage.INSTANCE);
    IStubElementType<PsiValStub, PsiVal> C_VAL_DECLARATION = new PsiValStubElementType(RmlLanguage.INSTANCE);
    IStubElementType<PsiRecordFieldStub, PsiRecordField> C_RECORD_FIELD = new PsiRecordFieldStubElementType(RmlLanguage.INSTANCE);
    IStubElementType<PsiVariantDeclarationStub, PsiVariantDeclaration> C_VARIANT_DECLARATION = new PsiVariantStubElementType(RmlLanguage.INSTANCE);

    IStubElementType<PsiIncludeStub, PsiInclude> C_INCLUDE = new PsiIncludeStubElementType(RmlLanguage.INSTANCE);
    IStubElementType<PsiOpenStub, PsiOpen> C_OPEN = new PsiOpenStubElementType(RmlLanguage.INSTANCE);

    // ?
    IStubElementType<PsiParameterStub, PsiParameter> C_FUN_PARAM = new PsiParameterStubElementType("C_FUN_PARAM", RmlLanguage.INSTANCE);
    IStubElementType<PsiParameterStub, PsiParameter> C_FUNCTOR_PARAM = new PsiParameterStubElementType("C_FUNCTOR_PARAM", RmlLanguage.INSTANCE);
}
