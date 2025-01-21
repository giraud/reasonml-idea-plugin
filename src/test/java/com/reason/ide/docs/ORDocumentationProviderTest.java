package com.reason.ide.docs;

import com.intellij.lang.*;
import com.intellij.lang.documentation.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.reason.*;
import com.reason.lang.rescript.*;
import org.junit.*;

public class ORDocumentationProviderTest extends ORBasePlatformTestCase {
    @Test
    public void test_customDocumentationElement_empty_parens_RML() {
        FileBase a = configureCode("A.re", "/** Doc for fn */ \nlet fn = (x) => x;");
        FileBase b = configureCode("B.re", "let _ = A.fn(<caret>);");

        int caretOffset = myFixture.getCaretOffset();
        PsiElement rParen = PsiTreeUtil.findChildOfType(b, RPsiParameters.class).getLastChild();
        DocumentationProvider docProvider = LanguageDocumentation.INSTANCE.forLanguage(RmlLanguage.INSTANCE);
        PsiElement docElement = docProvider.getCustomDocumentationElement(myFixture.getEditor(), b, rParen, caretOffset);
        assertEquals("A.fn", ((RPsiQualifiedPathElement) docElement).getQualifiedName());
    }

    @Test
    public void test_customDocumentationElement_empty_parens_RES() {
        FileBase a = configureCode("A.res", "/** Doc for fn */ \nlet fn = (x) => x");
        FileBase b = configureCode("B.res", "let _ = A.fn(<caret>)");

        int caretOffset = myFixture.getCaretOffset();
        PsiElement rParen = PsiTreeUtil.findChildOfType(b, RPsiParameters.class).getLastChild();
        DocumentationProvider docProvider = LanguageDocumentation.INSTANCE.forLanguage(ResLanguage.INSTANCE);
        PsiElement docElement = docProvider.getCustomDocumentationElement(myFixture.getEditor(), b, rParen, caretOffset);
        assertEquals("A.fn", ((RPsiQualifiedPathElement) docElement).getQualifiedName());
    }
}
