package com.reason.ide.doc;

import com.intellij.codeInsight.documentation.actions.ShowQuickDocInfoAction;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.docs.DocumentationProvider;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;

@SuppressWarnings("ConstantConditions")
public class ShowDocTest extends ORBasePlatformTestCase {

    public void testMultipleSpaceTest() {
        FileBase f = configureCode("Doc.ml", "let x = 1;  \t\n  (** doc for x *)");
        PsiLet e = BaseParsingTestCase.first(f.getExpressions("x", PsiLet.class));

        // zzz
        //String doc = new DocumentationProvider().generateDoc(e.getNameIdentifier(), e.getNameIdentifier().getFirstChild());
        //assertNotNull(doc);
    }

    public void testGH_155() {
        configureCode("Mod.re", "/** add 1 */\nlet fn = x => x + 1;");
        configureCode("Doc.re", "Mod.fn(<caret>);");

        ShowQuickDocInfoAction action = new ShowQuickDocInfoAction();
        // TODO test !?
    }

    public void testGH_156() {
        configureCode("A.re", "/** Doc for y */\nlet y = 1;");
        configureCode("Dummy.re", "let x = A.y;\nx<caret>");

        ShowQuickDocInfoAction action = new ShowQuickDocInfoAction();
        // TODO test !?
    }

}
