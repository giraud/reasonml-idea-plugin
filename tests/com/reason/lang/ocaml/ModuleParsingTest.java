package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class ModuleParsingTest extends OclParsingTestCase {
    public void test_empty() {
        Collection<PsiModule> modules = moduleExpressions(parseCode("module M = struct end"));

        assertEquals(1, modules.size());
        PsiInnerModule e = (PsiInnerModule) first(modules);
        assertEquals("M", e.getName());
        assertEquals("Dummy.M", e.getQualifiedName());
        assertEquals("struct end", e.getBody().getText());
    }

    public void test_alias() {
        PsiModule module = first(moduleExpressions(parseCode("module M = Y")));

        assertEquals("M", module.getName());
        assertEquals("Y", module.getAlias());
    }

    public void test_aliasPath() {
        PsiModule module = first(moduleExpressions(parseCode("module M = Y.Z")));

        assertEquals("M", module.getName());
        assertEquals("Y.Z", module.getAlias());
    }

    public void test_moduleType() {
        PsiModule e = first(moduleExpressions(parseCode("module type Intf = sig val x : bool end")));

        assertEquals("Intf", e.getName());
        assertTrue(e.isInterface());
        assertNotNull(e.getValExpression("x"));
    }

    public void test_moduleSig() {
        PsiFile file = parseCode("module Level : sig end\ntype t");

        assertEquals(2, expressions(file).size());
        PsiInnerModule module = firstOfType(file, PsiInnerModule.class);
        assertEquals("Level", module.getName());
        assertEquals("sig end", module.getModuleType().getText());
    }

    public void test_moduleSig2() {
        PsiFile file = parseCode("module Constraint : Set.S with type elt = univ_constraint\ntype t");

        assertEquals(2, expressions(file).size());
        PsiInnerModule module = firstOfType(file, PsiInnerModule.class);
        assertEquals("Constraint", module.getName());
        PsiModuleType modType = module.getModuleType();
        assertEquals("Set.S", modType.getText());
    }

    public void test_moduleChaining() {
        PsiFile file = parseCode("module A = sig type t end\nmodule B = struct end");

        assertEquals(2, moduleExpressions(file).size());
    }

    public void test_signatureWithConstraints() {
        // From coq: PCoq
        FileBase file = parseCode("module G : sig end with type 'a Entry.e = 'a Extend.entry = struct end");

        assertEquals(1, expressions(file).size());
        PsiInnerModule e = firstOfType(file, PsiInnerModule.class);
        assertEquals("G", e.getName());
        assertEquals("sig end", e.getModuleType().getText());
        assertEquals("struct end", e.getBody().getText());
    }

    public void test_moduleAliasBody() {
        PsiFile file = parseCode("module M = struct module O = B.Option let _ = O.m end");

        assertEquals(1, expressions(file).size());
        PsiModule e = first(moduleExpressions(file));
        assertEquals("M", e.getName());
        Collection<PsiNamedElement> expressions = e.getExpressions(ExpressionScope.pub, ExpressionFilterConstants.NO_FILTER);
        assertSize(2, expressions);
    }

    public void test_recSig() {
        PsiFile file = parseCode("module rec A : sig type output = (Constr.constr * UState.t) option type task end = struct end");

        assertEquals(1, expressions(file).size());
        PsiInnerModule e = (PsiInnerModule) first(moduleExpressions(file));
        assertEquals("A", e.getName());
        assertEquals("sig type output = (Constr.constr * UState.t) option type task end", e.getModuleType().getText());
        assertEquals("struct end", e.getBody().getText());
    }

    // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/91
    public void test_GH_91() {
        FileBase file = parseCode("module Branch : (module type of Vcs_.Branch with type t = Vcs_.Branch.t)\ntype id");

        assertEquals(2, expressions(file).size());
        assertEquals("Branch", first(moduleExpressions(file)).getName());
        PsiInnerModule e = (PsiInnerModule) expressions(file).iterator().next();
        PsiModuleType modType = e.getModuleType();
        assertEquals("module type of Vcs_.Branch", modType.getText());
        assertEmpty(PsiTreeUtil.findChildrenOfType(modType, PsiUpperIdentifier.class));
        assertNull(modType.getName());
    }
}
