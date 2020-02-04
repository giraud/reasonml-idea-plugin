package com.reason.bs;

import java.io.*;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.json.JsonLanguage;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;

public class BsConfigTest extends BasePlatformTestCase {

    @NotNull
    @Override
    protected String getTestDataPath() {
        return "testData/com/reason/bs";
    }

    public void testName() {
        BsConfig bsConf = BsConfig.read(null, createJson("{'name': 'x'}"), false);

        assertEquals("x", bsConf.getName());
    }

    public void testNamespace() {
        BsConfig conf1 = BsConfig.read(null, createJson("{'name': 'x', 'namespace': 'Foo'}"), false);
        assertTrue(conf1.hasNamespace());
        assertEquals("Foo", conf1.getNamespace());

        BsConfig conf2 = BsConfig.read(null, createJson("{'name': 'auto', 'namespace': true}"), false);
        assertTrue(conf2.hasNamespace());
        assertEquals("Auto", conf2.getNamespace());

        assertFalse(BsConfig.read(null, createJson("{'name': 'x', 'namespace': false}"), false).hasNamespace());
        assertFalse(BsConfig.read(null, createJson("{'name': 'x'}"), false).hasNamespace());
    }

    public void testJsonWithComment() throws IOException {
        BsConfig bsConf = BsConfig.read(null, loadJson("comments.json"), false);
        assertEquals("comments", bsConf.getName());
    }

    public void testBsPlatform() throws IOException {
        BsConfig bsConfig = BsConfig.read(null, loadJson("bsplatform.json"), false);
        assertEquals("bs-platform", bsConfig.getName());
    }

    public void testSourcesAsString() throws IOException {
        BsConfig bsConfig = BsConfig.read(null, loadJson("src_string.json"), false);

        Set<String> sources = bsConfig.getSources();
        assertSize(1, sources);
        assertEquals("xxx", sources.iterator().next());
    }

    public void testSourcesAsSourceItem() throws IOException {
        BsConfig bsConfig = BsConfig.read(null, loadJson("src_object.json"), false);

        Set<String> sources = bsConfig.getSources();
        assertSize(1, sources);
        assertEquals("yyy", sources.iterator().next());
    }

    public void testSourcesAsArray() throws IOException {
        BsConfig bsConfig = BsConfig.read(null, loadJson("src_array.json"), false);

        Set<String> sources = bsConfig.getSources();
        assertSize(3, sources);
        assertContainsElements(sources, "x", "y", "z");
    }

    public void testDepsRead() throws IOException {
        BsConfig bsConfig = BsConfig.read(null, loadJson("deps.json"), false);
        assertSize(2, bsConfig.getDependencies());
    }

    public void testJsx() {
        BsConfig bsConfig = BsConfig.read(null, createJson("{'name': 'x', 'reason': {'react-jsx': 2}}"), false);
        assertEquals("2", bsConfig.getJsxVersion());
    }

    public void testPpx() {
        BsConfig bsConfig = BsConfig.read(null, createJson("{'name': 'x', 'ppx-flags': ['graphql/ppx', 'other/ppx']}"), false);
        assertSize(2, bsConfig.getPpx());
        assertEquals("graphql/ppx", bsConfig.getPpx()[0]);
        assertEquals("other/ppx", bsConfig.getPpx()[1]);
    }

    @NotNull
    private String toJson(@NotNull String value) {
        return value.replaceAll("'", "\"").replaceAll("@", "\n");
    }

    private PsiFile loadJson(@NotNull String filename) throws IOException {
        String text = FileUtil.loadFile(new File(getTestDataPath(), filename), CharsetToolkit.UTF8, true).trim();
        return createJson(text);
    }

    private PsiFile createJson(@NotNull String content) {
        return createLightFile("dummy.json", JsonLanguage.INSTANCE, toJson(content));
    }
}
