package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.navigation.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@SuppressWarnings("DataFlowIssue")
public class LineMarkerProviderOCLTest extends ORBasePlatformTestCase {
    @Test
    public void test_let_val_files() {
        FileBase intf = configureCode("A.mli", "val x: int");
        FileBase impl = configureCode("A.ml", "let x = 1");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements let/val", markers.getFirst().getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.getFirst().getIcon());
        assertEquals("Declare let/val", markers.getFirst().getLineMarkerTooltip());
    }

    @Test
    public void test_let_val() {
        FileBase f = configureCode("A.ml", """
                module type I = sig
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
        assertSize(6, markers);

        RelatedItemLineMarkerInfo<?> m0 = (RelatedItemLineMarkerInfo<?>) markers.getFirst();
        assertEquals(ORIcons.IMPLEMENTED, m0.getIcon());
        assertEquals("Implements module", m0.getLineMarkerTooltip());
        List<? extends GotoRelatedItem> m0Targets = new ArrayList<>(m0.createGotoRelatedItems());
        assertSize(2, m0Targets);
        assertInstanceOf(m0Targets.getFirst().getElement(), RPsiInnerModule.class);
        assertInstanceOf(m0Targets.get(1).getElement(), RPsiInnerModule.class);

        RelatedItemLineMarkerInfo<?> m1 = (RelatedItemLineMarkerInfo<?>) markers.get(1);
        assertEquals(ORIcons.IMPLEMENTED, m1.getIcon());
        assertEquals("Implements let/val", m1.getLineMarkerTooltip());
        List<? extends GotoRelatedItem> m1Targets = new ArrayList<>(m1.createGotoRelatedItems());
        assertSize(2, m1Targets);
        assertInstanceOf(m1Targets.getFirst().getElement(), RPsiLet.class);
        assertInstanceOf(m1Targets.get(1).getElement(), RPsiLet.class);

        assertEquals(ORIcons.IMPLEMENTING, markers.get(2).getIcon());
        assertEquals("Declare module", markers.get(2).getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, markers.get(3).getIcon());
        assertEquals("Declare let/val", markers.get(3).getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, markers.get(4).getIcon());
        assertEquals("Declare module", markers.get(4).getLineMarkerTooltip());
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

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements type", markers.getFirst().getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.getFirst().getIcon());
        assertEquals("Declare type", markers.getFirst().getLineMarkerTooltip());
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

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements module", markers.getFirst().getLineMarkerTooltip());
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

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements external", markers.getFirst().getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.getFirst().getIcon());
        assertEquals("Declare external", markers.getFirst().getLineMarkerTooltip());
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

        assertEquals(ORIcons.IMPLEMENTED, lineMarkers.getFirst().getIcon());
        assertEquals("Implements module", lineMarkers.getFirst().getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, lineMarkers.get(1).getIcon());
        assertEquals("Declare module", lineMarkers.get(1).getLineMarkerTooltip());

        assertSize(2, lineMarkers);
    }

    @Test
    public void test_module_type_files() {
        FileBase intf = configureCode("A.mli", "module type A1 = sig end");
        FileBase impl = configureCode("A.ml", "module type A1 = sig end");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements module", markers.getFirst().getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.getFirst().getIcon());
        assertEquals("Declare module", markers.getFirst().getLineMarkerTooltip());
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
        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements module", markers.getFirst().getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertSize(3, markers);
        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals(19, markers.getFirst().startOffset); // A1->A2
        assertEquals(ORIcons.IMPLEMENTING, markers.get(1).getIcon());
        assertEquals(19, markers.get(1).startOffset); // A1->A1.mli
        assertEquals(ORIcons.IMPLEMENTING, markers.get(2).getIcon());
        assertEquals(40, markers.get(2).startOffset); // A2->A1
    }

    @Test
    public void test_module_deep() {
        configureCode("A.ml", """
                module B = struct
                  module type Intf = sig end
                end
                
                module IncorrectImpl : Intf = struct end
                module CorrectImpl : B.Intf = struct end
                """);

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());

        LineMarkerInfo<?> m0 = lineMarkers.getFirst();
        assertEquals(ORIcons.IMPLEMENTED, m0.getIcon());
        assertEquals("Implements module", m0.getLineMarkerTooltip());
        LineMarkerInfo<?> m1 = lineMarkers.get(1);
        assertEquals(ORIcons.IMPLEMENTING, m1.getIcon());
        assertEquals("Declare module", m1.getLineMarkerTooltip());
        assertSize(2, lineMarkers);
    }

    @Test
    public void test_modules() {
        FileBase f = configureCode("A.ml", """
                module type Intf = sig end
                module ImplA : Intf = struct end
                module ImplB : Intf = struct end
                """);

        List<LineMarkerInfo<?>> markers = doHighlight(f);

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements module", markers.getFirst().getLineMarkerTooltip());

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

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements exception", markers.getFirst().getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.getFirst().getIcon());
        assertEquals("Declare exception", markers.getFirst().getLineMarkerTooltip());
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
        LineMarkerInfo<?> m0 = lineMarkers.getFirst();
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

    // https://github.com/giraud/reasonml-idea-plugin/issues/485
    // Module signature with inline signature
    @Test
    public void test_GH_485_signature_in_different_files() {
        FileBase mli = configureCode("A.mli", """
                module M: sig
                  val x: int
                end
                """);
        FileBase ml = configureCode("A.ml", """
                module M: sig
                   val x: int
                 end = struct
                   let x = 1
                 end
                """);

        myFixture.openFileInEditor(mli.getVirtualFile());
        myFixture.doHighlighting();
        List<LineMarkerInfo<?>> mliMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), getProject());

        myFixture.openFileInEditor(ml.getVirtualFile());
        myFixture.doHighlighting();
        List<LineMarkerInfo<?>> mlMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), getProject());

        assertSize(2, mliMarkers);
        assertSize(4, mlMarkers);

        // val x in A.mli
        RelatedItemLineMarkerInfo<?> mi1 = (RelatedItemLineMarkerInfo<?>) mliMarkers.get(1);
        assertEquals("Implements let/val", mi1.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, mi1.getIcon());
        // targets definition in signature and implementation in body
        List<? extends GotoRelatedItem> mi1RelatedItems = new ArrayList<>(mi1.createGotoRelatedItems());
        assertSize(2, mi1RelatedItems);
        assertInstanceOf(mi1RelatedItems.getFirst().getElement(), RPsiVal.class);
        assertInstanceOf(mi1RelatedItems.get(1).getElement(), RPsiLet.class);

        assertEquals(ORIcons.IMPLEMENTING, mlMarkers.getFirst().getIcon());
        assertEquals(7, mlMarkers.getFirst().startOffset); // M->M.mli
        assertEquals("Declare module", mlMarkers.getFirst().getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, mlMarkers.get(1).getIcon());
        assertEquals("Implements let/val", mlMarkers.get(1).getLineMarkerTooltip());
        assertEquals(21, mlMarkers.get(1).startOffset); // x[sig] -> x[impl]
        assertEquals(ORIcons.IMPLEMENTING, mlMarkers.get(2).getIcon());
        assertEquals("Declare let/val", mlMarkers.get(2).getLineMarkerTooltip());
        assertEquals(21, mlMarkers.get(2).startOffset); // x[sig] -> x.rei
        RelatedItemLineMarkerInfo<?> r3 = (RelatedItemLineMarkerInfo<?>) mlMarkers.get(3);
        assertTrue(r3.getLineMarkerTooltip().contains("Declare let/val"));
        assertEquals(ORIcons.IMPLEMENTING, r3.getIcon());
        // targets both definitions (signatures in ml and mli)
        List<? extends GotoRelatedItem> r3RelatedItems = new ArrayList<>(r3.createGotoRelatedItems());
        assertSize(2, r3RelatedItems);
        assertInstanceOf(r3RelatedItems.getFirst().getElement(), RPsiVal.class);
        assertInstanceOf(r3RelatedItems.get(1).getElement(), RPsiVal.class);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/485
    // Module signature with named signature type
    @Test
    public void test_GH_485_named_signature_module_type() {
        FileBase mli = configureCode("A.mli", """
                module type MT = sig
                  val x: int
                end
                """);
        FileBase ml = configureCode("A.ml", """
                module type MT = sig
                  val x : int
                end
                
                module Mm: MT = struct
                  let x = 1
                end
                """);

        myFixture.openFileInEditor(mli.getVirtualFile());
        myFixture.doHighlighting();
        List<LineMarkerInfo<?>> mliMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), getProject());

        myFixture.openFileInEditor(ml.getVirtualFile());
        myFixture.doHighlighting();
        List<LineMarkerInfo<?>> mlMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), getProject());

        assertSize(2, mliMarkers);
        assertSize(6, mlMarkers);

        // module type in A.mli
        RelatedItemLineMarkerInfo<?> mi1 = (RelatedItemLineMarkerInfo<?>) mliMarkers.getFirst();
        assertTextEquals("Implements module", mi1.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, mi1.getIcon());
        List<? extends GotoRelatedItem> mi1RelatedItems = new ArrayList<>(mi1.createGotoRelatedItems());
        assertSize(2, mi1RelatedItems);
        assertContainsElements(mi1RelatedItems.stream().map(m -> ((RPsiInnerModule) m.getElement()).getQualifiedName()).toList(), "A.MT", "A.Mm");
        // module type in A.ml
        LineMarkerInfo<?> m0 = mlMarkers.getFirst();
        assertEquals("Implements module", m0.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, m0.getIcon());
        LineMarkerInfo<?> m1 = mlMarkers.get(1);
        assertEquals("Declare module", m1.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m1.getIcon());
        // val x in module type in A.ml
        LineMarkerInfo<?> m2 = mlMarkers.get(2);
        assertEquals("Implements let/val", m2.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, m2.getIcon());
        // val x in module type in A.ml
        LineMarkerInfo<?> m3 = mlMarkers.get(3);
        assertEquals("Declare let/val", m3.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m3.getIcon());
        // val x in A.ml (to mli)
        LineMarkerInfo<?> m4 = mlMarkers.get(4);
        assertEquals("Declare module", m4.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m4.getIcon());
        // let in A.ml
        LineMarkerInfo<?> m5 = mlMarkers.get(5);
        assertEquals("Declare let/val", m5.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m5.getIcon());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/485
    @Test
    public void test_GH_485_anonymous_signature() {
        configureCode("A.ml", """
                module type M = sig
                  val x: int
                end
                
                module M : sig
                  val x: int
                end = struct
                  let x = 1
                end
                """);

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());
        assertSize(2, lineMarkers);

        // val x
        LineMarkerInfo<?> m0 = lineMarkers.getFirst();
        assertEquals("Implements let/val", m0.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, m0.getIcon());
        // let x
        LineMarkerInfo<?> m1 = lineMarkers.get(1);
        assertEquals("Declare let/val", m1.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m1.getIcon());
    }
}
