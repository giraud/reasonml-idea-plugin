package com.reason.ide;

import com.intellij.psi.*;

import java.nio.file.*;

public class ORFileUtilsTest extends ORBasePlatformTestCase {
    public void testRelativeSourceWithNamespace() {
        myFixture.configureByText("bsconfig.json", toJson("{'name': 'foo', 'namespace': 'foo'}"));
        PsiFile binary = myFixture.configureByText("Config-Foo.cm", "binary, should be .cmt");
        FileSystem fs = FileSystems.getDefault();
        String relativeSource = ORFileUtils.toRelativeSourceName(getProject(), binary.getVirtualFile(), fs.getPath("src/Config-Foo.cmt"));
        assertEquals(fs.getPath("src", "Config.re").toString(), relativeSource);
    }
}
