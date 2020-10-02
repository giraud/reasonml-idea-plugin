package com.reason.bs;

import com.intellij.mock.MockVirtualFile;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.BsConfigJsonFileType;

public class BsConfigJsonTest extends ORBasePlatformTestCase {

  public void testIsBsConfig() {
    String bsConfigFileName = BsConfigJsonFileType.getDefaultFilename();
    MockVirtualFile mockBsConfig = MockVirtualFile.file(bsConfigFileName);
    assertTrue(BsConfigJson.isBsConfigJson(mockBsConfig));
    assertFalse(BsConfigJson.isBsConfigJson(MockVirtualFile.file("package.json")));
  }
}
