package com.reason.ide;

import java.util.*;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.PsiModule;

import static com.intellij.psi.search.GlobalSearchScope.allScope;
import static com.reason.lang.core.ORFileType.*;

@SuppressWarnings("ConstantConditions")
public class PsiFinderTest extends BasePlatformTestCase {

    public void testModuleLet() {
        myFixture.configureByText("ReasonReact.rei", "module Router: { let api = 1; };");
        myFixture.configureByText("ReasonReact.re", "module Router: { let implRE = 1; };");

        Collection<PsiModule> modules = PsiFinder.getInstance(getProject()).findModules("Router", interfaceOrImplementation, allScope(getProject()));

        assertSize(1, modules);
        assertEquals("let api = 1", modules.iterator().next().getExpressions().iterator().next().getText());
    }

    public void testFindRelatedFile() {
        FileBase intf = (FileBase) myFixture.configureByText("A.rei", "module Router: { let x = 1; };");
        FileBase impl = (FileBase) myFixture.configureByText("A.re", "module Router: { let x = 1; };");
        FileBase implOnly = (FileBase) myFixture.configureByText("B.re", "module Router: { let x = 1; };");

        FileBase implRelated = PsiFinder.getInstance(getProject()).findRelatedFile(intf);
        FileBase intfRelated = PsiFinder.getInstance(getProject()).findRelatedFile(impl);
        FileBase onlyRelated = PsiFinder.getInstance(getProject()).findRelatedFile(implOnly);

        assertEquals("A.re", implRelated.getName());
        assertEquals("A.rei", intfRelated.getName());
        assertNull(onlyRelated);
    }

    public void testException() {
        myFixture.configureByText("A.rei", "exception Ex;");
        myFixture.configureByText("A.re", "exception Ex;");

        PsiQualifiedNamedElement intf1 = PsiFinder.getInstance(getProject()).findException("A.Ex", interfaceOnly, allScope(getProject()));
        PsiQualifiedNamedElement intf2 = PsiFinder.getInstance(getProject()).findException("A.Ex", interfaceOrImplementation, allScope(getProject()));
        PsiQualifiedNamedElement impl = PsiFinder.getInstance(getProject()).findException("A.Ex", implementationOnly, allScope(getProject()));

        assertEquals("rei", intf1.getContainingFile().getFileType().getDefaultExtension());
        assertEquals("rei", intf2.getContainingFile().getFileType().getDefaultExtension());
        assertEquals("re", impl.getContainingFile().getFileType().getDefaultExtension());
    }


}
