package com.reason.ide.docs;

import com.intellij.lang.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.ocaml.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@RunWith(JUnit4.class)
public class ShowDocOCLTest extends ORBasePlatformTestCase {
    public static final Language LANG = OclLanguage.INSTANCE;

    @Test
    public void test_multiple_spaces_below() {
        configureCode("Doc.ml", "let x = 1;  \t\n  (** doc for x *)");
        FileBase a = configureCode("A.ml", "Doc.x<caret>");

        String doc = getDoc(a, LANG);
        assertEquals("<div class=\"definition\"><b>Doc</b><p><i>let x</i></p></div><div class=\"content\"><p>doc for x</p></div>", doc);
    }

    @Test
    public void test_type() {
        FileBase a = configureCode("A.ml", "(** my type *) type t<caret> = string");

        String doc = getDoc(a, LANG);
        assertEquals("<div class=\"definition\"><b>A</b><p><i>type t</i></p></div><div class=\"content\"><p>my type</p></div>", doc);
    }

    @Test
    public void test_GH_350() {
        configureCode("A.mli", "val compare : string -> string -> int\n(** compare doc *)");
        FileBase a = configureCode("A.ml", "let compare<caret> s1 s2 = 1");

        String doc = getDoc(a, LANG);
        assertEquals("<div class=\"definition\"><b>A</b><p><i>let compare</i></p></div><div class=\"content\"><p>compare doc</p></div>", doc);
    }
}
