package com.reason.ide;

import java.util.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.PsiException;
import com.reason.lang.core.psi.PsiFakeModule;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;

import static com.intellij.psi.search.GlobalSearchScope.allScope;
import static com.reason.lang.core.ExpressionFilterConstants.NO_FILTER;
import static com.reason.lang.core.ORFileType.*;
import static com.reason.lang.core.psi.ExpressionScope.all;

@SuppressWarnings("ConstantConditions")
public class PsiFinderTest extends ORBasePlatformTestCase {

    public void testModuleLet() {
        myFixture.configureByText("ReasonReact.rei", "module Router: { type api;  };");
        myFixture.configureByText("ReasonReact.re", "module Router = { type impl; };");

        GlobalSearchScope scope = allScope(getProject());
        List<PsiModule> modulesA = new ArrayList<>(PsiFinder.getInstance(getProject()).findModulesbyName("Router", interfaceOnly, null, scope));
        List<PsiModule> modulesB = new ArrayList<>(PsiFinder.getInstance(getProject()).findModulesbyName("Router", implementationOnly, null, scope));
        List<PsiModule> modulesC = new ArrayList<>(PsiFinder.getInstance(getProject()).findModulesbyName("Router", interfaceOrImplementation, null, scope));
        List<PsiModule> modulesD = new ArrayList<>(PsiFinder.getInstance(getProject()).findModulesbyName("Router", both, null, scope));

        assertSize(1, modulesA);
        assertEquals("type api", modulesA.get(0).getExpressions(all, NO_FILTER).iterator().next().getText());

        assertSize(1, modulesB);
        assertEquals("type impl", modulesB.get(0).getExpressions(all, NO_FILTER).iterator().next().getText());

        assertSize(1, modulesC);
        assertEquals("type api", modulesC.get(0).getExpressions(all, NO_FILTER).iterator().next().getText());

        assertSize(2, modulesD);
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

    public void testFindModuleByQn() {
        myFixture.configureByText("a.rei", "module A: { let x = 1; };");
        myFixture.configureByText("A.re", "module A = { let x = 1; };");

        List<PsiModule> fileModules = new ArrayList<>(PsiFinder.getInstance(getProject()).findModulesFromQn("A", true, both, allScope(getProject())));
        List<PsiModule> innerModules = new ArrayList<>(PsiFinder.getInstance(getProject()).findModulesFromQn("A.A", true, both, allScope(getProject())));

        assertSize(2, fileModules);
        assertInstanceOf(fileModules.get(0), PsiFakeModule.class);
        assertInstanceOf(fileModules.get(1), PsiFakeModule.class);

        assertSize(2, innerModules);
        assertInstanceOf(innerModules.get(0), PsiInnerModule.class);
        assertInstanceOf(innerModules.get(1), PsiInnerModule.class);
    }

    public void testException() {
        myFixture.configureByText("A.rei", "exception Ex;");
        myFixture.configureByText("A.re", "exception Ex;");

        PsiFinder psiFinder = PsiFinder.getInstance(getProject());
        PsiException intf1 = psiFinder.findException("A.Ex", interfaceOnly, allScope(getProject()));
        PsiException intf2 = psiFinder.findException("A.Ex", interfaceOrImplementation, allScope(getProject()));
        PsiException impl = psiFinder.findException("A.Ex", implementationOnly, allScope(getProject()));

        assertEquals("rei", intf1.getContainingFile().getFileType().getDefaultExtension());
        assertEquals("rei", intf2.getContainingFile().getFileType().getDefaultExtension());
        assertEquals("re", impl.getContainingFile().getFileType().getDefaultExtension());
    }

    public void testFindModuleAlias() {
        myFixture.configureByText("Belt_Option.mli", "type t;");
        myFixture.configureByText("Belt_Option.ml", "type t;");
        myFixture.configureByText("Belt.ml", "module Option = Belt_Option;");

        PsiFinder psiFinder = PsiFinder.getInstance(getProject());
        List<PsiModule> moduleAliases = new ArrayList<>(psiFinder.findModuleAlias("Belt.Option", allScope(getProject())));

        assertSize(2, moduleAliases);
        assertInstanceOf(moduleAliases.get(0), PsiFakeModule.class);
        assertInstanceOf(moduleAliases.get(1), PsiFakeModule.class);
    }

    public void testRml_letDeconstruction() {
        configureCode("A.re", "let x = something;");
        configureCode("B.re", "let (x, y) = other;");

        Set<PsiLet> lets = PsiFinder.getInstance(getProject()).findLets("x", both);

        assertSize(2, lets);
    }

    public void testOcl_letDeconstruction() {
        configureCode("A.ml", "let x = something");
        configureCode("B.ml", "let (x, y) = other");

        Set<PsiLet> lets = PsiFinder.getInstance(getProject()).findLets("x", both);

        assertSize(2, lets);
    }
}
