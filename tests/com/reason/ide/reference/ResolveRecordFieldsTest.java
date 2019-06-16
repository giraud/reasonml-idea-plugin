package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiType;

public class ResolveRecordFieldsTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testSameNameWithPath() {
        myFixture.configureByText("A.re", "type t = { f: string }; let x: f<caret> = \"\";");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        assertEquals("A.t.f", ((PsiRecordField) elementAtCaret.getParent()).getQualifiedName());
    }

}
