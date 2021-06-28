package com.reason.hints;

import org.junit.*;

import static com.reason.hints.InsightManagerImpl.*;
import static org.junit.Assert.*;

public class InsightManagerImplTest {
    @Test
    public void test_bucklescript() {
        assertEquals("4.02.3", ocamlVersionExtractor("BuckleScript 4.0.6 (Using OCaml4.02.3+BS )"));
        assertEquals("4.06.1", ocamlVersionExtractor("BuckleScript 6.0.0-dev.1 (Using OCaml4.06.1+BS )"));
        assertEquals("4.06.1", ocamlVersionExtractor("BuckleScript 6.2.1 ( Using OCaml:4.06.1+BS )"));
    }

    @Test
    public void test_rescript() {
        assertEquals("4.06.1", ocamlVersionExtractor("ReScript 9.1.4"));
    }
}
