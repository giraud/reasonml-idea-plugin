package com.reason.lang.napkin;

import java.io.*;
import org.jetbrains.annotations.NotNull;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiInnerModule;

public class ComponentTestJsx2 extends BaseParsingTestCase {
    public ComponentTestJsx2() {
        super("component", "res", new NsParserDefinition());
    }

    @NotNull
    @Override
    protected String getTestDataPath() {
        return "testData/com/reason/lang";
    }

    public void testProxy() throws IOException {
        FileBase psiFile = (FileBase) parseFile("CompMessage");
        assertEquals(true, psiFile.isComponent());
    }

    public void testInnerComponent() throws IOException {
        PsiInnerModule innerModule = firstOfType(parseFile("Inner"), PsiInnerModule.class);

        assertEquals(true, innerModule.isComponent());
    }

}
