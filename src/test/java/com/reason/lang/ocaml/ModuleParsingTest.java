package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ModuleParsingTest extends OclParsingTestCase {
    @Test
    public void test_empty() {
        Collection<RPsiInnerModule> modules = moduleExpressions(parseCode("module M = struct end"));

        assertEquals(1, modules.size());
        RPsiInnerModule e = first(modules);
        assertEquals("M", e.getName());
        assertEquals(OclTypes.INSTANCE.A_MODULE_NAME, e.getNameIdentifier().getNode().getElementType());
        assertEquals("Dummy.M", e.getQualifiedName());
        assertEquals("struct end", e.getBody().getText());
    }

    @Test
    public void test_alias() {
        RPsiInnerModule e = firstOfType(parseCode("module M = Y"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y", e.getAlias());
        assertEquals("Y", e.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    @Test
    public void test_alias_path() {
        RPsiInnerModule e = firstOfType(parseCode("module M = Y.Z"), RPsiInnerModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y.Z", e.getAlias());
        assertEquals("Z", e.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    @Test
    public void test_module_type() {
        RPsiInnerModule e = first(moduleExpressions(parseCode("module type Intf = sig val x : bool end")));

        assertEquals("Intf", e.getName());
        assertTrue(e.isModuleType());
        assertInstanceOf(e.getBody(), RPsiModuleBinding.class);
        assertEquals("bool", firstOfType(e, RPsiVal.class).getSignature().getText());
    }

    @Test
    public void test_module_signature() {
        PsiFile file = parseCode("module Level : sig end\ntype t");

        assertEquals(2, expressions(file).size());
        RPsiInnerModule module = firstOfType(file, RPsiInnerModule.class);
        assertEquals("Level", module.getName());
        assertEquals("sig end", module.getModuleSignature().getText());
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
        assertEquals("sig end", e.getModuleSignature().getText());
        assertEquals("struct end", e.getBody().getText());
    }

    @Test
    public void test_module_constraint() {
        PsiFile file = parseCode("module Constraint : Set.S with type elt = univ_constraint\ntype t");

        assertEquals(2, expressions(file).size());
        RPsiInnerModule module = firstOfType(file, RPsiInnerModule.class);
        assertEquals("Constraint", module.getName());
        RPsiModuleSignature modType = module.getModuleSignature();
        assertEquals("Set.S", modType.getText());
    }

    @Test
    public void test_module_alias_body() {
        PsiFile file = parseCode("module M = struct module O = B.Option let _ = O.m end");

        assertEquals(1, expressions(file).size());
        RPsiInnerModule e = first(moduleExpressions(file));
        assertEquals("M", e.getName());
        Collection<PsiNamedElement> expressions = PsiTreeUtil.findChildrenOfType(e.getBody(), RPsiQualifiedPathElement.class);
        assertSize(2, expressions);
    }

    @Test
    public void test_rec_signature() {
        PsiFile file = parseCode("module rec A : sig type output = (Constr.constr * UState.t) option type task end = struct end");

        assertEquals(1, expressions(file).size());
        RPsiInnerModule e = first(moduleExpressions(file));
        assertEquals("A", e.getName());
        assertEquals("sig type output = (Constr.constr * UState.t) option type task end", e.getModuleSignature().getText());
        assertEquals("struct end", e.getBody().getText());
    }

    @Test
    public void test_decode_first_class_module() {
        RPsiInnerModule e = firstOfType(parseCode("module M = (val selectors)"), RPsiInnerModule.class);

        assertFalse(e instanceof RPsiFunctor);
        assertEquals("M", e.getName());
        assertEquals("(val selectors)", e.getBody().getText());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiVal.class));
    }

    @Test
    public void test_decode_first_class_module_in_let() {
        RPsiInnerModule e = firstOfType(parseCode("let _ = let module M = (val m : S)"), RPsiInnerModule.class);

        assertFalse(e instanceof RPsiFunctor);
        assertEquals("M", e.getName());
        assertEquals("(val m : S)", e.getBody().getText());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiVal.class));
    }

    @Test
    public void test_decode_first_class_module_with_in() {
        RPsiInnerModule e = firstOfType(parseCode("let module Visit = Visit(Repr) in printf x"), RPsiInnerModule.class);

        assertFalse(e instanceof RPsiFunctor);
        assertEquals("Visit", e.getName());
        assertEquals("Visit(Repr)", e.getBody().getText());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiVal.class));
    }

    @Test // coq::clib/cArray.ml
    public void test_signature() {
        RPsiModuleSignature e = firstOfType(parseCode("module Smart : sig val map : ('a -> 'a) -> 'a array -> 'a array end"), RPsiModuleSignature.class);

        RPsiVal ev = PsiTreeUtil.findChildOfType(e, RPsiVal.class);
        assertNoParserError(ev);
        assertEquals("map", ev.getName());
        assertEquals("('a -> 'a) -> 'a array -> 'a array", ev.getSignature().getText());
    }


    @Test
    public void test_signature_many() {
        List<RPsiModuleSignature> es = childrenOfType(parseCode("module A: sig val a: int end\n module B: sig val b: int end"), RPsiModuleSignature.class);

        RPsiModuleSignature e0 = es.get(0);
        assertNoParserError(e0);
        assertEquals("sig val a: int end", e0.getText());
        RPsiModuleSignature e1 = es.get(1);
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

        RPsiModuleSignature modType = e.getModuleSignature();
        assertEquals("module type of Vcs_.Branch", modType.getText());
        assertNull(PsiTreeUtil.findChildOfType(modType, RPsiInnerModule.class));
        assertEquals("Branch", modType.getName());
        //assertEquals("Vcs_.Branch", modType.getQualifiedName());
    }
}
