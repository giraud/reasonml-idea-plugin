package com.reason.lang.napkin;

import java.util.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiTag;
import com.reason.lang.core.psi.PsiTagBody;
import com.reason.lang.core.psi.PsiTagClose;
import com.reason.lang.core.psi.PsiTagProperty;
import com.reason.lang.core.psi.PsiTagPropertyValue;
import com.reason.lang.core.psi.PsiTagStart;

@SuppressWarnings("ConstantConditions")
public class JsxParsingTest extends NsParsingTestCase {
    public void test_emptyTag() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div>children</div>"));

        PsiTagStart tag = PsiTreeUtil.findChildOfType(e, PsiTagStart.class);
        assertEquals("<div>", tag.getText());
        assertNotNull(ORUtil.nextSiblingWithTokenType(tag.getFirstChild(), m_types.TAG_GT));
        assertEquals("children", PsiTreeUtil.findChildOfType(e, PsiTagBody.class).getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(e, PsiTagClose.class).getText());
    }

    public void test_innerClosingTag() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div><div/></div>"));

        assertEquals("<div>", PsiTreeUtil.findChildOfType(e, PsiTagStart.class).getText());
        assertEquals("<div/>", PsiTreeUtil.findChildOfType(e, PsiTagBody.class).getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(e, PsiTagClose.class).getText());
    }

    public void test_optionAsTag() {
        // option here is not a Napkin keyword
        PsiLet let = first(letExpressions(parseCode("let _ = <option className/>")));

        PsiTagStart jsx = first(PsiTreeUtil.findChildrenOfType(let, PsiTagStart.class));
        assertNotNull(jsx);
    }

    public void test_tagNameWithDot() {
        PsiLet let = first(letExpressions(parseCode("let _ = <Container.Test></Container.Test>")));

        PsiTagStart tagStart = first(PsiTreeUtil.findChildrenOfType(let, PsiTagStart.class));
        PsiElement nextSibling = tagStart.getFirstChild().getNextSibling();
        assertEquals(m_types.TAG_NAME, nextSibling.getFirstChild().getNode().getElementType());
        nextSibling = nextSibling.getNextSibling().getNextSibling();
        assertEquals(m_types.TAG_NAME, nextSibling.getFirstChild().getNode().getElementType());

        PsiTagClose tagClose = first(PsiTreeUtil.findChildrenOfType(let, PsiTagClose.class));
        nextSibling = tagClose.getFirstChild().getNextSibling();
        assertEquals(m_types.TAG_NAME, nextSibling.getFirstChild().getNode().getElementType());
        nextSibling = nextSibling.getNextSibling().getNextSibling();
        assertEquals(m_types.TAG_NAME, nextSibling.getFirstChild().getNode().getElementType());
    }

    public void test_tagPropWithParen() {
        PsiTag tag = (PsiTag) firstElement(parseCode("<div style=(x) onFocus=a11y.onFocus/>"));

        Collection<PsiTagProperty> properties = PsiTreeUtil.findChildrenOfType(tag, PsiTagProperty.class);
        assertEquals(2, properties.size());
        Iterator<PsiTagProperty> itProperties = properties.iterator();
        assertEquals("style=(x)", itProperties.next().getText());
        assertEquals("onFocus=a11y.onFocus", itProperties.next().getText());
    }

    public void test_tagPropsWithDot() {
        PsiTag e = (PsiTag) firstElement(parseCode("<a className=Styles.link href=h download=d>"));

        List<PsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(3, props);
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(0), PsiTagPropertyValue.class));
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(1), PsiTagPropertyValue.class));
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(2), PsiTagPropertyValue.class));
    }

    public void test_tagPropsWithLocalOpen() {
        PsiTag e = (PsiTag) firstElement(parseCode("<Icon width=Dimensions.(3->px) height=Dimensions.(2->rem)>"));

        List<PsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(2, props);
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(0), PsiTagPropertyValue.class));
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(1), PsiTagPropertyValue.class));
    }

    public void test_tagChaining() {
        Collection<PsiModule> psiModules = moduleExpressions(
                parseCode("module GalleryItem = { let make = () => { let x = <div/> } }\n module GalleryContainer = {}"));
        assertEquals(2, psiModules.size());
    }

    public void test_prunning() {
        PsiTag e = (PsiTag) firstElement(parseCode("<MyComp prunningProp prop=1/>"));

        Collection<PsiTagProperty> properties = PsiTreeUtil.findChildrenOfType(e, PsiTagProperty.class);
        assertEquals(2, properties.size());
    }

    public void test_prop02() {
        PsiTag e = (PsiTag) firstElement(
                parseCode("<Splitter left={<NotificationsList notifications />} right={<div> {React.string(\"switch inside\")} </div>}/>"));

        List<PsiTagProperty> properties = ((PsiTagStart) e.getFirstChild()).getProperties();
        assertEquals(2, properties.size());
        assertEquals("{<NotificationsList notifications />}", properties.get(0).getValue().getText());
        assertEquals("{<div> {React.string(\"switch inside\")} </div>}", properties.get(1).getValue().getText());
    }

    public void test_prop03() {
        PsiTag e = (PsiTag) firstElement(parseCode("<PageContentGrid height={computePageHeight(miniDashboardHeight)} title=\"X\"/>"));

        List<PsiTagProperty> properties = ((PsiTagStart) e.getFirstChild()).getProperties();
        assertEquals(2, properties.size());
        assertEquals("{computePageHeight(miniDashboardHeight)}", properties.get(0).getValue().getText());
        assertEquals("\"X\"", properties.get(1).getValue().getText());
    }
}
