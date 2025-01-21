package com.reason.lang.doc.ocaml;

import com.reason.ide.*;
import org.junit.*;

import static com.reason.ide.docs.ORDocumentationProvider.*;

public class OclDocConverterTest extends ORBasePlatformTestCase {
    @Test
    public void test_detection() {
        assertTrue(isSpecialComment(configureCode("A.ml", "(** ok *)").getFirstChild()));
        assertTrue(isSpecialComment(configureCode("A.ml", "(**\n ok *)").getFirstChild()));
        assertFalse(isSpecialComment(configureCode("A.ml", "(**********)").getFirstChild()));
    }

    @Test
    public void test_basic() {
        String comment = "(** Hello doc*)";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" Hello doc", html);
    }

    @Test
    public void test_paragraphs() {
        String comment = """
                (**
                 1st paragraph
                   multiline
                 \s
                 Another paragraph
                 *)""";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" 1st paragraph multiline<p/> Another paragraph ", html);
    }

    @Test
    public void test_code() {
        String comment = "(** [% [flags] [width] [.precision] type] *)";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" <code><span class='grayed'>% [flags] [width] [.precision] type</span></code> ", html);
    }

    @Test
    public void test_bold() {
        String comment = "(** {b See} other *)";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" <b>See</b> other ", html);
    }

    @Test
    public void test_italic() {
        String comment = "(** {i See} other *)";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" <i>See</i> other ", html);
    }

    @Test
    public void test_emphasis() {
        String comment = "(** {e See} other *)";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" <em>See</em> other ", html);
    }

    @Test
    public void test_oList() {
        String comment = "(** {ol {- l1 {b l11}} {-l2}} *)";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" <ol><li>l1 <b>l11</b></li> <li>l2</li></ol> ", html);
    }

    @Test
    public void test_uList() {
        String comment = "(** {ul {- l1} {-l2}} *)";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" <ul><li>l1</li> <li>l2</li></ul> ", html);
    }

    @Test
    public void test_section() {
        String comment = """
                (**
                  a paragraph
                  {3 Title}
                  another paragraph
                 *)""";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" a paragraph <h3>Title</h3> another paragraph ", html);
    }

    @Test
    public void test_pre() {
        String comment = "(** {[\n Test\n  ]} *)";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" <pre><code>\n Test\n  </code></pre> ", html);
    }

    @Test
    public void test_link() {
        String comment = "(** {{:http://unicode.org/glossary/#unicode_scalar_value}scalar\n    value} other text *)";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" <a href=\"http://unicode.org/glossary/#unicode_scalar_value\">scalar value</a> other text ", html);
    }

    @Test
    public void test_tag() {
        String comment = "(** @author author1 author2 & author3 *)";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" " + """
                <table class='sections'><tr>\
                <td valign='top' class='section'><p>Author:</p></td>\
                <td valign='top'><p>author1 author2 &amp; author3 </p></td>\
                </tr></table>""", html);
    }

    @Test
    public void test_tags() {
        String comment = """
                (**
                 @author  author1 author2 & author3
                 @version 1
                 *)""";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals(" " + """
                <table class='sections'>\
                <tr><td valign='top' class='section'><p>Author:</p></td><td valign='top'><p>author1 author2 &amp; author3 </p></td></tr>\
                <tr><td valign='top' class='section'><p>Version:</p></td><td valign='top'><p>1 </p></td></tr>\
                </table>""", html);
    }

    @Test
    public void test_tag_pre() {
        String comment = """
                (**
                  @example {[
                    forEach Some (* comment *)
                    forEach None
                  ]}\
                 *)""";
        String html = new OclDocConverter().convert(null, comment).toString();

        assertEquals("""
                 <table class='sections'><tr>\
                <td valign='top' class='section'><p>Example:</p></td>\
                <td valign='top'><p><pre><code>
                    forEach Some (* comment *)
                    forEach None
                  </code></pre> </p></td>\
                </tr></table>""", html);
    }
}
