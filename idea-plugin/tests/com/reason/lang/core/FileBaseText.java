package com.reason.lang.core;

import java.util.*;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiLet;

public class FileBaseText extends ORBasePlatformTestCase {

    public void test_Rml_getQNameExpression() {
        FileBase f = configureCode("A.re", "module B = { let x = 1; }; let x = 2;");
        List<PsiLet> e = f.getExpressions("A.B.x", PsiLet.class);

        assertSize(1, e);
        assertInstanceOf(e.iterator().next(), PsiLet.class);
    }
}
