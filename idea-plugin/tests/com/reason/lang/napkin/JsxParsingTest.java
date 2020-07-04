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
    public void testEmptyTag() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div>children</div>", true));

        PsiTagStart tag = PsiTreeUtil.findChildOfType(e, PsiTagStart.class);
        assertEquals("<div>", tag.getText());
        assertNotNull(ORUtil.nextSiblingWithTokenType(tag.getFirstChild(), m_types.TAG_GT));
        assertEquals("children", PsiTreeUtil.findChildOfType(e, PsiTagBody.class).getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(e, PsiTagClose.class).getText());
    }

    public void testInnerClosingTag() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div><div/></div>"));

        assertEquals("<div>", PsiTreeUtil.findChildOfType(e, PsiTagStart.class).getText());
        assertEquals("<div/>", PsiTreeUtil.findChildOfType(e, PsiTagBody.class).getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(e, PsiTagClose.class).getText());
    }

    public void testOptionAsTag() {
        // option here is not a Napkin keyword
        PsiLet let = first(letExpressions(parseCode("let _ = <option className/>")));

        PsiTagStart jsx = first(PsiTreeUtil.findChildrenOfType(let, PsiTagStart.class));
        assertNotNull(jsx);
    }

    public void testTagNameWithDot() {
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

    public void testTagPropWithParen() {
        PsiTag tag = (PsiTag) firstElement(parseCode("<div style=(x) onFocus=a11y.onFocus/>"));

        Collection<PsiTagProperty> properties = PsiTreeUtil.findChildrenOfType(tag, PsiTagProperty.class);
        assertEquals(2, properties.size());
        Iterator<PsiTagProperty> itProperties = properties.iterator();
        assertEquals("style=(x)", itProperties.next().getText());
        assertEquals("onFocus=a11y.onFocus", itProperties.next().getText());
    }

    public void testTagPropsWithDot() {
        PsiTag e = (PsiTag) firstElement(parseCode("<a className=Styles.link href=h download=d>"));

        List<PsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(3, props);
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(0), PsiTagPropertyValue.class));
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(1), PsiTagPropertyValue.class));
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(2), PsiTagPropertyValue.class));
    }

    public void testTagPropsWithLocalOpen() {
        PsiTag e = (PsiTag) firstElement(parseCode("<Icon width=Dimensions.(3->px) height=Dimensions.(2->rem)>"));

        List<PsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(2, props);
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(0), PsiTagPropertyValue.class));
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(1), PsiTagPropertyValue.class));
    }

    public void testTagChaining() {
        Collection<PsiModule> psiModules = moduleExpressions(
                parseCode("module GalleryItem = { let make = () => { let x = <div/> } }\n module GalleryContainer = {}"));
        assertEquals(2, psiModules.size());
    }

    public void testIncorrectProp() {
        PsiTag e = (PsiTag) firstElement(parseCode("<MyComp prunningProp prop=1/>"));

        Collection<PsiTagProperty> properties = PsiTreeUtil.findChildrenOfType(e, PsiTagProperty.class);
        assertEquals(2, properties.size());
    }

    public void testProp02() {
        PsiTag e = (PsiTag) firstElement(
                parseCode("<Splitter left={<NotificationsList notifications />} right={<div> {React.string(\"switch inside\")} </div>}/>"));

        List<PsiTagProperty> properties = ((PsiTagStart) e.getFirstChild()).getProperties();
        assertEquals(2, properties.size());
        assertEquals("{<NotificationsList notifications />}", properties.get(0).getValue().getText());
        assertEquals("{<div> {ReasonReact.string(\"switch inside\")} </div>}", properties.get(1).getValue().getText());
    }

    public void testProp03() {
        PsiTag e = (PsiTag) firstElement(parseCode("<PageContentGrid height={computePageHeight(miniDashboardHeight)} title=\"X\"/>"));

        List<PsiTagProperty> properties = ((PsiTagStart) e.getFirstChild()).getProperties();
        assertEquals(2, properties.size());
        assertEquals("{computePageHeight(miniDashboardHeight)}", properties.get(0).getValue().getText());
        assertEquals("\"X\"", properties.get(1).getValue().getText());
    }
}
