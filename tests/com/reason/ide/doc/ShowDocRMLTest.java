package com.reason.ide.doc;

import com.intellij.lang.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;

public class ShowDocRMLTest extends ORBasePlatformTestCase {
    public static final Language LANG = RmlLanguage.INSTANCE;

    public void test_GH_155() {
        FileBase doc = configureCode("Doc.re", "/** add 1 */\nlet fn = x => x + 1;");
        FileBase a = configureCode("A.re", "Mod.fn(<caret>);");

        PsiLet resolvedElement = doc.getQualifiedExpressions("Doc.fn", PsiLet.class).get(0);
        assertEquals("<div class=\"definition\"><b>Doc</b><p><i>let fn</i></p></div><div class=\"content\"><p>add 1</p></div>", getDocForElement(a, LANG, resolvedElement));
    }

    public void test_GH_156() {
        configureCode("Doc.re", "/** Doc for y */\nlet y = 1;");
        FileBase a = configureCode("A.re", "let x = Doc.y;\nx<caret>");

        assertEquals("<div class=\"definition\"><b>Doc</b><p><i>let y</i></p></div><div class=\"content\"><p>Doc for y</p></div>", getDoc(a, LANG));
    }

    public void test_GH_359() {
        FileBase a = configureCode("A.re", "module InnerComp = {\n" +
                "  /**\n" +
                "   Doc for my component\n" +
                "   @param text Label\n" +
                "   */\n" +
                "  [@react.component]\n" +
                "  let make = (~text) => <div> text->React.string </div>;\n" +
                "};\n" +
                "\n" +
                "[@react.component]\n" +
                "let make = () => <InnerComp<caret> text=\"my text\" />;");

        assertEquals("<div class=\"definition\"><b>A.InnerComp</b><p><i>let make</i></p></div><div class=\"content\"><p>Doc for my component</p><table class=\"sections\"><tr><td class=\"section\" valign=\"top\"><p>Param:</p></td><td valign=\"top\"><p>text - Label</p></td></tr></table></div>", getDoc(a, LANG));
    }
}
