package com.reason.comp.rescript;

import com.reason.ide.*;
import com.reason.ide.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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
    @Test
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

    /*
     */
    @Test
    public void test_error_06() {
        String error = """
                FAILED: src/NotGood.cmj
                    
                  We've found a bug for you!
                  C:\\project\\src\\NotGood.res:3:9
                    
                  1 │ let x = (a, b) => a + b
                  2 │ 
                  3 │ let y = x(10)
                    
                  This uncurried function has type (int, int) => int
                  It is applied with 1 arguments but it requires 2.
                    
                FAILED: cannot make progress due to previous errors.
                """;
        String[] lines = error.split("\n");

        RescriptOutputAnalyzer analyzer = new RescriptOutputAnalyzer();
        for (String line : lines) {
            analyzer.onTextAvailable(line);
        }

        assertSize(1, analyzer.getOutputInfo());

        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertEquals("This uncurried function has type (int, int) => int It is applied with 1 arguments but it requires 2.", outputInfo.message);
        assertEquals(3, outputInfo.lineStart);
        assertEquals(3, outputInfo.lineEnd);
        assertEquals(9, outputInfo.colStart);
        assertEquals(10, outputInfo.colEnd);
    }

    @Test
    public void test_multi() {
        String error = """
                FAILED: src/Colors.ast
                                
                  Syntax error!
                  C:\\myProject\\src\\Colors.res:31:23-28
                                
                  29 │   let mainColor = #hex("9D4B70")
                  30 │   let dark = #hex("6C1D45)
                  31 │   let lighter = #hex("EFE7EB")
                  32 │   let light = #hex("C3A4B4")
                  33 │ }
                                
                  Did you forget a `,` here?
                                
                                
                  Syntax error!
                  C:\\myProject\\src\\Colors.res:163:13-165:1
                                
                  161 │     "#FFFFFF"
                  162 │   } else {
                  163 │     "#000000"
                  164 │   }
                  165 │ }
                                
                  This string is missing a double quote at the end
                                
                                
                  Syntax error!
                  C:\\myProject\\src\\Colors.res:165:2
                                
                  163 │     "#000000"
                  164 │   }
                  165 │ }
                                
                  Did you forget a `}` here?
                                
                FAILED: cannot make progress due to previous errors.
                Process finished in 97ms
                """;

        String[] lines = error.split("\n");

        RescriptOutputAnalyzer analyzer = new RescriptOutputAnalyzer();
        for (String line : lines) {
            analyzer.onTextAvailable(line);
        }

        assertSize(3, analyzer.getOutputInfo());

        OutputInfo outputInfo = analyzer.getOutputInfo().get(0);
        assertTrue(outputInfo.isError);
        assertEquals("Did you forget a `,` here?", outputInfo.message);
        assertEquals(31, outputInfo.lineStart);
        assertEquals(31, outputInfo.lineEnd);
        assertEquals(23, outputInfo.colStart);
        assertEquals(28, outputInfo.colEnd);

         outputInfo = analyzer.getOutputInfo().get(1);
        assertTrue(outputInfo.isError);
        assertEquals("This string is missing a double quote at the end", outputInfo.message);
        assertEquals(163, outputInfo.lineStart);
        assertEquals(165, outputInfo.lineEnd);
        assertEquals(13, outputInfo.colStart);
        assertEquals(1, outputInfo.colEnd);

         outputInfo = analyzer.getOutputInfo().get(2);
        assertTrue(outputInfo.isError);
        assertEquals("Did you forget a `}` here?", outputInfo.message);
        assertEquals(165, outputInfo.lineStart);
        assertEquals(165, outputInfo.lineEnd);
        assertEquals(2, outputInfo.colStart);
        assertEquals(3, outputInfo.colEnd);
    }
}
