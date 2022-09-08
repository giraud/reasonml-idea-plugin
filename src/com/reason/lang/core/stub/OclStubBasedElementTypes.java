package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.type.*;
import com.reason.lang.ocaml.*;

public interface OclStubBasedElementTypes {
    IStubElementType<PsiOpenStub, PsiOpen> C_OPEN = new PsiOpenStubElementType("C_OPEN", OclLanguage.INSTANCE);
    IStubElementType<PsiIncludeStub, PsiInclude> C_INCLUDE = new PsiIncludeStubElementType("C_INCLUDE", OclLanguage.INSTANCE);

    IStubElementType<PsiModuleStub, PsiModule> C_MODULE_DECLARATION = new PsiInnerModuleStubElementType("C_MODULE_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiModuleStub, PsiModule> C_FUNCTOR_DECLARATION = new PsiFunctorModuleStubElementType("C_FUNCTOR_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiModuleStub, PsiModule> C_FAKE_MODULE = new PsiFakeModuleStubElementType("C_FAKE_MODULE", OclLanguage.INSTANCE);

    IStubElementType<PsiKlassStub, PsiKlass> C_CLASS_DECLARATION = new PsiKlassStubElementType("C_CLASS_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiExceptionStub, PsiException> C_EXCEPTION_DECLARATION = new PsiExceptionStubElementType("C_EXCEPTION_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiTypeStub, PsiType> C_TYPE_DECLARATION = new PsiTypeStubElementType("C_TYPE_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiExternalStub, PsiExternal> C_EXTERNAL_DECLARATION = new PsiExternalStubElementType("C_EXTERNAL_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiLetStub, PsiLet> C_LET_DECLARATION = new PsiLetStubElementType("C_LET_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiValStub, PsiVal> C_VAL_DECLARATION = new PsiValStubElementType("C_VAL_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiVariantDeclarationStub, PsiVariantDeclaration> C_VARIANT_DECLARATION = new PsiVariantStubElementType("C_VARIANT_DECLARATION", OclLanguage.INSTANCE);
    IStubElementType<PsiParameterDeclarationStub, PsiParameterDeclaration> C_PARAM_DECLARATION = new PsiParameterDeclarationStubElementType("C_PARAM_DECLARATION", OclLanguage.INSTANCE);

    IStubElementType<PsiRecordFieldStub, PsiRecordField> C_RECORD_FIELD = new PsiRecordFieldStubElementType("C_RECORD_FIELD", OclLanguage.INSTANCE);
    IStubElementType<PsiObjectFieldStub, PsiObjectField> C_OBJECT_FIELD = new PsiObjectFieldStubElementType("C_OBJECT_FIELD", OclLanguage.INSTANCE);
}
