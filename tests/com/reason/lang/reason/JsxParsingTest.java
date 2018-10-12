package com.reason.lang.reason;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
        PsiTagStart tag = (PsiTagStart) firstElement(parseCode("<div style=(x) onFocus=a11y.onFocus/>"));

        Collection<PsiTagProperty> properties = PsiTreeUtil.findChildrenOfType(tag, PsiTagProperty.class);
        assertEquals(2, properties.size());
        Iterator<PsiTagProperty> itProperties = properties.iterator();
        assertEquals("style=(x)", itProperties.next().getText());
        assertEquals("onFocus=a11y.onFocus", itProperties.next().getText());
    }

    public void testTagPropsWithDot() {
        PsiTagStart e = (PsiTagStart) firstElement(parseCode("<a className=Styles.link href=h download=d>", true));

        List<PsiTagProperty> props = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiTagProperty.class));
        assertSize(3, props);
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(0), PsiTagPropertyValue.class));
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(1), PsiTagPropertyValue.class));
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(2), PsiTagPropertyValue.class));

    }

    public void testTagChaining() {
        Collection<PsiModule> psiModules = moduleExpressions(parseCode("module GalleryItem = { let make = () => { let x = <div/>; }; };\nmodule GalleryContainer = {};"));
        assertEquals(2, psiModules.size());
    }
}
