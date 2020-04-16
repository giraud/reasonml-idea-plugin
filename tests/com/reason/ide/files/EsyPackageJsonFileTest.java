package com.reason.ide.files;

import com.intellij.mock.MockVirtualFile;
import com.reason.ide.ORBasePlatformTestCase;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class EsyPackageJsonFileTest extends ORBasePlatformTestCase {

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
        assertTrue(EsyPackageJsonFile.isEsyPackageJson(mockVirtualFile));
        mockVirtualFile.setText("{}");
        assertFalse(EsyPackageJsonFile.isEsyPackageJson(mockVirtualFile));
    }
}