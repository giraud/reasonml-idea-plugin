package com.reason.lang.odoc;

import com.intellij.testFramework.ParsingTestCase;
import com.reason.lang.ocaml.OclParserDefinition;

import java.io.IOException;

@SuppressWarnings("unchecked")
public class ODocTest extends ParsingTestCase {
    public ODocTest() {
        super("", "odoc", new OclParserDefinition());
    }

    private final ODocLexer lexer = new ODocLexer();

    public void testBasic() throws IOException {
        String comment = "(** Hello doc *)\n";

        String s = new ODocConverter(lexer).convert(comment);
        assertEquals("<p> Hello doc </p>", s);
    }

    public void testParapgrap() throws IOException {
        String comment = "(** 1st paraph\n   multiline\n  \n  Another parah *)\n";

        String s = new ODocConverter(lexer).convert(comment);
        assertEquals("<p> 1st paraph multiline </p> <p>Another parah </p>", s);
    }

    public void testCode() throws IOException {
        String comment = //
                "(** [fprintf outchan format arg1 ... argN] formats the arguments\n" +
                        "[arg1] to [argN] according to the format string [format], and\n" +
                        "outputs the resulting string on the channel [outchan].\n" +
                        "\n" +
                        "The format string is a character string which contains two types of\n" +
                        "objects: plain characters, which are simply copied to the output\n" +
                        "channel, and conversion specifications, each of which causes\n" +
                        "conversion and printing of arguments.\n" +
                        "\n" +
                        "Conversion specifications have the following form:\n" +
                        "\n" +
                        "[% [flags] [width] [.precision] type]\n" +
                        "\n" +
                        "In short, a conversion specification consists in the [%] character,\n" +
                        "followed by optional modifiers and a type which is made of one or\n" +
                        "two characters. *)\n";

        System.out.println("----");

        ODocLexer lexer = new ODocLexer();
        String s = new ODocConverter(lexer).convert(comment);
        System.out.println(s);
    }


}
