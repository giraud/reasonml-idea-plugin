package com.reason.lang.core.stub;

import com.intellij.psi.stubs.IStubElementType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.PsiFakeModule;
import com.reason.lang.core.stub.type.*;
import com.reason.lang.napkin.NsLanguage;

public interface RescriptStubBasedElementTypes {
  IStubElementType<PsiModuleStub, PsiFakeModule> C_FAKE_MODULE =
      new PsiFakeModuleStubElementType(NsLanguage.INSTANCE);
  IStubElementType<PsiExceptionStub, PsiException> C_EXCEPTION_DECLARATION =
      new PsiExceptionStubElementType(NsLanguage.INSTANCE);
  IStubElementType<PsiTypeStub, PsiType> C_TYPE_DECLARATION =
      new PsiTypeStubElementType(NsLanguage.INSTANCE);
  IStubElementType<PsiExternalStub, PsiExternal> C_EXTERNAL_DECLARATION =
      new PsiExternalStubElementType(NsLanguage.INSTANCE);
  // ?
  IStubElementType<PsiParameterStub, PsiParameter> C_FUN_PARAM =
      new PsiParameterStubElementType("C_FUN_PARAM", NsLanguage.INSTANCE);
  IStubElementType<PsiParameterStub, PsiParameter> C_FUNCTOR_PARAM =
      new PsiParameterStubElementType("C_FUNCTOR_PARAM", NsLanguage.INSTANCE);
  //
  IStubElementType<PsiModuleStub, PsiModule> C_FUNCTOR_DECLARATION =
      new PsiFunctorModuleStubElementType(NsLanguage.INSTANCE);
  IStubElementType<PsiLetStub, PsiLet> C_LET_DECLARATION =
      new PsiLetStubElementType(NsLanguage.INSTANCE);
  IStubElementType<PsiModuleStub, PsiModule> C_MODULE_DECLARATION =
      new PsiInnerModuleStubElementType(NsLanguage.INSTANCE);
  IStubElementType<PsiRecordFieldStub, PsiRecordField> C_RECORD_FIELD =
      new PsiRecordFieldStubElementType(NsLanguage.INSTANCE);
  IStubElementType<PsiValStub, PsiVal> C_VAL_DECLARATION =
      new PsiValStubElementType(NsLanguage.INSTANCE);
  IStubElementType<PsiVariantDeclarationStub, PsiVariantDeclaration> C_VARIANT_DECLARATION =
      new PsiVariantStubElementType(NsLanguage.INSTANCE);
}
