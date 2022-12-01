package com.reason.comp.bs;

import com.intellij.mock.MockVirtualFile;
import com.reason.ide.ORBasePlatformTestCase;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class BsConfigJsonTest extends ORBasePlatformTestCase {
    @Test
    public void testIsBsConfig() {
        String bsConfigFileName = "bsconfig.json";
        MockVirtualFile mockBsConfig = MockVirtualFile.file(bsConfigFileName);
        Assert.assertTrue(BsConfigJson.isBsConfigJson(mockBsConfig));
        Assert.assertFalse(BsConfigJson.isBsConfigJson(MockVirtualFile.file("package.json")));
    }
}
