package com.reason.ide;

import com.intellij.codeInsight.*;
import com.intellij.codeInsight.daemon.*;
import com.intellij.codeInsight.daemon.impl.*;
import com.intellij.lang.*;
import com.intellij.lang.documentation.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.util.io.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.*;
import com.intellij.testFramework.fixtures.*;
import com.intellij.usageView.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;
import java.util.*;

@RunWith(JUnit4.class)
public abstract class ORBasePlatformTestCase extends BasePlatformTestCase {
    @NotNull
    @SuppressWarnings("UnusedReturnValue")
    protected FileBase configureCode(@NotNull String fileName, @NotNull String code) {
        PsiFile file = myFixture.configureByText(fileName, code);
        System.out.println("» " + fileName + " " + this.getClass());
        System.out.println(DebugUtil.psiToString(file, false, true));

        return (FileBase) file;
    }

    protected @NotNull String toJson(@NotNull String value) {
        return value.replaceAll("'", "\"").replaceAll("@", "\n");
    }

    protected @NotNull String loadFile(@NotNull String filename) throws IOException {
        return FileUtil.loadFile(new File(getTestDataPath(), filename), CharsetToolkit.UTF8, true).trim();
    }

    protected @Nullable String getQuickDoc(@NotNull FileBase file, @NotNull Language lang) {
        DocumentationProvider docProvider = LanguageDocumentation.INSTANCE.forLanguage(lang);
        PsiElement resolvedElement = myFixture.getElementAtCaret();
        PsiElement element = file.findElementAt(myFixture.getCaretOffset() - 1);
        return docProvider.getQuickNavigateInfo(resolvedElement, element);
    }

    protected @Nullable String getDocForElement(@NotNull FileBase file, @NotNull Language lang, PsiElement resolvedElement) {
        DocumentationProvider docProvider = LanguageDocumentation.INSTANCE.forLanguage(lang);
        PsiElement element = file.findElementAt(myFixture.getCaretOffset() - 1);
        return docProvider.generateDoc(resolvedElement, element);
    }

    protected @Nullable String getDoc(@NotNull FileBase file, @NotNull Language lang) {
        PsiElement resolvedElement = myFixture.getElementAtCaret();
        return getDocForElement(file, lang, resolvedElement);
    }

    protected @NotNull List<UsageInfo> findUsages(String fileName) {
        return (List<UsageInfo>) myFixture.testFindUsages(fileName);
    }

    protected @NotNull PsiElement getNavigationElementAtCaret() {
        PsiElement elementAtCaret = myFixture.getElementAtCaret();
        return TargetElementUtil.getInstance().getGotoDeclarationTarget(elementAtCaret, elementAtCaret.getNavigationElement());
    }

    protected @NotNull List<LineMarkerInfo<?>> doHighlight(FileBase f) {
        myFixture.openFileInEditor(f.getVirtualFile());
        myFixture.doHighlighting();
        Document document = myFixture.getEditor().getDocument();
        return DaemonCodeAnalyzerImpl.getLineMarkers(document, myFixture.getProject());
    }
}
