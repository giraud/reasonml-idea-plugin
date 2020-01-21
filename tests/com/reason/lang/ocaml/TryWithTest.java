package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiTry;

@SuppressWarnings("ConstantConditions")
public class TryWithTest extends BaseParsingTestCase {
    public TryWithTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testTryStructure() {
        PsiTry e = (PsiTry) firstElement(parseCode("try x with Not_found -> ()"));

        assertEquals("try", e.getFirstChild().getText());
        assertNotNull(e.getBody());
        assertEquals("x", e.getBody().getText());
        assertNotNull(e.getHandlers());
        assertSize(1, e.getHandlers());
        assertEquals("Not_found -> ()", e.getHandlers().iterator().next().getText());
    }

    public void testTryIn() {
        FileBase file = parseCode("try x with Not_found -> assert false in otherExpression");
        assertEquals(1, childrenCount(file));
    }

    public void testTryLet() {
        FileBase file = parseCode("let e = try let t = 6 with Not_found -> ()");
        assertEquals(1, childrenCount(file));
    }

    public void testTry() {
        PsiFile file = parseCode("try f() with e -> let e = CErrors.push e");
        PsiTry try_ = (PsiTry) firstElement(file);

        assertEquals("e -> let e = CErrors.push e", try_.getHandlers().iterator().next().getText());
    }
}
