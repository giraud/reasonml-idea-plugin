package com.reason.ide.insight;

import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.PsiModule;

import java.util.Collection;

import static com.intellij.psi.search.GlobalSearchScope.allScope;
import static com.reason.lang.core.ORFileType.interfaceOrImplementation;

public class PsiFinderTest extends LightPlatformCodeInsightFixtureTestCase {

    public void testModuleLetCompletion() {
        myFixture.configureByText("ReasonReact.rei", "module Router: { let api = 1; };");
        myFixture.configureByText("ReasonReact.re", "module Router: { let implRE = 1; };");
        myFixture.configureByText("ReasonReact.ml", "module Router: { let implML = 1; };");

        Collection<PsiModule> modules = PsiFinder.getInstance(getProject()).findModules("Router", interfaceOrImplementation, allScope(getProject()));

        assertSize(1, modules);
        assertEquals("let api = 1", modules.iterator().next().getExpressions().iterator().next().getText());
    }
}
