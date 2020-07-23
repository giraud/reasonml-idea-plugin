package com.reason.lang.ocaml;

import java.util.*;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiModule;

public class ModuleParsingTest extends BaseParsingTestCase {
    public ModuleParsingTest() {
        super("module", "ml", new OclParserDefinition());
    }

    public void testEmpty() {
        Collection<PsiModule> modules = moduleExpressions(parseCode("module M = struct end"));

        assertEquals(1, modules.size());
        assertEquals("M", first(modules).getName());
        assertEquals("Dummy.M", first(modules).getQualifiedName());
    }

    public void testAlias() {
        PsiModule module = first(moduleExpressions(parseCode("module M = Y")));

        assertEquals("M", module.getName());
        assertEquals("Y", module.getAlias());
    }

    public void testAliasPath() {
        PsiModule module = first(moduleExpressions(parseCode("module M = Y.Z")));

        assertEquals("M", module.getName());
        assertEquals("Y.Z", module.getAlias());
    }

    public void testModuleType() {
        PsiModule module = first(moduleExpressions(parseCode("module type RedFlagsSig = sig end")));

        assertEquals("RedFlagsSig", module.getName());
    }

    public void testModuleSig() {
        PsiFile file = parseCode("module Level : sig end\ntype t");

        assertEquals(2, expressions(file).size());
        assertEquals("Level", first(moduleExpressions(file)).getName());
    }

    public void testModuleSig2() {
        PsiFile file = parseCode("module Constraint : Set.S with type elt = univ_constraint\ntype t");

        assertEquals(2, expressions(file).size());
        assertEquals("Constraint", first(moduleExpressions(file)).getName());
    }

    public void testModuleSig3() {
        FileBase file = parseCode("module Branch : (module type of Vcs_.Branch with type t = Vcs_.Branch.t)\ntype id");

        assertEquals(2, expressions(file).size());
        assertEquals("Branch", first(moduleExpressions(file)).getName());
    }

    public void testModuleChaining() {
        PsiFile file = parseCode("module A = sig type t end\nmodule B = struct end");

        assertEquals(2, moduleExpressions(file).size());
    }

    public void testSignatureWithConstraints() {
        FileBase file = parseCode("module G : sig end with type 'a Entry.e = 'a Extend.entry = struct end"); // From coq: PCoq

        assertEquals(1, expressions(file).size());
        assertEquals("G", first(moduleExpressions(file)).getName());
    }
}
