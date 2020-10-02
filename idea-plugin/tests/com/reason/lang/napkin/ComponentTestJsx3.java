package com.reason.lang.napkin;

import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiInnerModule;

public class ComponentTestJsx3 extends NsParsingTestCase {
  public void testFileComponent() {
    FileBase psiFile = parseCode("@react.component\nlet make = () => { <div/> }");
    assertTrue(psiFile.isComponent());
  }

  public void testInnerComponent() {
    PsiInnerModule innerModule =
        firstOfType(
            parseCode("module X = {\n @react.component\n let make = (~name) => <div/>\n }"),
            PsiInnerModule.class);
    assertTrue(innerModule.isComponent());
  }
}
