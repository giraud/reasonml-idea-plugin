package com.reason.ide.docs;

import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.*;
import com.reason.lang.ocaml.*;
import org.junit.*;

public class DocFormatterTest extends ORBasePlatformTestCase {
    @Test
    public void test_file_comment() {
        FileBase e = configureCode("A.mli", """
                (**
                 Doc for file
                 *)""");

        String html = DocFormatter.format(e, e, ORLanguageProperties.cast(OclLanguage.INSTANCE), e.getText());
        assertTextEquals("""
                        <div class="definition"><b>A.ml</b></div><div class="content"><p>Doc for file</p></div>""",
                html);
    }

    @Test
    public void test_no_file_comment() {
        FileBase e = configureCode("A.mli", """
                (** Module type [S] is the one from OCaml Stdlib. *)
                module type S = module type of String""");

        String html = DocFormatter.format(e, e, ORLanguageProperties.cast(OclLanguage.INSTANCE), e.getText());
        assertTextEquals("", html);
    }
}