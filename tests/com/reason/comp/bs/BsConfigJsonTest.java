package com.reason.comp.bs;

import com.intellij.mock.MockVirtualFile;
import com.reason.ide.ORBasePlatformTestCase;

public class BsConfigJsonTest extends ORBasePlatformTestCase {

  public void testIsBsConfig() {
    String bsConfigFileName = "bsconfig.json";
    MockVirtualFile mockBsConfig = MockVirtualFile.file(bsConfigFileName);
    assertTrue(BsConfigJson.isBsConfigJson(mockBsConfig));
    assertFalse(BsConfigJson.isBsConfigJson(MockVirtualFile.file("package.json")));
  }
}
