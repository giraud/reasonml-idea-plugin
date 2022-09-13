package com.reason.comp.bs;

import com.reason.ide.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;

@RunWith(JUnit4.class)
public class NinjaTest extends ORBasePlatformTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "testData/com/reason/bs";
    }

    @Test
    public void test_read_rescript_format() throws IOException {
        String content = loadFile("ninja-rescript.build");
        Ninja ninja = new Ninja(content);
    }
}
