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
      2 │                                             -
      3 │ start of line...                            -
        │ ...end of line                              -
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

    /*
    File "C:\bla\bla\src\InputTest.res", line 2, characters 5-9:          unknown -> fileLocation
    Error (warning 32): unused value make.                                fileLocation -> errorMessage(warning)
    File "C:\bla\bla\src\InputTest.res", line 8, characters 13-17:        errorMessage -> fileLocation
    Error (warning 27): unused variable sss.                              fileLocation -> errorMessage(warning)
    */
    public void test_error_04() {
        String[] lines = new String[]{
                "File \"C:\\bla\\bla\\src\\InputTest.res\", line 2, characters 5-9:", //
                "Error (warning 32): unused value make.", //
                "File \"C:\\bla\\bla\\src\\InputTest.res\", line 8, characters 13-17:", //
                "Error (warning 27): unused variable xxx.", //
        };

        RescriptOutputAnalyzer analyzer = new RescriptOutputAnalyzer();

        for (String line : lines) {
            analyzer.onTextAvailable(line);
        }

        assertSize(2, analyzer.getOutputInfo());

        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertFalse(outputInfo.isError);
        assertEquals("unused value make.", outputInfo.message);
        assertEquals(2, outputInfo.lineStart);
        assertEquals(2, outputInfo.lineEnd);
        assertEquals(5, outputInfo.colStart);
        assertEquals(9, outputInfo.colEnd);

        outputInfo = analyzer.getOutputInfo().get(1);
        assertFalse(outputInfo.isError);
        assertEquals("unused variable xxx.", outputInfo.message);
        assertEquals(8, outputInfo.lineStart);
        assertEquals(8, outputInfo.lineEnd);
        assertEquals(13, outputInfo.colStart);
        assertEquals(17, outputInfo.colEnd);
    }

    /*
    File "C:\bla\bla\src\InputTest.res", line 185, characters 8-17:       unknown -> fileLocation
    Warning 27: unused variable onCreated.                                fileLocation -> warningMessage
    File "C:\bla\bla\src\InputTest.res", line 186, characters 8-18:       errorMessage -> fileLocation
    Warning 27: unused variable onCanceled.                               fileLocation -> warningMessage
    */
    public void test_error_05() {
        String[] lines = new String[]{
                "File \"C:\\bla\\bla\\src\\InputTest.res\", line 185, characters 8-17:", //
                "Warning 27: unused variable onCreated.", //
                "File \"C:\\bla\\bla\\src\\InputTest.res\", line 186, characters 8-18:", //
                "Warning 27: unused variable onCanceled.", //
        };

        RescriptOutputAnalyzer analyzer = new RescriptOutputAnalyzer();

        for (String line : lines) {
            analyzer.onTextAvailable(line);
        }

        assertSize(2, analyzer.getOutputInfo());

        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertFalse(outputInfo.isError);
        assertEquals("unused variable onCreated.", outputInfo.message);
        assertEquals(185, outputInfo.lineStart);
        assertEquals(185, outputInfo.lineEnd);
        assertEquals(8, outputInfo.colStart);
        assertEquals(17, outputInfo.colEnd);

        outputInfo = analyzer.getOutputInfo().get(1);
        assertFalse(outputInfo.isError);
        assertEquals("unused variable onCanceled.", outputInfo.message);
        assertEquals(186, outputInfo.lineStart);
        assertEquals(186, outputInfo.lineEnd);
        assertEquals(8, outputInfo.colStart);
        assertEquals(18, outputInfo.colEnd);
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
