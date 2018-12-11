package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class AndParsingTest extends BaseParsingTestCase {
    public AndParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testLetChaining() {
        List<PsiLet> lets = new ArrayList(letExpressions(parseCode("let rec lx x = x + 1 and ly y = 3 + (lx y)")));

        assertEquals(2, lets.size());
        assertEquals("lx", lets.get(0).getName());
        assertEquals("ly", lets.get(1).getName());
    }

    public void testModuleChaining() {
        PsiFile file = parseCode("module rec X : sig end = struct end and Y : sig end = struct end");
        List<PsiModule> mods = new ArrayList(moduleExpressions(file));

        assertEquals(2, mods.size());
        assertEquals("X", mods.get(0).getName());
        assertEquals("Y", mods.get(1).getName());
    }

    public void testPatternChaining() {
        PsiFile file = parseCode("match optsign with | Some sign -> let mtb1 = 1 and mtb2 = 2");
        Collection<PsiNamedElement> exps = expressions(file);

        assertInstanceOf(firstElement(file), PsiSwitch.class);
        assertEquals(0, exps.size());

    }

    public void testTypeChaining() {
        Collection<PsiType> types = typeExpressions(parseCode("type update = | NoUpdate and 'state self = {state: 'state;}", true));

        assertEquals(2, types.size());
        assertEquals("update", first(types).getName());
        assertEquals("self", second(types).getName());
    }

}
