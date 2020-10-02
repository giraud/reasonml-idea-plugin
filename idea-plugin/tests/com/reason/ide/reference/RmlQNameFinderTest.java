package com.reason.ide.reference;

import com.reason.ide.ORBasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.reason.RmlQNameFinder;
import java.util.*;

public class RmlQNameFinderTest extends ORBasePlatformTestCase {

  public void testLetBinding() {
    FileBase f = configureCode("A.re", "let make = { increase<caret>(); }");

    Set<String> paths = RmlQNameFinder.INSTANCE.extractPotentialPaths(getFromCaret(f));
    assertSameElements(paths, "A.make", "A");
  }

  public void testLocalOpenList() {
    FileBase f = configureCode("A.re", "let item = Css.[ margin<caret>");

    Set<String> paths = RmlQNameFinder.INSTANCE.extractPotentialPaths(getFromCaret(f));
    assertSameElements(paths, "A", "A.item", "A.item.Css", "A.Css", "Css");
  }

  // Local module alias must be resolved/replaced in the qname finder
  public void testLocalModuleAliasResolution() {
    FileBase f =
        configureCode("A.re", "module B = Belt; module M = { module O = B.Option; O.m<caret>");

    Set<String> paths = RmlQNameFinder.INSTANCE.extractPotentialPaths(getFromCaret(f));
    assertSameElements(paths, "A.O", "O", "A.Belt.Option", "Belt.Option");
  }
}
