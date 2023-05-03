package com.reason.ide;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class NavigateTest extends ORBasePlatformTestCase {
    @Test
    public void testFileComponent() {
        FileBase file = (FileBase) myFixture.configureByText("Comp.re", "[@react.component] let make = () => <div/>;");
        assertEquals("make", ((PsiNamedElement) file.getNavigationElement()).getName());
    }

    @Test
    public void testInnerComponent() {
        FileBase file = configureCode("NotComp.re", "module Comp = { [@react.component] let make = () => <div/>; };");
        assertSame(file, file.getNavigationElement());
        assertEquals("make", ((PsiNamedElement) PsiTreeUtil.getStubChildrenOfTypeAsList(file, RPsiInnerModule.class).get(0).getNavigationElement()).getName());
    }
}
