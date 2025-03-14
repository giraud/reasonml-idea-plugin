package com.reason.lang.rescript;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class JsxParsingTest extends ResParsingTestCase {
    @Test
    public void test_empty_tag() {
        RPsiTag e = firstOfType(parseCode("<div>children</div>"), RPsiTag.class);
        assertNoParserError(e);

        RPsiTagStart tag = PsiTreeUtil.findChildOfType(e, RPsiTagStart.class);
        assertEquals("div", tag.getNameIdentifier().getText());
        assertEquals("children", PsiTreeUtil.findChildOfType(e, RPsiTagBody.class).getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(e, RPsiTagClose.class).getText());
    }

    @Test
    public void test_tag_name() {
        RPsiTag e = firstOfType(parseCode("<Comp disabled=false/>"), RPsiTag.class);

        RPsiTagStart tag = PsiTreeUtil.findChildOfType(e, RPsiTagStart.class);
        assertEquals("Comp", tag.getNameIdentifier().getText());
    }

    @Test
    public void test_empty_tag_with_lf() {
        RPsiTag e = firstOfType(parseCode("""
                let _ = <div>
                \tchildren
                </div>
                """), RPsiTag.class);

        RPsiTagStart tag = PsiTreeUtil.findChildOfType(e, RPsiTagStart.class);
        assertEquals("div", tag.getNameIdentifier().getText());
        assertEquals("children", PsiTreeUtil.findChildOfType(e, RPsiTagBody.class).getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(e, RPsiTagClose.class).getText());
    }

    @Test
    public void test_tag_name_with_dot() {
        RPsiLet let = firstOfType(parseCode("let _ = <Container.Test></Container.Test>"), RPsiLet.class);

        RPsiTag tag = first(PsiTreeUtil.findChildrenOfType(let, RPsiTag.class));
        assertEquals("Container.Test", tag.getName());
        RPsiTagStart tagStart = first(PsiTreeUtil.findChildrenOfType(let, RPsiTagStart.class));
        assertInstanceOf(tagStart.getNameIdentifier(), RPsiUpperTagName.class);
        assertEquals("Test", tagStart.getNameIdentifier().getText());
        PsiElement nextSibling = tagStart.getFirstChild().getNextSibling();
        assertEquals(myTypes.A_UPPER_TAG_NAME, nextSibling.getNode().getElementType());
        nextSibling = nextSibling.getNextSibling().getNextSibling();
        assertEquals(myTypes.A_UPPER_TAG_NAME, nextSibling.getNode().getElementType());

        RPsiTagClose tagClose = first(PsiTreeUtil.findChildrenOfType(let, RPsiTagClose.class));
        nextSibling = tagClose.getFirstChild().getNextSibling();
        assertEquals(myTypes.A_UPPER_TAG_NAME, nextSibling.getNode().getElementType());
        nextSibling = nextSibling.getNextSibling().getNextSibling();
        assertEquals(myTypes.A_UPPER_TAG_NAME, nextSibling.getNode().getElementType());
    }

    @Test
    public void test_prop_function() {
        RPsiTag e = firstOfType(parseCode("<Comp render={() => <Another/>}/>"), RPsiTag.class);
        assertNoParserError(e);

        RPsiTagStart tag = PsiTreeUtil.findChildOfType(e, RPsiTagStart.class);
        assertEquals("Comp", tag.getNameIdentifier().getText());
    }

    @Test
    public void test_inner_closing_tag() {
        RPsiLet e = firstOfType(parseCode("let _ = \n<div><div/></div>"), RPsiLet.class);
        assertNoParserError(e);

        RPsiTag et = PsiTreeUtil.findChildOfType(e, RPsiTag.class);
        assertEquals("<div>", PsiTreeUtil.findChildOfType(et, RPsiTagStart.class).getText());
        assertEquals("<div/>", PsiTreeUtil.findChildOfType(et, RPsiTagBody.class).getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(et, RPsiTagClose.class).getText());
    }

    @Test
    public void test_multiple_closing_tag() {
        RPsiTag e = firstOfType(parseCode("<div><div></div></div>"), RPsiTag.class);

        assertEquals("<div>", PsiTreeUtil.findChildOfType(e, RPsiTagStart.class).getText());
        assertEquals("<div></div>", PsiTreeUtil.findChildOfType(e, RPsiTagBody.class).getText());
        assertEquals("</div>", PsiTreeUtil.findChildOfType(e, RPsiTagClose.class).getText());
    }

    @Test
    public void test_option_tag() {
        RPsiTag e = firstOfType(parseCode("<option>children</option>"), RPsiTag.class);

        RPsiTagStart tag = PsiTreeUtil.findChildOfType(e, RPsiTagStart.class);
        assertEquals("<option>", tag.getText());
        assertEquals("children", PsiTreeUtil.findChildOfType(e, RPsiTagBody.class).getText());
        assertEquals("</option>", PsiTreeUtil.findChildOfType(e, RPsiTagClose.class).getText());
    }

    @Test
    public void test_option_closeable_tag() {
        // option here is not a Rescript keyword
        RPsiLet let = firstOfType(parseCode("let _ = <option className/>"), RPsiLet.class);

        RPsiTagStart jsx = first(PsiTreeUtil.findChildrenOfType(let, RPsiTagStart.class));
        assertNotNull(jsx);
    }

    @Test
    public void test_option_prop() {
        RPsiTag e = firstOfType(parseCode("<div option=x prop=2/>"), RPsiTag.class);

        assertSize(2, e.getProperties());
        assertEquals("option=x", e.getProperties().get(0).getText());
    }

    @Test
    public void test_match_prop() {
        RPsiTag e = firstOfType(parseCode("<App.Menu match=urlMatch prop=2/>"), RPsiTag.class);

        assertSize(2, e.getProperties());
        assertEquals("match=urlMatch", e.getProperties().get(0).getText());
    }

    @Test
    public void test_tag_prop_with_paren() {
        RPsiTag tag = firstOfType(parseCode("<div style=(x) onFocus=a11y.onFocus/>"), RPsiTag.class);

        Collection<RPsiTagProperty> properties = PsiTreeUtil.findChildrenOfType(tag, RPsiTagProperty.class);
        assertEquals(2, properties.size());
        Iterator<RPsiTagProperty> itProperties = properties.iterator();
        assertEquals("style=(x)", itProperties.next().getText());
        assertEquals("onFocus=a11y.onFocus", itProperties.next().getText());
    }

    @Test
    public void test_tag_props_with_dot() {
        RPsiTag e = firstOfType(parseCode("<a className=Styles.link onClick={C.call()} download=d></a>"), RPsiTag.class);

        List<RPsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(3, props);
        assertEquals("className", props.get(0).getName());
        assertEquals("Styles.link", props.get(0).getValue().getText());
        assertEquals("onClick", props.get(1).getName());
        assertEquals("{C.call()}", props.get(1).getValue().getText());
        assertEquals("download", props.get(2).getName());
        assertEquals("d", props.get(2).getValue().getText());

        RPsiFunctionCall f = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        assertNotNull(f);
    }

    @Test
    public void test_optional_prop() {
        RPsiTag e = firstOfType(parseCode("<div ?layout></div>"), RPsiTag.class);

        assertNoParserError(e);
        List<RPsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(1, props);
        assertEquals("?layout", props.get(0).getText());
        assertEquals("layout", props.get(0).getName());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiTernary.class));
    }

    @Test
    public void test_optional_props() {
        RPsiTag e = firstOfType(parseCode("<div ?layout ?style onClick=?cb ?other></div>"), RPsiTag.class);

        assertNoParserError(e);
        List<RPsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(4, props);
        assertEquals("?layout", props.get(0).getText());
        assertEquals("layout", props.get(0).getName());
        assertEquals("?style", props.get(1).getText());
        assertEquals("onClick=?cb", props.get(2).getText());
        assertEquals("?other", props.get(3).getText());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiTernary.class));
    }

    @Test
    public void test_optional_prop_autoclose() {
        RPsiTag e = firstOfType(parseCode("<div ?layout ?style onClick=?cb ?other/>"), RPsiTag.class);

        List<RPsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(4, props);
        assertEquals("?layout", props.get(0).getText());
        assertEquals("layout", props.get(0).getName());
        assertEquals("?style", props.get(1).getText());
        assertEquals("style", props.get(1).getName());
        assertEquals("onClick=?cb", props.get(2).getText());
        assertEquals("?other", props.get(3).getText());
        assertEquals("other", props.get(3).getName());
        assertNull(PsiTreeUtil.findChildOfType(e, RPsiTernary.class));
    }

    @Test
    public void test_optional_prop_call() {
        RPsiTag e = firstOfType(parseCode("<div style={fn(~margin?, ())} />"), RPsiTag.class);

        assertSize(1, e.getProperties());
        assertEquals("{fn(~margin?, ())}", e.getProperties().get(0).getValue().getText());
    }

    @Test
    public void test_ternary_in_value() {
        RPsiTag e = firstOfType(parseCode("<AppIcons.Trash colors={isSelected ? green : red} disabled=true/>"), RPsiTag.class);

        List<RPsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(2, props);
        assertEquals("colors", props.get(0).getName());
        assertEquals("{isSelected ? green : red}", props.get(0).getValue().getText());
        RPsiTernary t = PsiTreeUtil.findChildOfType(props.get(0), RPsiTernary.class);
        assertEquals("isSelected", t.getCondition().getText());
        assertEquals("green", t.getThenExpression().getText());
        assertEquals("red", t.getElseExpression().getText());
    }

    @Test
    public void test_ternary_in_value_function() {
        RPsiTag e = firstOfType(parseCode("<div p1={fn(~x=a ? b : c)} disabled=true/>"), RPsiTag.class);

        List<RPsiTagProperty> props = e.getProperties();
        assertSize(2, props);
        assertEquals("p1", props.get(0).getName());
        assertEquals("{fn(~x=a ? b : c)}", props.get(0).getValue().getText());
        RPsiTernary t = PsiTreeUtil.findChildOfType(props.get(0), RPsiTernary.class);
        assertEquals("a", t.getCondition().getText());
        assertEquals("b", t.getThenExpression().getText());
        assertEquals("c", t.getElseExpression().getText());
    }

    @Test
    public void test_tag_props_with_local_open() {
        RPsiTag e = firstOfType(parseCode("<Icon width=Dimensions.(3->px) height=Dimensions.(2->rem)>x</Icon>"), RPsiTag.class);

        List<RPsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(2, props);
        assertEquals("Dimensions.(3->px)", props.get(0).getValue().getText());
        assertEquals("Dimensions.(2->rem)", props.get(1).getValue().getText());
        assertEquals("x", e.getBody().getText());
    }

    @Test
    public void test_tag_chaining() {
        Collection<RPsiInnerModule> psiModules = moduleExpressions(parseCode(
                "module GalleryItem = { let make = () => { let x = <div/>; }; };\nmodule GalleryContainer = {};"));

        assertEquals(2, psiModules.size());
    }

    @Test
    public void test_incorrect_prop() {
        RPsiTag e = firstOfType(parseCode("<MyComp prunningProp prop=1/>"), RPsiTag.class);

        Collection<RPsiTagProperty> properties = PsiTreeUtil.findChildrenOfType(e, RPsiTagProperty.class);
        assertEquals(2, properties.size());
    }

    @Test
    public void test_prop02() {
        RPsiTag e = firstOfType(parseCode(
                "<Splitter left={<NotificationsList notifications />} right={<div> {ReasonReact.string(\"switch inside\")} </div>}/>"), RPsiTag.class);

        List<RPsiTagProperty> properties = ((RPsiTagStart) e.getFirstChild()).getProperties();
        assertEquals(2, properties.size());
        assertEquals("{<NotificationsList notifications />}", properties.get(0).getValue().getText());
        assertEquals("{<div> {ReasonReact.string(\"switch inside\")} </div>}", properties.get(1).getValue().getText());
    }

    @Test
    public void test_prop03() {
        RPsiTagStart e = firstOfType(parseCode("<PageContentGrid onClick={(. _e) => action(true, ())} title=\"X\"/>"), RPsiTagStart.class);

        List<RPsiTagProperty> ep = e.getProperties();
        assertEquals(2, ep.size());
        assertEquals("{(. _e) => action(true, ())}", ep.get(0).getValue().getText());
        assertEquals("\"X\"", ep.get(1).getValue().getText());

        RPsiFunction f = PsiTreeUtil.findChildOfType(e, RPsiFunction.class);
        assertEquals("_e", f.getParameters().get(0).getText());
        assertEquals("action(true, ())", f.getBody().getText());
    }

    @Test
    public void test_prop04() {
        RPsiTag e = firstOfType(parseCode("<Icon colors=[|white, red|] />"), RPsiTag.class);

        List<RPsiTagProperty> properties = ((RPsiTagStart) e.getFirstChild()).getProperties();
        assertEquals(1, properties.size());
        assertEquals("[|white, red|]", properties.get(0).getValue().getText());
    }

    @Test
    public void test_prop05() {
        RPsiTag e = firstOfType(parseCode("<div className=Styles.wrappingContainer>{appliedFilters->React.array}</div>"), RPsiTag.class);

        List<RPsiTagProperty> props = new ArrayList<>(e.getProperties());
        assertSize(1, props);
        assertEquals("Styles.wrappingContainer", props.get(0).getValue().getText());
        assertEquals("{appliedFilters->React.array}", e.getBody().getText());
    }

    @Test
    public void test_prop_ref() {
        RPsiTag e = firstOfType(parseCode("<div ref={ReactDOMRe.Ref.domRef(formRef)}/>"), RPsiTag.class);

        Collection<RPsiTagProperty> properties = PsiTreeUtil.findChildrenOfType(e, RPsiTagProperty.class);
        RPsiTagProperty prop = properties.iterator().next();
        assertEquals("ref={ReactDOMRe.Ref.domRef(formRef)}", prop.getText());
    }

    @Test
    public void test_fragment() {
        RPsiTag e = firstOfType(parseCode("<></>"), RPsiTag.class);

        assertEquals("<></>", e.getText());
        assertNotNull(PsiTreeUtil.findChildOfType(e, RPsiTagStart.class));
        assertNotNull(PsiTreeUtil.findChildOfType(e, RPsiTagClose.class));
    }

    @Test
    public void test_prop_no_upper_tag() {
        RPsiTag e = firstOfType(parseCode("<InputText onTextChange={(. id) => dispatch(. ParametersReducers.UpdateURLId(id))}/>"), RPsiTag.class);

        RPsiFunctionCall f = PsiTreeUtil.findChildOfType(e, RPsiFunctionCall.class);
        assertEmpty(PsiTreeUtil.findChildrenOfType(f, RPsiUpperTagName.class));
        assertSize(2, PsiTreeUtil.findChildrenOfType(f, RPsiUpperSymbol.class));
    }

    @Test
    public void test_prop_func() {
        RPsiTag e = firstOfType(parseCode("<QueryAttributeSelectionDialog onSelect={(indicator:GlobalStateTypes.AttributeEntity.t) => {()}} onCancel={(.) => closeAttributeSelector()}/>"), RPsiTag.class);

        RPsiFunction f = PsiTreeUtil.findChildOfType(e, RPsiFunction.class);
        assertEmpty(PsiTreeUtil.findChildrenOfType(f, RPsiUpperTagName.class));
        assertSize(2, PsiTreeUtil.findChildrenOfType(f, RPsiUpperSymbol.class));
    }

    @Test
    public void test_ternary_01() {
        RPsiTag e = firstOfType(parseCode("""
                <>
                  <div> {test ? React.null : <div> {(. x) => <div onClick={(e: option(string), _) => ()} />} </div>} </div>
                  <div className=Styles.s> <Title text="title" /> </div>
                </>
                """), RPsiTag.class);

        RPsiTagBody body = e.getBody();
        List<RPsiTag> ets = ORUtil.findImmediateChildrenOfClass(body, RPsiTag.class);
        assertSize(2, ets);

        List<RPsiTagProperty> ps = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiTagProperty.class));
        RPsiTagProperty p0 = ps.get(0);
        assertEquals("onClick={(e: option(string), _) => ()}", p0.getText());
    }

    @Test
    public void test_ternary_02() {
        RPsiTag e = firstOfType(parseCode("<div className={join(style, alignOnRight ? right : left)}/>"), RPsiTag.class);

        RPsiTagPropertyValue v = PsiTreeUtil.findChildOfType(e, RPsiTagPropertyValue.class);
        RPsiFunctionCall f = PsiTreeUtil.findChildOfType(v, RPsiFunctionCall.class);
        List<RPsiParameterReference> ps = f.getParameters();
        assertSize(2, ps);
        assertEquals("style", ps.get(0).getText());
        assertEquals("alignOnRight ? right : left", ps.get(1).getText());
    }

    @Test
    public void test_ternary_03() {
        RPsiTag e = firstOfType(parseCode("<Comp myProp={ switch (cond) { | Var => flag ? false : true } } />"), RPsiTag.class);

        RPsiTagPropertyValue v = PsiTreeUtil.findChildOfType(e, RPsiTagPropertyValue.class);
        RPsiSwitch s = PsiTreeUtil.findChildOfType(v, RPsiSwitch.class);
        RPsiPatternMatch p = s.getPatterns().get(0);
        assertEquals("flag ? false : true", p.getBody().getText());
    }

    @Test
    public void test_function_call() {
        RPsiTag e = firstOfType(parseCode("<div  rules=[fn(#hv(pct(50.), pct(50.))),]/>"), RPsiTag.class);

        RPsiTagPropertyValue v = PsiTreeUtil.findChildOfType(e, RPsiTagPropertyValue.class);
        assertEquals("[fn(#hv(pct(50.), pct(50.))),]", v.getText());
        RPsiFunctionCall f1 = PsiTreeUtil.findChildOfType(v, RPsiFunctionCall.class);
        assertSize(1, f1.getParameters());
    }

    @Test
    public void test_mutation() {
        RPsiTag e = firstOfType(parseCode("let _ = <Dashboard onRef={domRef => nodeRef.current = domRef->Js.Nullable.toOption} />"), RPsiTag.class);

        RPsiTagPropertyValue v = PsiTreeUtil.findChildOfType(e, RPsiTagPropertyValue.class);
        assertEquals("{domRef => nodeRef.current = domRef->Js.Nullable.toOption}", v.getText());
        RPsiFunction f = PsiTreeUtil.findChildOfType(v, RPsiFunction.class);
        assertEquals("domRef", f.getParameters().get(0).getText());
    }

    @Test
    public void test_if() {
        RPsiTagPropertyValue e = firstOfType(parseCode("""
                let _ = <InputText onKeyDown={(e) => {
                  let key = false
                  if true {
                    ()
                  }
                }}/>
                """), RPsiTagPropertyValue.class);

        RPsiFunction f = PsiTreeUtil.findChildOfType(e, RPsiFunction.class);
        RPsiLet l = PsiTreeUtil.findChildOfType(f.getBody(), RPsiLet.class);
        assertEquals("let key = false", l.getText());
        RPsiIfStatement i = PsiTreeUtil.findChildOfType(f.getBody(), RPsiIfStatement.class);
        assertEquals("if true {\n    ()\n  }", i.getText());
    }

    @Test
    public void test_inside_switch() {
        RPsiTagPropertyValue e = firstOfType(parseCode("let _ = switch x { | _ => <MovableMashlet onPositionChange={(y: M.t) => { fn(alias, y) }}/> }"), RPsiTagPropertyValue.class);

        assertEquals("{(y: M.t) => { fn(alias, y) }}", e.getText());
    }

    @Test
    public void test_incomplete_value_scope_autoclose() {
        RPsiTagStart e = firstOfType(parseCode("<ListRe values={x />"), RPsiTagStart.class);

        RPsiTagProperty ep = e.getProperties().get(0);
        assertEquals("{x", ep.getValue().getText());
        assertEquals("/>", e.getLastChild().getText());
    }

    @Test
    public void test_binary_condition() {
        RPsiTagStart e = firstOfType(parseCode("<Comp prop={i => i > 1}/>"), RPsiTagStart.class);

        assertNoParserError(e);
        RPsiTagProperty ep = e.getProperties().get(0);
        assertEquals("{i => i > 1}", ep.getValue().getText());
    }

    @Test
    public void test_function_as_parameter() {
        RPsiTag e = firstOfType(parseCode("let _ = <Comp p1={fn(function => 1)} p2=1 />"), RPsiTag.class);

        assertSize(2, e.getProperties());
        assertTextEquals("p1", e.getProperties().get(0).getName());
        assertTextEquals("{fn(function => 1)}", e.getProperties().get(0).getValue().getText());
        assertTextEquals("p2", e.getProperties().get(1).getName());
        assertTextEquals("1", e.getProperties().get(1).getValue().getText());
    }
}
