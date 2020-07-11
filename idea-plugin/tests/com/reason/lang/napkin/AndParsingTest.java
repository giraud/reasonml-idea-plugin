package com.reason.lang.napkin;

import java.util.*;
import com.intellij.psi.PsiFile;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiType;

@SuppressWarnings("unchecked")
public class AndParsingTest extends NsParsingTestCase {
    public void test_letChaining() {
        List<PsiLet> lets = new ArrayList<>(letExpressions(parseCode("let rec lx = x => x + 1 and ly = y => 3 + lx(y)")));

        assertEquals(2, lets.size());
        assertEquals("lx", lets.get(0).getName());
        assertEquals("ly", lets.get(1).getName());
    }

    public void test_moduleChaining() {
        PsiFile file = parseCode("module rec X: {} = {} and Y: {} = {}");
        List<PsiModule> mods = new ArrayList<>(moduleExpressions(file));

        assertEquals(2, mods.size());
        assertEquals("X", mods.get(0).getName());
        assertEquals("Y", mods.get(1).getName());
    }

    /* type update = | NoUpdate and 'state self = {state: 'state;}*/
    public void test_and() {
        PsiFile file = parseCode("type update = | NoUpdate and self<'state> = {state: 'state}");
        Collection<PsiType> types = typeExpressions(file);

        assertEquals(2, types.size());
        assertEquals("update", first(types).getName());
        assertEquals("self", second(types).getName());
    }
}
