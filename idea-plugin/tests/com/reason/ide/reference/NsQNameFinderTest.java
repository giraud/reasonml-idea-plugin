package com.reason.ide.reference;

import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.QNameFinder;
import com.reason.lang.napkin.NsQNameFinder;
import java.util.Set;

public class NsQNameFinderTest extends ORBasePlatformTestCase {

  private final QNameFinder qNameFinder = NsQNameFinder.INSTANCE;

  public void testLetBinding() {
    FileBase f = configureCode("A.re", "let make = { increase<caret>(); }");

    Set<String> paths = qNameFinder.extractPotentialPaths(getFromCaret(f));
    assertSameElements(paths, "A.make", "A");
  }

  /*
  public void testLocalOpenList() {
    FileBase f = configureCode("A.re", "let item = Css.[ margin<caret>");

    Set<String> paths = qNameFinder.extractPotentialPaths(getFromCaret(f));
    assertSameElements(paths, "A", "A.item", "A.item.Css", "A.Css", "Css");
  }
  */

  // Local module alias must be resolved/replaced in the qname finder
  /*
  public void testLocalModuleAliasResolution() {
    FileBase f =
        configureCode("A.re", "module B = Belt; module M = { module O = B.Option; O.m<caret>");

    Set<String> paths = qNameFinder.extractPotentialPaths(getFromCaret(f));
    assertSameElements(
        paths, "A.O", "O", "A.M.O", "A.M.Belt.Option", "A.Belt.Option", "Belt.Option");
  }
  */

  public void test_in_module_binding() {
    FileBase f = configureCode("A.re", "module X = { let foo = 1; let z = foo<caret>; };");

    Set<String> paths = qNameFinder.extractPotentialPaths(getFromCaret(f));
    assertSameElements(paths, "A.X.z", "A.X", "A");
  }
}
