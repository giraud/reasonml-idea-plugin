package com.reason.ide.go;

import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.daemon.impl.*;
import com.reason.ide.*;

import java.util.*;

public class RmlLineMarkerProviderTest extends ORBasePlatformTestCase {
    public void test_basic() {
        configureCode("A.rei", "type t");
        configureCode("A.re", "type t; module Inner = { type t; };");

        myFixture.doHighlighting();

        List<LineMarkerInfo<?>> lineMarkers = DaemonCodeAnalyzerImpl.getLineMarkers(myFixture.getEditor().getDocument(), myFixture.getProject());
        assertSize(1, lineMarkers);
        assertEquals("<html><body><p>Declare type in <a href=\"#navigation//src/A.rei:0\"><code>A.rei</code></a></p></body></html>", lineMarkers.get(0).getLineMarkerTooltip());
    }
}
