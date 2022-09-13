package com.reason.comp.esy;

import com.intellij.mock.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;

@RunWith(JUnit4.class)
public class EsyPackageJsonTest extends ORBasePlatformTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "testData/com/reason/esy";
    }

    @Test
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
