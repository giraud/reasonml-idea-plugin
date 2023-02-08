package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.daemon.impl.*;
import com.reason.ide.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.util.*;

@RunWith(JUnit4.class)
public class ResLineMarkerProviderTest extends ORBasePlatformTestCase {
    @Test
    public void test_basic() {
        configureCode("A.resi", "type t");
        configureCode("A.res", "type t\n module Inner = { type t }");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());
        assertSize(1, lineMarkers);
        assertEquals("<html><body><p>Declare type in <a href=\"#navigation//src/A.resi:0\"><code>A.resi</code></a></p></body></html>", lineMarkers.get(0).getLineMarkerTooltip());
    }
}
