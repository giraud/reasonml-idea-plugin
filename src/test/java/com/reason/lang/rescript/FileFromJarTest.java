package com.reason.lang.rescript;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;
import org.junit.*;

import java.io.*;

public class FileFromJarTest extends ResParsingTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "src/test/testData/icu4j";
    }

    @Test
    public void test_icu4j_mt() throws IOException {
        PsiFile psiFile = parseFile("mt");
    }
}
