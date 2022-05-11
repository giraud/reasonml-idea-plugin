package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

public class SamplesParsingTest extends OclParsingTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "testData/com/reason/lang/samples";
    }

    public void test_stream() throws IOException {
        PsiFile e = parseFile("stream");
    }
}
