package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.type.*;
import com.reason.lang.rescript.*;

public interface ResStubBasedElementTypes {
    IStubElementType<PsiModuleStub, RPsiModule> C_FUNCTOR_DECLARATION = new PsiFunctorModuleStubElementType("C_FUNCTOR_DECLARATION", ResLanguage.INSTANCE);
    IStubElementType<PsiModuleStub, RPsiModule> C_MODULE_DECLARATION = new PsiInnerModuleStubElementType("C_MODULE_DECLARATION", ResLanguage.INSTANCE);

    IStubElementType<RsiClassStub, RPsiClass> C_CLASS_DECLARATION = new RPsiClassStubElementType("C_CLASS_DECLARATION", ResLanguage.INSTANCE);
    IStubElementType<RsiClassMethodStub, RPsiClassMethod> C_CLASS_METHOD = new RPsiClassMethodStubElementType("C_CLASS_METHOD", ResLanguage.INSTANCE);

    IStubElementType<PsiExceptionStub, RPsiException> C_EXCEPTION_DECLARATION = new PsiExceptionStubElementType("C_EXCEPTION_DECLARATION", ResLanguage.INSTANCE);
    IStubElementType<PsiTypeStub, RPsiType> C_TYPE_DECLARATION = new PsiTypeStubElementType("C_TYPE_DECLARATION", ResLanguage.INSTANCE);
    IStubElementType<PsiExternalStub, RPsiExternal> C_EXTERNAL_DECLARATION = new PsiExternalStubElementType("C_EXTERNAL_DECLARATION", ResLanguage.INSTANCE);
    IStubElementType<PsiLetStub, RPsiLet> C_LET_DECLARATION = new PsiLetStubElementType("C_LET_DECLARATION", ResLanguage.INSTANCE);
    IStubElementType<RsiRecordFieldStub, RPsiRecordField> C_RECORD_FIELD = new RPsiRecordFieldStubElementType("C_RECORD_FIELD", ResLanguage.INSTANCE);
    IStubElementType<PsiObjectFieldStub, RPsiObjectField> C_OBJECT_FIELD = new PsiObjectFieldStubElementType("C_OBJECT_FIELD", ResLanguage.INSTANCE);
    IStubElementType<PsiValStub, RPsiVal> C_VAL_DECLARATION = new PsiValStubElementType("C_VAL_DECLARATION", ResLanguage.INSTANCE);
    IStubElementType<PsiVariantDeclarationStub, RPsiVariantDeclaration> C_VARIANT_DECLARATION = new PsiVariantStubElementType("C_VARIANT_DECLARATION", ResLanguage.INSTANCE);

    IStubElementType<PsiIncludeStub, RPsiInclude> C_INCLUDE = new PsiIncludeStubElementType("C_INCLUDE", ResLanguage.INSTANCE);
    IStubElementType<PsiOpenStub, RPsiOpen> C_OPEN = new PsiOpenStubElementType("C_OPEN", ResLanguage.INSTANCE);

    IStubElementType<PsiParameterDeclarationStub, RPsiParameterDeclaration> C_PARAM_DECLARATION = new PsiParameterDeclarationStubElementType("C_PARAM_DECLARATION", ResLanguage.INSTANCE);
}
