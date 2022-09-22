package com.reason.lang.core.stub;

import com.intellij.psi.stubs.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.stub.type.*;
import com.reason.lang.reason.*;

public interface RmlStubBasedElementTypes {
    IStubElementType<PsiModuleStub, PsiModule> C_FAKE_MODULE = new PsiFakeModuleStubElementType("C_FAKE_MODULE", RmlLanguage.INSTANCE);
    IStubElementType<PsiModuleStub, PsiModule> C_FUNCTOR_DECLARATION = new PsiFunctorModuleStubElementType("C_FUNCTOR_DECLARATION", RmlLanguage.INSTANCE);
    IStubElementType<PsiModuleStub, PsiModule> C_MODULE_DECLARATION = new PsiInnerModuleStubElementType("C_MODULE_DECLARATION", RmlLanguage.INSTANCE);

    IStubElementType<RsiClassStub, RPsiClass> C_CLASS_DECLARATION = new RsiClassStubElementType("C_CLASS_DECLARATION", RmlLanguage.INSTANCE);
    IStubElementType<RsiClassMethodStub, RPsiClassMethod> C_CLASS_METHOD = new RsiClassMethodStubElementType("C_CLASS_METHOD", RmlLanguage.INSTANCE);

    IStubElementType<PsiExceptionStub, PsiException> C_EXCEPTION_DECLARATION = new PsiExceptionStubElementType("C_EXCEPTION_DECLARATION", RmlLanguage.INSTANCE);
    IStubElementType<PsiTypeStub, PsiType> C_TYPE_DECLARATION = new PsiTypeStubElementType("C_TYPE_DECLARATION", RmlLanguage.INSTANCE);
    IStubElementType<PsiExternalStub, PsiExternal> C_EXTERNAL_DECLARATION = new PsiExternalStubElementType("C_EXTERNAL_DECLARATION", RmlLanguage.INSTANCE);
    IStubElementType<PsiLetStub, PsiLet> C_LET_DECLARATION = new PsiLetStubElementType("C_LET_DECLARATION", RmlLanguage.INSTANCE);
    IStubElementType<PsiValStub, PsiVal> C_VAL_DECLARATION = new PsiValStubElementType("C_VAL_DECLARATION", RmlLanguage.INSTANCE);
    IStubElementType<RsiRecordFieldStub, RPsiRecordField> C_RECORD_FIELD = new RsiRecordFieldStubElementType("C_RECORD_FIELD", RmlLanguage.INSTANCE);
    IStubElementType<PsiObjectFieldStub, PsiObjectField> C_OBJECT_FIELD = new PsiObjectFieldStubElementType("C_OBJECT_FIELD", RmlLanguage.INSTANCE);
    IStubElementType<PsiVariantDeclarationStub, PsiVariantDeclaration> C_VARIANT_DECLARATION = new PsiVariantStubElementType("C_VARIANT_DECLARATION", RmlLanguage.INSTANCE);

    IStubElementType<PsiIncludeStub, PsiInclude> C_INCLUDE = new PsiIncludeStubElementType("C_INCLUDE", RmlLanguage.INSTANCE);
    IStubElementType<PsiOpenStub, PsiOpen> C_OPEN = new PsiOpenStubElementType("C_OPEN", RmlLanguage.INSTANCE);

    IStubElementType<PsiParameterDeclarationStub, PsiParameterDeclaration> C_PARAM_DECLARATION = new PsiParameterDeclarationStubElementType("C_PARAM_DECLARATION", RmlLanguage.INSTANCE);
}
