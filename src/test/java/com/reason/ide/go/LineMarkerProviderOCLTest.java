package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.daemon.impl.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@RunWith(JUnit4.class)
public class LineMarkerProviderOCLTest extends ORBasePlatformTestCase {
    @Test
    public void test_let_val_files() {
        FileBase intf = configureCode("A.mli", "val x: int");
        FileBase impl = configureCode("A.ml", "let x = 1");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements let/val", markers.get(0).getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.get(0).getIcon());
        assertEquals("Declare let/val", markers.get(0).getLineMarkerTooltip());
    }

    @Test
    public void test_let_val() {
        FileBase f = configureCode("A.ml", """
                module type I = struct
                  val x: int
                end
                                
                module M1 : I = struct
                  let x = 1
                end
                                
                module M2 : I = struct
                  let x = 2
                end
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
        FileBase intf = configureCode("A.mli", "type t");
        FileBase impl = configureCode("A.ml", """
                type t
                                
                module Inner = struct
                  type t
                end
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
        FileBase f = configureCode("A.ml", """
                module type I = struct
                  type t
                end
                                
                module M1 : I = struct
                  type t
                end
                                
                module M2 : I = struct
                  type t
                end
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
        FileBase intf = configureCode("A.mli", "external t: int -> unit = \"\";");
        FileBase impl = configureCode("A.ml", "external t: int -> unit = \"\";");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements external", markers.get(0).getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.get(0).getIcon());
        assertEquals("Declare external", markers.get(0).getLineMarkerTooltip());
    }

    @Test
    public void test_external() {
        FileBase f = configureCode("A.ml", """
                module type I = sig
                  external t: int = ""
                end
                                
                module M1 : I = struct
                  external t: int = ""
                end
                                
                module M2 : I = struct
                  external t: int = ""
                end
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
        FileBase f = configureCode("A.ml", """
                module type Intf = sig end
                module Impl : Intf = struct end
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
        FileBase intf = configureCode("A.mli", "module type A1 = sig end");
        FileBase impl = configureCode("A.ml", "module type A1 = sig end");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements module", markers.get(0).getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.get(0).getIcon());
        assertEquals("Declare module", markers.get(0).getLineMarkerTooltip());
    }

    @Test
    public void test_module_type_both_direction() {
        FileBase intf = configureCode("A.mli", "module type A1 = sig end");
        FileBase impl = configureCode("A.ml", """
                // ...
                module type A1 = sig end
                                
                module A2 : A1 = struct end
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
        FileBase f = configureCode("A.ml", """
                module type Intf = sig end
                module ImplA : Intf = struct end
                module ImplB : Intf = struct end
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
        FileBase intf = configureCode("A.mli", "exception X");
        FileBase impl = configureCode("A.ml", "exception X");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(0).getIcon());
        assertEquals("Implements exception", markers.get(0).getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.get(0).getIcon());
        assertEquals("Declare exception", markers.get(0).getLineMarkerTooltip());
    }

    @Test
    public void test_exception() {
        FileBase f = configureCode("A.ml", """
                module type I = sig
                  exception X
                end
                                
                module M : I = struct
                  exception X
                end
                """);

        List<LineMarkerInfo<?>> markers = doHighlight(f);

        assertEquals(ORIcons.IMPLEMENTED, markers.get(1).getIcon());
        assertEquals("Implements exception", markers.get(1).getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, markers.get(3).getIcon());
        assertEquals("Declare exception", markers.get(3).getLineMarkerTooltip());

        assertSize(4, markers);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/322
    // Class types in .mli files should link to the corresponding definition in the .ml file
    @Test
    public void test_GH_322() {
        configureCode("A.mli", """
                class type proof_view =
                  object
                  end
                """);
        configureCode("A.ml", """
                class type proof_view =
                  object
                  end
                """);

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());
        LineMarkerInfo<?> m0 = lineMarkers.get(0);
        assertEquals(ORIcons.IMPLEMENTING, m0.getIcon());
        assertEquals("Declare class", m0.getLineMarkerTooltip());
        assertSize(1, lineMarkers);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/323
    // Method declarations in .ml files should link to their implementations
    @Test
    public void test_GH_323() {
        configureCode("A.mli", """
                class type proof_view =
                  object
                    inherit GObj.widget
                    method buffer: GText.buffer
                  end
                """);
        configureCode("A.ml", """
                class type proof_view =
                  object
                    inherit GObj.widget
                    method buffer: GText.buffer
                  end""");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());
        LineMarkerInfo<?> m1 = lineMarkers.get(1);
        assertEquals("Declare method", m1.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m1.getIcon());
        assertSize(2, lineMarkers);
    }
}
