package com.reason.lang.reason;

import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiAnnotation;

public class AnnotationParsingTest extends BaseParsingTestCase {
    public AnnotationParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testAnnotationWithString() {
        PsiAnnotation annotation = (PsiAnnotation) firstElement(parseCode("[@bs.module \"xyz\"]"));

        assertEquals("@bs.module", annotation.getName());
    }

}
