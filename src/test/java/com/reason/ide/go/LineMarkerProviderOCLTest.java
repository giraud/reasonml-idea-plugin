package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.openapi.editor.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@RunWith(JUnit4.class)
public class LineMarkerProviderOCLTest extends ORBasePlatformTestCase {
    @Test
    public void test_let_val_files() {
        configureCode("A.mli", "val x: int");
        configureCode("A.ml", "let x = 1");

        myFixture.doHighlighting();

        Document document = myFixture.getEditor().getDocument();
        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(document, myFixture.getProject());
        LineMarkerInfo<?> m0 = lineMarkers.get(0);
        assertEquals(ORIcons.IMPLEMENTING, m0.getIcon());
        assertEquals("Declare let/val", m0.getLineMarkerTooltip());
        assertSize(1, lineMarkers);
    }

    @Test
    public void test_let_val() {
        configureCode("A.ml", "module type I = struct\n" +
                "  val x: int\n" +
                "end\n" +
                "module M1 : I = struct\n" +
                "  let x = 1\n" +
                "end\n" +
                "module M2 : I = struct\n" +
                "  let x = 2\n" +
                "end"

        );

        myFixture.doHighlighting();

        Document document = myFixture.getEditor().getDocument();
        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(document, myFixture.getProject());
        LineMarkerInfo<?> m1 = lineMarkers.get(1);
        assertEquals(ORIcons.IMPLEMENTED, m1.getIcon());
        assertEquals("Implements let/val", m1.getLineMarkerTooltip());
        LineMarkerInfo<?> m3 = lineMarkers.get(3);
        assertEquals(ORIcons.IMPLEMENTING, m3.getIcon());
        assertEquals("Declare let/val", m3.getLineMarkerTooltip());
        LineMarkerInfo<?> m5 = lineMarkers.get(5);
        assertEquals(ORIcons.IMPLEMENTING, m5.getIcon());
        assertEquals("Declare let/val", m5.getLineMarkerTooltip());
        assertSize(6, lineMarkers);
    }

    @Test
    public void test_type_files() {
        configureCode("A.mli", "type t");
        configureCode("A.ml", "type t\n module Inner = struct type t end");

        myFixture.doHighlighting();

        Document document = myFixture.getEditor().getDocument();
        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(document, myFixture.getProject());
        LineMarkerInfo<?> m0 = lineMarkers.get(0);
        assertEquals(ORIcons.IMPLEMENTING, m0.getIcon());
        assertEquals("Declare type", m0.getLineMarkerTooltip());
        assertSize(1, lineMarkers);
    }

    @Test
    public void test_type() {
        configureCode("A.ml", "module type I = struct\n" +
                "  type t\n" +
                "end\n" +
                "module M1 : I = struct\n" +
                "  type t\n" +
                "end\n" +
                "module M2 : I = struct\n" +
                "  type t\n" +
                "end"
        );

        myFixture.doHighlighting();

        Document document = myFixture.getEditor().getDocument();
        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(document, myFixture.getProject());
        LineMarkerInfo<?> m1 = lineMarkers.get(1);
        assertEquals(ORIcons.IMPLEMENTED, m1.getIcon());
        assertEquals("Implements type", m1.getLineMarkerTooltip());
        LineMarkerInfo<?> m3 = lineMarkers.get(3);
        assertEquals(ORIcons.IMPLEMENTING, m3.getIcon());
        assertEquals("Declare type", m3.getLineMarkerTooltip());
        LineMarkerInfo<?> m5 = lineMarkers.get(5);
        assertEquals(ORIcons.IMPLEMENTING, m5.getIcon());
        assertEquals("Declare type", m5.getLineMarkerTooltip());
        assertSize(6, lineMarkers);
    }

    @Test
    public void test_module() {
        configureCode("A.ml", "module type Intf = struct end\n" +
                "module Impl : Intf = struct end");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());

        LineMarkerInfo<?> m0 = lineMarkers.get(0);
        assertEquals("Implements module", m0.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTED, m0.getIcon());
        LineMarkerInfo<?> m1 = lineMarkers.get(1);
        assertEquals("Declare module", m1.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m1.getIcon());
        assertSize(2, lineMarkers);
    }

    @Test
    public void test_modules() {
        configureCode("A.ml", "module type Intf = struct end\n" +
                "module ImplA : Intf = struct end\n" +
                "module ImplB : Intf = struct end");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());

        LineMarkerInfo<?> m0 = lineMarkers.get(0);
        assertEquals(ORIcons.IMPLEMENTED, m0.getIcon());
        assertEquals("Implements module", m0.getLineMarkerTooltip());
        LineMarkerInfo<?> m1 = lineMarkers.get(1);
        assertEquals(ORIcons.IMPLEMENTING, m1.getIcon());
        assertEquals("Declare module", m1.getLineMarkerTooltip());
        LineMarkerInfo<?> m2 = lineMarkers.get(2);
        assertEquals(ORIcons.IMPLEMENTING, m2.getIcon());
        assertEquals("Declare module", m2.getLineMarkerTooltip());
        assertSize(3, lineMarkers);
    }

    @Test
    public void test_exception_files() {
        configureCode("A.mli", "exception X");
        configureCode("A.ml", "exception X");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());

        LineMarkerInfo<?> m0 = lineMarkers.get(0);
        assertEquals(ORIcons.IMPLEMENTING, m0.getIcon());
        assertEquals("Declare exception", m0.getLineMarkerTooltip());
        assertSize(1, lineMarkers);
    }

    @Test
    public void test_exception() {
        configureCode("A.ml", "module type I = struct\n" +
                "  exception X\n" +
                "end\n" +
                "module M:I = struct\n" +
                "  exception X\n" +
                "end");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());

        LineMarkerInfo<?> m1 = lineMarkers.get(1);
        assertEquals(ORIcons.IMPLEMENTED, m1.getIcon());
        assertEquals("Implements exception", m1.getLineMarkerTooltip());
        LineMarkerInfo<?> m3 = lineMarkers.get(3);
        assertEquals(ORIcons.IMPLEMENTING, m3.getIcon());
        assertEquals("Declare exception", m3.getLineMarkerTooltip());
        assertSize(4, lineMarkers);
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/322
    // Class types in .mli files should link to the corresponding definition in the .ml file
    @Test
    public void test_GH_322() {
        configureCode("A.mli", "class type proof_view =\n object\n end");
        configureCode("A.ml", "class type proof_view =\n object\n end");

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
        configureCode("A.mli", "class type proof_view =\n " +
                "object\n" +
                "  inherit GObj.widget\n" +
                "  method buffer: GText.buffer\n" +
                "end");
        configureCode("A.ml", "class type proof_view =\n" +
                "object\n" +
                "  inherit GObj.widget\n" +
                "  method buffer: GText.buffer\n" +
                "end");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());
        LineMarkerInfo<?> m1 = lineMarkers.get(1);
        assertEquals("Declare method", m1.getLineMarkerTooltip());
        assertEquals(ORIcons.IMPLEMENTING, m1.getIcon());
        assertSize(2, lineMarkers);
    }
}
