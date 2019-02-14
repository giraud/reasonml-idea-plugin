package com.reason.build.bs;

import com.intellij.json.JsonParserDefinition;
import com.intellij.psi.PsiFile;
import com.intellij.testFramework.ParsingTestCase;

import java.io.IOException;
import java.util.Set;

public class BsConfigTest extends ParsingTestCase {

    public BsConfigTest() {
        super("", "json", new JsonParserDefinition());
    }

    @Override
    protected String getTestDataPath() {
        return "testData/com/reason/bs";
    }

    public void testName() {
        BsConfig bsConf = BsConfig.read(null, createJson("{'name': 'x'}"), false);

        assertEquals("x", bsConf.getName());
    }

    public void testNamespace() {
        assertTrue(BsConfig.read(null, createJson("{'name': 'x', 'namespace': true}"), false).hasNamespace());
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

    private String toJson(String value) {
        return value.replaceAll("'", "\"").replaceAll("@", "\n");
    }

    private PsiFile loadJson(String filename) throws IOException {
        String text = loadFile(filename);
        return createPsiFile("bsConfig", text);
    }

    private PsiFile createJson(String content) {
        return createPsiFile("dummy", toJson(content));
    }
}
