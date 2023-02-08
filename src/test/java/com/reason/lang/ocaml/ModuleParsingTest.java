package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ModuleParsingTest extends OclParsingTestCase {
    @Test
    public void test_empty() {
        Collection<RPsiModule> modules = moduleExpressions(parseCode("module M = struct end"));

        assertEquals(1, modules.size());
        RPsiInnerModule e = (RPsiInnerModule) first(modules);
        assertEquals("M", e.getName());
        assertEquals(OclTypes.INSTANCE.A_MODULE_NAME, e.getNavigationElement().getNode().getElementType());
        assertEquals("Dummy.M", e.getQualifiedName());
        assertEquals("struct end", e.getBody().getText());
    }

    @Test
    public void test_alias() {
        RPsiModule e = firstOfType(parseCode("module M = Y"), RPsiModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y", e.getAlias());
        assertEquals("Y", e.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    @Test
    public void test_alias_path() {
        RPsiModule e = firstOfType(parseCode("module M = Y.Z"), RPsiModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y.Z", e.getAlias());
        assertEquals("Z", e.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    @Test
    public void test_module_type() {
        RPsiModule e = first(moduleExpressions(parseCode("module type Intf = sig val x : bool end")));

        assertEquals("Intf", e.getName());
        assertTrue(e.isInterface());
        assertInstanceOf(e.getBody(), RPsiModuleBinding.class);
        assertEquals("bool", firstOfType(e, RPsiVal.class).getSignature().getText());
    }

    @Test
    public void test_module_signature() {
        PsiFile file = parseCode("module Level : sig end\ntype t");

        assertEquals(2, expressions(file).size());
        RPsiInnerModule module = firstOfType(file, RPsiInnerModule.class);
        assertEquals("Level", module.getName());
        assertEquals("sig end", module.getModuleType().getText());
    }

    @Test
    public void test_module_chaining() {
        PsiFile file = parseCode("module A = sig type t end\nmodule B = struct end");

        assertEquals(2, moduleExpressions(file).size());
    }

    @Test
    public void test_signature_with_constraint() {
        // From coq: PCoq
        FileBase file = parseCode("module G : sig end with type 'a Entry.e = 'a Extend.entry = struct end");

        assertEquals(1, expressions(file).size());
        RPsiInnerModule e = firstOfType(file, RPsiInnerModule.class);
        assertEquals("G", e.getName());
        assertEquals("sig end", e.getModuleType().getText());
        assertEquals("struct end", e.getBody().getText());
    }

    @Test
    public void test_module_constraint() {
        PsiFile file = parseCode("module Constraint : Set.S with type elt = univ_constraint\ntype t");

        assertEquals(2, expressions(file).size());
        RPsiInnerModule module = firstOfType(file, RPsiInnerModule.class);
        assertEquals("Constraint", module.getName());
        RPsiModuleType modType = module.getModuleType();
        assertEquals("Set.S", modType.getText());
    }

    @Test
    public void test_module_alias_body() {
        PsiFile file = parseCode("module M = struct module O = B.Option let _ = O.m end");

        assertEquals(1, expressions(file).size());
        RPsiModule e = first(moduleExpressions(file));
        assertEquals("M", e.getName());
        Collection<PsiNamedElement> expressions = e.getExpressions(ExpressionScope.pub, ExpressionFilterConstants.NO_FILTER);
        assertSize(2, expressions);
    }

    @Test
    public void test_rec_signature() {
        PsiFile file = parseCode("module rec A : sig type output = (Constr.constr * UState.t) option type task end = struct end");

        assertEquals(1, expressions(file).size());
        RPsiInnerModule e = (RPsiInnerModule) first(moduleExpressions(file));
        assertEquals("A", e.getName());
        assertEquals("sig type output = (Constr.constr * UState.t) option type task end", e.getModuleType().getText());
        assertEquals("struct end", e.getBody().getText());
    }

    @Test
    public void test_decode_first_class_module() {
        RPsiModule e = firstOfType(parseCode("module M = (val selectors)"), RPsiModule.class);

        assertFalse(e instanceof RPsiFunctor);
        assertEquals("M", e.getName());
        assertEquals("(val selectors)", e.getBody().getText());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiVal.class));
    }

    @Test
    public void test_decode_first_class_module_in_let() {
        RPsiModule e = firstOfType(parseCode("let _ = let module M = (val m : S)"), RPsiModule.class);

        assertFalse(e instanceof RPsiFunctor);
        assertEquals("M", e.getName());
        assertEquals("(val m : S)", e.getBody().getText());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiVal.class));
    }

    @Test
    public void test_decode_first_class_module_with_in() {
        RPsiModule e = firstOfType(parseCode("let module Visit = Visit(Repr) in printf x"), RPsiModule.class);

        assertFalse(e instanceof RPsiFunctor);
        assertEquals("Visit", e.getName());
        assertEquals("Visit(Repr)", e.getBody().getText());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiVal.class));
    }

    @Test // coq::clib/cArray.ml
    public void test_signature() {
        RPsiModuleType e = firstOfType(parseCode("module Smart : sig val map : ('a -> 'a) -> 'a array -> 'a array end"), RPsiModuleType.class);

        RPsiVal ev = PsiTreeUtil.findChildOfType(e, RPsiVal.class);
        assertNoParserError(ev);
        assertEquals("map", ev.getName());
        assertEquals("('a -> 'a) -> 'a array -> 'a array", ev.getSignature().getText());
    }


    @Test
    public void test_signature_many() {
        List<RPsiModuleType> es = childrenOfType(parseCode("module A: sig val a: int end\n module B: sig val b: int end"), RPsiModuleType.class);

        RPsiModuleType e0 = es.get(0);
        assertNoParserError(e0);
        assertEquals("sig val a: int end", e0.getText());
        RPsiModuleType e1 = es.get(1);
        assertNoParserError(e1);
        assertEquals("sig val b: int end", e1.getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/91
    @Test
    public void test_GH_91() {
        FileBase file = parseCode("module Branch : (module type of Vcs_.Branch with type t = Vcs_.Branch.t)\ntype id");

        assertEquals(2, expressions(file).size());
        assertEquals("Branch", first(moduleExpressions(file)).getName());
        RPsiInnerModule e = (RPsiInnerModule) expressions(file).iterator().next();
        RPsiModuleType modType = e.getModuleType();
        assertEquals("module type of Vcs_.Branch", modType.getText());
        assertNull(modType.getName());
    }
}
