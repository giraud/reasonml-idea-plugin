package com.reason.lang.reason;

import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class FunParsingTest extends RmlParsingTestCase {
    @Test
    public void test_fun() {
        RPsiLet e = first(letExpressions(parseCode("let timeUnitToString = fun | Second => \"s\" | Minute => \"m\" | Hour => \"h\";")));

        RPsiLetBinding binding = e.getBinding();
        assertEquals("fun | Second => \"s\" | Minute => \"m\" | Hour => \"h\"", binding.getText());
    }

    @Test
    public void test_chaining() {
        Collection<RPsiLet> es = letExpressions(parseCode("let a = fun | Second => \"s\"; let b = fun | Minute => \"m\";"));

        assertEquals("fun | Second => \"s\"", first(es).getBinding().getText());
        assertEquals("fun | Minute => \"m\"", second(es).getBinding().getText());
    }
}
