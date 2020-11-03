package com.reason.ide;

import com.reason.ide.files.*;
import com.reason.lang.ocaml.*;

import java.util.*;

public class QNameFinderOclTest extends ORBasePlatformTestCase {

  public void test_letBinding() {
    FileBase f = configureCode("A.ml", "let make = increase<caret>()");

    Set<String> paths = OclQNameFinder.INSTANCE.extractPotentialPaths(getFromCaret(f));
    assertEquals(makePaths("A.make", "A", "Pervasives"), paths);
  }

  // Local module alias can be resolved/replaced in the qname finder
  public void test_localModuleAliasResolution() {
    FileBase f = configureCode("A.ml", "module B = Belt\n module M = struct module O = B.Option let _ = O.m<caret>");

    Set<String> paths = OclQNameFinder.INSTANCE.extractPotentialPaths(getFromCaret(f));
    assertSameElements(makePaths("A.O", "O", "A.Belt.Option", "Belt.Option", "Pervasives"), paths);
  }
}
