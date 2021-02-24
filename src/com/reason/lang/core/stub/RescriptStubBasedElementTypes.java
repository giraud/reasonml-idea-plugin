package com.reason.lang.core.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.PsiFakeModule;
import com.reason.lang.core.stub.type.*;
import com.reason.lang.napkin.ResLanguage;

public interface RescriptStubBasedElementTypes {
  IStubElementType<PsiModuleStub, PsiFakeModule> C_FAKE_MODULE =
      new PsiFakeModuleStubElementType(ResLanguage.INSTANCE);
  IStubElementType<PsiExceptionStub, PsiException> C_EXCEPTION_DECLARATION =
      new PsiExceptionStubElementType(ResLanguage.INSTANCE);
  IStubElementType<PsiTypeStub, PsiType> C_TYPE_DECLARATION =
      new PsiTypeStubElementType(ResLanguage.INSTANCE);
  IStubElementType<PsiExternalStub, PsiExternal> C_EXTERNAL_DECLARATION =
      new PsiExternalStubElementType(ResLanguage.INSTANCE);
  // ?
  IStubElementType<PsiParameterStub, PsiParameter> C_FUN_PARAM =
      new PsiParameterStubElementType("C_FUN_PARAM", ResLanguage.INSTANCE);
  IStubElementType<PsiParameterStub, PsiParameter> C_FUNCTOR_PARAM =
      new PsiParameterStubElementType("C_FUNCTOR_PARAM", ResLanguage.INSTANCE);
  //
  IStubElementType<PsiModuleStub, PsiModule> C_FUNCTOR_DECLARATION =
      new PsiFunctorModuleStubElementType(ResLanguage.INSTANCE);
  IStubElementType<PsiLetStub, PsiLet> C_LET_DECLARATION =
      new PsiLetStubElementType(ResLanguage.INSTANCE);
  IStubElementType<PsiModuleStub, PsiModule> C_MODULE_DECLARATION =
      new PsiInnerModuleStubElementType(ResLanguage.INSTANCE);
  IStubElementType<PsiRecordFieldStub, PsiRecordField> C_RECORD_FIELD =
      new PsiRecordFieldStubElementType(ResLanguage.INSTANCE);
  IStubElementType<PsiValStub, PsiVal> C_VAL_DECLARATION =
      new PsiValStubElementType(ResLanguage.INSTANCE);
  IStubElementType<PsiVariantDeclarationStub, PsiVariantDeclaration> C_VARIANT_DECLARATION =
      new PsiVariantStubElementType(ResLanguage.INSTANCE);
}
