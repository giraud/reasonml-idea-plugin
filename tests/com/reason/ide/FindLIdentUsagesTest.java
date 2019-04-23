package com.reason.ide;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.intellij.usageView.UsageInfo;

import java.util.Collection;

@SuppressWarnings("ConstantConditions")
public class FindLIdentUsagesTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testFromModule() {
        myFixture.configureByText("A.re", "let x<caret> = 1;");
        myFixture.configureByText("B.re", "let y = A.x + 2;");

        Collection<UsageInfo> usages = myFixture.testFindUsages("A.re");
        assertSize(1, usages);

    }

    public void testSameModule() {
        myFixture.configureByText("A.re", "let x<caret> = 1; let y = x + 1;");

        Collection<UsageInfo> usages = myFixture.testFindUsages("A.re");
        assertSize(1, usages);
        UsageInfo usageInfo = usages.iterator().next();
        assertEquals("x + 1", usageInfo.getElement().getParent().getText());
    }

}
