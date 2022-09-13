package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ModuleParsingTest extends OclParsingTestCase {
    public void test_empty() {
        Collection<PsiModule> modules = moduleExpressions(parseCode("module M = struct end"));

        assertEquals(1, modules.size());
        PsiInnerModule e = (PsiInnerModule) first(modules);
        assertEquals("M", e.getName());
        assertEquals(OclTypes.INSTANCE.A_MODULE_NAME, e.getNavigationElement().getNode().getElementType());
        assertEquals("Dummy.M", e.getQualifiedName());
        assertEquals("struct end", e.getBody().getText());
    }

    public void test_alias() {
        PsiModule e = firstOfType(parseCode("module M = Y"), PsiModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y", e.getAlias());
        assertEquals("Y", e.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    public void test_alias_path() {
        PsiModule e = firstOfType(parseCode("module M = Y.Z"), PsiModule.class);

        assertEquals("M", e.getName());
        assertEquals("Y.Z", e.getAlias());
        assertEquals("Z", e.getAliasSymbol().getText());
        assertEquals(myTypes.A_MODULE_NAME, e.getAliasSymbol().getNode().getElementType());
    }

    public void test_module_type() {
        PsiModule e = first(moduleExpressions(parseCode("module type Intf = sig val x : bool end")));

        assertEquals("Intf", e.getName());
        assertTrue(e.isInterface());
        assertInstanceOf(e.getBody(), PsiModuleBinding.class);
        assertEquals("bool", firstOfType(e, PsiVal.class).getSignature().getText());
    }

    public void test_module_signature() {
        PsiFile file = parseCode("module Level : sig end\ntype t");

        assertEquals(2, expressions(file).size());
        PsiInnerModule module = firstOfType(file, PsiInnerModule.class);
        assertEquals("Level", module.getName());
        assertEquals("sig end", module.getModuleType().getText());
    }

    public void test_module_chaining() {
        PsiFile file = parseCode("module A = sig type t end\nmodule B = struct end");

        assertEquals(2, moduleExpressions(file).size());
    }

    public void test_signature_with_constraint() {
        // From coq: PCoq
        FileBase file = parseCode("module G : sig end with type 'a Entry.e = 'a Extend.entry = struct end");

        assertEquals(1, expressions(file).size());
        PsiInnerModule e = firstOfType(file, PsiInnerModule.class);
        assertEquals("G", e.getName());
        assertEquals("sig end", e.getModuleType().getText());
        assertEquals("struct end", e.getBody().getText());
    }

    public void test_module_constraint() {
        PsiFile file = parseCode("module Constraint : Set.S with type elt = univ_constraint\ntype t");

        assertEquals(2, expressions(file).size());
        PsiInnerModule module = firstOfType(file, PsiInnerModule.class);
        assertEquals("Constraint", module.getName());
        PsiModuleType modType = module.getModuleType();
        assertEquals("Set.S", modType.getText());
    }

    public void test_module_alias_body() {
        PsiFile file = parseCode("module M = struct module O = B.Option let _ = O.m end");

        assertEquals(1, expressions(file).size());
        PsiModule e = first(moduleExpressions(file));
        assertEquals("M", e.getName());
        Collection<PsiNamedElement> expressions = e.getExpressions(ExpressionScope.pub, ExpressionFilterConstants.NO_FILTER);
        assertSize(2, expressions);
    }

    public void test_rec_signature() {
        PsiFile file = parseCode("module rec A : sig type output = (Constr.constr * UState.t) option type task end = struct end");

        assertEquals(1, expressions(file).size());
        PsiInnerModule e = (PsiInnerModule) first(moduleExpressions(file));
        assertEquals("A", e.getName());
        assertEquals("sig type output = (Constr.constr * UState.t) option type task end", e.getModuleType().getText());
        assertEquals("struct end", e.getBody().getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/91
    public void test_GH_91() {
        FileBase file = parseCode("module Branch : (module type of Vcs_.Branch with type t = Vcs_.Branch.t)\ntype id");

        assertEquals(2, expressions(file).size());
        assertEquals("Branch", first(moduleExpressions(file)).getName());
        PsiInnerModule e = (PsiInnerModule) expressions(file).iterator().next();
        PsiModuleType modType = e.getModuleType();
        assertEquals("module type of Vcs_.Branch", modType.getText());
        assertNull(modType.getName());
    }

    public void test_decode_first_class_module() {
        PsiModule e = firstOfType(parseCode("module M = (val selectors)"), PsiModule.class);

        assertFalse(e instanceof PsiFunctor);
        assertEquals("M", e.getName());
        assertEquals("(val selectors)", e.getBody().getText());
        assertNull(PsiTreeUtil.findChildOfType(e, PsiVal.class));
    }
}
