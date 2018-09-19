package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiStruct;

import java.util.Collection;

public class ModuleParsingTest extends BaseParsingTestCase {
    public ModuleParsingTest() {
        super("module", "ml", new OclParserDefinition());
    }

    public void testEmpty() {
        Collection<PsiModule> modules = moduleExpressions(parseCode("module M = struct end"));

        assertEquals(1, modules.size());
        assertEquals("M", first(modules).getName());
    }

    public void testAlias() {
        PsiModule module = first(moduleExpressions(parseCode("module M = Y")));

        assertEquals("M", module.getName());
        assertEquals("Y", module.getAlias());
    }

    public void testModuleFunctor() {
        PsiModule module = first(moduleExpressions(parseCode("module Printing = Make (struct let encode = encode_record end)")));
        PsiStruct struct = PsiTreeUtil.findChildOfType(module.getBody(), PsiStruct.class);

        assertNotNull(struct);
    }

    public void testModuleFunctor2() {
        Collection<PsiNamedElement> expressions = expressions(parseCode("module Make (M : Input) : S with type input = M.t"));

        assertEquals(1, expressions.size());
    }

    public void testModuleFunctorInstantiation() {
        PsiFile file = parseCode("module KeyTable = Hashtbl.Make(KeyHash)\ntype infos");
        Collection<PsiNamedElement> expressions = expressions(file);

        assertEquals(2, expressions.size());
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

    public void testModuleChaining() {
        PsiFile file = parseCode(" module A = sig type t end\nmodule B = struct end");

        assertEquals(2, moduleExpressions(file).size());
    }



}
