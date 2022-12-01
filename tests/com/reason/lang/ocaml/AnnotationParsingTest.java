package com.reason.lang.ocaml;

import com.reason.ide.files.FileBase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.impl.RPsiAnnotation;
import com.reason.lang.core.psi.RPsiLet;
import com.reason.lang.core.psi.impl.RPsiLetBinding;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class AnnotationParsingTest extends OclParsingTestCase {
    @Test
    public void test_algebraic_let() {
        RPsiLet e = firstOfType(parseCode("let find_reference = Coqlib.find_reference [@ocaml.warning \"-3\"]"), RPsiLet.class);

        RPsiAnnotation attribute = ORUtil.findImmediateFirstChildOfClass(e.getBinding(), RPsiAnnotation.class);

        assertEquals("[@ocaml.warning \"-3\"]", attribute.getText());
        assertEquals("@ocaml.warning", attribute.getName());
    }

    @Test
    public void test_block_let() {
        RPsiLet e = firstOfType(parseCode("let val_to_int (x:t) = (Obj.magic x : int) [@@ocaml.inline always]"), RPsiLet.class);

        RPsiLetBinding b = e.getBinding();
        RPsiAnnotation attribute = ORUtil.findImmediateFirstChildOfClass(b, RPsiAnnotation.class);
        assertEquals("[@@ocaml.inline always]", attribute.getText());
        assertEquals("@@ocaml.inline", attribute.getName());
    }

    @Test
    public void test_floating_let() {
        FileBase f = parseCode("let prefix_small_string = 0x20\n [@@@ocaml.warning \"-32\"]");

        RPsiLet e = firstOfType(f, RPsiLet.class);
        RPsiLetBinding b = e.getBinding();
        assertEquals("0x20", b.getText());

        RPsiAnnotation attribute = firstOfType(f, RPsiAnnotation.class);
        assertEquals("@@@ocaml.warning", attribute.getName());
    }
}
