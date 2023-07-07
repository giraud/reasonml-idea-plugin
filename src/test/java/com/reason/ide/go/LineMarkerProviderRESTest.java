package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.openapi.editor.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@RunWith(JUnit4.class)
public class LineMarkerProviderRESTest extends ORBasePlatformTestCase {
    @Test
    public void test_let_files() {
        FileBase intf = configureCode("A.resi", "let x: int");
        FileBase impl = configureCode("A.res", "let x = 1");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements let/val", markers.get(0).getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.get(0).getIcon());
        assertEquals("Declare let/val", markers.get(0).getLineMarkerTooltip());
    }

    @Test
    public void test_let() {
        FileBase f = configureCode("A.res", """
                module type I = {
                  let x : int
                }
                                
                module M1 : I = {
                  let x = 1
                }
                                
                module M2 : I = {
                  let x = 2
                }
                """);

        List<LineMarkerInfo<?>> markers = doHighlight(f);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements module", markers.get(0).getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, markers.get(1).getIcon());
        assertEquals("Implements let/val", markers.get(1).getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, markers.get(2).getIcon());
        assertEquals("Declare module", markers.get(2).getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, markers.get(3).getIcon());
        assertEquals("Declare let/val", markers.get(3).getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, markers.get(2).getIcon());
        assertEquals("Declare module", markers.get(2).getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, markers.get(5).getIcon());
        assertEquals("Declare let/val", markers.get(5).getLineMarkerTooltip());
    }

    @Test
    public void test_type_files() {
        FileBase intf = configureCode("A.resi", "type t");
        FileBase impl = configureCode("A.res", """
                type t

                module Inner = {
                  type t
                }
                """);

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements type", markers.get(0).getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.get(0).getIcon());
        assertEquals("Declare type", markers.get(0).getLineMarkerTooltip());
        assertSize(1, markers);
    }

    @Test
    public void test_type() {
        FileBase f = configureCode("A.res", """
                module type I = {
                  type t
                }
                                
                module M1 : I = {
                  type t
                }
                                
                module M2 : I = {
                  type t
                }
                """);

        List<LineMarkerInfo<?>> markers = doHighlight(f);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements module", markers.get(0).getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, markers.get(1).getIcon());
        assertEquals("Implements type", markers.get(1).getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, markers.get(2).getIcon());
        assertEquals("Declare module", markers.get(2).getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, markers.get(3).getIcon());
        assertEquals("Declare type", markers.get(3).getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, markers.get(2).getIcon());
        assertEquals("Declare module", markers.get(2).getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, markers.get(5).getIcon());
        assertEquals("Declare type", markers.get(5).getLineMarkerTooltip());

        assertSize(6, markers);
    }

    @Test
    public void test_external_files() {
        FileBase intf = configureCode("A.resi", "external t: int = \"\"");
        FileBase impl = configureCode("A.res", "external t: int = \"\"");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements external", markers.get(0).getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.get(0).getIcon());
        assertEquals("Declare external", markers.get(0).getLineMarkerTooltip());
    }

    @Test
    public void test_external() {
        FileBase f = configureCode("A.res", """
                module type I = {
                  external t: int = ""
                }
                                
                module M1 : I = {
                  external t: int = ""
                }
                                
                module M2 : I = {
                  external t: int = ""
                }
                """);

        List<LineMarkerInfo<?>> markers = doHighlight(f);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(1).getIcon());
        assertEquals("Implements external", markers.get(1).getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, markers.get(3).getIcon());
        assertEquals("Declare external", markers.get(3).getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, markers.get(5).getIcon());
        assertEquals("Declare external", markers.get(5).getLineMarkerTooltip());

        assertSize(6, markers);
    }

    @Test
    public void test_module_type() {
        FileBase f = configureCode("A.res", """
                module type Intf = {}
                module Impl : Intf = {}
                """);

        List<LineMarkerInfo<?>> lineMarkers = doHighlight(f);

        assertEquals(ORIcons.IMPLEMENTED, lineMarkers.get(0).getIcon());
        assertEquals("Implements module", lineMarkers.get(0).getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, lineMarkers.get(1).getIcon());
        assertEquals("Declare module", lineMarkers.get(1).getLineMarkerTooltip());

        assertSize(2, lineMarkers);
    }

    @Test
    public void test_module_type_files() {
        FileBase intf = configureCode("A.resi", "module type A1 = {}");
        FileBase impl = configureCode("A.res", "module type A1 = {}");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements module", markers.get(0).getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.get(0).getIcon());
        assertEquals("Declare module", markers.get(0).getLineMarkerTooltip());
    }

    @Test
    public void test_module_type_both_direction() {
        FileBase intf = configureCode("A.resi", "module type A1 = {}");
        FileBase impl = configureCode("A.res", """
                // ...
                module type A1 = {};
                                
                module A2 : A1 = {};
                """);

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertSize(1, markers);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements module", markers.get(0).getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.get(0).getIcon());
        assertEquals("Declare module", markers.get(0).getLineMarkerTooltip());
    }

    // TODO: test_module_deep

    @Test
    public void test_modules() {
        FileBase f = configureCode("A.res", """
                module type Intf = {}
                module ImplA : Intf = {}
                module ImplB : Intf = {}
                """);

        List<LineMarkerInfo<?>> markers = doHighlight(f);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements module", markers.get(0).getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, markers.get(1).getIcon());
        assertEquals("Declare module", markers.get(1).getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, markers.get(2).getIcon());
        assertEquals("Declare module", markers.get(2).getLineMarkerTooltip());

        assertSize(3, markers);
    }

    @Test
    public void test_exception_files() {
        FileBase intf = configureCode("A.resi", "exception X");
        FileBase impl = configureCode("A.res", "exception X");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements exception", markers.get(0).getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.get(0).getIcon());
        assertEquals("Declare exception", markers.get(0).getLineMarkerTooltip());
    }

    @Test
    public void test_exception() {
        FileBase f = configureCode("A.res", """
                module type I = {
                  exception X
                }
                                
                module M : I = {
                  exception X
                }
                """);

        List<LineMarkerInfo<?>> markers = doHighlight(f);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(1).getIcon());
        assertEquals("Implements exception", markers.get(1).getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, markers.get(3).getIcon());
        assertEquals("Declare exception", markers.get(3).getLineMarkerTooltip());

        assertSize(4, markers);
    }
}
