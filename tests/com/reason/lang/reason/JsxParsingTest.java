package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class JsxParsingTest extends RmlParsingTestCase {
    public void test_empty_tag() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div>children</div>"));

        PsiTagStart tag = PsiTreeUtil.findChildOfType(e, PsiTagStart.class);
        assertEquals("div", tag.getNameIdentifier().getText());
        assertEquals("<div>", tag.getText());
        assertEquals("children", e.getBody().getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(e, PsiTagClose.class).getText());
    }

    public void test_tag_name() {
        PsiTag e = (PsiTag) firstElement(parseCode("<Comp render={() => <Another/>}/>"));

        PsiTagStart tag = PsiTreeUtil.findChildOfType(e, PsiTagStart.class);
        assertEquals("Comp", tag.getNameIdentifier().getText());
    }

    public void test_tag_name_with_dot() {
        PsiLet let = first(letExpressions(parseCode("let _ = <Container.Test></Container.Test>")));

        PsiTag tag = first(PsiTreeUtil.findChildrenOfType(let, PsiTag.class));
        assertEquals("Container.Test", tag.getName());
        PsiTagStart tagStart = first(PsiTreeUtil.findChildrenOfType(let, PsiTagStart.class));
        assertInstanceOf(tagStart.getNameIdentifier(), PsiUpperTagName.class);
        assertEquals("Test", tagStart.getNameIdentifier().getText());
        PsiElement nextSibling = tagStart.getFirstChild().getNextSibling();
        assertEquals(myTypes.A_UPPER_TAG_NAME, nextSibling.getNode().getElementType());
        nextSibling = nextSibling.getNextSibling().getNextSibling();
        assertEquals(myTypes.A_UPPER_TAG_NAME, nextSibling.getNode().getElementType());

        PsiTagClose tagClose = first(PsiTreeUtil.findChildrenOfType(let, PsiTagClose.class));
        nextSibling = tagClose.getFirstChild().getNextSibling();
        assertEquals(myTypes.A_UPPER_TAG_NAME, nextSibling.getNode().getElementType());
        nextSibling = nextSibling.getNextSibling().getNextSibling();
        assertEquals(myTypes.A_UPPER_TAG_NAME, nextSibling.getNode().getElementType());
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
        // option here is not a ReasonML keyword
        PsiLet let = first(letExpressions(parseCode("let _ = <option className/>")));

        PsiTagStart jsx = first(PsiTreeUtil.findChildrenOfType(let, PsiTagStart.class));
        assertNotNull(jsx);
    }

    public void test_option_prop() {
        PsiTag e = firstOfType(parseCode("<div option=x prop=2/>"), PsiTag.class);

        assertSize(2, e.getProperties());
        assertEquals("option=x", e.getProperties().get(0).getText());
    }

    public void test_match_prop() {
        PsiTag e = firstOfType(parseCode("<App.Menu match=urlMatch prop=2 />"), PsiTag.class);

        assertSize(2, e.getProperties());
        assertEquals("match=urlMatch", e.getProperties().get(0).getText());
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
        PsiTag e = (PsiTag) firstElement(parseCode("<a className=A.B.link onClick={C.call()} download=d></a>"));

        List<PsiTagProperty> props = e.getProperties();
        assertSize(3, props);
        assertEquals("className", props.get(0).getName());
        assertEquals("A.B.link", props.get(0).getValue().getText());
        assertEquals("onClick", props.get(1).getName());
        assertEquals("{C.call()}", props.get(1).getValue().getText());
        assertEquals("download", props.get(2).getName());
        assertEquals("d", props.get(2).getValue().getText());

        PsiFunctionCall f = PsiTreeUtil.findChildOfType(e, PsiFunctionCall.class);
        assertNotNull(f);
    }

    public void test_optional_prop() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div ?layout ?style onClick=?cb ?other>x</div>"));

        List<PsiTagProperty> props = e.getProperties();
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

        List<PsiTagProperty> props = e.getProperties();
        assertSize(4, props);
        assertEquals("?layout", props.get(0).getText());
        assertEquals("layout", props.get(0).getName());
        assertEquals("?style", props.get(1).getText());
        assertEquals("style", props.get(1).getName());
        assertEquals("onClick=?cb", props.get(2).getText());
        assertEquals("?other", props.get(3).getText());
        assertEquals("other", props.get(3).getName());
        assertNull(props.get(3).getValue());

        assertNull(PsiTreeUtil.findChildOfType(e, PsiTernary.class));
    }

    public void test_optional_prop_call() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div style={fn(~margin?, ())} />"));

        assertSize(1, e.getProperties());
        assertEquals("{fn(~margin?, ())}", e.getProperties().get(0).getValue().getText());
    }

    public void test_ternary_in_value() {
        PsiTag e = (PsiTag) firstElement(parseCode("<AppIcons.Trash colors={isSelected ? green : red} disabled=true/>"));

        List<PsiTagProperty> props = e.getProperties();
        assertSize(2, props);
        assertEquals("colors", props.get(0).getName());
        assertEquals("{isSelected ? green : red}", props.get(0).getValue().getText());
        PsiTernary t = PsiTreeUtil.findChildOfType(props.get(0), PsiTernary.class);
        assertEquals("isSelected", t.getCondition().getText());
        assertEquals("green", t.getThenExpression().getText());
        assertEquals("red", t.getElseExpression().getText());
    }

    public void test_ternary_in_value_function() {
        PsiTag e = (PsiTag) firstElement(parseCode("<div p1={fn(~x=a ? b : c)} disabled=true/>"));

        List<PsiTagProperty> props = e.getProperties();
        assertSize(2, props);
        assertEquals("p1", props.get(0).getName());
        assertEquals("{fn(~x=a ? b : c)}", props.get(0).getValue().getText());
        PsiTernary t = PsiTreeUtil.findChildOfType(props.get(0), PsiTernary.class);
        assertEquals("a", t.getCondition().getText());
        assertEquals("b", t.getThenExpression().getText());
        assertEquals("c", t.getElseExpression().getText());
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

        assertEquals(2, e.getProperties().size());
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

    public void test_prop_func() {
        PsiTag e = (PsiTag) firstElement(parseCode("<QueryAttributeSelectionDialog onSelect={(indicator:GlobalStateTypes.AttributeEntity.t) => {()}} onCancel={(.) =>closeAttributeSelector()}/>"));

        PsiFunction f = PsiTreeUtil.findChildOfType(e, PsiFunction.class);
        assertEmpty(PsiTreeUtil.findChildrenOfType(f, PsiUpperTagName.class));
        assertSize(2, PsiTreeUtil.findChildrenOfType(f, PsiUpperSymbol.class));
    }

    public void test_ternary_01() {
        PsiTag e = firstOfType(parseCode(//
                "<>\n" +
                        "  <div> {test ? React.null : <div> {(. x) => <div onClick={(e: option(string), _) => ()} />} </div>} </div>\n" +
                        "  <div className=Styles.s> <Title text=\"title\" /> </div>\n" +
                        "</>"), PsiTag.class);

        List<PsiTagProperty> ps = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiTagProperty.class));
        PsiTagProperty p0 = ps.get(0);
        assertEquals("onClick={(e: option(string), _) => ()}", p0.getText());
    }

    public void test_ternary_02() {
        PsiTag e = firstOfType(parseCode("<div className={join(style, alignOnRight ? right : left)}/>"), PsiTag.class);

        PsiTagPropertyValue v = PsiTreeUtil.findChildOfType(e, PsiTagPropertyValue.class);
        PsiFunctionCall f = PsiTreeUtil.findChildOfType(v, PsiFunctionCall.class);
        List<PsiParameterReference> ps = f.getParameters();
        assertSize(2, ps);
        assertEquals("style", ps.get(0).getText());
        assertEquals("alignOnRight ? right : left", ps.get(1).getText());
    }

    public void test_ternary_03() {
        PsiTag e = firstOfType(parseCode("<Comp myProp={ switch (cond) { | Var => flag ? false : true } } />"), PsiTag.class);

        PsiTagPropertyValue v = PsiTreeUtil.findChildOfType(e, PsiTagPropertyValue.class);
        PsiSwitch s = PsiTreeUtil.findChildOfType(v, PsiSwitch.class);
        PsiPatternMatch p = s.getPatterns().get(0);
        assertEquals("flag ? false : true", p.getBody().getText());
    }

    public void test_ternary_in_option() {
        PsiTag e = firstOfType(parseCode("<div prop={(. p1) => " +
                "map(p2 =>" +
                " <div onClick={Some(cond ? Checked : NotChecked)}></div>" +
                ")}/>"), PsiTag.class);

        PsiTagProperty p1 = e.getProperties().iterator().next();
        PsiFunction f1 = PsiTreeUtil.findChildOfType(p1.getValue(), PsiFunction.class);

        PsiFunction f2 = PsiTreeUtil.findChildOfType(f1.getBody(), PsiFunction.class);
        assertEquals("p2 => <div onClick={Some(cond ? Checked : NotChecked)}></div>", f2.getText());
    }

    public void test_function_call() {
        PsiTag e = firstOfType(parseCode("<div  rules=[|fn(`hv(pct(50.), pct(50.))),|]/>"), PsiTag.class);

        PsiTagPropertyValue v = PsiTreeUtil.findChildOfType(e, PsiTagPropertyValue.class);
        assertEquals("[|fn(`hv(pct(50.), pct(50.))),|]", v.getText());
        PsiFunctionCall f1 = PsiTreeUtil.findChildOfType(v, PsiFunctionCall.class);
        assertSize(1, f1.getParameters());
    }

    public void test_mutation() {
        PsiTag e = firstOfType(parseCode("let _ = <Dashboard onRef={domRef => nodeRef.current = domRef->Js.Nullable.toOption} />"), PsiTag.class);

        PsiTagPropertyValue v = PsiTreeUtil.findChildOfType(e, PsiTagPropertyValue.class);
        assertEquals("{domRef => nodeRef.current = domRef->Js.Nullable.toOption}", v.getText());
        PsiFunction f = PsiTreeUtil.findChildOfType(v, PsiFunction.class);
        assertEquals("domRef", f.getParameters().get(0).getText());
    }
}
