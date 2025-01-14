package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.navigation.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("DataFlowIssue")
public class LineMarkerProviderRESTest extends ORBasePlatformTestCase {
    @Test
    public void test_let_files() {
        FileBase intf = configureCode("A.resi", "let x: int");
        FileBase impl = configureCode("A.res", "let x = 1");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements let/val", markers.getFirst().getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.getFirst().getIcon());
        assertEquals("Declare let/val", markers.getFirst().getLineMarkerTooltip());
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

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements module", markers.getFirst().getLineMarkerTooltip());
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

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements type", markers.getFirst().getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.getFirst().getIcon());
        assertEquals("Declare type", markers.getFirst().getLineMarkerTooltip());
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
        FileBase intf = configureCode("A.resi", "external t: int = \"\"");
        FileBase impl = configureCode("A.res", "external t: int = \"\"");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements external", markers.getFirst().getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.getFirst().getIcon());
        assertEquals("Declare external", markers.getFirst().getLineMarkerTooltip());
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

        assertEquals(ORIcons.IMPLEMENTED, lineMarkers.getFirst().getIcon());
        assertEquals("Implements module", lineMarkers.getFirst().getLineMarkerTooltip());

        assertEquals(ORIcons.IMPLEMENTING, lineMarkers.get(1).getIcon());
        assertEquals("Declare module", lineMarkers.get(1).getLineMarkerTooltip());

        assertSize(2, lineMarkers);
    }

    @Test
    public void test_module_type_files() {
        FileBase intf = configureCode("A.resi", "module type A1 = {}");
        FileBase impl = configureCode("A.res", "module type A1 = {}");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements module", markers.getFirst().getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.getFirst().getIcon());
        assertEquals("Declare module", markers.getFirst().getLineMarkerTooltip());
    }

    @Test
    public void test_module_type_both_direction() {
        FileBase intf = configureCode("A.resi", "module type A1 = {}");
        FileBase impl = configureCode("A.res", """
                // ...
                module type A1 = {}
                
                module A2 : A1 = {}
                """);

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertSize(1, markers);
        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements module", markers.getFirst().getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertSize(3, markers);
        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements module", markers.getFirst().getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, markers.get(1).getIcon());
        assertEquals("Declare module", markers.get(1).getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, markers.get(2).getIcon());
        assertEquals("Declare module", markers.get(2).getLineMarkerTooltip());
    }

    @Test
    public void test_module_deep() {
        configureCode("A.res", """
                module B = {
                  module type Intf = {}
                }
                
                module IncorrectImpl : Intf = {}
                module CorrectImpl : B.Intf = {}
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
        FileBase f = configureCode("A.res", """
                module type Intf = {}
                module ImplA : Intf = {}
                module ImplB : Intf = {}
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
        FileBase intf = configureCode("A.resi", "exception X");
        FileBase impl = configureCode("A.res", "exception X");

        List<LineMarkerInfo<?>> markers = doHighlight(intf);

        assertEquals(ORIcons.IMPLEMENTED, markers.getFirst().getIcon());
        assertEquals("Implements exception", markers.getFirst().getLineMarkerTooltip());

        markers = doHighlight(impl);

        assertEquals(ORIcons.IMPLEMENTING, markers.getFirst().getIcon());
        assertEquals("Declare exception", markers.getFirst().getLineMarkerTooltip());
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

    // https://github.com/giraud/reasonml-idea-plugin/issues/485
    // Module signature with inline signature
    @Test
    public void test_GH_485_signature_in_different_files() {
        FileBase resi = configureCode("A.resi", """
                module M: {
                  let x: int
                };
                """);
        FileBase res = configureCode("A.res", """
                module M: {
                   let x: int
                 } = {
                   let x = 1
                 };
                """);

        myFixture.openFileInEditor(resi.getVirtualFile());
        myFixture.doHighlighting();
        List<LineMarkerInfo<?>> resiMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), getProject());

        myFixture.openFileInEditor(res.getVirtualFile());
        myFixture.doHighlighting();
        List<LineMarkerInfo<?>> resMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), getProject());

        assertSize(2, resiMarkers);
        assertSize(4, resMarkers);

        // val x in A.rei
        RelatedItemLineMarkerInfo<?> ri1 = (RelatedItemLineMarkerInfo<?>) resiMarkers.get(1);
        assertEquals("Implements let/val", ri1.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, ri1.getIcon());
        // targets definition in signature and implementation in body
        List<? extends GotoRelatedItem> ri1RelatedItems = new ArrayList<>(ri1.createGotoRelatedItems());
        assertSize(2, ri1RelatedItems);
        assertInstanceOf(ri1RelatedItems.getFirst().getElement(), RPsiLet.class);
        assertInstanceOf(ri1RelatedItems.get(1).getElement(), RPsiLet.class);

        assertEquals(ORIcons.IMPLEMENTING, resMarkers.getFirst().getIcon());
        assertEquals(7, resMarkers.getFirst().startOffset); // M->M.mli
        assertEquals("Declare module", resMarkers.getFirst().getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, resMarkers.get(1).getIcon());
        assertEquals("Implements let/val", resMarkers.get(1).getLineMarkerTooltip());
        assertEquals(19, resMarkers.get(1).startOffset); // x[sig] -> x[impl]
        assertEquals(ORIcons.IMPLEMENTING, resMarkers.get(2).getIcon());
        assertEquals("Declare let/val", resMarkers.get(2).getLineMarkerTooltip());
        assertEquals(19, resMarkers.get(2).startOffset); // x[sig] -> x.rei
        RelatedItemLineMarkerInfo<?> r3 = (RelatedItemLineMarkerInfo<?>) resMarkers.get(3);
        assertTrue(r3.getLineMarkerTooltip().contains("Declare let/val"));
        assertEquals(ORIcons.IMPLEMENTING, r3.getIcon());
        // targets both definitions (signatures in ml and mli)
        List<? extends GotoRelatedItem> r3RelatedItems = new ArrayList<>(r3.createGotoRelatedItems());
        assertSize(2, r3RelatedItems);
        assertInstanceOf(r3RelatedItems.getFirst().getElement(), RPsiLet.class);
        assertInstanceOf(r3RelatedItems.get(1).getElement(), RPsiLet.class);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/485
    // Module signature with named signature type
    @Test
    public void test_GH_485_named_signature_module_type() {
        FileBase resi = configureCode("A.resi", """
                module type MT = {
                  let x: int
                };
                """);
        FileBase res = configureCode("A.res", """
                module type MT = {
                  let x : int
                };
                
                module Mm: MT = {
                  let x = 1
                };
                """);

        myFixture.openFileInEditor(resi.getVirtualFile());
        myFixture.doHighlighting();
        List<LineMarkerInfo<?>> resiMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), getProject());

        myFixture.openFileInEditor(res.getVirtualFile());
        myFixture.doHighlighting();
        List<LineMarkerInfo<?>> resMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), getProject());

        assertSize(2, resiMarkers);
        assertSize(6, resMarkers);

        // module type in A.rei
        RelatedItemLineMarkerInfo<?> mi1 = (RelatedItemLineMarkerInfo<?>) resiMarkers.getFirst();
        assertTextEquals("Implements module", mi1.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, mi1.getIcon());
        List<? extends GotoRelatedItem> mi1RelatedItems = new ArrayList<>(mi1.createGotoRelatedItems());
        assertSize(2, mi1RelatedItems);
        assertContainsElements(mi1RelatedItems.stream().map(m -> ((RPsiInnerModule) m.getElement()).getQualifiedName()).toList(), "A.MT", "A.Mm");
        // module type in A.re
        LineMarkerInfo<?> m0 = resMarkers.getFirst();
        assertEquals("Implements module", m0.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, m0.getIcon());
        LineMarkerInfo<?> m1 = resMarkers.get(1);
        assertEquals("Declare module", m1.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m1.getIcon());
        // val x in module type in A.re
        LineMarkerInfo<?> m2 = resMarkers.get(2);
        assertEquals("Implements let/val", m2.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, m2.getIcon());
        // val x in module type in A.re
        LineMarkerInfo<?> m3 = resMarkers.get(3);
        assertEquals("Declare let/val", m3.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m3.getIcon());
        // val x in A.re (to mli)
        LineMarkerInfo<?> m4 = resMarkers.get(4);
        assertEquals("Declare module", m4.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m4.getIcon());
        // let in A.re
        LineMarkerInfo<?> m5 = resMarkers.get(5);
        assertEquals("Declare let/val", m5.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m5.getIcon());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/485
    @Test
    public void test_GH_485_anonymous_signature() {
        configureCode("A.re", """
                module type M = {
                  let x: int;
                };
                
                module M : {
                  let x: int;
                } = {
                  let x = 1;
                };
                """);

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());
        assertSize(2, lineMarkers);

        // let x
        LineMarkerInfo<?> m0 = lineMarkers.getFirst();
        assertEquals("Implements let/val", m0.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, m0.getIcon());
        // let x
        LineMarkerInfo<?> m1 = lineMarkers.get(1);
        assertEquals("Declare let/val", m1.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m1.getIcon());
    }
}
