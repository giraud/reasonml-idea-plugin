package com.reason;

import com.intellij.mock.MockVirtualFile;
import com.reason.comp.*;
import com.reason.ide.ORBasePlatformTestCase;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class FileHelperTest extends ORBasePlatformTestCase {
    @Test
    public void testIsBsConfig() {
        MockVirtualFile mockConfig = MockVirtualFile.file(ORConstants.BS_CONFIG_FILENAME);
        assertTrue(FileHelper.isBsConfigJson(mockConfig));
        assertFalse(FileHelper.isBsConfigJson(MockVirtualFile.file("package.json")));
    }

    @Test
    public void testIsRescriptConfig() {
        MockVirtualFile mockConfig = MockVirtualFile.file(ORConstants.RESCRIPT_CONFIG_FILENAME);
        assertTrue(FileHelper.isRescriptConfigJson(mockConfig));
        assertFalse(FileHelper.isRescriptConfigJson(MockVirtualFile.file("package.json")));
    }
}
