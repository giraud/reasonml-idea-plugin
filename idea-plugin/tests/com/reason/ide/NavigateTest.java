package com.reason.ide;

import com.intellij.psi.PsiNamedElement;
import com.reason.ide.files.FileBase;
import com.reason.lang.PsiFileHelper;

public class NavigateTest extends ORBasePlatformTestCase {

  // public void testFileComponent() {
  //    FileBase file = (FileBase) myFixture.configureByText("Comp.re", "[@react.component] let make
  // = () => <div/>;");
  // zzz ClassCastException assertEquals("make", ((PsiNamedElement)
  // file.getNavigationElement()).getName());
  // }

  public void testInnerComponent() {
    FileBase file =
        configureCode(
            "NotComp.re", "module Comp = { [@react.component] let make = () => <div/>; };");
    assertEquals("NotComp.re", ((PsiNamedElement) file.getNavigationElement()).getName());
    assertEquals(
        "make",
        ((PsiNamedElement) PsiFileHelper.getModuleExpressions(file).get(0).getNavigationElement())
            .getName());
  }
}
