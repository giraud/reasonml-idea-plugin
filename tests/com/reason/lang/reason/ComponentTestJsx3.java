package com.reason.lang.reason;

import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiInnerModule;

public class ComponentTestJsx3 extends BaseParsingTestCase {
    public ComponentTestJsx3() {
        super("component", "re", new RmlParserDefinition());
    }

    public void testFileComponent() {
        FileBase psiFile = parseCode("[@react.component]\nlet make = () => { <div/> };");
        assertEquals(true, psiFile.isComponent());
    }

    public void testInnerComponent() {
        PsiInnerModule innerModule = firstOfType(parseCode("module X = {\n  [@react.component]\n  let make = (~name) => { <div/> }\n};"), PsiInnerModule.class);
        assertEquals(true, innerModule.isComponent());
    }

}
