package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class DirectiveParsingTest extends OclParsingTestCase {
    @Test
    public void test_if() {
        FileBase f = parseCode("#if BS then\nx\n#else\ny\n#end");
        RPsiDirective e = PsiTreeUtil.findChildOfType(f, RPsiDirective.class);

        assertNotNull(e);
        assertEquals("#if BS then\nx\n#else\ny\n#end", e.getText());
    }

    @Test
    public void test_endif() {
        FileBase f = parseCode("#if BS then\nx\n#else\ny\n#endif");
        RPsiDirective e = PsiTreeUtil.findChildOfType(f, RPsiDirective.class);

        assertNotNull(e);
        assertEquals("#if BS then\nx\n#else\ny\n#endif", e.getText());
    }

    @Test
    public void test_let_binding() {
        RPsiLet e = firstOfType(parseCode("let usage_b buf speclist errmsg =\n" +
                "#if 0  \n" +
                "  a;\n" +
                "#else\n" +
                "  b;\n" +
                "#end\n" +
                "  c\n"), RPsiLet.class);

        assertEquals("#if 0  \n  a;\n#else\n  b;\n#end\n  c", e.getFunction().getBody().getText());
        RPsiDirective d = PsiTreeUtil.findChildOfType(e, RPsiDirective.class);
        assertEquals("#if 0  \n  a;\n#else\n  b;\n#end", d.getText());
    }

    @Test
    public void test_oCamlBeforeDirective() {
        RPsiVal e = firstOfType(parseCode("val bool_of_string_opt : string -> bool option\n(** This is a comment *)\n\n#if BS then\n#end"), RPsiVal.class);

        RPsiSignature signature = e.getSignature();
        assertEquals("string -> bool option", signature.asText(OclLanguage.INSTANCE));
    }
}
