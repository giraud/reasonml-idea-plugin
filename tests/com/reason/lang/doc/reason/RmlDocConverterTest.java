package com.reason.lang.doc.reason;

import com.reason.ide.*;
import org.junit.*;

import static com.reason.ide.docs.ORDocumentationProvider.*;

public class RmlDocConverterTest extends ORBasePlatformTestCase {
    @Test
    public void test_detection() {
        assertTrue(isSpecialComment(configureCode("A.re", "/** ok */").getFirstChild()));
        assertTrue(isSpecialComment(configureCode("A.re", "/**\n ok */").getFirstChild()));
        assertFalse(isSpecialComment(configureCode("A.re", "/**********/").getFirstChild()));
    }

    @Test
    public void test_basic() {
        String comment = "/** Hello doc*/";
        RmlDocConverter rDoc = new RmlDocConverter();
        assertEquals("<p>Hello doc</p>", rDoc.convert(null, comment).toString());
    }

    @Test
    public void test_lines() {
        String comment = "/** Hello\n doc\n  \n  another\nline\n\nthird*/";
        RmlDocConverter rDoc = new RmlDocConverter();
        assertEquals("<p>Hello doc</p><p>another line</p><p>third</p>", rDoc.convert(null, comment).toString());
    }

    @Test
    public void test_tags() {
        String comment = "/**\n Hello doc\n  @param p1 multi\n line  @param p2 desc2\n@return string */";
        RmlDocConverter rDoc = new RmlDocConverter();
        assertEquals("<p>Hello doc</p>" +
                "<table class=\"sections\">" +
                "<tr><td class=\"section\" valign=\"top\"><p>Param:</p></td><td valign=\"top\"><p>p1 - multi line</p><p>p2 - desc2</p></td></tr>" +
                "<tr><td class=\"section\" valign=\"top\"><p>Return:</p></td><td valign=\"top\"><p>string</p></td></tr>" +
                "</table>", rDoc.convert(null, comment).toString());
    }
}
