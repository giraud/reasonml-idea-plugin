package com.reason.comp.bs;

import com.reason.ide.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class NinjaTest extends ORBasePlatformTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "testData/com/reason/bs";
    }

    public void test_read_rescript_format() throws IOException {
        String content = loadFile("ninja-rescript.build");
        Ninja ninja = new Ninja(content);
    }
}
