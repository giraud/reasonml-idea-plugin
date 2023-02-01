package com.reason.ide;

import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;
import java.util.stream.*;

@RunWith(JUnit4.class)
public class QNameFinderOclTest extends ORBasePlatformTestCase {
    @Test
    public void test_letBinding() {
        FileBase f = configureCode("A.ml", "let make = increase<caret>()");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("A.make", "A", "", "Pervasives"), paths);
    }

    // Local module alias can be resolved/replaced in the qname finder
    @Test
    public void test_localModuleAliasResolution() {
        FileBase f = configureCode("A.ml", "module B = Belt\n module M = struct module O = B.Option let _ = O.m<caret>");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        // TODO: assertEquals(makePaths("A.O", "O", "A.Belt.Option", "Belt.Option", "", "Pervasives"), paths);
    }

    @Test
    public void test_parameter() {
        FileBase f = configureCode("A.ml", "let add x y = x<caret> + y");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("A.add", "A.add[x]", "A.add[y]", "A", "", "Pervasives"), paths);
    }

    @NotNull
    private List<String> extractPotentialPaths(@Nullable PsiElement fromCaret) {
        return OclQNameFinder.INSTANCE.extractPotentialPaths(fromCaret).stream().sorted().collect(Collectors.toList());
    }
}
