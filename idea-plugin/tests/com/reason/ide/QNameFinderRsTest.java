package com.reason.ide;

import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.napkin.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class QNameFinderRsTest extends ORBasePlatformTestCase {

  private final QNameFinder qNameFinder = NsQNameFinder.INSTANCE;

  public void testLetBinding() {
    FileBase f = configureCode("A.res", "let make = { increase<caret>(); }");

    Set<String> paths = qNameFinder.extractPotentialPaths(getFromCaret(f));
    assertEquals(makePaths("A.make", "A", "Pervasives"), paths);
  }

  /*
  public void testLocalOpenList() {
    FileBase f = configureCode("A.re", "let item = Css.[ margin<caret>");

    Set<String> paths = qNameFinder.extractPotentialPaths(getFromCaret(f));
    assertSameElements(paths, "A", "A.item", "A.item.Css", "A.Css", "Css");
  }

  // Local module alias must be resolved/replaced in the qname finder
  public void testLocalModuleAliasResolution() {
    FileBase f =
        configureCode("A.re", "module B = Belt; module M = { module O = B.Option; O.m<caret>");

    Set<String> paths = qNameFinder.extractPotentialPaths(getFromCaret(f));
    assertSameElements(
        paths, "A.O", "O", "A.M.O", "A.M.Belt.Option", "A.Belt.Option", "Belt.Option");
  }
  */

  public void test_in_module_binding() {
    FileBase f = configureCode("A.res", "module X = { let foo = 1; let z = foo<caret>; };");

    Set<String> paths = qNameFinder.extractPotentialPaths(getFromCaret(f));
    assertEquals(makePaths("A.X.z", "A.X", "A", "Pervasives"), paths);
  }

  public void test_component() {
    FileBase f = configureCode("A.res", "open X; <Comp <caret> />;");

    Set<String> paths = qNameFinder.extractPotentialPaths(getFromCaret(f));
    assertEquals(makePaths("X.Comp", "A.Comp", "Comp", "Pervasives"), paths);
  }

  public void test_component_path() {
    FileBase f = configureCode("A.res", "open X; <B.C.Comp <caret> />;");

    Set<String> paths = qNameFinder.extractPotentialPaths(getFromCaret(f));
    assertEquals(makePaths("X.B.C.Comp", "A.B.C.Comp", "B.C.Comp", "Pervasives"), paths);
  }

  public void test_component_path_from_tagname() {
    FileBase f = configureCode("A.res", "open X; <B.C.Comp />;");

    Set<String> paths = qNameFinder.extractPotentialPaths(PsiTreeUtil.findChildOfType(f, PsiTagStart.class).getNameIdentifier());
    assertEquals(makePaths("X.B.C.Comp", "A.B.C.Comp", "B.C.Comp", "Pervasives"), paths);
  }

}
