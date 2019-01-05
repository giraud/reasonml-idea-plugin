package com.reason.lang.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ORSignatureTest {

    @Test
    public void ReasonSingleFun() {
        assertEquals("unit -> unit", (new ORSignature("unit => unit")).toReason());
    }

    @Test
    public void ReasonSingle() {
        assertEquals("unit", (new ORSignature("unit")).toReason());
    }

    @Test
    public void OCamlSingleFun() {
        assertEquals("unit => unit", (new ORSignature("unit => unit")).toOCaml());
    }

    @Test
    public void ReasonMultiFun() {
        assertEquals("(unit, string, float) -> unit", (new ORSignature("unit => string => float => unit")).toReason());
    }

    @Test
    public void OCamlMultiFun() {
        assertEquals("unit => string => float => unit", (new ORSignature("unit => string => float => unit")).toOCaml());
    }

    @Test
    public void ReasonParam() {
        assertEquals("show(bool)", (new ORSignature("bool show")).toReason());
    }

}
