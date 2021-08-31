package com.reason.comp.rescript;

import com.reason.ide.*;
import com.reason.ide.annotations.*;

public class RescriptOutputAnalyzerTest extends ORBasePlatformTestCase {
/*
FAILED: src/InputTest.ast

  Syntax error!                                   unknown -> syntaxError
  C:\bla\bla\src\InputTest.res:1:11-12            syntaxError -> syntaxErrorLocation
                                                  syntaxErrorLocation -> syntaxErrorSourceCode
  1 │ let x = 1 +                                 -
                                                  syntaxErrorSourceCode -> syntaxErrorMessage
  Did you forget to write an expression here?     -

FAILED: cannot make progress due to previous errors.
*/
    public void test_error_01() {
        String[] lines = new String[]{
                "FAILED: src/InputTest.ast", //
                "", //
                "  Syntax error!", //
                "  C:\\bla\\bla\\src\\InputTest.res:1:11-12", //
                "", //
                "  1 │ let x = 1 +", //
                "", //
                "  Did you forget to write an expression here?", //
                "", //
                "FAILED: cannot make progress due to previous errors.",  //
        };

        RescriptOutputAnalyzer analyzer = new RescriptOutputAnalyzer();

        for (String line : lines) {
            analyzer.onTextAvailable(line);
        }

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertEquals("Did you forget to write an expression here?", outputInfo.message);
        assertEquals(1, outputInfo.lineStart);
        assertEquals(1, outputInfo.lineEnd);
        assertEquals(11, outputInfo.colStart);
        assertEquals(12, outputInfo.colEnd);
    }

    /*
    FAILED: src/InputTest.ast

      Syntax error!
      C:\bla\bla\src\InputTest.res:1:11

      1 │ let x = [1|]

      Did you forget a `,` here?

    FAILED: cannot make progress due to previous errors.
     */
    public void test_error_02() {
        String[] lines = new String[]{
                "FAILED: src/InputTest.ast", //
                "", //
                "  Syntax error!", //
                "  C:\\bla\\bla\\src\\InputTest.res:1:11", //
                "", //
                "  1 │ let x = [1|]", //
                "", //
                "  Did you forget a `,` here?", //
                "", //
                "FAILED: cannot make progress due to previous errors.",  //
        };

        RescriptOutputAnalyzer analyzer = new RescriptOutputAnalyzer();

        for (String line : lines) {
            analyzer.onTextAvailable(line);
        }

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertEquals("Did you forget a `,` here?", outputInfo.message);
        assertEquals(1, outputInfo.lineStart);
        assertEquals(1, outputInfo.lineEnd);
        assertEquals(11, outputInfo.colStart);
        assertEquals(12, outputInfo.colEnd);
    }

    /*
File "C:\bla\bla\src\InputTest.res", line 1, characters 9-10:                 unknown -> fileLocation
Error: This expression has type int but an expression was expected of type    fileLocation -> errorMessage
         float                                                                -
FAILED: cannot make progress due to previous errors.                          * -> unknown
     */
    public void test_error_03() {
        String[] lines = new String[]{
                "File \"C:\\bla\\bla\\src\\InputTest.res\", line 1, characters 9-10:", //
                "Error: This expression has type int but an expression was expected of type", //
                "         float", //
                "FAILED: cannot make progress due to previous errors.", //
        };

        RescriptOutputAnalyzer analyzer = new RescriptOutputAnalyzer();

        for (String line : lines) {
            analyzer.onTextAvailable(line);
        }

        assertSize(1, analyzer.getOutputInfo());
        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertEquals("This expression has type int but an expression was expected of type float", outputInfo.message);
        assertEquals(1, outputInfo.lineStart);
        assertEquals(1, outputInfo.lineEnd);
        assertEquals(9, outputInfo.colStart);
        assertEquals(10, outputInfo.colEnd);
    }

    // ---- SUPER ERRORS

/*
FAILED: src/InputTest.cmj

  We've found a bug for you!
  T:\reason\projects\re-basic\src\InputTest.res:3:9

  1 │ let x = (. x) => x
  2 │
  3 │ let y = x(10)

  This is an uncurried ReScript function. It must be applied with a dot.

  Like this: foo(. a, b)
  Not like this: foo(a, b)

  This guarantees that your function is fully applied. More info here:
  https://rescript-lang.org/docs/manual/latest/function#uncurried-function

 */
}
