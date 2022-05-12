package com.reason.lang.reason;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class SamplesParsingTest extends RmlParsingTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "testData/com/reason/lang/samples";
    }

    public void test_stream() throws IOException {
        //PsiFile e = parseFile("ternary");
    }
}
