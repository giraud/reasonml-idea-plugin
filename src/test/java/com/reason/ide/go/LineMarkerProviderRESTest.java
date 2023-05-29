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
public class LineMarkerProviderRESTest extends ORBasePlatformTestCase {
    @Test
    public void test_let_files() {
        configureCode("A.resi", "let x: int");
        configureCode("A.res", "let x = 1");

        myFixture.doHighlighting();

        Document document = myFixture.getEditor().getDocument();
        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(document, myFixture.getProject());
        LineMarkerInfo<?> m0 = lineMarkers.get(0);
        assertEquals(ORIcons.IMPLEMENTING, m0.getIcon());
        assertEquals("Declare let/val", m0.getLineMarkerTooltip());
        assertSize(1, lineMarkers);
    }

    @Test
    public void test_let() {
        configureCode("A.res", "module type I = {\n" +
                "  let x: int\n" +
                "}\n" +
                "module M1 : I = {\n" +
                "  let x = 1\n" +
                "}\n" +
                "module M2 : I = {\n" +
                "  let x = 2\n" +
                "}"

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
        configureCode("A.resi", "type t");
        configureCode("A.res", "type t\n module Inner = { type t }");

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
        configureCode("A.res", "module type I = {\n" +
                "  type t\n" +
                "}\n" +
                "module M1 : I = {\n" +
                "  type t\n" +
                "}\n" +
                "module M2 : I = {\n" +
                "  type t\n" +
                "}"

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
    public void test_external_files() {
        configureCode("A.resi", "external t: int = \"\"");
        configureCode("A.res", "external t: int = \"\"");

        myFixture.doHighlighting();

        Document document = myFixture.getEditor().getDocument();
        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(document, myFixture.getProject());
        LineMarkerInfo<?> m0 = lineMarkers.get(0);
        assertEquals(ORIcons.IMPLEMENTING, m0.getIcon());
        assertEquals("Declare external", m0.getLineMarkerTooltip());
        assertSize(1, lineMarkers);
    }

    @Test
    public void test_external() {
        configureCode("A.res", "module type I = {\n" +
                "  external t: int = \"\"\n" +
                "}\n" +
                "module M1 : I = {\n" +
                "  external t: int = \"\"\n" +
                "}\n" +
                "module M2 : I = {\n" +
                "  external t: int = \"\"\n" +
                "}"

        );

        myFixture.doHighlighting();

        Document document = myFixture.getEditor().getDocument();
        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(document, myFixture.getProject());
        LineMarkerInfo<?> m1 = lineMarkers.get(1);
        assertEquals(ORIcons.IMPLEMENTED, m1.getIcon());
        assertEquals("Implements external", m1.getLineMarkerTooltip());
        LineMarkerInfo<?> m3 = lineMarkers.get(3);
        assertEquals(ORIcons.IMPLEMENTING, m3.getIcon());
        assertEquals("Declare external", m3.getLineMarkerTooltip());
        LineMarkerInfo<?> m5 = lineMarkers.get(5);
        assertEquals(ORIcons.IMPLEMENTING, m5.getIcon());
        assertEquals("Declare external", m5.getLineMarkerTooltip());
        assertSize(6, lineMarkers);
    }

    @Test
    public void test_module() {
        configureCode("A.res", "module type Intf = {}\n" +
                "module Impl : Intf = {}");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());

        LineMarkerInfo<?> m0 = lineMarkers.get(0);
        assertEquals(ORIcons.IMPLEMENTED, m0.getIcon());
        assertEquals("Implements module", m0.getLineMarkerTooltip());
        LineMarkerInfo<?> m1 = lineMarkers.get(1);
        assertEquals(ORIcons.IMPLEMENTING, m1.getIcon());
        assertEquals("Declare module", m1.getLineMarkerTooltip());
        assertSize(2, lineMarkers);
    }

    @Test
    public void test_modules() {
        configureCode("A.res", "module type Intf = {}\n" +
                "module ImplA : Intf = {}\n" +
                "module ImplB : Intf = {}");

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
        configureCode("A.resi", "exception X");
        configureCode("A.res", "exception X");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());

        LineMarkerInfo<?> m0 = lineMarkers.get(0);
        assertEquals(ORIcons.IMPLEMENTING, m0.getIcon());
        assertEquals("Declare exception", m0.getLineMarkerTooltip());
        assertSize(1, lineMarkers);
    }

    @Test
    public void test_exception() {
        configureCode("A.res", "module type I = {\n" +
                "  exception X\n" +
                "}\n" +
                "module M:I = {\n" +
                "  exception X\n" +
                "}");

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
}
