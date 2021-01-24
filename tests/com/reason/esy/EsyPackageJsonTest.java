package com.reason.esy;

import com.intellij.mock.MockVirtualFile;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.EsyPackageJsonFileType;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public class EsyPackageJsonTest extends ORBasePlatformTestCase {

  @NotNull
  @Override
  protected String getTestDataPath() {
    return "testData/com/reason/esy";
  }

  public void testIsEsyPackageJson() throws IOException {
    String packageJsonFilename = EsyPackageJsonFileType.getDefaultFilename();
    String mockJson = loadJson(packageJsonFilename);
    MockVirtualFile mockVirtualFile = MockVirtualFile.file(packageJsonFilename);
    mockVirtualFile.setText(mockJson);
    assertTrue(EsyPackageJson.isEsyPackageJson(mockVirtualFile));
    mockVirtualFile.setText("{}");
    assertFalse(EsyPackageJson.isEsyPackageJson(mockVirtualFile));
  }
}
