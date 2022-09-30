package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

@SuppressWarnings("ConstantConditions")
public class TryWithParsingTest extends OclParsingTestCase {
    @Test
    public void test_structure() {
        RPsiTry e = (RPsiTry) firstElement(parseCode("try x with Not_found -> ()"));

        assertEquals("try", e.getFirstChild().getText());
        assertNotNull(e.getBody());
        assertEquals("x", e.getBody().getText());
        assertNotNull(e.getHandlers());
        assertSize(1, e.getHandlers());
        assertEquals("Not_found -> ()", e.getHandlers().iterator().next().getText());
    }

    @Test
    public void test_in() {
        FileBase file = parseCode("try x with Not_found -> assert false in otherExpression");
        assertEquals(1, childrenCount(file));
    }

    @Test // coq/util.ml
    public void test_call() {
        RPsiTry e = firstOfType(parseCode("let system_getenv name = try Sys.getenv name with Not_found -> getenv_from_file name "), RPsiTry.class);

        assertEquals("Sys.getenv name", e.getBody().getText());
        assertSize(1, e.getHandlers());
        RPsiTryHandler handler0 = e.getHandlers().get(0);
        assertEquals("Not_found -> getenv_from_file name", handler0.getText());
        assertEquals("getenv_from_file name", handler0.getBody().getText());
    }

    @Test
    public void test_let() {
        FileBase file = parseCode("let e = try let t = 6 with Not_found -> ()");
        assertEquals(1, childrenCount(file));
    }

    @Test
    public void test_try() {
        PsiFile file = parseCode("try f() with e -> let e = CErrors.push e");
        RPsiTry try_ = (RPsiTry) firstElement(file);

        assertEquals("e -> let e = CErrors.push e", try_.getHandlers().iterator().next().getText());
    }

    @Test // coq/util.ml
    public void test_semi() {
        RPsiTry e = firstOfType(parseCode("let _ = try\n let rc = f ic in\n close_in ic;\n rc\n with e -> close_in ic; raise e"), RPsiTry.class);

        assertEquals("let rc = f ic in\n close_in ic;\n rc", e.getBody().getText());
        assertEquals("e -> close_in ic; raise e", e.getHandlers().get(0).getText());
    }

    @Test // coq/util.ml
    public void test_util() {
        RPsiTry e = firstOfType(parseCode("let getenv_from_file name =\n" +
                "  try\n" +
                "    with_ic (base ^ \"/coq_environment.txt\") (fun ic ->\n" +
                "      let rec find () =\n" +
                "        match cond with\n" +
                "        | _ -> ()\n" +
                "      in\n" +
                "        find ())\n" +
                "  with\n" +
                "  | Sys_error s -> raise Not_found\n" +
                "  | End_of_file -> raise Not_found\n"), RPsiTry.class);

        assertEquals("with_ic (base ^ \"/coq_environment.txt\") (fun ic ->\n" +
                        "      let rec find () =\n" +
                        "        match cond with\n" +
                        "        | _ -> ()\n" +
                        "      in\n" +
                        "        find ())"
                , e.getBody().getText());
        RPsiFunctionCall fc = ORUtil.findImmediateFirstChildOfClass(e.getBody(), RPsiFunctionCall.class);
        assertEquals("with_ic", fc.getName());
        assertSize(2, fc.getParameters());
    }

    @Test
    public void test_GH_256() {
        PsiFile file = parseCode("try find nt with Not_found -> (error \"Missing nt '%s' for splice\" nt; []) in let splice_prods = xxx");
        RPsiTry e = (RPsiTry) firstElement(file);
        PsiElement handlers = ORUtil.findImmediateFirstChildOfType(e, myTypes.C_TRY_HANDLERS);

        assertEquals("Not_found -> (error \"Missing nt '%s' for splice\" nt; [])", handlers.getText());
    }
}
