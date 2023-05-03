package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.type.*;
import com.reason.lang.ocaml.*;

public interface OclStubBasedElementTypes {
    IStubElementType<PsiOpenStub, RPsiOpen> C_OPEN = new PsiOpenStubElementType("C_OPEN", OclLanguage.INSTANCE);
    IStubElementType<PsiIncludeStub, RPsiInclude> C_INCLUDE = new PsiIncludeStubElementType("C_INCLUDE", OclLanguage.INSTANCE);

    IStubElementType<PsiModuleStub, RPsiModule> C_MODULE_DECLARATION = new PsiInnerModuleStubElementType("C_MODULE_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiModuleStub, RPsiModule> C_FUNCTOR_DECLARATION = new PsiFunctorModuleStubElementType("C_FUNCTOR_DECLARATION", OclLanguage.INSTANCE);

    IStubElementType<RsiClassStub, RPsiClass> C_CLASS_DECLARATION = new RPsiClassStubElementType("C_CLASS_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<RsiClassMethodStub, RPsiClassMethod> C_CLASS_METHOD = new RPsiClassMethodStubElementType("C_CLASS_METHOD", OclLanguage.INSTANCE);

    IStubElementType<PsiExceptionStub, RPsiException> C_EXCEPTION_DECLARATION = new PsiExceptionStubElementType("C_EXCEPTION_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiTypeStub, RPsiType> C_TYPE_DECLARATION = new PsiTypeStubElementType("C_TYPE_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiExternalStub, RPsiExternal> C_EXTERNAL_DECLARATION = new PsiExternalStubElementType("C_EXTERNAL_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiLetStub, RPsiLet> C_LET_DECLARATION = new PsiLetStubElementType("C_LET_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiValStub, RPsiVal> C_VAL_DECLARATION = new PsiValStubElementType("C_VAL_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiVariantDeclarationStub, RPsiVariantDeclaration> C_VARIANT_DECLARATION = new PsiVariantStubElementType("C_VARIANT_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiParameterDeclarationStub, RPsiParameterDeclaration> C_PARAM_DECLARATION = new PsiParameterDeclarationStubElementType("C_PARAM_DECLARATION", OclLanguage.INSTANCE);

    IStubElementType<RsiRecordFieldStub, RPsiRecordField> C_RECORD_FIELD = new RPsiRecordFieldStubElementType("C_RECORD_FIELD", OclLanguage.INSTANCE);
    IStubElementType<PsiObjectFieldStub, RPsiObjectField> C_OBJECT_FIELD = new PsiObjectFieldStubElementType("C_OBJECT_FIELD", OclLanguage.INSTANCE);
}
