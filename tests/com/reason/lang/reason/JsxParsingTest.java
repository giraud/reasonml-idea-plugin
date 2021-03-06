package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class JsxParsingTest extends RmlParsingTestCase {
    public void test_empty_tag() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div>children</div>"));

        PsiTagStart tag = PsiTreeUtil.findChildOfType(e, PsiTagStart.class);
        assertEquals("<div>", tag.getText());
        assertNotNull(ORUtil.nextSiblingWithTokenType(tag.getFirstChild(), m_types.TAG_GT));
        assertEquals("children", PsiTreeUtil.findChildOfType(e, PsiTagBody.class).getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(e, PsiTagClose.class).getText());
    }

    public void test_tag_name() {
        PsiTag e = (PsiTag) firstElement(parseCode("<Comp render={() => <Another/>}/>"));

        PsiTagStart tag = PsiTreeUtil.findChildOfType(e, PsiTagStart.class);
        assertEquals("Comp", tag.getNameIdentifier().getText());
    }

    public void test_inner_closing_tag() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div><div/></div>"));

        assertEquals("<div>", PsiTreeUtil.findChildOfType(e, PsiTagStart.class).getText());
        assertEquals("<div/>", PsiTreeUtil.findChildOfType(e, PsiTagBody.class).getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(e, PsiTagClose.class).getText());
    }

    public void test_multiple_closing_tag() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div><div></div></div>"));

        assertEquals("<div>", PsiTreeUtil.findChildOfType(e, PsiTagStart.class).getText());
        assertEquals("<div></div>", PsiTreeUtil.findChildOfType(e, PsiTagBody.class).getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(e, PsiTagClose.class).getText());
    }

    public void test_option_tag() {
        PsiTag e = (PsiTag) firstElement(parseCode("<option>children</option>"));

        PsiTagStart tag = PsiTreeUtil.findChildOfType(e, PsiTagStart.class);
        assertEquals("<option>", tag.getText());
        assertNotNull(ORUtil.nextSiblingWithTokenType(tag.getFirstChild(), m_types.TAG_GT));
        assertEquals("children", PsiTreeUtil.findChildOfType(e, PsiTagBody.class).getText());
        assertEquals("</option>", PsiTreeUtil.findChildOfType(e, PsiTagClose.class).getText());
    }

    public void test_option_closeable_tag() {
        // option here is not a ReasonML keyword
        PsiLet let = first(letExpressions(parseCode("let _ = <option className/>")));

        PsiTagStart jsx = first(PsiTreeUtil.findChildrenOfType(let, PsiTagStart.class));
        assertNotNull(jsx);
    }

    public void test_tag_name_with_dot() {
        // option here is not a ReasonML keyword
        PsiLet let = first(letExpressions(parseCode("let _ = <Container.Test></Container.Test>")));

        PsiTagStart tagStart = first(PsiTreeUtil.findChildrenOfType(let, PsiTagStart.class));
        assertInstanceOf(tagStart.getNameIdentifier(), PsiUpperSymbol.class);
        assertEquals("Test", tagStart.getNameIdentifier().getText());
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

    public void test_tag_prop_with_paren() {
        PsiTag tag = (PsiTag) firstElement(parseCode("<div style=(x) onFocus=a11y.onFocus/>"));

        Collection<PsiTagProperty> properties =
                PsiTreeUtil.findChildrenOfType(tag, PsiTagProperty.class);
        assertEquals(2, properties.size());
        Iterator<PsiTagProperty> itProperties = properties.iterator();
        assertEquals("style=(x)", itProperties.next().getText());
        assertEquals("onFocus=a11y.onFocus", itProperties.next().getText());
    }

    public void test_tag_props_with_dot() {
        PsiTag e = (PsiTag) firstElement(parseCode("<a className=Styles.link href=h download=d></a>"));

        List<PsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(3, props);
        assertEquals("className", props.get(0).getName());
        assertEquals("Styles.link", props.get(0).getValue().getText());
        assertEquals("href", props.get(1).getName());
        assertEquals("h", props.get(1).getValue().getText());
        assertEquals("download", props.get(2).getName());
        assertEquals("d", props.get(2).getValue().getText());
    }

    public void test_optional_prop() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div ?layout ?style onClick=?cb ?other></div>"));

        List<PsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(4, props);
        assertEquals("?layout", props.get(0).getText());
        assertEquals("layout", props.get(0).getName());
        assertEquals("?style", props.get(1).getText());
        assertEquals("onClick=?cb", props.get(2).getText());
        assertEquals("?other", props.get(3).getText());
        assertNull(PsiTreeUtil.findChildOfType(e, PsiTernary.class));
    }

    public void test_optional_prop_autoclose() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div ?layout ?style onClick=?cb ?other/>"));

        List<PsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(4, props);
        assertEquals("?layout", props.get(0).getText());
        assertEquals("layout", props.get(0).getName());
        assertEquals("?style", props.get(1).getText());
        assertEquals("style", props.get(1).getName());
        assertEquals("onClick=?cb", props.get(2).getText());
        assertEquals("?other", props.get(3).getText());
        assertEquals("other", props.get(3).getName());
        assertNull(PsiTreeUtil.findChildOfType(e, PsiTernary.class));
    }

    public void test_tag_props_with_local_open() {
        PsiTag e =
                (PsiTag)
                        firstElement(parseCode("<Icon width=Dimensions.(3->px) height=Dimensions.(2->rem)>"));

        List<PsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(2, props);
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(0), PsiTagPropertyValue.class));
        assertNotNull(PsiTreeUtil.findChildrenOfType(props.get(1), PsiTagPropertyValue.class));
    }

    public void test_tag_chaining() {
        Collection<PsiModule> psiModules =
                moduleExpressions(
                        parseCode(
                                "module GalleryItem = { let make = () => { let x = <div/>; }; };\nmodule GalleryContainer = {};"));
        assertEquals(2, psiModules.size());
    }

    public void test_incorrect_prop() {
        PsiTag e = (PsiTag) firstElement(parseCode("<MyComp prunningProp prop=1/>"));

        Collection<PsiTagProperty> properties = PsiTreeUtil.findChildrenOfType(e, PsiTagProperty.class);
        assertEquals(2, properties.size());
    }

    public void test_prop02() {
        PsiTag e =
                (PsiTag)
                        firstElement(
                                parseCode(
                                        "<Splitter left={<NotificationsList notifications />} right={<div> {ReasonReact.string(\"switch inside\")} </div>}/>"));

        List<PsiTagProperty> properties = ((PsiTagStart) e.getFirstChild()).getProperties();
        assertEquals(2, properties.size());
        assertEquals("{<NotificationsList notifications />}", properties.get(0).getValue().getText());
        assertEquals(
                "{<div> {ReasonReact.string(\"switch inside\")} </div>}",
                properties.get(1).getValue().getText());
    }

    public void test_prop03() {
        PsiTag e =
                (PsiTag)
                        firstElement(
                                parseCode(
                                        "<PageContentGrid height={computePageHeight(miniDashboardHeight)} title=\"X\"/>"));

        List<PsiTagProperty> properties = ((PsiTagStart) e.getFirstChild()).getProperties();
        assertEquals(2, properties.size());
        assertEquals(
                "{computePageHeight(miniDashboardHeight)}", properties.get(0).getValue().getText());
        assertEquals("\"X\"", properties.get(1).getValue().getText());
    }

    public void test_prop04() {
        PsiTag e = (PsiTag) firstElement(parseCode("<Icon colors=[|white, red|] />"));

        List<PsiTagProperty> properties = ((PsiTagStart) e.getFirstChild()).getProperties();
        assertEquals(1, properties.size());
        assertEquals("[|white, red|]", properties.get(0).getValue().getText());
    }

    public void test_prop_ref() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div ref={ReactDOMRe.Ref.domRef(formRef)}/>"));

        Collection<PsiTagProperty> properties = PsiTreeUtil.findChildrenOfType(e, PsiTagProperty.class);
        PsiTagProperty prop = properties.iterator().next();
        assertEquals("ref={ReactDOMRe.Ref.domRef(formRef)}", prop.getText());
    }

    public void test_fragment() {
        PsiTag e = (PsiTag) firstElement(parseCode("<></>"));

        assertEquals("<></>", e.getText());
        assertNotNull(PsiTreeUtil.findChildOfType(e, PsiTagStart.class));
        assertNotNull(PsiTreeUtil.findChildOfType(e, PsiTagClose.class));
    }
}
