package com.reason.lang.reason;

import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunParsingTest extends RmlParsingTestCase {
  public void test_fun() {
    PsiLet e =
        first(
            letExpressions(
                parseCode(
                    "let timeUnitToString = fun | Second => \"s\" | Minute => \"m\" | Hour => \"h\";")));

    PsiLetBinding binding = e.getBinding();
    assertEquals("fun | Second => \"s\" | Minute => \"m\" | Hour => \"h\"", binding.getText());
  }

  public void test_chaining() {
    Collection<PsiLet> es =
        letExpressions(parseCode("let a = fun | Second => \"s\"; let b = fun | Minute => \"m\";"));

    assertEquals("fun | Second => \"s\"", first(es).getBinding().getText());
    assertEquals("fun | Minute => \"m\"", second(es).getBinding().getText());
  }
}
