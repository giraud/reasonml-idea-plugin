package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.impl.*;

@SuppressWarnings("ConstantConditions")
public class TryWithTest extends OclParsingTestCase {
    public void test_structure() {
        PsiTry e = (PsiTry) firstElement(parseCode("try x with Not_found -> ()"));

        assertEquals("try", e.getFirstChild().getText());
        assertNotNull(e.getBody());
        assertEquals("x", e.getBody().getText());
        assertNotNull(e.getHandlers());
        assertSize(1, e.getHandlers());
        assertEquals("Not_found -> ()", e.getHandlers().iterator().next().getText());
    }

    public void test_in() {
        FileBase file = parseCode("try x with Not_found -> assert false in otherExpression");
        assertEquals(1, childrenCount(file));
    }

    public void test_let() {
        FileBase file = parseCode("let e = try let t = 6 with Not_found -> ()");
        assertEquals(1, childrenCount(file));
    }

    public void test_try() {
        PsiFile file = parseCode("try f() with e -> let e = CErrors.push e");
        PsiTry try_ = (PsiTry) firstElement(file);

        assertEquals("e -> let e = CErrors.push e", try_.getHandlers().iterator().next().getText());
    }

    public void test_GH_256() {
        PsiFile file =
                parseCode(
                        "try find nt with Not_found -> (error \"Missing nt '%s' for splice\" nt; []) in let splice_prods = xxx");
        PsiTry e = (PsiTry) firstElement(file);
        PsiElement handlers = ORUtil.findImmediateFirstChildOfType(e, m_types.C_TRY_HANDLERS);

        assertEquals("Not_found -> (error \"Missing nt '%s' for splice\" nt; [])", handlers.getText());
    }
}
