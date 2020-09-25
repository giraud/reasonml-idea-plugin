package com.reason.ide;

import com.intellij.lang.LanguageDocumentation;
import com.intellij.lang.documentation.DocumentationProvider;
import com.intellij.psi.PsiElement;
import com.reason.ide.files.FileBase;
import com.reason.lang.reason.RmlLanguage;

public class QuickDocTest extends ORBasePlatformTestCase {

  public void test_abstract_type() {
    FileBase e = configureCode("A.re", "type t; let x: t<caret>");

    String info = getQuickDoc(e);
    assertEquals("A<br/>type <b>t</b><hr/>This is an abstract type", info);
  }

  private String getQuickDoc(FileBase e) {
    DocumentationProvider docProvider =
        LanguageDocumentation.INSTANCE.forLanguage(RmlLanguage.INSTANCE);
    PsiElement resolvedElement = myFixture.getElementAtCaret();
    PsiElement element = e.findElementAt(myFixture.getCaretOffset() - 1);
    return docProvider.getQuickNavigateInfo(resolvedElement, element);
  }
}
