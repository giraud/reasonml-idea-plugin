package com.reason.build.bs.compiler;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BsProcessTest {

    /*
     Bsc 4.0.6  => BuckleScript 4.0.6 (Using OCaml4.02.3+BS )
     Bsc 5.  =>
     Bsc 6.  =>
    */
    @Test
    public void ocamlVersionExtractor() {
        assertEquals("4.02", BsProcess.ocamlVersionExtractor("BuckleScript 4.0.6 (Using OCaml4.02.3+BS )"));
    }
}