package com.reason.comp.bs;

import com.reason.ide.ORBasePlatformTestCase;

import java.io.*;
import java.util.*;

import org.jetbrains.annotations.NotNull;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class BsConfigReaderTest extends ORBasePlatformTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "src/test/testData/com/reason/bs";
    }

    @Test
    public void testName() {
        BsConfig bsConfig = BsConfigReader.parse(toJson("{'name': 'x'}"));
        assertEquals("x", bsConfig.getName());
    }

    @Test
    public void testTrailingComma() {
        BsConfig bsConfig = BsConfigReader.parse(toJson("{'sources': ['a',  ],\n  },"));
        assertContainsElements(bsConfig.getSources(), "a");
    }

    @Test
    public void test_GHIssue214() throws IOException {
        BsConfig bsConf = BsConfigReader.parse(loadFile("issue_214.json"));
        assertNotNull(bsConf);
    }

    @Test
    public void testJsx() {
        BsConfig bsConfig = BsConfigReader.parse(toJson("{'name': 'x', 'reason': {'react-jsx': 2}}"));
        assertEquals("2", bsConfig.getJsxVersion());

        BsConfig bsConfig2 = BsConfigReader.parse(toJson("{'name': 'x', 'reason': true}")); // ?
        assertNull(bsConfig2.getJsxVersion());
    }

    @Test
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

    @Test
    public void testPpx() {
        BsConfig bsConfig = BsConfigReader.parse(toJson("{'name': 'x', 'ppx-flags': ['graphql/ppx', 'other/ppx']}"));
        assertSize(2, bsConfig.getPpx());
        assertEquals("graphql/ppx", bsConfig.getPpx()[0]);
        assertEquals("other/ppx", bsConfig.getPpx()[1]);
    }

    @Test
    public void testJsonWithComment() throws IOException {
        BsConfig bsConf = BsConfigReader.parse(loadFile("comments.json"));
        assertEquals("comments", bsConf.getName());
    }

    @Test
    public void testBsPlatform() throws IOException {
        BsConfig bsConfig = BsConfigReader.parse(loadFile("bsplatform.json"));
        assertEquals("bs-platform", bsConfig.getName());
    }

    @Test
    public void testSourcesAsString() throws IOException {
        BsConfig bsConfig = BsConfigReader.parse(loadFile("src_string.json"));

        Set<String> sources = bsConfig.getSources();
        assertSize(1, sources);
        assertEquals("xxx", sources.iterator().next());
    }

    @Test
    public void testSourcesAsSourceItem() throws IOException {
        BsConfig bsConfig = BsConfigReader.parse(loadFile("src_object.json"));

        Set<String> sources = bsConfig.getSources();
        assertSize(1, sources);
        assertEquals("yyy", sources.iterator().next());
    }

    @Test
    public void testSourcesAsArray() throws IOException {
        BsConfig bsConfig = BsConfigReader.parse(loadFile("src_array.json"));

        Set<String> sources = bsConfig.getSources();
        assertSize(3, sources);
        assertContainsElements(sources, "x", "y", "z");
    }

    @Test
    public void testDepsRead() throws IOException {
        BsConfig bsConfig = BsConfigReader.parse(loadFile("deps.json"));
        assertSize(2, bsConfig.getDependencies());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/418
    @Test
    public void testBscFlags() {
        BsConfig bsConfig = BsConfigReader.parse(toJson("{'name': 'x', 'bsc-flags': ['-no-alias-deps', '-open RescriptCore']}"));
        assertSize(2, bsConfig.getBscFlags());
        assertSize(1, bsConfig.getOpenedDeps());
        assertContainsElements(bsConfig.getOpenedDeps(), "RescriptCore");
    }
}
