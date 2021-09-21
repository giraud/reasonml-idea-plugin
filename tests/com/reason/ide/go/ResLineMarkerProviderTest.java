package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.daemon.impl.*;
import com.reason.ide.*;

import java.util.*;

public class ResLineMarkerProviderTest extends ORBasePlatformTestCase {
    public void test_basic() {
        configureCode("A.resi", "type t");
        configureCode("A.res", "type t\n module Inner = { type t }");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());
        assertSize(1, lineMarkers);
        assertEquals("<html><body><p>Declare type in <a href=\"#navigation//src/A.resi:0\"><code>A.resi</code></a></p></body></html>", lineMarkers.get(0).getLineMarkerTooltip());
    }
}
