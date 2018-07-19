package com.reason.build.bs;

import junit.framework.TestCase;

import java.nio.file.Path;

public class BsConfigTest extends TestCase {
    public void testDepsRead() {
        Path[] paths = BsConfig.readDependencies("\"bs-dependencies\": [" +
                "    \"bs-webapi\"," +
                "    \"@glennsl/rebug\"," +
                "    \"\"," +
                "  ],");
        assertEquals(2, paths.length);
    }
}
