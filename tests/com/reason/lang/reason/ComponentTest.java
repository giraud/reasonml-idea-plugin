package com.reason.lang.reason;

import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;

import java.io.IOException;

public class ComponentTest extends BaseParsingTestCase {
    public ComponentTest() {
        super("", "re", new RmlParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "testData/com/reason/lang";
    }

    public void testProxy() throws IOException {
        FileBase psiFile = (FileBase) parseFile("FormattedMessage");
        assertEquals(true, psiFile.isComponent());
    }

}
