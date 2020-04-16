package com.reason.ide.files;

import com.intellij.mock.MockVirtualFile;
import com.reason.ide.ORBasePlatformTestCase;
import org.junit.Test;

public class BsConfigJsonFileTest extends ORBasePlatformTestCase {

    public void testIsBsConfig() {
        String bsConfigFileName = BsConfigJsonFileType.getDefaultFilename();
        MockVirtualFile mockBsConfig = MockVirtualFile.file(bsConfigFileName);
        assertTrue(BsConfigJsonFile.isBsConfigJson(mockBsConfig));
        assertFalse(BsConfigJsonFile.isBsConfigJson(MockVirtualFile.file("package.json")));
    }
}