package com.reason.bs;

import java.io.*;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.reason.ide.ORBasePlatformTestCase;

public class BsConfigReaderTest extends ORBasePlatformTestCase {

    @NotNull
    @Override
    protected String getTestDataPath() {
        return "testData/com/reason/bs";
    }

    public void testName() {
        BsConfig bsConfig = BsConfigReader.parse(toJson("{'name': 'x'}"));
        assertEquals("x", bsConfig.getName());
    }

    public void testTrailingComma() {
        BsConfig bsConfig = BsConfigReader.parse(toJson("{'sources': ['a',  ],\n  },"));
        assertContainsElements(bsConfig.getSources(), "a");
    }

    public void test_GHIssue214() throws IOException {
        BsConfig bsConf = BsConfigReader.parse(loadJson("issue_214.json"));
        assertNotNull(bsConf);
    }

    public void testJsx() {
        BsConfig bsConfig = BsConfigReader.parse(toJson("{'name': 'x', 'reason': {'react-jsx': 2}}"));
        assertEquals("2", bsConfig.getJsxVersion());

        BsConfig bsConfig2 = BsConfigReader.parse(toJson("{'name': 'x', 'reason': true}")); // ?
        assertNull(bsConfig2.getJsxVersion());
    }

    public void testNamespace() {
        BsConfig conf1 = BsConfigReader.parse(toJson("{'name': 'x', 'namespace': 'Foo'}"));
        assertTrue(conf1.hasNamespace());
        assertEquals("Foo", conf1.getNamespace());

        BsConfig conf2 = BsConfigReader.parse(toJson("{'name': 'auto', 'namespace': true}"));
        assertTrue(conf2.hasNamespace());
        assertEquals("Auto", conf2.getNamespace());

        assertFalse(BsConfigReader.parse(toJson("{'name': 'x', 'namespace': false}")).hasNamespace());
        assertFalse(BsConfigReader.parse(toJson("{'name': 'x'}")).hasNamespace());
    }

    public void testPpx() {
        BsConfig bsConfig = BsConfigReader.parse(toJson("{'name': 'x', 'ppx-flags': ['graphql/ppx', 'other/ppx']}"));
        assertSize(2, bsConfig.getPpx());
        assertEquals("graphql/ppx", bsConfig.getPpx()[0]);
        assertEquals("other/ppx", bsConfig.getPpx()[1]);
    }

    public void testJsonWithComment() throws IOException {
        BsConfig bsConf = BsConfigReader.parse(loadJson("comments.json"));
        assertEquals("comments", bsConf.getName());
    }

    public void testBsPlatform() throws IOException {
        BsConfig bsConfig = BsConfigReader.parse(loadJson("bsplatform.json"));
        assertEquals("bs-platform", bsConfig.getName());
    }

    public void testSourcesAsString() throws IOException {
        BsConfig bsConfig = BsConfigReader.parse(loadJson("src_string.json"));

        Set<String> sources = bsConfig.getSources();
        assertSize(1, sources);
        assertEquals("xxx", sources.iterator().next());
    }

    public void testSourcesAsSourceItem() throws IOException {
        BsConfig bsConfig = BsConfigReader.parse(loadJson("src_object.json"));

        Set<String> sources = bsConfig.getSources();
        assertSize(1, sources);
        assertEquals("yyy", sources.iterator().next());
    }

    public void testSourcesAsArray() throws IOException {
        BsConfig bsConfig = BsConfigReader.parse(loadJson("src_array.json"));

        Set<String> sources = bsConfig.getSources();
        assertSize(3, sources);
        assertContainsElements(sources, "x", "y", "z");
    }

    public void testDepsRead() throws IOException {
        BsConfig bsConfig = BsConfigReader.parse(loadJson("deps.json"));
        assertSize(2, bsConfig.getDependencies());
    }
}
