package com.reason.ide.doc;

import com.intellij.codeInsight.documentation.actions.ShowQuickDocInfoAction;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.docs.ORDocumentationProvider;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.ocaml.OclLanguage;

public class ShowDocTest extends ORBasePlatformTestCase {

  public void test_Ocl_multipleSpaceTest() {
    configureCode("Doc.ml", "let x = 1;  \t\n  (** doc for x *)");
    FileBase a = configureCode("A.ml", "Doc.x<caret>");

    String doc = getDoc(a, OclLanguage.INSTANCE);
    assertEquals("<div style='padding-bottom: 5px; border-bottom: 1px solid #AAAAAAEE'>Doc</div><div><p> doc for x </p></div>", doc);
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
