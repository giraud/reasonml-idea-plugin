package com.reason.ide;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

@SuppressWarnings("ConstantConditions")
public class QNameFinderRmlTest extends ORBasePlatformTestCase {
    private final QNameFinder qNameFinder = RmlQNameFinder.INSTANCE;

    public void testLetBinding() {
        FileBase f = configureCode("A.re", "let make = { increase<caret>(); }");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("A.make", "A", "", "Pervasives"), paths);
    }

    public void test_local_open_list() {
        FileBase f = configureCode("A.re", "let item = Css.[ margin<caret>");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("A", "A.item", "A.item.Css", "A.Css", "Css", "", "Pervasives"), paths);
    }

    public void test_local_open() {
        FileBase f = configureCode("A.re", "let item = Css.( margin<caret>");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("A", "A.item", "A.item.Css", "A.Css", "Css", "", "Pervasives"), paths);
    }

    // Local module alias must be resolved/replaced in the qname finder
    public void testLocalModuleAliasResolution() {
        FileBase f = configureCode("A.re", "module B = Belt; module M = { module O = B.Option; O.m<caret>");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("A.O", "O", "A.M.O", "A.M.Belt.Option", "A.Belt.Option", "Belt.Option", "", "Pervasives"), paths);
    }

    public void test_in_module_binding() {
        FileBase f = configureCode("A.re", "module X = { let foo = 1; let z = foo<caret>; };");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("A.X.z", "A.X", "A", "", "Pervasives"), paths);
    }

    public void test_component() {
        FileBase f = configureCode("A.re", "open X; <Comp <caret> />;");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("X.Comp", "A.Comp", "Comp", "", "Pervasives"), paths);
    }

    public void test_component_path() {
        FileBase f = configureCode("A.re", "open X; <B.C.Comp <caret> />;");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("X.B.C.Comp", "A.B.C.Comp", "B.C.Comp", "", "Pervasives"), paths);
    }

    public void test_component_path_from_tagname() {
        FileBase f = configureCode("A.re", "open X; <B.C.Comp />;");

        List<String> paths = extractPotentialPaths(PsiTreeUtil.findChildOfType(f, PsiTagStart.class).getNameIdentifier());
        assertEquals(makePaths("X.B.C.Comp", "A.B.C.Comp", "B.C.Comp", "", "Pervasives"), paths);
    }

    @NotNull
    private List<String> extractPotentialPaths(@Nullable PsiElement fromCaret) {
        return qNameFinder.extractPotentialPaths(fromCaret).stream().sorted().collect(Collectors.toList());
    }
}
