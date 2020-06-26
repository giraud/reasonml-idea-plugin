package com.reason.ide.reference;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.intellij.usageView.UsageInfo;

import java.util.Collection;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class FindLIdentUsagesTest extends BasePlatformTestCase {

    public void testFromModule() {
        myFixture.configureByText("FLIA.re", "let x<caret> = 1;");
        myFixture.configureByText("FLIB.re", "let y = FLIA.x + 2;");

        Collection<UsageInfo> usages = myFixture.testFindUsages("FLIA.re");
        assertSize(1, usages);
    }

    public void testSameModule() {
        myFixture.configureByText("FLIC.re", "let x<caret> = 1; let y = x + 1;");

        List<UsageInfo> usages = (List<UsageInfo>) myFixture.testFindUsages("FLIC.re");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.get(0);
        assertEquals("x + 1", usageInfo.getElement().getParent().getText());
    }

}
