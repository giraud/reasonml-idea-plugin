package com.reason.comp.esy;

import com.intellij.mock.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;

import static com.reason.comp.esy.EsyConstants.ESY_CONFIG_FILENAME;

@RunWith(JUnit4.class)
public class EsyPackageJsonTest extends ORBasePlatformTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "src/test/testData/com/reason/esy";
    }

    @Test
    public void testIsEsyPackageJson() throws IOException {
        String packageJsonFilename = ESY_CONFIG_FILENAME;
        String mockJson = loadFile(packageJsonFilename);
        MockVirtualFile mockVirtualFile = MockVirtualFile.file(packageJsonFilename);

        mockVirtualFile.setText(mockJson);
        assertTrue(EsyPackageJson.isEsyPackageJson(mockVirtualFile));

        mockVirtualFile.setText("{}");
        assertFalse(EsyPackageJson.isEsyPackageJson(mockVirtualFile));
    }
}
