package com.reason.ide.reference;

import com.intellij.psi.PsiElement;
import com.reason.ide.ORBasePlatformTestCase;

public class ResolveDeconstructionTest extends ORBasePlatformTestCase {

    public void testGH_167() {
        configureCode("Dummy.re", "let (count, setCount) = React.useState(() => 0);\n" + "setCount<caret>(1);");

        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        //assertEquals(12, elementAtCaret.getTextOffset());
        //TODO
    }

}
