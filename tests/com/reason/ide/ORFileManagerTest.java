package com.reason.ide;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import com.intellij.psi.PsiFile;

@SuppressWarnings("ConstantConditions")
public class ORFileManagerTest extends ORBasePlatformTestCase {

    public void testRelativeSourceWithNamespace() {
        myFixture.configureByText("bsconfig.json", toJson("{'name': 'foo', 'namespace': 'foo'}"));
        PsiFile binary = myFixture.configureByText("Config-Foo.cm", "binary, should be .cmt");
        FileSystem fs = FileSystems.getDefault();
        String relativeSource = ORFileManager.toRelativeSourceName(getProject(), binary.getVirtualFile(), fs.getPath("src/Config-Foo.cmt"));
        assertEquals(fs.getPath("src", "Config.re").toString(), relativeSource);
    }
}
