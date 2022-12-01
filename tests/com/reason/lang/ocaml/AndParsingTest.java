package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.RPsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class AndParsingTest extends OclParsingTestCase {
    @Test
    public void test_let_chaining() {
        List<RPsiLet> lets = new ArrayList<>(letExpressions(parseCode("let rec lx x = x + 1 and ly y = 3 + (lx y)")));

        assertSize(2, lets);
        assertEquals("lx", lets.get(0).getName());
        assertEquals("ly", lets.get(1).getName());
    }

    @Test
    public void test_let_chaining_in_function() {
        List<RPsiLet> lets = new ArrayList<>(letExpressions(parseCode("let fn x = let ax = Instance.to_array x and ay = Instance.to_array y")));

        assertSize(1, lets);
        assertEquals("fn", lets.get(0).getName());
    }

    @Test
    public void test_module_chaining() {
        PsiFile file = parseCode("module rec X : sig end = struct end and Y : sig end = struct end");
        List<RPsiModule> mods = new ArrayList<>(moduleExpressions(file));

        assertSize(2, mods);
        assertEquals("X", mods.get(0).getName());
        assertEquals("Y", mods.get(1).getName());
    }

    @Test
    public void test_pattern_chaining() {
        PsiFile file = parseCode("match optsign with | Some sign -> let mtb1 = 1 and mtb2 = 2");
        Collection<PsiNamedElement> exps = expressions(file);

        assertInstanceOf(firstElement(file), RPsiSwitch.class);
        assertEquals(0, exps.size());
        RPsiPatternMatchBody body = PsiTreeUtil.findChildOfType(file, RPsiPatternMatchBody.class);
        assertEquals("let mtb1 = 1 and mtb2 = 2", body.getText());
        Collection<RPsiLet> lets = PsiTreeUtil.findChildrenOfType(body, RPsiLet.class);
        assertSize(2, lets);
    }

    @Test
    public void test_type_chaining() {
        Collection<RPsiType> types = typeExpressions(parseCode("type update = | NoUpdate and 'state self = {state: 'state;}"));

        assertSize(2, types);
        assertEquals("update", first(types).getName());
        assertEquals("self", second(types).getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/135
    @Test
    public void test_GH_135() {
        List<RPsiLet> lets = new ArrayList<>(letExpressions(parseCode("let f1 = function | _ -> ()\nand missing = ()")));

        assertSize(2, lets);
        assertEquals("f1", lets.get(0).getName());
        assertEquals("missing", lets.get(1).getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/175
    @Test
    public void test_GH_175() {
        List<RPsiLet> lets = new ArrayList<>(letExpressions(parseCode("let f1 = let f11 = function | _ -> \"\" in ()\n and f2 = let f21 = function | _ -> \"\" in ()\n and f3 = ()\n")));

        assertSize(3, lets);
        assertEquals("f1", lets.get(0).getName());
        assertEquals("f2", lets.get(1).getName());
        assertEquals("f3", lets.get(2).getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/271
    @Test
    public void test_GH_271() {
        List<RPsiLet> lets = new ArrayList<>(letExpressions(parseCode("let parser_of_token_list a = \nlet loop x = () in \n() \nand parser_of_symbol b = ()")));

        assertSize(2, lets);
        assertEquals("parser_of_token_list", lets.get(0).getName());
        assertEquals("parser_of_symbol", lets.get(1).getName());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/272
    @Test
    public void test_GH_272() {
        FileBase file = parseCode("let x = match xx with | Y -> let fn y = 1 in () and z = 1 ");
        List<RPsiLet> exps = letExpressions(file);

        assertEquals(2, exps.size());
        assertEquals("x", exps.get(0).getName());
        assertEquals("z", exps.get(1).getName());
        RPsiPatternMatchBody body = PsiTreeUtil.findChildOfType(file, RPsiPatternMatchBody.class);
        assertEquals("let fn y = 1", PsiTreeUtil.findChildOfType(body, RPsiLet.class).getText());
    }
}
