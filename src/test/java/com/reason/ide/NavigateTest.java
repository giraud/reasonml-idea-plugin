package com.reason.ide;

import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class NavigateTest extends ORBasePlatformTestCase {
    @Test
    public void testFileComponent() {
        FileBase file = (FileBase) myFixture.configureByText("Comp.re", "[@react.component] let make = () => <div/>;");
        assertEquals("make", ((PsiNamedElement) file.getComponentNavigationElement()).getName());
    }

    @Test
    public void testInnerComponent() {
        FileBase file = configureCode("NotComp.re", "module Comp = { [@react.component] let make = () => <div/>; };");
        assertNull(file.getComponentNavigationElement());
        assertEquals("make", ((PsiNamedElement) PsiFileHelper.getModuleExpressions(file).get(0).getComponentNavigationElement()).getName());
    }
}
