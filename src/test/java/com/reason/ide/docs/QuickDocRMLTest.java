package com.reason.ide.docs;

import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.reason.RmlLanguage;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class QuickDocRMLTest extends ORBasePlatformTestCase {
    public static final RmlLanguage LANG = RmlLanguage.INSTANCE;

    @Test
    public void test_abstract_type() {
        FileBase a = configureCode("A.re", "type t; let x: t<caret>");

        String info = getQuickDoc(a, LANG);
        assertEquals("A<br/>type <b>t</b><hr/>This is an abstract type", info);
    }

    @Test
    public void test_external() {
        FileBase a = configureCode("A.re", "external e : string -> int = \"name\"; let x = e<caret>");

        String info = getQuickDoc(a, LANG);
        assertEquals("A<br/>external <b>e</b><hr/>string -&gt; int", info);
    }
}
