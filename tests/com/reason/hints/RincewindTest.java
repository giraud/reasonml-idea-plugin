package com.reason.hints;

import org.junit.*;

import static org.junit.Assert.*;

public class RincewindTest {
    @Test
    public void test_bucklescript() {
        assertEquals("4.02", Rincewind.extractOcamlVersion("BuckleScript 4.0.6 (Using OCaml4.02.3+BS )"));
        assertEquals("4.06", Rincewind.extractOcamlVersion("BuckleScript 6.0.0-dev.1 (Using OCaml4.06.1+BS )"));
        assertEquals("4.06", Rincewind.extractOcamlVersion("BuckleScript 6.2.1 ( Using OCaml:4.06.1+BS )"));
    }

    @Test
    public void test_rescript() {
        assertEquals("4.06", Rincewind.extractOcamlVersion("ReScript 9.1.4"));
    }

    @Test
    public void test_dune() {
        assertEquals("4.07", Rincewind.extractOcamlVersion("Dune (OCaml:4.07.1)"));
        assertEquals("4.12", Rincewind.extractOcamlVersion("Dune (OCaml:4.12.0+mingw64c)"));
    }
}
