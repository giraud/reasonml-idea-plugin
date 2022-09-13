package com.reason.ide;

import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

public class QNameFinderOclTest extends ORBasePlatformTestCase {

    public void test_letBinding() {
        FileBase f = configureCode("A.ml", "let make = increase<caret>()");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        assertEquals(makePaths("A.make", "A", "", "Pervasives"), paths);
    }

    // Local module alias can be resolved/replaced in the qname finder
    public void test_localModuleAliasResolution() {
        FileBase f = configureCode("A.ml", "module B = Belt\n module M = struct module O = B.Option let _ = O.m<caret>");

        List<String> paths = extractPotentialPaths(getFromCaret(f));
        // TODO: assertEquals(makePaths("A.O", "O", "A.Belt.Option", "Belt.Option", "", "Pervasives"), paths);
    }

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
