package com.reason.ide.reference;

import java.util.*;
import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.ocaml.OclQNameFinder;

public class OclQNameFinderTest extends ORBasePlatformTestCase {

    public void test_letBinding() {
        FileBase f = configureCode("A.ml", "let make = increase<caret>()");

        Set<String> paths = OclQNameFinder.INSTANCE.extractPotentialPaths(getFromCaret(f));
        assertSameElements(paths, "A.make", "A");
    }

    // Local module alias can be resolved/replaced in the qname finder
    public void test_localModuleAliasResolution() {
        FileBase f = configureCode("A.ml", "module B = Belt\n module M = struct module O = B.Option let _ = O.m<caret>");

        Set<String> paths = OclQNameFinder.INSTANCE.extractPotentialPaths(getFromCaret(f));
        assertSameElements(paths, "A.O", "O", "A.Belt.Option", "Belt.Option");
    }
}
