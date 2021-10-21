package com.reason.ide;

import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.reason.*;
import com.reason.lang.rescript.*;

@SuppressWarnings("ConstantConditions")
public class TypeConversionOclTest extends ORBasePlatformTestCase {
    public static final RmlLanguage REASON = RmlLanguage.INSTANCE;
    public static final ResLanguage RESCRIPT = ResLanguage.INSTANCE;

    public void test_option() {
        FileBase e = configureCode("A.ml", "val x: int option");
        PsiSignature sig = PsiTreeUtil.findChildOfType(e, PsiSignature.class);

        assertEquals("option(int)", sig.asText(REASON));
        assertEquals("option<int>", sig.asText(RESCRIPT));
    }

    public void test_option_named_params() {
        FileBase e = configureCode("A.ml", "external add : x:int option -> y:int -> int = \"\"");
        PsiSignature sig = PsiTreeUtil.findChildOfType(e, PsiSignature.class);

        assertEquals("(~x:option(int), ~y:int) => int", sig.asText(REASON));
        assertEquals("(~x:option<int>, ~y:int) => int", sig.asText(RESCRIPT));
    }
}
