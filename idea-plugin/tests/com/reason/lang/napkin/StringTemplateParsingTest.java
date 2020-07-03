package com.reason.lang.napkin;

import java.util.*;
import com.intellij.psi.PsiElement;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiInterpolation;
import com.reason.lang.core.psi.PsiInterpolationReference;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;

public class StringTemplateParsingTest extends BaseParsingTestCase {

    public StringTemplateParsingTest() {
        super("", "res", new NsParserDefinition());
    }

    public void testBasic() {
        PsiLet e = first(letExpressions(parseCode("let _ = {j|this is a $var Template string|j}")));
        PsiLetBinding binding = e.getBinding();
        PsiInterpolation inter = (PsiInterpolation) binding.getFirstChild();

        Collection<PsiElement> parts = ORUtil.findImmediateChildrenOfType(inter, NsTypes.INSTANCE.C_INTERPOLATION_PART);
        assertSize(2, parts);
        PsiInterpolationReference ref = ORUtil.findImmediateFirstChildOfClass(inter, PsiInterpolationReference.class);
        assertEquals(ref.getText(), "var");
    }
}
