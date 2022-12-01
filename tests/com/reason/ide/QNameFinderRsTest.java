package com.reason.ide;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.rescript.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;
import java.util.stream.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class QNameFinderRsTest extends ORBasePlatformTestCase {
    @Test
    public void testLetBinding() {
        FileBase f = configureCode("A.res", "let make = { increase<caret>(); }");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("A.make", "A", "", "Pervasives"), paths);
    }

  /*   zzz
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

    @Test
    public void test_in_module_binding() {
        FileBase f = configureCode("A.res", "module X = { let foo = 1; let z = foo<caret>; };");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("A.X.z", "A.X", "A", "", "Pervasives"), paths);
    }

    @Test
    public void test_component() {
        FileBase f = configureCode("A.res", "open X; <Comp <caret> />;");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("X.Comp", "A.Comp", "Comp", "", "Pervasives"), paths);
    }

    @Test
    public void test_component_path() {
        FileBase f = configureCode("A.res", "open X; <B.C.Comp <caret> />;");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("X.B.C.Comp", "A.B.C.Comp", "B.C.Comp", "", "Pervasives"), paths);
    }

    @Test
    public void test_component_path_from_tagname() {
        FileBase f = configureCode("A.res", "open X; <B.C.Comp />;");

        List<String> paths = extractPotentialPaths(PsiTreeUtil.findChildOfType(f, RPsiTagStart.class).getNameIdentifier());
        assertEquals(makePaths("X.B.C.Comp", "A.B.C.Comp", "B.C.Comp", "", "Pervasives"), paths);
    }

    @NotNull
    private List<String> extractPotentialPaths(@Nullable PsiElement fromCaret) {
        return ResQNameFinder.INSTANCE.extractPotentialPaths(fromCaret).stream().sorted().collect(Collectors.toList());
    }
}
