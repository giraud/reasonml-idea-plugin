package com.reason.ide.doc;

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

}
