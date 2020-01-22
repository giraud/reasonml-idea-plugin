package com.reason.ide;

import com.intellij.psi.PsiNamedElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.reason.ide.files.FileBase;

@SuppressWarnings("ConstantConditions")
public class NavigateTest extends BasePlatformTestCase {

    public void testFileComponent() {
        FileBase file = (FileBase) myFixture.configureByText("Comp.re", "[@react.component] let make = () => <div/>;");
        // zzz ClassCastException assertEquals("make", ((PsiNamedElement) file.getNavigationElement()).getName());
    }

    public void testInnerComponent() {
        FileBase file = (FileBase) myFixture.configureByText("NotComp.re", "module Comp = { [@react.component] let make = () => <div/>; };");
        assertEquals("NotComp.re", ((PsiNamedElement) file.getNavigationElement()).getName());
        assertEquals("make", ((PsiNamedElement) file.getModuleExpression("Comp").getNavigationElement()).getName());
    }
}
