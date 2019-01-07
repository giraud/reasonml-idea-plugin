package com.reason.lang.core;

import com.reason.lang.core.signature.ORSignature;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.reason.RmlLanguage;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ORSignatureTest {

    @Test
    public void ReasonSingleFun() {
        assertEquals("unit => unit", (new ORSignature("unit => unit")).asString(RmlLanguage.INSTANCE));
        assertEquals("unit => unit", (new ORSignature("unit -> unit")).asString(RmlLanguage.INSTANCE));
    }

    @Test
    public void ReasonSingle() {
        assertEquals("unit", (new ORSignature("unit")).asString(RmlLanguage.INSTANCE));
    }

    @Test
    public void OCamlSingleFun() {
        assertEquals("unit -> unit", (new ORSignature("unit => unit")).asString(OclLanguage.INSTANCE));
    }

    @Test
    public void ReasonMultiFun() {
        assertEquals("(unit, string, float) => unit", (new ORSignature("unit => string => float => unit")).asString(RmlLanguage.INSTANCE));
    }

    @Test
    public void OCamlMultiFun() {
        assertEquals("unit -> string -> float -> unit", (new ORSignature("unit => string => float => unit")).asString(OclLanguage.INSTANCE));
    }

    @Test
    public void ReasonParam() {
        assertEquals("show(bool)", (new ORSignature("bool show")).asString(RmlLanguage.INSTANCE));
    }

}
