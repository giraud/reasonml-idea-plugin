package com.reason.ide.doc;

import com.intellij.codeInsight.documentation.actions.ShowQuickDocInfoAction;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.ide.docs.DocumentationProvider;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;

@SuppressWarnings("ConstantConditions")
public class ShowDocTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testMultipleSpaceTest() {
        FileBase psiFile = (FileBase) myFixture.configureByText("Doc.ml", "let x = 1;  \t\n  (** doc for x *)");
        PsiLet e = BaseParsingTestCase.first(psiFile.getExpressions("x", PsiLet.class));

        String doc = new DocumentationProvider().generateDoc(e.getNameIdentifier(), e.getNameIdentifier().getFirstChild());
        assertNotNull(doc);
    }

    public void testGH_155() {
        myFixture.configureByText("Mod.re", "/** add 1 */\nlet fn = x => x + 1;");
        myFixture.configureByText("Doc.re", "Mod.fn(<caret>);");

        ShowQuickDocInfoAction action = new ShowQuickDocInfoAction();
        // TODO test !
    }

}
