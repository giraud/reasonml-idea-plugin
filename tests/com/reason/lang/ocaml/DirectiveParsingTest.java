package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class DirectiveParsingTest extends OclParsingTestCase {
    public void test_if() {
        FileBase f = parseCode("#if BS then\nx\n#else\ny\n#end");
        PsiDirective e = PsiTreeUtil.findChildOfType(f, PsiDirective.class);

        assertNotNull(e);
        assertEquals("#if BS then\nx\n#else\ny\n#end", e.getText());
    }

    public void test_endif() {
        FileBase f = parseCode("#if BS then\nx\n#else\ny\n#endif");
        PsiDirective e = PsiTreeUtil.findChildOfType(f, PsiDirective.class);

        assertNotNull(e);
        assertEquals("#if BS then\nx\n#else\ny\n#endif", e.getText());
    }

    public void test_oCamlBeforeDirective() {
        PsiVal e = first(valExpressions(parseCode("val bool_of_string_opt : string -> bool option\n(** This is a comment *)\n\n#if BS then\n#end")));

        PsiSignature signature = e.getSignature();
        assertEquals("string -> bool option", signature.asText(OclLanguage.INSTANCE));
    }
}
