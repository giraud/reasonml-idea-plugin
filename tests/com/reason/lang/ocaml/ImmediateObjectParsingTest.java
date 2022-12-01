package com.reason.lang.ocaml;

import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

public class ImmediateObjectParsingTest extends OclParsingTestCase {
    @Test
    public void test_immediate_object() {
        RPsiType e = firstOfType(parseCode("type t = <a:string; b:int>"), RPsiType.class);

        RPsiObject object = PsiTreeUtil.findChildOfType(e, RPsiObject.class);// not exactly...
        assertNotNull(object);
        assertSize(2, object.getFields());
    }

    @Test
    public void test_js_object() {
        RPsiType e = firstOfType(parseCode("type t = <a:string; b:int> Js.t"), RPsiType.class);

        RPsiObject object = PsiTreeUtil.findChildOfType(e, RPsiObject.class);
        assertNotNull(object);
        assertEquals("<a:string; b:int>", object.getText());
        assertSize(2, object.getFields());
    }
}
