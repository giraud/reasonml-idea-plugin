package com.reason.sdk;

import junit.framework.TestCase;

public class OCamlSdkTypeTest extends TestCase {
    public void testVersionString() {
        assertEquals("4.07", new OCamlSdkType().getVersionString("/user/.opam/4.07"));
        assertEquals("4.07.1", new OCamlSdkType().getVersionString("/user/.opam/4.07.1"));
    }
}
