package com.reason.bs;

import com.reason.bs.BsProcess;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BsProcessTest {

    @Test
    public void ocamlVersionExtractor() {
        assertEquals("4.02", BsProcess.ocamlVersionExtractor("BuckleScript 4.0.6 (Using OCaml4.02.3+BS )"));
        assertEquals("4.06", BsProcess.ocamlVersionExtractor("BuckleScript 6.0.0-dev.1 (Using OCaml4.06.1+BS )"));
    }
}