package com.reason.ide.doc;

import com.intellij.lang.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.ocaml.*;

public class ShowDocOCLTest extends ORBasePlatformTestCase {
    public static final Language LANG = OclLanguage.INSTANCE;

    public void test_multiple_spaces_below() {
        configureCode("Doc.ml", "let x = 1;  \t\n  (** doc for x *)");
        FileBase a = configureCode("A.ml", "Doc.x<caret>");

        String doc = getDoc(a, LANG);
        assertEquals("<div style='padding-bottom: 5px; border-bottom: 1px solid #AAAAAAEE'>Doc</div><div><p> doc for x </p></div>", doc);
    }
}
