package com.reason.lang.odoc;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.ocaml.OclParserDefinition;

import static com.reason.ide.docs.DocumentationProvider.isSpecialComment;

@SuppressWarnings("unchecked")
public class ODocTest extends BaseParsingTestCase {
    public ODocTest() {
        super("", "odoc", new OclParserDefinition());
    }

    public void testDetection() {
        assertTrue(isSpecialComment(parseCode("(** ok *)").getFirstChild()));
        assertTrue(isSpecialComment(parseCode("(**\n ok *)").getFirstChild()));
        assertFalse(isSpecialComment(parseCode("(**********)").getFirstChild()));
    }

    public void testBasic() {
        String comment = "(** Hello doc*)\n";
        assertEquals("<p> Hello doc</p>", new ODocConverter(new ODocLexer()).convert(comment));
    }

    public void testParapgrah() {
        String comment = "(** 1st paraph\n   multiline\n  \n  Another parah *)\n";
        assertEquals("<p> 1st paraph multiline </p> <p>Another parah </p>", new ODocConverter(new ODocLexer()).convert(comment));
    }

    public void testCode() {
        String comment = "(** [% [flags] [width] [.precision] type] *)\n";
        assertEquals("<p> <code>% [flags] [width] [.precision] type</code> </p>", new ODocConverter(new ODocLexer()).convert(comment));
    }

    public void testBold() {
        String comment = "(** {b See} other *)\n";
        assertEquals("<p> <b>See</b> other </p>", new ODocConverter(new ODocLexer()).convert(comment));
    }

    public void testItalic() {
        String comment = "(** {i See} other *)\n";
        assertEquals("<p> <i>See</i> other </p>", new ODocConverter(new ODocLexer()).convert(comment));
    }

    public void testEmphasis() {
        String comment = "(** {e See} other *)\n";
        assertEquals("<p> <em>See</em> other </p>", new ODocConverter(new ODocLexer()).convert(comment));
    }

    public void testOList() {
        String comment = "(** {ol {- l1} {-l2}} *)\n";
        assertEquals("<p> <ol><li>l1</li> <li>l2</li></ol> </p>", new ODocConverter(new ODocLexer()).convert(comment));
    }

    public void testUList() {
        String comment = "(** {ul {- l1} {-l2}} *)\n";
        assertEquals("<p> <ul><li>l1</li> <li>l2</li></ul> </p>", new ODocConverter(new ODocLexer()).convert(comment));
    }

    public void testSection() {
        String comment = "(** {3 Title} *)\n";
        assertEquals("<p> <h3>Title</h3> </p>", new ODocConverter(new ODocLexer()).convert(comment));
    }

    public void testPre() {
        String comment = "(** {[\n Test\n  ]} *)\n";
        assertEquals("<p> <pre> Test\n </pre> </p>", new ODocConverter(new ODocLexer()).convert(comment));
    }

    public void testLink() {
        String comment = "(** {{:http://unicode.org/glossary/#unicode_scalar_value}scalar\n    value} *)\n";
        assertEquals("<p> <a href=\"http://unicode.org/glossary/#unicode_scalar_value\">scalar value</a> </p>", new ODocConverter(new ODocLexer()).convert(comment));
    }

}
