package com.reason.lang.reason;

import java.util.*;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;

@SuppressWarnings("ConstantConditions")
public class FunParsingTest extends BaseParsingTestCase {
    public FunParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testFun() {
        PsiLet e = first(letExpressions(parseCode("let timeUnitToString = fun | Second => \"s\" | Minute => \"m\" | Hour => \"h\";")));

        PsiLetBinding binding = e.getBinding();
        assertEquals("fun | Second => \"s\" | Minute => \"m\" | Hour => \"h\"", binding.getText());
    }

    public void testChaining() {
        Collection<PsiLet> es = letExpressions(parseCode("let a = fun | Second => \"s\"; let b = fun | Minute => \"m\";"));

        assertEquals("fun | Second => \"s\"", first(es).getBinding().getText());
        assertEquals("fun | Minute => \"m\"", second(es).getBinding().getText());
    }
}
