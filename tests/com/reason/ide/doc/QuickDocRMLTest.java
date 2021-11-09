package com.reason.ide.doc;

import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.reason.RmlLanguage;

public class QuickDocRMLTest extends ORBasePlatformTestCase {
    public static final RmlLanguage LANG = RmlLanguage.INSTANCE;

    public void test_abstract_type() {
        FileBase a = configureCode("A.re", "type t; let x: t<caret>");

        String info = getQuickDoc(a, LANG);
        assertEquals("A<br/>type <b>t</b><hr/>This is an abstract type", info);
    }

    public void test_external() {
        FileBase a = configureCode("A.re", "external e : string -> int = \"name\"; let x = e<caret>");

        String info = getQuickDoc(a, LANG);
        assertEquals("A<br/>external <b>e</b><hr/>string -&gt; int", info);
    }
}
