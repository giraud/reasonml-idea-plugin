package com.reason.comp.dune;

import com.reason.comp.*;
import com.reason.ide.*;
import com.reason.ide.annotations.*;
import com.reason.ide.console.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class DuneOutputAnalyzerTest extends ORBasePlatformTestCase {
    /*
     | File "lib/InputTest.ml", line 3, characters 0-1:     unknown -> fileLocation
     | 3 | X + y                                            fileLocation -> sourceCode
     |     ^                                                -
     | Error: Unbound constructor X                         sourceCode -> ErrorMessage
     */
    @Test
    public void test_error_01() {
        CompilerOutputAnalyzer analyzer = analyze(
                "File \"lib/InputTest.ml\", line 3, characters 0-1:",
                "3 | X + y",
                "    ^",
                "Error: Unbound constructor X"
        );

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertEquals("Unbound constructor X", outputInfo.message);
        assertEquals(3, outputInfo.lineStart);
        assertEquals(3, outputInfo.lineEnd);
        assertEquals(0, outputInfo.colStart);
        assertEquals(1, outputInfo.colEnd);
    }

    /*
     | File "lib/InputTest.ml", line 3, characters 10-12:
     | Error: Syntax error
     */
    @Test
    public void test_error_02() {
        CompilerOutputAnalyzer analyzer = analyze(
                "File \"lib/InputTest.ml\", line 3, characters 10-12:",
                "Error: Syntax error"
        );

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertEquals("Syntax error", outputInfo.message);
        assertEquals(3, outputInfo.lineStart);
        assertEquals(3, outputInfo.lineEnd);
        assertEquals(10, outputInfo.colStart);
        assertEquals(12, outputInfo.colEnd);
    }

    @Test
    public void test_common_0() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.common.get(0));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertLines(4, 4, outputInfo);
        assertCols(6, 7, outputInfo);
        assertEquals("This expression has type int This is not a function; it cannot be applied.", outputInfo.message);
    }

    @Test
    public void test_common_1() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.common.get(1));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertFalse(outputInfo.isError);
        assertLines(3, 3, outputInfo);
        assertCols(6, 7, outputInfo);
        assertEquals("unused variable y.", outputInfo.message);
    }

    @Test
    public void test_common_2() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.common.get(2));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertLines(6, 6, outputInfo);
        assertCols(15, 38, outputInfo);
        assertEquals("Signature mismatch: Modules do not match: sig val x : float end is not included in X Values do not match: val x : float is not included in val x : int", outputInfo.message);
    }

    @Test
    public void test_since408_0() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.since408.get(0));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertLines(2, 2, outputInfo);
        assertCols(36, 64, outputInfo);
        assertEquals("Cannot safely evaluate the definition of the following cycle of recursively-defined modules: A -> B -> A.", outputInfo.message);
    }

    @Test
    public void test_since408_1() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.since408.get(1));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertLines(4, 7, outputInfo);
        assertCols(6, 3, outputInfo);
        assertEquals("Cannot safely evaluate the definition of the following cycle of recursively-defined modules: A -> B -> A.", outputInfo.message);
    }

    @Test
    public void test_since408_2() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.since408.get(2));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertFalse(outputInfo.isError);
        assertLines(33, 37, outputInfo);
        assertCols(6, 23, outputInfo);
        assertEquals("this pattern-matching is not exhaustive.", outputInfo.message);
    }

    @Test
    public void test_since408_3() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.since408.get(3));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertLines(2, 2, outputInfo);
        assertCols(36, 64, outputInfo);
        assertEquals("Cannot safely evaluate the definition of the following cycle of recursively-defined modules: A -> B -> A.", outputInfo.message);
    }

    @Test
    public void test_since408_4() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.since408.get(4));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertFalse(outputInfo.isError);
        assertLines(2, 2, outputInfo);
        assertCols(36, 64, outputInfo);
        assertEquals("Cannot safely evaluate the definition of the following cycle", outputInfo.message);
    }

    @Test
    public void test_since408_5() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.since408.get(5));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertFalse(outputInfo.isError);
        assertLines(2, 2, outputInfo);
        assertCols(36, 64, outputInfo);
        assertEquals("Cannot safely evaluate the definition of the following cycle", outputInfo.message);
    }

    @Test
    public void test_since408_6() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.since408.get(6));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertLines(3, 3, outputInfo);
        assertCols(8, 50, outputInfo);
        assertEquals("This expression has type float but an expression was expected of type int", outputInfo.message);
    }

    @Test
    public void test_since408_7() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.since408.get(7));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertFalse(outputInfo.isError);
        assertLines(3, 3, outputInfo);
        assertCols(8, 50, outputInfo);
        assertEquals("This expression has type float but an expression was expected of type", outputInfo.message);
    }

    @Test
    public void test_since408_8() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.since408.get(8));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertLines(13, 13, outputInfo);
        assertCols(34, 35, outputInfo);
        assertEquals("This expression has type M/2.t but an expression was expected of type M/1.t", outputInfo.message);
    }

    @Test
    public void test_since408_9() {
        CompilerOutputAnalyzer analyzer = analyze(OCamlMessages.since408.get(9));

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertLines(13, 13, outputInfo);
        assertCols(34, 35, outputInfo);
        assertEquals("This expression has type M/2.t but an expression was expected of type M/1.t", outputInfo.message);
    }

    private void assertLines(int start, int end, OutputInfo info) {
        assertEquals(start, info.lineStart);
        assertEquals(end, info.lineEnd);
    }

    private void assertCols(int start, int end, OutputInfo info) {
        assertEquals(start, info.colStart);
        assertEquals(end, info.colEnd);
    }

    private @NotNull CompilerOutputAnalyzer analyze(String... lines) {
        CompilerOutputAnalyzer analyzer = new DuneOutputAnalyzer();

        for (String line : lines) {
            analyzer.onTextAvailable(line);
        }
        return analyzer;
    }
}
