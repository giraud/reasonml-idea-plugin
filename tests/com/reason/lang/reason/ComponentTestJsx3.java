package com.reason.lang.reason;

import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiInnerModule;

public class ComponentTestJsx3 extends RmlParsingTestCase {
  public void test_fileComponent() {
    FileBase psiFile = parseCode("[@react.component]\nlet make = () => { <div/> };");
    assertEquals(true, psiFile.isComponent());
  }

  public void test_innerComponent() {
    PsiInnerModule innerModule =
        firstOfType(
            parseCode("module X = {\n  [@react.component]\n  let make = (~name) => { <div/> }\n};"),
            PsiInnerModule.class);
    assertEquals(true, innerModule.isComponent());
  }
}
