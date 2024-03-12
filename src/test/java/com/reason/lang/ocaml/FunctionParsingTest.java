package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctionParsingTest extends OclParsingTestCase {
    @Test
    public void test_single_param() {
        RPsiLet e = firstOfType(parseCode("let fn x = x"), RPsiLet.class);

        assertTrue(e.isFunction());
        RPsiFunction function = e.getFunction();
        List<RPsiParameterDeclaration> parameters = function.getParameters();
        assertSize(1, parameters);
        assertInstanceOf(first(parameters).getNameIdentifier(), RPsiLowerSymbol.class);
        assertEquals("Dummy.fn[x]", first(parameters).getQualifiedName());
        assertNotNull(function.getBody());
    }

    @Test
    public void test_multiple_params() {
        RPsiLet e = firstOfType(parseCode("let add x y = x + y"), RPsiLet.class);

        assertTrue(e.isFunction());
        RPsiFunction function = e.getFunction();
        assertSize(2, function.getParameters());
        assertInstanceOf(first(function.getParameters()).getNameIdentifier(), RPsiLowerSymbol.class);
        assertInstanceOf(second(function.getParameters()).getNameIdentifier(), RPsiLowerSymbol.class);
        assertNotNull(function.getBody());
    }

    @Test
    public void test_let_binding() {
        RPsiLet e = firstOfType(parseCode("let getAttributes node = let attr = \"r\" in attr"), RPsiLet.class);

        assertTrue(e.isFunction());
        RPsiFunction function = e.getFunction();
        assertSize(1, function.getParameters());
        assertNotNull(function.getBody());
    }

    @Test
    public void test_let_binding_2() {
        RPsiLet e = firstOfType(parseCode("let visit_vo f = Printf.printf \"a\"; Printf.printf \"b\""), RPsiLet.class);

        assertTrue(e.isFunction());
        RPsiFunction function = e.getFunction();
        assertEquals("Printf.printf \"a\"; Printf.printf \"b\"", function.getBody().getText());
    }

    @Test
    public void test_fun() {
        RPsiLet e = firstOfType(parseCode("let _ = fun (_, info as ei) -> x"), RPsiLet.class);

        assertTrue(e.isFunction());
        RPsiFunction function = e.getFunction();
        assertSize(1, function.getParameters());
        assertEquals("(_, info as ei)", PsiTreeUtil.findChildOfType(function, RPsiParameters.class).getText());
        assertEquals("x", function.getBody().getText());
    }

    @Test
    public void test_fun_signature() {
        RPsiLet e = firstOfType(parseCode("let _: int -> int = fun x y -> x + y"), RPsiLet.class);

        assertTrue(e.isFunction());
        assertEquals("fun x y -> x + y", e.getBinding().getText());
        RPsiFunction f = e.getFunction();
        List<RPsiParameterDeclaration> p = f.getParameters();
        assertSize(2, p);
        assertEquals("x", p.get(0).getText());
        assertEquals("y", p.get(1).getText());
        assertEquals("x + y", f.getBody().getText());
    }

    @Test
    public void test_complex_params() {
        RPsiLet e = firstOfType(parseCode("let resolve_morphism env ?(hook=(fun _ -> ())) args' (b, cStr) = let x = 1"), RPsiLet.class);

        assertNoParserError(e);
        assertTrue(e.isFunction());
        RPsiFunction ef = e.getFunction();
        assertSize(4, ef.getParameters());
        assertEquals("env", ef.getParameters().get(0).getText());
        assertEquals("?(hook=(fun _ -> ()))", ef.getParameters().get(1).getText());
        assertEquals("args'", ef.getParameters().get(2).getText());
        assertEquals("(b, cStr)", ef.getParameters().get(3).getText());
        assertEquals("let x = 1", ef.getBody().getText());
    }

    @Test
    public void test_parameters_named_symbols() {
        RPsiLet e = firstOfType(parseCode("let make ~id:(id:string)  ~values:(values:'a Js.t option) children) = 1"), RPsiLet.class);

        RPsiFunction function = (RPsiFunction) e.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> parameters = new ArrayList<>(function.getParameters());
        assertSize(3, parameters);

        assertEquals("id", parameters.get(0).getName());
        assertEquals("values", parameters.get(1).getName());
        assertEquals("children", parameters.get(2).getName());
    }

    @Test
    public void test_rollback() {
        RPsiFunction f = firstOfType(parseCode("let _ = let x = 1 in let y = 2 in fun () -> 3"), RPsiFunction.class); // test infinite rollback
        assertEquals("fun () -> 3", f.getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/291
    @Test
    public void test_GH_291() {
        RPsiLet e = firstOfType(parseCode("let fn = function | OpenedModule -> true | _ -> false"), RPsiLet.class);

        assertTrue(e.isFunction());
        assertEquals("function | OpenedModule -> true | _ -> false", e.getBinding().getText());
        RPsiFunction f = e.getFunction();
        assertEquals("| OpenedModule -> true | _ -> false", f.getBody().getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/456
    @Test
    public void test_GH_456() {
        RPsiLet e = firstOfType(parseCode("let append ~param1 param2 = param1 + param2"), RPsiLet.class);

        RPsiFunction ef = (RPsiFunction) e.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> efps = new ArrayList<>(ef.getParameters());
        assertSize(2, efps);

        RPsiParameterDeclaration efp0 = efps.get(0);
        assertEquals("param1", efp0.getName());
        RPsiParameterDeclaration efp1 = efps.get(1);
        assertEquals("param2", efp1.getName());

        assertEquals("param1 + param2", ef.getBody().getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/456
    @Test
    public void test_GH_456_tuples() {
        RPsiLet e = firstOfType(parseCode("let append ~combine (b1,ll1) (b2,ll2) = (combine b1 b2, ll1 @ ll2)"), RPsiLet.class);

        RPsiFunction ef = (RPsiFunction) e.getBinding().getFirstChild();
        List<RPsiParameterDeclaration> efps = new ArrayList<>(ef.getParameters());
        assertSize(3, efps);

        RPsiParameterDeclaration efp0 = efps.get(0);
        assertEquals("combine", efp0.getName());
        RPsiParameterDeclaration efp1 = efps.get(1);
        assertEquals("(b1,ll1)", efp1.getText());
        RPsiParameterDeclaration efp2 = efps.get(2);
        assertEquals("(b2,ll2)", efp2.getText());

        assertEquals("(combine b1 b2, ll1 @ ll2)", ef.getBody().getText());
    }
}
