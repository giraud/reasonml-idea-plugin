package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctorCallParsingTest extends RmlParsingTestCase {
  public void test_instanciation() {
    PsiInnerModule module = (PsiInnerModule) first(moduleExpressions(
        parseCode("module Printing = Make({ let encode = encode_record; });")));

    assertNull(module.getBody());
    PsiFunctorCall call = PsiTreeUtil.findChildOfType(module, PsiFunctorCall.class);
    assertNotNull(call);
    assertEquals("Make({ let encode = encode_record; })", call.getText());
    PsiLet let = PsiTreeUtil.findChildOfType(module, PsiLet.class);
    assertEquals("Dummy.Printing.Make[0].encode", let.getQualifiedName());
  }

  public void test_chaining() {
    PsiFile file = parseCode("module KeyTable = Hashtbl.Make(KeyHash);\ntype infos;");
    List<PsiNamedElement> expressions = new ArrayList<>(expressions(file));

    assertEquals(2, expressions.size());

    PsiInnerModule module = (PsiInnerModule) expressions.get(0);
    assertNull(module.getBody());
    PsiFunctorCall call = PsiTreeUtil.findChildOfType(module, PsiFunctorCall.class);
    assertNotNull(call);
    assertEquals("Hashtbl.Make(KeyHash)", call.getText());
  }
}
