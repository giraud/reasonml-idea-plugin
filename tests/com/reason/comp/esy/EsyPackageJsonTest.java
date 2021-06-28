package com.reason.comp.esy;

import com.intellij.mock.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class EsyPackageJsonTest extends ORBasePlatformTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "testData/com/reason/esy";
    }

    public void testIsEsyPackageJson() throws IOException {
        String packageJsonFilename = EsyPackageJsonFileType.getDefaultFilename();
        String mockJson = loadFile(packageJsonFilename);
        MockVirtualFile mockVirtualFile = MockVirtualFile.file(packageJsonFilename);

        mockVirtualFile.setText(mockJson);
        assertTrue(EsyPackageJson.isEsyPackageJson(mockVirtualFile));

        mockVirtualFile.setText("{}");
        assertFalse(EsyPackageJson.isEsyPackageJson(mockVirtualFile));
    }
}
