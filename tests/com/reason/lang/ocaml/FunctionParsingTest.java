package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunctionParsingTest extends OclParsingTestCase {
    @Test
    public void test_single_param() {
        RPsiLet e = first(letExpressions(parseCode("let fn x = x")));

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
        RPsiLet e = first(letExpressions(parseCode("let add x y = x + y")));

        assertTrue(e.isFunction());
        RPsiFunction function = e.getFunction();
        assertSize(2, function.getParameters());
        assertInstanceOf(first(function.getParameters()).getNameIdentifier(), RPsiLowerSymbol.class);
        assertInstanceOf(second(function.getParameters()).getNameIdentifier(), RPsiLowerSymbol.class);
        assertNotNull(function.getBody());
    }

    @Test
    public void test_let_binding() {
        RPsiLet e = first(letExpressions(parseCode("let getAttributes node = let attr = \"r\" in attr")));

        assertTrue(e.isFunction());
        RPsiFunction function = e.getFunction();
        assertSize(1, function.getParameters());
        assertNotNull(function.getBody());
    }

    @Test
    public void test_let_binding_2() {
        RPsiLet e = first(letExpressions(parseCode("let visit_vo f = Printf.printf \"a\"; Printf.printf \"b\"")));

        assertTrue(e.isFunction());
        RPsiFunction function = e.getFunction();
        assertEquals("Printf.printf \"a\"; Printf.printf \"b\"", function.getBody().getText());
    }

    @Test
    public void test_fun() {
        RPsiLet e = first(letExpressions(parseCode("let _ = fun (_, info as ei) -> x")));

        assertTrue(e.isFunction());
        RPsiFunction function = e.getFunction();
        assertSize(1, function.getParameters());
        assertEquals("(_, info as ei)", PsiTreeUtil.findChildOfType(function, RPsiParameters.class).getText());
        assertEquals("x", function.getBody().getText());
    }

    @Test
    public void test_fun_signature() {
        RPsiLet e = first(letExpressions(parseCode("let _: int -> int = fun x y -> x + y")));

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
        Collection<PsiNamedElement> expressions = expressions(parseCode(
                "let resolve_morphism env ?(fnewt=fun x -> x) args' (b,cstr) = let x = 1"));

        assertSize(1, expressions);
        RPsiLet let = (RPsiLet) first(expressions);
        assertTrue(let.isFunction());
        assertSize(4, let.getFunction().getParameters());
        assertEquals("let x = 1", let.getFunction().getBody().getText());
    }

    @Test
    public void test_rollback() {
        RPsiFunction f = firstOfType(parseCode("let _ = let x = 1 in let y = 2 in fun () -> 3"), RPsiFunction.class); // test infinite rollback
        assertEquals("fun () -> 3", f.getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/291
    @Test
    public void test_GH_291() {
        RPsiLet e = first(letExpressions(parseCode("let fn = function | OpenedModule -> true | _ -> false")));

        assertTrue(e.isFunction());
        assertEquals("function | OpenedModule -> true | _ -> false", e.getBinding().getText());
        RPsiFunction f = e.getFunction();
        assertEquals("| OpenedModule -> true | _ -> false", f.getBody().getText());
    }
}
