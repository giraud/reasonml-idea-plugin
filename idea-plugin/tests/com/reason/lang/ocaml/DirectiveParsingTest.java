package com.reason.lang.ocaml;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiDirective;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.signature.ORSignature;

public class DirectiveParsingTest extends BaseParsingTestCase {

    public DirectiveParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testIf() {
        FileBase f = parseCode("#if BS then\nx\n#else\ny\n#end");
        PsiDirective e = PsiTreeUtil.findChildOfType(f, PsiDirective.class);

        assertNotNull(e);
        assertEquals("#if BS then\nx\n#else\ny\n#end", e.getText());
    }

    public void testEndif() {
        FileBase f = parseCode("#if BS then\nx\n#else\ny\n#endif");
        PsiDirective e = PsiTreeUtil.findChildOfType(f, PsiDirective.class);

        assertNotNull(e);
        assertEquals("#if BS then\nx\n#else\ny\n#endif", e.getText());
    }

    public void testOCamlBeforeDirective() {
        PsiVal e = first(valExpressions(parseCode("val bool_of_string_opt : string -> bool option\n(** This is a comment *)\n\n#if BS then\n#end")));

        ORSignature signature = e.getORSignature();
        assertEquals("string -> bool option", signature.asString(OclLanguage.INSTANCE));
    }

}
