package com.reason.comp.bs;

import static com.intellij.testFramework.UsefulTestCase.assertSize;
import static org.junit.Assert.*;

import jpsplugin.com.reason.Log;
import com.reason.ide.annotations.OutputInfo;
import org.junit.Test;

import java.util.List;

public class BsLineProcessorTest {

    @Test
    public void testWarningMessage() {
        String[] output =
                new String[]{
                        "[3/3] Building File.cmj\n",
                        "\n",
                        "  Warning number 44\n",
                        "  C:\\__tests__\\Instant_spec.re 3:1-12\n",
                        "  \n",
                        "  1 | open Jest;\n",
                        "  2 | open Expect;\n",
                        "  3 | open Instant;\n",
                        "  4 | \n",
                        "  5 | describe(\"Instant.millis\", () =>\n",
                        "  \n",
                        "  this open statement shadows the value identifier floor (which is later used)\n",
                        "Compilation ended\n"
                };

        BsLineProcessor outputListener = new BsLineProcessor(Log.create("test"));

        for (String line : output) {
            outputListener.onRawTextAvailable(line);
        }

        List<OutputInfo> allErrors = outputListener.getInfo();
        assertSize(1, allErrors);

        OutputInfo info = allErrors.get(0);
        assertFalse(info.isError);
        assertEquals("3:1", info.lineStart + ":" + info.colStart);
        assertEquals("3:13", info.lineEnd + ":" + info.colEnd);
        assertEquals(
                "this open statement shadows the value identifier floor (which is later used)",
                info.message);
    }

    @Test
    public void testSyntaxError() {
        String[] output =
                new String[]{ //
                        "File \"C:\\sources\\File.re\", line 38, characters 2-108:",
                        "Error: SyntaxError in block\n",
                        "\n",
                        "  We've found a bug for you!\n",
                        "  C:\\sources\\File.re\n",
                        "  \n",
                        "  There's been an error running Reason's parser on a file.\n",
                        "  The error location should be slightly above this message.\n",
                        "  Please file an issue on github.com/facebook/reason. Thanks!\n",
                        "  \n",
                        "Compilation ended\n"
                };

        BsLineProcessor outputListener = new BsLineProcessor(Log.create("test"));

        for (String line : output) {
            outputListener.onRawTextAvailable(line);
        }

        List<OutputInfo> allErrors = outputListener.getInfo();
        assertSize(1, allErrors);

        OutputInfo info = allErrors.get(0);
        assertTrue(info.isError);
        assertEquals("38:2", info.lineStart + ":" + info.colStart);
        assertEquals("38:108", info.lineEnd + ":" + info.colEnd);
        assertEquals("SyntaxError in block", info.message);
    }

    @Test
    public void testError() {
        String[] output =
                new String[]{ //
                        "[3/6] Building src\\time\\Instant.cmj\n",
                        "FAILED: src/time/Instant.cmj C:/src/time/Instant.bs.js \n",
                        "\"C:\\node_modules\\bs-platform\\lib\\bsc.exe\" -nostdlib -bs-package-name xxx  -bs-package-output commonjs:lib\\js\\src\\time -color always -bs-suffix -bs-read-cmi -I src\\time -I src -I \"C:\\node_modules\\bs-platform\\lib\\ocaml\" -w -30-40+6+7+27+32..39+44+45+101+A-48-40-42 -warn-error,A-3-44-102 -bs-no-version-header -o src\\time\\Instant.cmj src\\time\\Instant.reast\n",
                        "\n",
                        "  We've found a bug for you!\n",
                        "  U:\\sources\\tin-umbrella\\packages\\tin-core\\src\\time\\Instant.re 60:16-25\n",
                        "  \n",
                        "  58 | let setMonth = dfSetMonth;\n",
                        "  59 | let setDay = dfSetDate;\n",
                        "  60 | let setHours = dfSetHours;\n",
                        "  61 | let setMinutes = dfSetMinutes;\n",
                        "  62 | let setSeconds = dfSetSeconds;\n",
                        "  \n",
                        "  The value dfSetHours can't be found\n",
                        "  \n",
                        "  Hint: Did you mean dfAddHours?\n",
                        "  \n",
                        "FAILED: subcommand failed.\n",
                        "Compilation ended"
                };

        BsLineProcessor outputListener = new BsLineProcessor(Log.create("test"));

        for (String line : output) {
            outputListener.onRawTextAvailable(line);
        }

        List<OutputInfo> allErrors = outputListener.getInfo();
        assertSize(1, allErrors);

        OutputInfo info = allErrors.get(0);
        assertTrue(info.isError);
        assertEquals("60:16", info.lineStart + ":" + info.colStart);
        assertEquals("60:26", info.lineEnd + ":" + info.colEnd);
        assertEquals(
                "The value dfSetHours can't be found. Hint: Did you mean dfAddHours?", info.message);
    }

    @Test
    public void testErrorColonDelimeter() {
        String[] output =
                new String[]{ //
                        "[3/6] Building src\\time\\Instant.cmj\n",
                        "FAILED: src/time/Instant.cmj C:/src/time/Instant.bs.js \n",
                        "\"C:\\node_modules\\bs-platform\\lib\\bsc.exe\" -nostdlib -bs-package-name xxx  -bs-package-output commonjs:lib\\js\\src\\time -color always -bs-suffix -bs-read-cmi -I src\\time -I src -I \"C:\\node_modules\\bs-platform\\lib\\ocaml\" -w -30-40+6+7+27+32..39+44+45+101+A-48-40-42 -warn-error,A-3-44-102 -bs-no-version-header -o src\\time\\Instant.cmj src\\time\\Instant.reast\n",
                        "\n",
                        "  We've found a bug for you!\n",
                        "  U:\\sources\\tin-umbrella\\packages\\tin-core\\src\\time\\Instant.re:60:16-25\n",
                        "  \n",
                        "  58 | let setMonth = dfSetMonth;\n",
                        "  59 | let setDay = dfSetDate;\n",
                        "  60 | let setHours = dfSetHours;\n",
                        "  61 | let setMinutes = dfSetMinutes;\n",
                        "  62 | let setSeconds = dfSetSeconds;\n",
                        "  \n",
                        "  The value dfSetHours can't be found\n",
                        "  \n",
                        "  Hint: Did you mean dfAddHours?\n",
                        "  \n",
                        "FAILED: subcommand failed.\n",
                        "Compilation ended"
                };

        BsLineProcessor outputListener = new BsLineProcessor(Log.create("test"));

        for (String line : output) {
            outputListener.onRawTextAvailable(line);
        }

        List<OutputInfo> allErrors = outputListener.getInfo();
        assertSize(1, allErrors);

        OutputInfo info = allErrors.get(0);
        assertTrue(info.isError);
        assertEquals("60:16", info.lineStart + ":" + info.colStart);
        assertEquals("60:26", info.lineEnd + ":" + info.colEnd);
        assertEquals(
                "The value dfSetHours can't be found. Hint: Did you mean dfAddHours?", info.message);
    }

    @Test
    public void testWarningError() {
        String[] output =
                new String[]{ //
                        "[3/6] Building src\\time\\Instant.cmj\n",
                        "FAILED: src/time/Instant.cmj C:/src/lib/js/src/time/Instant.bs.js\n",
                        "\"C:\\node_modules\\bs-platform\\lib\\bsc.exe\" -nostdlib -bs-package-name tin-core  -bs-package-output commonjs:lib\\js\\src\\time -color always -bs-suffix -bs-read-cmi -I src\\time -I src -I \"C:\\node_modules\\bs-platform\\lib\\ocaml\" -w -30-40+6+7+27+32..39+44+45+101+A-48-40-42 -warn-error,A-3-44-102 -bs-no-version-header -o src\\time\\Instant.cmj src\\time\\Instant.reast\n",
                        "\n",
                        "  Warning number 32\n",
                        "  C:\\src\\time\\Instant.re 3:1-58\n",
                        "\n",
                        "  1 | type t = Js.Date.t;\n",
                        "  2 |\n",
                        "  3 | [@bs.module] external parseDate: t => t = \"date-fns/parse\";\n",
                        "  4 | [@bs.module] external dfIsEqual: (t, t) => bool = \"date-fns/is_equal\";\n",
                        "  5 | [@bs.module] external dfIsBefore: (t, t) => bool = \"date-fns/is_before\"\n",
                        "      ;\n",
                        "\n",
                        "  unused value parseDate.\n",
                        "\n",
                        "  We've found a bug for you!\n",
                        "  C:\\src\\time\\Instant.re\n",
                        "\n",
                        "  Some fatal warnings were triggered (1 occurrences)\n",
                        "\n",
                        "FAILED: subcommand failed.\n",
                        "Compilation ended"
                };

        BsLineProcessor outputListener = new BsLineProcessor(Log.create("test"));

        long start = System.nanoTime();
        for (String line : output) {
            outputListener.onRawTextAvailable(line);
        }
        long end = System.nanoTime();

        System.out.println("process in " + (end - start) + "ns");
        List<OutputInfo> allErrors = outputListener.getInfo();
        assertSize(1, allErrors);

        OutputInfo info = allErrors.get(0);
        // assertTrue(info.isError);
        assertEquals("3:1", info.lineStart + ":" + info.colStart);
        assertEquals("3:59", info.lineEnd + ":" + info.colEnd);
        assertEquals("unused value parseDate.", info.message);
    }

    @Test
    public void testError2() {
        String[] output =
                new String[]{ //
                        "[5/287] Building \\src\\UUID.cmj\n",
                        "\n",
                        "  We've found a bug for you!\n",
                        "  C:\\src\\UUID.re 18:11-14\n",
                        "  \n",
                        "  16 | \n",
                        "  17 | let eq = (uuid1, uuid2) => {\n",
                        "  18 |   compare(uuid, uuid2) === 0;\n",
                        "  19 | };\n",
                        "  20 | \n",
                        "  \n",
                        "  The value uuid can't be found\n",
                        "  \n",
                        "  Hint: Did you mean uuid2 or uuid1?\n",
                        "  \n",
                        "Compilation ended\n"
                };

        BsLineProcessor outputListener = new BsLineProcessor(Log.create("test"));

        for (String line : output) {
            outputListener.onRawTextAvailable(line);
        }

        List<OutputInfo> allErrors = outputListener.getInfo();
        assertSize(1, allErrors);

        OutputInfo info = allErrors.get(0);
        assertTrue(info.isError);
        assertEquals("18:11", info.lineStart + ":" + info.colStart);
        assertEquals("18:15", info.lineEnd + ":" + info.colEnd);
        assertEquals("The value uuid can't be found. Hint: Did you mean uuid2 or uuid1?", info.message);
    }

    @Test
    public void testPpxError() {
        String[] output =
                new String[]{ //
                        "File \"C:\\reason\\src\\Demo.re\", line 6, characters 2-12:\n",
                        "6 | ..}\n",
                        "6 |   allUs.......\n",
                        "Error: Unknown field 'allUsessrs' on type Query\n",
                        "\n",
                        "  We've found a bug for you!\n",
                        "  C:\\reason\\src\\Demo.re\n",
                        "  \n",
                        "  Error while running external preprocessor\n"
                };

        BsLineProcessor outputListener = new BsLineProcessor(Log.create("test"));

        for (String line : output) {
            outputListener.onRawTextAvailable(line);
        }

        List<OutputInfo> allErrors = outputListener.getInfo();
        assertSize(1, allErrors);

        OutputInfo info = allErrors.get(0);
        assertTrue(info.isError);
        assertEquals("6:2", info.lineStart + ":" + info.colStart);
        assertEquals("6:12", info.lineEnd + ":" + info.colEnd);
        assertEquals("Unknown field 'allUsessrs' on type Query", info.message);
    }
}
