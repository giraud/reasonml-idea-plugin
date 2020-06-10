package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.lang.core.psi.PsiRecordField;

public class ResolveRecordFieldsTest extends ORBasePlatformTestCase {

    public void testSameNameWithPath() {
        myFixture.configureByText("A.re", "type t = { f: string }; let x: f<caret> = \"\";");

        PsiElement e = myFixture.getElementAtCaret();
        assertEquals("A.t.f", ((PsiRecordField) e.getParent()).getQualifiedName());
    }
}
