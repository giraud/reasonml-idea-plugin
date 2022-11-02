package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctionCallParsingTest extends OclParsingTestCase {
    @Test
    public void test_call() {
        RPsiLetBinding e = first(letExpressions(parseCode("let _ = string_of_int 1"))).getBinding();

        RPsiFunctionCall call = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        assertEquals("string_of_int 1", call.getText());
        assertEquals(1, call.getParameters().size());
        assertEquals("1", call.getParameters().get(0).getText());
    }

    @Test
    public void test_call_ints() {
        RPsiFunctionCall e = PsiTreeUtil.findChildOfType(parseCode("add 1 2"), RPsiFunctionCall.class);

        assertEquals("add 1 2", e.getText());
        assertEquals(2, e.getParameters().size());
        assertEquals("1", e.getParameters().get(0).getText());
        assertEquals("2", e.getParameters().get(1).getText());
    }

    @Test
    public void test_call_floats() {
        RPsiFunctionCall e = PsiTreeUtil.findChildOfType(parseCode("add 1. 2."), RPsiFunctionCall.class);

        assertEquals("add 1. 2.", e.getText());
        assertEquals(2, e.getParameters().size());
        assertEquals("1.", e.getParameters().get(0).getText());
        assertEquals("2.", e.getParameters().get(1).getText());
    }

    @Test
    public void test_call_many() {
        RPsiLetBinding e = first(letExpressions(parseCode("let _ = fn a b c"))).getBinding();

        RPsiFunctionCall call = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        assertEquals("fn a b c", call.getText());
        assertEquals(3, call.getParameters().size());
        assertEquals("a", call.getParameters().get(0).getText());
        assertEquals("b", call.getParameters().get(1).getText());
        assertEquals("c", call.getParameters().get(2).getText());
    }

    @Test
    public void test_inner_call() {
        RPsiLetBinding e = first(letExpressions(parseCode("let _ = fn a (b \"{\" c) d"))).getBinding();

        RPsiFunctionCall f = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        List<RPsiParameterReference> p = f.getParameters();
        assertEquals("fn a (b \"{\" c) d", f.getText());
        assertEquals(3, p.size());
        assertEquals("a", p.get(0).getText());
        assertEquals("(b \"{\" c)", p.get(1).getText());
        assertEquals("d", p.get(2).getText());
        RPsiFunctionCall f1 = PsiTreeUtil.findChildOfType(p.get(1), RPsiFunctionCall.class);
        assertEquals("b \"{\" c", f1.getText());
        assertEquals(2, f1.getParameters().size());
        assertEquals("\"{\"", f1.getParameters().get(0).getText());
        assertEquals("c", f1.getParameters().get(1).getText());
    }

    @Test
    public void test_call_02() {
        RPsiFunctionCall e = firstOfType(parseCode("let _ = hov 0 (anomaly_string () ++ str \"xxx\")"), RPsiFunctionCall.class);

        assertEquals("hov 0 (anomaly_string () ++ str \"xxx\")", e.getText());
        List<RPsiParameterReference> ep = e.getParameters();
        assertSize(2, ep);
        assertEquals("(anomaly_string () ++ str \"xxx\")", ep.get(1).getText());
        List<RPsiFunctionCall> ee = new ArrayList<>(PsiTreeUtil.findChildrenOfType(ep.get(1), RPsiFunctionCall.class));
        assertSize(2, ee);
        assertEquals("anomaly_string ()", ee.get(0).getText());
        assertEquals("str \"xxx\"", ee.get(1).getText());
    }

    @Test
    public void test_call_03() {
        RPsiFunctionCall e = firstOfType(parseCode("let _ = hov 0 (str \"xxx\" ++ str txt)"), RPsiFunctionCall.class);

        assertEquals("hov 0 (str \"xxx\" ++ str txt)", e.getText());
        List<RPsiParameterReference> ep = e.getParameters();
        assertSize(2, ep);
        assertEquals("(str \"xxx\" ++ str txt)", ep.get(1).getText());
        List<RPsiFunctionCall> ee = new ArrayList<>(PsiTreeUtil.findChildrenOfType(ep.get(1), RPsiFunctionCall.class));
        assertSize(2, ee);
        assertEquals("str \"xxx\"", ee.get(0).getText());
        assertEquals("str txt", ee.get(1).getText());
    }

    @Test
    public void test_call_04() { // env.ml L39
        RPsiFunctionCall e = firstOfType(parseCode("let _ = Util.check_file_else ~dir:Coq_config.coqlibsuffix ~file:prelude"), RPsiFunctionCall.class);

        assertSize(2, e.getParameters());
        RPsiParameterReference p0 = e.getParameters().get(0);
        assertEquals("~dir:Coq_config.coqlibsuffix", p0.getText());
        assertEquals("dir", p0.getName());
        assertEquals("Coq_config.coqlibsuffix", p0.getValue().getText());
        RPsiParameterReference p1 = e.getParameters().get(1);
        assertEquals("~file:prelude", p1.getText());
        assertEquals("file", p1.getName());
        assertEquals("prelude", p1.getValue().getText());
    }

    @Test
    public void test_call_05() {
        RPsiFunctionCall e = firstOfType(parseCode("let _ = f1 \"x\" (1)"), RPsiFunctionCall.class);

        List<RPsiParameterReference> ps = e.getParameters();
        assertSize(2, ps);
        assertEquals("\"x\"", ps.get(0).getText());
        assertEquals("(1)", ps.get(1).getText());
    }

    @Test
    public void test_call_06() {
        RPsiFunctionCall e = firstOfType(parseCode("let _ = print_usage_common co (\"Usage:\" ^ executable_name ^ \" < options > \" ^ extra_args ^ \"\n\n\")"), RPsiFunctionCall.class);

        List<RPsiParameterReference> ps = e.getParameters();
        assertSize(2, ps);
        assertEquals("co", ps.get(0).getText());
        assertEquals("(\"Usage:\" ^ executable_name ^ \" < options > \" ^ extra_args ^ \"\n\n\")", ps.get(1).getText());
    }

    @Test
    public void test_call_07() {
        RPsiFunctionCall e = firstOfType(parseCode("let _ = sscanf l \"%[^=]=%S\" (fun name value -> Some(name))"),RPsiFunctionCall.class);

        List<RPsiParameterReference> ps = e.getParameters();
        assertSize(3, ps);
        assertEquals("l", ps.get(0).getText());
        assertEquals("\"%[^=]=%S\"", ps.get(1).getText());
        RPsiParameterReference p2 = ps.get(2);
        RPsiFunction f = ORUtil.findImmediateFirstChildOfClass(p2, RPsiFunction.class);
        assertEquals("fun name value -> Some(name)", f.getText());
    }

    @Test // coq::checker/analyze.ml
    public void test_parens_01() {
        RPsiLet e = firstOfType(parseCode("let memory = make size (Struct ((-1), [||]))"), RPsiLet.class);

        RPsiFunctionCall fc = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        assertEquals("make", fc.getName());
        assertSize(2, fc.getParameters());
        assertEquals("size", fc.getParameters().get(0).getText());
        assertEquals("(Struct ((-1), [||]))", fc.getParameters().get(1).getText());
        assertEquals("make size (Struct ((-1), [||]))", e.getBinding().getText());
        assertContainsElements(extractUpperSymbolTypes(e), myTypes.A_VARIANT_NAME);
    }

    @Test // coq::checker/votour.ml
    public void test_parens_02() {
        RPsiPatternMatchBody e = firstOfType(parseCode("let _ = match cond with | BLOCK -> loop tl (1 :: pos) ((v, hd, 0 :: pos) :: accu) |_ -> raise_notrace Exit"), RPsiPatternMatchBody.class);

        RPsiFunctionCall fc = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        assertEquals("loop", fc.getName());
        assertSize(3, fc.getParameters());
    }

    @Test
    public void test_xxx() {
        RPsiFunctionCall e = firstOfType(parseCode("let _ = list_iteri (fun i ((start, stop), value) -> tree.(k) <- (i, Some i))"), RPsiFunctionCall.class);
        RPsiFunction ef = PsiTreeUtil.findChildOfType(e, RPsiFunction.class);

        assertSize(1, e.getParameters());
        assertEquals("(fun i ((start, stop), value) -> tree.(k) <- (i, Some i))", e.getParameters().get(0).getText());
        assertEquals("tree.(k) <- (i, Some i)", ef.getBody().getText());
    }
}
