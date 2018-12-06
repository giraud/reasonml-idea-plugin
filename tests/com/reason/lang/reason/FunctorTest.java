package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.PsiNamedElement;

public class FunctorTest extends BaseParsingTestCase {
    public FunctorTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasic() {
        PsiNamedElement e = first(expressions(parseCode("module Make = (M: Def) : S => {};")));

        assertNotNull(e);
        assertInstanceOf(e, PsiFunctor.class);
    }

}
