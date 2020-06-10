package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiSwitch;
import com.reason.lang.core.psi.PsiType;

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

        assertSize(2, lets);
        assertEquals("lx", lets.get(0).getName());
        assertEquals("ly", lets.get(1).getName());
    }

    public void testModuleChaining() {
        PsiFile file = parseCode("module rec X : sig end = struct end and Y : sig end = struct end");
        List<PsiInnerModule> mods = new ArrayList(moduleExpressions(file));

        assertSize(2, mods);
        assertEquals("X", mods.get(0).getName());
        assertEquals("Y", mods.get(1).getName());
    }

    public void testPatternChaining() {
        PsiFile file = parseCode("match optsign with | Some sign -> let mtb1 = 1 and mtb2 = 2");
        Collection<PsiNameIdentifierOwner> exps = expressions(file);

        assertInstanceOf(firstElement(file), PsiSwitch.class);
        assertEquals(0, exps.size());

    }

    public void testTypeChaining() {
        Collection<PsiType> types = typeExpressions(parseCode("type update = | NoUpdate and 'state self = {state: 'state;}"));

        assertSize(2, types);
        assertEquals("update", first(types).getName());
        assertEquals("self", second(types).getName());
    }

    public void testGH_135() {
        List<PsiLet> lets = new ArrayList(letExpressions(parseCode("let f1 = function | _ -> ()\nand missing = ()")));

        assertSize(2, lets);
        assertEquals("f1", lets.get(0).getName());
        assertEquals("missing", lets.get(1).getName());
    }

    public void testGH_175() {
        List<PsiLet> lets = new ArrayList(letExpressions(parseCode("let f1 = let f11 = function | _ -> \"\" in ()\n" +
                "and f2 = let f21 = function | _ -> \"\" in ()\n" +
                "and f3 = ()\n")));

        assertSize(3, lets);
        assertEquals("f1", lets.get(0).getName());
        assertEquals("f2", lets.get(1).getName());
        assertEquals("f3", lets.get(2).getName());
    }
}
