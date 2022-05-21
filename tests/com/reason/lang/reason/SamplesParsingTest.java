package com.reason.lang.reason;

import com.intellij.psi.*;
import org.jetbrains.annotations.*;

import java.io.*;

public class SamplesParsingTest extends RmlParsingTestCase {
    @Override
    protected @NotNull String getTestDataPath() {
        return "testData/com/reason/lang/samples";
    }

    public void test_stream() throws IOException {
        parseCode("module type CssImplementationIntf = {\n" +
                "  type styleEncoding;\n" +
                "  type renderer; // some implementations might need a renderer\n" +
                "\n" +
                "  let injectRaw: (. string /*css*/) => unit;\n" +
                "  let renderRaw: (. renderer, string /*css*/) => unit;\n" +
                "\n" +
                "  let injectRules: (. string /*selector*/, Js.Json.t) => unit;\n" +
                "  let renderRules: (. renderer, string /*selector*/, Js.Json.t) => unit;\n" +
                "\n" +
                "  let make: (. Js.Json.t) => styleEncoding;\n" +
                "  let mergeStyles: (. array(styleEncoding)) => styleEncoding;\n" +
                "\n" +
                "  let makeKeyframes: (. Js.Dict.t(Js.Json.t)) => string /*animationName*/;\n" +
                "  let renderKeyframes: (. renderer, Js.Dict.t(Js.Json.t)) => string /*animationName*/;\n" +
                "};\n");
        //PsiFile e = parseFile("ternary");
    }
}
