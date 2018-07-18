package com.reason.reason;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiTagClose;
import com.reason.lang.core.psi.PsiTagProperty;
import com.reason.lang.core.psi.PsiTagStart;
import com.reason.lang.reason.RmlParserDefinition;
import com.reason.lang.reason.RmlTypes;

import java.util.Collection;

public class JsxParsingTest extends BaseParsingTestCase {
    public JsxParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testOptionAsTag() {
        // option here is not a ReasonML keyword
        PsiLet let = first(letExpressions(parseCode("let _ = <option className/>")));

        PsiTagStart jsx = first(PsiTreeUtil.findChildrenOfType(let, PsiTagStart.class));
        assertNotNull(jsx);
    }

    public void testTagNameWithDot() {
        // option here is not a ReasonML keyword
        PsiLet let = first(letExpressions(parseCode("let _ = <Container.Test></Container.Test>")));

        PsiTagStart tagStart = first(PsiTreeUtil.findChildrenOfType(let, PsiTagStart.class));
        PsiElement nextSibling = tagStart.getFirstChild().getNextSibling();
        assertEquals(RmlTypes.INSTANCE.TAG_NAME, nextSibling.getFirstChild().getNode().getElementType());
        nextSibling = nextSibling.getNextSibling().getNextSibling();
        assertEquals(RmlTypes.INSTANCE.TAG_NAME, nextSibling.getFirstChild().getNode().getElementType());

        PsiTagClose tagClose = first(PsiTreeUtil.findChildrenOfType(let, PsiTagClose.class));
        nextSibling = tagClose.getFirstChild().getNextSibling();
        assertEquals(RmlTypes.INSTANCE.TAG_NAME, nextSibling.getFirstChild().getNode().getElementType());
        nextSibling = nextSibling.getNextSibling().getNextSibling();
        assertEquals(RmlTypes.INSTANCE.TAG_NAME, nextSibling.getFirstChild().getNode().getElementType());
    }

    public void testTagPropWithParen() {
        PsiElement psiElement = firstElement(parseCode("<div style=(x)/>"));

        PsiTagProperty tag = PsiTreeUtil.findChildrenOfType(psiElement, PsiTagProperty.class).iterator().next();
        assertEquals("style=(x)", tag.getText());
    }

    public void testTagPropsWithDot() {
        PsiElement psiElement = firstElement(parseCode("<a className=Styles.link href=h download=d>"));

        Collection<PsiTagProperty> props = PsiTreeUtil.findChildrenOfType(psiElement, PsiTagProperty.class);
        assertEquals(3, props.size());
    }
}
