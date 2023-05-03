package com.reason.ide.console.ocaml;


import com.intellij.execution.filters.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.testFramework.*;
import com.intellij.testFramework.fixtures.*;
import com.reason.ide.console.*;
import com.reason.ide.console.dune.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@SuppressWarnings({"ConstantConditions", "SameParameterValue"})
@RunWith(JUnit4.class)
public class OCamlConsoleFilterTest extends BasePlatformTestCase {
    @Test
    public void test_common_0() {
        String[] message = OCamlMessages.common.get(0);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 13, resultItem);
        assertHyperlink("file.ml", 4, 6, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_common_1() {
        String[] message = OCamlMessages.common.get(1);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 13, resultItem);
        assertHyperlink("file.ml", 3, 6, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_common_2() {
        String[] message = OCamlMessages.common.get(2);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 13, resultItem);
        assertHyperlink("file.ml", 6, 15, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_408_0() {
        String[] message = OCamlMessages.since408.get(0);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 19, resultItem);
        assertHyperlink("helloworld.ml", 2, 36, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_408_1() {
        String[] message = OCamlMessages.since408.get(1);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 19, resultItem);
        assertHyperlink("helloworld.ml", 4, 6, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_408_2() {
        String[] message = OCamlMessages.since408.get(2);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 20, resultItem);
        assertHyperlink("robustmatch.ml", 33, 6, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_408_3() {
        String[] message = OCamlMessages.since408.get(3);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 19, resultItem);
        assertHyperlink("helloworld.ml", 2, 36, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_408_4() {
        String[] message = OCamlMessages.since408.get(4);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 19, resultItem);
        assertHyperlink("helloworld.ml", 2, 36, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_408_5() {
        String[] message = OCamlMessages.since408.get(5);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 19, resultItem);
        assertHyperlink("helloworld.ml", 2, 36, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_408_6() {
        String[] message = OCamlMessages.since408.get(6);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 13, resultItem);
        assertHyperlink("main.ml", 3, 8, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_408_7() {
        String[] message = OCamlMessages.since408.get(7);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 13, resultItem);
        assertHyperlink("main.ml", 3, 8, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_408_8() {
        String[] message = OCamlMessages.since408.get(8);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 13, resultItem);
        assertHyperlink("main.ml", 13, 34, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_408_9() {
        String[] message = OCamlMessages.since408.get(9);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 13, resultItem);
        assertHyperlink("main.ml", 13, 34, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_412_0() {
        String[] message = OCamlMessages.since412.get(0);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 12, resultItem);
        assertHyperlink("moo.ml", 6, 6, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    @Test
    public void test_ancillary_0() {
        String[] message = OCamlMessages.ancillary.get(0);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        assertNull(results[0]);
        Filter.ResultItem resultItem = results[4].getResultItems().get(0);
        assertHighlight(6, 12, resultItem);
        assertHyperlink("urk.ml", 23, 2, resultItem.getHyperlinkInfo());
        resultItem = results[6].getResultItems().get(0);
        assertHighlight(6, 12, resultItem);
        assertHyperlink("urk.ml", 17, 0, resultItem.getHyperlinkInfo());
    }

    @Test
    public void test_ancillary_1() {
        String[] message = OCamlMessages.ancillary.get(1);

        OCamlConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result[] results = applyMessage(message, filter);

        Filter.ResultItem resultItem = results[0].getResultItems().get(0);
        assertHighlight(6, 13, resultItem);
        assertHyperlink("alrt.ml", 25, 9, resultItem.getHyperlinkInfo());
        assertNullResult(results, 1);
    }

    private Filter.Result[] applyMessage(String[] message, OCamlConsoleFilter filter) {
        Filter.Result[] results = new Filter.Result[message.length];
        for (int i = 0; i < message.length; i++) {
            String line = message[i];
            results[i] = filter.applyFilter(line, line.length());
        }
        return results;
    }

    private void assertNullResult(Filter.Result[] results, int start) {
        for (int i = start; i < results.length; i++) {
            assertNull(results[i]);
        }
    }

    private void assertHyperlink(String filename, int line, int col, HyperlinkInfo hyperlinkInfo) {
        OpenFileHyperlinkInfo info = (OpenFileHyperlinkInfo) hyperlinkInfo;
        assertEquals(filename, info.getVirtualFile().getName());
        assertEquals(line, info.getDescriptor().getLine() + 1);
        assertEquals(col, info.getDescriptor().getColumn());
    }

    private void assertHighlight(int start, int end, Filter.ResultItem item) { // file path
        assertEquals(start, item.getHighlightStartOffset());
        assertEquals(end, item.getHighlightEndOffset());
    }

    static class MemoryRescriptConsoleFilter extends OCamlConsoleFilter {
        public MemoryRescriptConsoleFilter(@NotNull Project project) {
            super(project);
        }

        @Override
        protected @Nullable OpenFileHyperlinkInfo getHyperlinkInfo(String filePath, int documentLine, int documentColumn) {
            VirtualFile sourceFile = new LightVirtualFile(filePath);
            return new OpenFileHyperlinkInfo(myProject, sourceFile, documentLine, documentColumn);
        }
    }
}
