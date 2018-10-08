package com.reason.lang.reason;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiJsObject;
import com.reason.lang.core.psi.PsiJsObjectField;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;

import java.util.Collection;

public class JsObjectParsingTest extends BaseParsingTestCase {
    public JsObjectParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testInclude() {
        PsiLet e = first(letExpressions(parseCode("let x = fn(~props={\"a\": id, \"b\": 0});")));

        PsiLetBinding binding = e.getBinding();
        PsiJsObject object = PsiTreeUtil.findChildOfType(binding, PsiJsObject.class);
        assertNotNull(object);

        Collection<PsiJsObjectField> fields = object.getFields();
        assertEquals(2, fields.size());
    }

}
