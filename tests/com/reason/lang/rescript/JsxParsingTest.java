package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class JsxParsingTest extends ResParsingTestCase {
    public void test_empty_tag() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div>children</div>"));

        PsiTagStart tag = PsiTreeUtil.findChildOfType(e, PsiTagStart.class);
        assertEquals("div", tag.getNameIdentifier().getText());
        assertEquals("children", PsiTreeUtil.findChildOfType(e, PsiTagBody.class).getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(e, PsiTagClose.class).getText());
    }

    public void test_tag_name() {
        PsiTag e = (PsiTag) firstElement(parseCode("<Comp disabled=false/>"));

        PsiTagStart tag = PsiTreeUtil.findChildOfType(e, PsiTagStart.class);
        assertEquals("Comp", tag.getNameIdentifier().getText());
    }

    public void test_prop_function() {
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
        assertEquals("children", PsiTreeUtil.findChildOfType(e, PsiTagBody.class).getText());
        assertEquals("</option>", PsiTreeUtil.findChildOfType(e, PsiTagClose.class).getText());
    }

    public void test_option_closeable_tag() {
        // option here is not a Rescript keyword
        PsiLet let = first(letExpressions(parseCode("let _ = <option className/>")));

        PsiTagStart jsx = first(PsiTreeUtil.findChildrenOfType(let, PsiTagStart.class));
        assertNotNull(jsx);
    }

    public void test_tag_name_with_dot() {
        PsiLet let = first(letExpressions(parseCode("let _ = <Container.Test></Container.Test>")));

        PsiTag tag = first(PsiTreeUtil.findChildrenOfType(let, PsiTag.class));
        assertEquals("Container.Test", tag.getName());
        PsiTagStart tagStart = first(PsiTreeUtil.findChildrenOfType(let, PsiTagStart.class));
        assertInstanceOf(tagStart.getNameIdentifier(), PsiUpperTagName.class);
        assertEquals("Test", tagStart.getNameIdentifier().getText());
        PsiElement nextSibling = tagStart.getFirstChild().getNextSibling();
        assertEquals(m_types.A_UPPER_TAG_NAME, nextSibling.getNode().getElementType());
        nextSibling = nextSibling.getNextSibling().getNextSibling();
        assertEquals(m_types.A_UPPER_TAG_NAME, nextSibling.getNode().getElementType());

        PsiTagClose tagClose = first(PsiTreeUtil.findChildrenOfType(let, PsiTagClose.class));
        nextSibling = tagClose.getFirstChild().getNextSibling();
        assertEquals(m_types.A_UPPER_TAG_NAME, nextSibling.getNode().getElementType());
        nextSibling = nextSibling.getNextSibling().getNextSibling();
        assertEquals(m_types.A_UPPER_TAG_NAME, nextSibling.getNode().getElementType());
    }

    public void test_tag_prop_with_paren() {
        PsiTag tag = (PsiTag) firstElement(parseCode("<div style=(x) onFocus=a11y.onFocus/>"));

        Collection<PsiTagProperty> properties = PsiTreeUtil.findChildrenOfType(tag, PsiTagProperty.class);
        assertEquals(2, properties.size());
        Iterator<PsiTagProperty> itProperties = properties.iterator();
        assertEquals("style=(x)", itProperties.next().getText());
        assertEquals("onFocus=a11y.onFocus", itProperties.next().getText());
    }

    public void test_tag_props_with_dot() {
        PsiTag e = (PsiTag) firstElement(parseCode("<a className=Styles.link onClick={C.call()} download=d></a>"));

        List<PsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(3, props);
        assertEquals("className", props.get(0).getName());
        assertEquals("Styles.link", props.get(0).getValue().getText());
        assertEquals("onClick", props.get(1).getName());
        assertEquals("{C.call()}", props.get(1).getValue().getText());
        assertEquals("download", props.get(2).getName());
        assertEquals("d", props.get(2).getValue().getText());

        PsiFunctionCall f = PsiTreeUtil.findChildOfType(e, PsiFunctionCall.class);
        assertNotNull(f);
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
        PsiTag e = (PsiTag) firstElement(parseCode("<Icon width=Dimensions.(3->px) height=Dimensions.(2->rem)>x</Icon>"));

        List<PsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(2, props);
        assertEquals("Dimensions.(3->px)", props.get(0).getValue().getText());
        assertEquals("Dimensions.(2->rem)", props.get(1).getValue().getText());
        assertEquals("x", e.getBody().getText());
    }

    public void test_tag_chaining() {
        Collection<PsiModule> psiModules = moduleExpressions(parseCode(
                "module GalleryItem = { let make = () => { let x = <div/>; }; };\nmodule GalleryContainer = {};"));

        assertEquals(2, psiModules.size());
    }

    public void test_incorrect_prop() {
        PsiTag e = (PsiTag) firstElement(parseCode("<MyComp prunningProp prop=1/>"));

        Collection<PsiTagProperty> properties = PsiTreeUtil.findChildrenOfType(e, PsiTagProperty.class);
        assertEquals(2, properties.size());
    }

    public void test_prop02() {
        PsiTag e = (PsiTag) firstElement(parseCode(
                "<Splitter left={<NotificationsList notifications />} right={<div> {ReasonReact.string(\"switch inside\")} </div>}/>"));

        List<PsiTagProperty> properties = ((PsiTagStart) e.getFirstChild()).getProperties();
        assertEquals(2, properties.size());
        assertEquals("{<NotificationsList notifications />}", properties.get(0).getValue().getText());
        assertEquals("{<div> {ReasonReact.string(\"switch inside\")} </div>}", properties.get(1).getValue().getText());
    }

    public void test_prop03() {
        PsiTag e = (PsiTag) firstElement(parseCode("<PageContentGrid onClick={(. _e) => action(true, ())} title=\"X\"/>"));

        List<PsiTagProperty> properties = ((PsiTagStart) e.getFirstChild()).getProperties();
        assertEquals(2, properties.size());
        assertEquals("{(. _e) => action(true, ())}", properties.get(0).getValue().getText());
        assertEquals("\"X\"", properties.get(1).getValue().getText());

        PsiFunction f = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertEquals("_e", f.getParameters().get(0).getText());
        assertEquals("action(true, ())", f.getBody().getText());
    }

    public void test_prop04() {
        PsiTag e = (PsiTag) firstElement(parseCode("<Icon colors=[|white, red|] />"));

        List<PsiTagProperty> properties = ((PsiTagStart) e.getFirstChild()).getProperties();
        assertEquals(1, properties.size());
        assertEquals("[|white, red|]", properties.get(0).getValue().getText());
    }

    public void test_prop05() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div className=Styles.wrappingContainer>{appliedFilters->React.array}</div>"));

        List<PsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(1, props);
        assertEquals("Styles.wrappingContainer", props.get(0).getValue().getText());
        assertEquals("{appliedFilters->React.array}", e.getBody().getText());
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

    public void test_prop_no_upper_tag() {
        PsiTag e = (PsiTag) firstElement(parseCode("<InputText onTextChange={(. id) => dispatch(. ParametersReducers.UpdateURLId(id))}/>"));

        PsiFunctionCall f = PsiTreeUtil.findChildOfType(e, PsiFunctionCall.class);
        assertEmpty(PsiTreeUtil.findChildrenOfType(f, PsiUpperTagName.class));
        assertSize(2, PsiTreeUtil.findChildrenOfType(f, PsiUpperSymbol.class));
    }
}
