package com.reason.comp.dune;

import com.reason.comp.*;
import com.reason.ide.*;
import com.reason.ide.annotations.*;
import org.jetbrains.annotations.*;

public class DuneOutputAnalyzerTest extends ORBasePlatformTestCase {
    /*
     | File "lib/InputTest.ml", line 3, characters 0-1:     unknown -> fileLocation
     | 3 | X + y                                            fileLocation -> sourceCode
     |     ^                                                -
     | Error: Unbound constructor X                         sourceCode -> ErrorMessage
     */
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

    private @NotNull CompilerOutputAnalyzer analyze(String... lines) {
        CompilerOutputAnalyzer analyzer = new DuneOutputAnalyzer();

        for (String line : lines) {
            analyzer.onTextAvailable(line);
        }
        return analyzer;
    }
}
