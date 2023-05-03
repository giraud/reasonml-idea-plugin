package com.reason.ide.console.rescript;


import com.intellij.execution.filters.*;
import com.intellij.openapi.project.*;
import com.intellij.testFramework.fixtures.*;
import org.jetbrains.annotations.*;
import org.junit.*;
import org.junit.runner.*;
import org.junit.runners.*;

@SuppressWarnings("ConstantConditions")
@RunWith(JUnit4.class)
public class RescriptConsoleFilterTest extends BasePlatformTestCase {
    @Test
    public void test_line_01() {
        String line = "File \"C:\\dev\\src\\MyFile.re\", line 234, characters 10-11:\n";

        RescriptConsoleFilter filter = new MemoryRescriptConsoleFilter(myFixture.getProject());
        Filter.Result result = filter.applyFilter(line, line.length());

        Filter.ResultItem resultItem = result.getResultItems().get(0);
        assertEquals(6, resultItem.getHighlightStartOffset());
        assertEquals(26, resultItem.getHighlightEndOffset());
    }

    static class MemoryRescriptConsoleFilter extends RescriptConsoleFilter {
        public MemoryRescriptConsoleFilter(@NotNull Project project) {
            super(project);
        }

        @Override
        protected @Nullable OpenFileHyperlinkInfo getHyperlinkInfo(String filePath, int documentLine, int documentColumn) {
            return null;
        }
    }
}
