package com.reason.lang.ocaml;

import org.jetbrains.annotations.*;
import org.junit.*;

import java.io.*;

public class SamplesParsingTest extends OclParsingTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "src/test/testData/com/reason/lang/samples";
    }

    @Test
    public void test_stream() throws IOException {
        parseFile("stream");
    }

    @Test
    public void test_belt_map() throws IOException {
        parseFile("belt_Map");
    }
}
