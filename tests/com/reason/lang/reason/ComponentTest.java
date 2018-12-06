package com.reason.lang.reason;

import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiModule;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ComponentTest extends BaseParsingTestCase {
    public ComponentTest() {
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
        PsiModule innerModule = firstOfType(parseFile("Inner"), PsiModule.class);

        assertEquals(true, innerModule.isComponent());
    }

}
