package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.daemon.impl.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@RunWith(JUnit4.class)
public class OclLineMarkerProviderTest extends ORBasePlatformTestCase {
    @Test
    public void test_basic() {
        configureCode("A.mli", "type t");
        configureCode("A.ml", "type t\n module Inner = struct type t end");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());
        assertSize(1, lineMarkers);
        assertEquals("<html><body><p>Declare type in <a href=\"#navigation//src/A.mli:0\"><code>A.mli</code></a></p></body></html>", lineMarkers.get(0).getLineMarkerTooltip());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/322
    // Class types in .mli files should link to the corresponding definition in the .ml file
    @Test
    public void test_GH_322() {
        configureCode("A.mli", "class type proof_view =\n object\n end");
        configureCode("A.ml", "class type proof_view =\n object\n end");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());
        assertSize(1, lineMarkers);
        assertEquals("<html><body><p>Declare class in <a href=\"#navigation//src/A.mli:0\"><code>A.mli</code></a></p></body></html>", lineMarkers.get(0).getLineMarkerTooltip());
    }
}
