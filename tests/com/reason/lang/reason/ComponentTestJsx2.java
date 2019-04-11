package com.reason.lang.reason;

import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiInnerModule;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ComponentTestJsx2 extends BaseParsingTestCase {
    public ComponentTestJsx2() {
        super("component", "re", new RmlParserDefinition());
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
