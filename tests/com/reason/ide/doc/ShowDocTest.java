package com.reason.ide.doc;

import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.reason.RmlLanguage;

public class ShowDocTest extends ORBasePlatformTestCase {

  public void test_Ocl_multiple_spaces_below() {
    configureCode("Doc.ml", "let x = 1;  \t\n  (** doc for x *)");
    FileBase a = configureCode("A.ml", "Doc.x<caret>");

    String doc = getDoc(a, OclLanguage.INSTANCE);
    assertEquals(
        "<div style='padding-bottom: 5px; border-bottom: 1px solid #AAAAAAEE'>Doc</div><div><p> doc for x </p></div>",
        doc);
  }

  public void test_GH_155() {
    FileBase doc = configureCode("Doc.re", "/** add 1 */\nlet fn = x => x + 1;");
    FileBase a = configureCode("A.re", "Mod.fn(<caret>);");

    PsiLowerIdentifier let =
        ORUtil.findImmediateFirstChildOfClass(
            doc.getQualifiedExpressions("Doc.fn", PsiLet.class).get(0), PsiLowerIdentifier.class);
    assertEquals(
        "<div style='padding-bottom: 5px; border-bottom: 1px solid #AAAAAAEE'>Doc</div><div><p> add 1 </p></div>", // zzz trim
        getDocForElement(a, RmlLanguage.INSTANCE, let));
  }

  public void test_GH_156() {
    configureCode("Doc.re", "/** Doc for y */\nlet y = 1;");
    FileBase a = configureCode("A.re", "let x = Doc.y;\nx<caret>");

    assertEquals(
        "<div style='padding-bottom: 5px; border-bottom: 1px solid #AAAAAAEE'>Doc</div><div><p> Doc for y </p></div>", // zzz trim
        getDoc(a, RmlLanguage.INSTANCE));
  }
}
