package com.reason.lang.ocaml;

import com.reason.ide.files.FileBase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiAnnotation;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.impl.PsiLetBinding;

@SuppressWarnings("ConstantConditions")
public class AnnotationParsingTest extends OclParsingTestCase {
    public void test_algebraic_let() {
        PsiLet e = firstOfType(parseCode("let find_reference = Coqlib.find_reference [@ocaml.warning \"-3\"]"), PsiLet.class);

        PsiAnnotation attribute = ORUtil.findImmediateFirstChildOfClass(e.getBinding(), PsiAnnotation.class);

        assertEquals("[@ocaml.warning \"-3\"]", attribute.getText());
        assertEquals("@ocaml.warning", attribute.getName());
    }

    public void test_block_let() {
        PsiLet e = firstOfType(parseCode("let val_to_int (x:t) = (Obj.magic x : int) [@@ocaml.inline always]"), PsiLet.class);

        PsiLetBinding b = e.getBinding();
        PsiAnnotation attribute = ORUtil.findImmediateFirstChildOfClass(b, PsiAnnotation.class);
        assertEquals("[@@ocaml.inline always]", attribute.getText());
        assertEquals("@@ocaml.inline", attribute.getName());
    }

    public void test_floating_let() {
        FileBase f = parseCode("let prefix_small_string = 0x20\n [@@@ocaml.warning \"-32\"]");

        PsiLet e = firstOfType(f, PsiLet.class);
        PsiLetBinding b = e.getBinding();
        assertEquals("0x20", b.getText());

        PsiAnnotation attribute = firstOfType(f, PsiAnnotation.class);
        assertEquals("@@@ocaml.warning", attribute.getName());
    }
}
