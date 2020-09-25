package com.reason.ide.doc;

import com.intellij.codeInsight.documentation.actions.ShowQuickDocInfoAction;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.docs.ORDocumentationProvider;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;

public class ShowDocTest extends ORBasePlatformTestCase {

  public void test_Ocl_multipleSpaceTest() {
    FileBase f = configureCode("Doc.ml", "let x = 1;  \t\n  (** doc for x *)");
    PsiLet e = BaseParsingTestCase.first(f.getExpressions("Doc.x", PsiLet.class));

    String doc =
        new ORDocumentationProvider()
            .generateDoc(getNameIdentifier(e), getNameIdentifier(e).getFirstChild());
    assertNotNull(doc);
  }

  public void test_GH_155() {
    configureCode("Mod.re", "/** add 1 */\nlet fn = x => x + 1;");
    configureCode("Doc.re", "Mod.fn(<caret>);");

    ShowQuickDocInfoAction action = new ShowQuickDocInfoAction();
    // TODO test !?
  }

  public void test_GH_156() {
    configureCode("A.re", "/** Doc for y */\nlet y = 1;");
    configureCode("Dummy.re", "let x = A.y;\nx<caret>");

    ShowQuickDocInfoAction action = new ShowQuickDocInfoAction();
    // TODO test !?
  }
}
