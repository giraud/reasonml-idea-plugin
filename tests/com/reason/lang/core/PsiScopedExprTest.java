package com.reason.lang.core;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.impl.PsiDeconstruction;
import com.reason.lang.core.psi.impl.PsiUnit;
import com.reason.lang.ocaml.OclParserDefinition;

@SuppressWarnings("ConstantConditions")
public class PsiScopedExprTest extends BaseParsingTestCase {
  public PsiScopedExprTest() {
    super("", "ml", new OclParserDefinition());
  }

  public void testEmptyScope() {
    PsiLet e = (PsiLet) firstElement(parseCode("let () = x1"));

    assertNotNull(ORUtil.findImmediateFirstChildOfClass(e, PsiUnit.class));
  }

  public void testNotEmptyScope() {
    PsiLet e = (PsiLet) firstElement(parseCode("let (a, b) = x"));

    assertNotNull(ORUtil.findImmediateFirstChildOfClass(e, PsiDeconstruction.class));
  }
}
