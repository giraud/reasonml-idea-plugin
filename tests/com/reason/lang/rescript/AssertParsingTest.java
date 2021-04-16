package com.reason.lang.rescript;

import com.reason.lang.core.psi.impl.PsiAssert;

public class AssertParsingTest extends ResParsingTestCase {

  public void testBasic() {
    PsiAssert assertExp = firstOfType(parseCode("assert (i < Array.length(t))"), PsiAssert.class);

    assertNotNull(assertExp);
    assertNotNull(assertExp.getAssertion());
  }
}
