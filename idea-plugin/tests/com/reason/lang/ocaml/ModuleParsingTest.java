package com.reason.lang.ocaml;

import java.util.*;
import com.intellij.execution.testframework.Filter;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.ExpressionFilterConstants;
import com.reason.lang.core.psi.ExpressionScope;
import com.reason.lang.core.psi.PsiModule;

public class ModuleParsingTest extends OclParsingTestCase {
    public void test_empty() {
        Collection<PsiModule> modules = moduleExpressions(parseCode("module M = struct end"));

        assertEquals(1, modules.size());
        assertEquals("M", first(modules).getName());
        assertEquals("Dummy.M", first(modules).getQualifiedName());
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
        assertNotNull(e.getValExpression("x"));
    }

    public void test_moduleSig() {
        PsiFile file = parseCode("module Level : sig end\ntype t");

        assertEquals(2, expressions(file).size());
        assertEquals("Level", first(moduleExpressions(file)).getName());
    }

    public void test_moduleSig2() {
        PsiFile file = parseCode("module Constraint : Set.S with type elt = univ_constraint\ntype t");

        assertEquals(2, expressions(file).size());
        assertEquals("Constraint", first(moduleExpressions(file)).getName());
    }

    public void test_moduleSig3() {
        FileBase file = parseCode("module Branch : (module type of Vcs_.Branch with type t = Vcs_.Branch.t)\ntype id");

        assertEquals(2, expressions(file).size());
        assertEquals("Branch", first(moduleExpressions(file)).getName());
    }

    public void test_moduleChaining() {
        PsiFile file = parseCode("module A = sig type t end\nmodule B = struct end");

        assertEquals(2, moduleExpressions(file).size());
    }

    public void test_signatureWithConstraints() {
        FileBase file = parseCode("module G : sig end with type 'a Entry.e = 'a Extend.entry = struct end"); // From coq: PCoq

        assertEquals(1, expressions(file).size());
        assertEquals("G", first(moduleExpressions(file)).getName());
    }

    public void test_moduleAliasBody() {
        PsiFile file = parseCode("module M = struct module O = B.Option let _ = O.m end");

        assertEquals(1, expressions(file).size());
        PsiModule e = first(moduleExpressions(file));
        assertEquals("M", e.getName());
        Collection<PsiNameIdentifierOwner> expressions = e.getExpressions(ExpressionScope.pub, ExpressionFilterConstants.NO_FILTER);
        assertSize(2, expressions);
    }
}
