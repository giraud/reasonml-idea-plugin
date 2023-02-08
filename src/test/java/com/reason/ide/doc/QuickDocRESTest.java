package com.reason.ide.doc;

import com.intellij.lang.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.rescript.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class QuickDocRESTest extends ORBasePlatformTestCase {
    public static final Language LANG = ResLanguage.INSTANCE;

    @Test
    public void test_abstract_type() {
        FileBase a = configureCode("A.res", "type t\n let x: t<caret>");

        String info = getQuickDoc(a, LANG);
        assertEquals("A<br/>type <b>t</b><hr/>This is an abstract type", info);
    }

    @Test
    public void test_external() {
        FileBase a = configureCode("A.res", "external e : string -> int = \"name\"\n let x = e<caret>");

        String info = getQuickDoc(a, LANG);
        assertEquals("A<br/>external <b>e</b><hr/>string -&gt; int", info);
    }
}
