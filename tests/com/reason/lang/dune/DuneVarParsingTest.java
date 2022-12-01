package com.reason.lang.dune;

import com.intellij.psi.PsiElement;
import com.reason.lang.BaseParsingTestCase;
import org.junit.*;

public class DuneVarParsingTest extends BaseParsingTestCase {
  public DuneVarParsingTest() {
    super("", "", new DuneParserDefinition());
  }

  @Test
  public void test_basic() {
    PsiElement e = firstElement(parseRawCode("%{x}"));

    assertEquals(DuneTypes.INSTANCE.C_VAR, e.getNode().getElementType());
    assertEquals("%{x}", e.getText());
  }
}
