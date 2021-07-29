package com.reason.sdk;

import jpsplugin.com.reason.sdk.*;
import junit.framework.TestCase;

public class OCamlSdkTypeTest extends TestCase {
  public void testVersionString() {
    assertEquals("4.07", new OCamlSdkType().getVersionString("/user/.opam/4.07"));
    assertEquals("4.07.1", new OCamlSdkType().getVersionString("/user/.opam/4.07.1"));
    assertEquals("4.06.1+mingw64c", new OCamlSdkType().getVersionString("/user/.opam/4.06.1+mingw64c"));
  }
}
