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
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<p>Hello doc</p>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_paragraphs() {
        String comment = "(** 1st paragraph\n   multiline\n  \n  Another paragraph *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<p>1st paragraph multiline</p><p>Another paragraph</p>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_code() {
        String comment = "(** [% [flags] [width] [.precision] type] *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<p><code><span class='grayed'>% [flags] [width] [.precision] type</span></code></p>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_bold() {
        String comment = "(** {b See} other *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<p><b>See</b> other</p>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_italic() {
        String comment = "(** {i See} other *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<p><i>See</i> other</p>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_emphasis() {
        String comment = "(** {e See} other *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<p><em>See</em> other</p>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_oList() {
        String comment = "(** {ol {- l1 {b l11}} {-l2}} *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<p><ol><li>l1 <b>l11</b></li><li>l2</li></ol></p>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_uList() {
        String comment = "(** {ul {- l1} {-l2}} *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<p><ul><li>l1</li><li>l2</li></ul></p>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_section() {
        String comment = "(** \n" +
                "  a paragraph\n" +
                "  {3 Title}\n" +
                "  another paragraph\n" +
                " *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<p>a paragraph</p><h3>Title</h3><p>another paragraph</p>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_pre() {
        String comment = "(** {[\n Test\n  ]} *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<p><pre><code>\n Test\n  </code></pre></p>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_link() {
        String comment = "(** {{:http://unicode.org/glossary/#unicode_scalar_value}scalar\n    value} other text *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<p><a href=\"http://unicode.org/glossary/#unicode_scalar_value\">scalar    value</a> other text</p>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_tag() {
        String comment = "(** @author author1 author2 & author3 *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<table class=\"sections\"><tr><td class=\"section\" valign=\"top\"><p>Author:</p></td><td valign=\"top\"><p>author1 author2 &amp; author3</p></td></tr></table>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_tags() {
        String comment = "(**\n" +
                " @author author1 author2 & author3\n" +
                " @version 1\n" +
                " *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<table class=\"sections\">" +
                "<tr><td class=\"section\" valign=\"top\"><p>Author:</p></td><td valign=\"top\"><p>author1 author2 &amp; author3</p></td></tr>" +
                "<tr><td class=\"section\" valign=\"top\"><p>Version:</p></td><td valign=\"top\"><p>1</p></td></tr>" +
                "</table>", oDoc.convert(null, comment).toString());
    }

    @Test
    public void test_tag_pre() {
        String comment = "(**\n" +
                "  @example {[\n" +
                "    forEach Some (* comment *)\n" +
                "    forEach None\n" +
                "  ]}" +
                " *)";
        OclDocConverter oDoc = new OclDocConverter();
        assertEquals("<table class=\"sections\"><tr>" +
                "<td class=\"section\" valign=\"top\"><p>Example:</p></td>" +
                "<td valign=\"top\"><p><pre><code>\n    forEach Some (* comment *)\n    forEach None\n  </code></pre></p></td>" +
                "</tr></table>", oDoc.convert(null, comment).toString());
    }
}
