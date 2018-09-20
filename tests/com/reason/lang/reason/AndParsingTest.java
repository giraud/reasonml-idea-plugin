package com.reason.lang.reason;

import com.intellij.psi.PsiFile;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("unchecked")
public class AndParsingTest extends BaseParsingTestCase {
    public AndParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testLetChaining() {
        List<PsiLet> lets = new ArrayList(letExpressions(parseCode("let rec lx = x => x + 1 and ly = y => 3 + lx(y)")));

        assertEquals(2, lets.size());
        assertEquals("lx", lets.get(0).getName());
        assertEquals("ly", lets.get(1).getName());
    }

    public void testModuleChaining() {
        PsiFile file = parseCode("module rec X: {} = {} and Y: {} = {};");
        List<PsiModule> mods = new ArrayList(moduleExpressions(file));

        assertEquals(2, mods.size());
        assertEquals("X", mods.get(0).getName());
        assertEquals("Y", mods.get(1).getName());
    }

    /* type update = | NoUpdate and 'state self = {state: 'state;}*/
    public void testAnd() {
        PsiFile file = parseCode("type update = | NoUpdate and self('state) = {state: 'state};");
        Collection<PsiType> types = typeExpressions(file);

        assertEquals(2, types.size());
        assertEquals("update", first(types).getName());
        assertEquals("self", second(types).getName());
    }
}
