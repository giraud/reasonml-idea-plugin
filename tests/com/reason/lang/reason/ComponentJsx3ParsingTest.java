package com.reason.lang.reason;

import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;
import java.util.stream.*;

@SuppressWarnings("ConstantConditions")
public class ComponentJsx3ParsingTest extends RmlParsingTestCase {
    @Test
    public void test_file_component() {
        FileBase e = parseCode("[@react.component]\nlet make = () => { <div/> };");

        assertTrue(e.isComponent());
    }

    @Test
    public void test_inner_component() {
        RPsiInnerModule e = firstOfType(parseCode("module X = {\n  [@react.component]\n  let make = (~name) => { <div/> }\n};"), RPsiInnerModule.class);

        assertTrue(e.isComponent());
    }

    @Test
    public void test_mandatory_property() {
        RPsiLet e = firstOfType(parseCode("[@react.component] let make = (~name, ~other:option(string)) => <div/>;"), RPsiLet.class);

        List<ComponentPropertyAdapter> params = e.getFunction().getParameters().stream().map(ComponentPropertyAdapter::new).collect(Collectors.toList());
        assertSize(2, params);
        assertTrue(params.get(0).isMandatory());
        assertTrue(params.get(1).isMandatory());
    }

    @Test
    public void test_optional_property() {
        RPsiLet e = firstOfType(parseCode("[@react.component] let make = (~layout=?) => <div/>;"), RPsiLet.class);

        List<RPsiParameterDeclaration> parameters = e.getFunction().getParameters();
        List<ComponentPropertyAdapter> params = parameters.stream().map(ComponentPropertyAdapter::new).collect(Collectors.toList());
        assertSize(1, params);
        assertFalse(params.get(0).isMandatory());
    }

    @Test
    public void test_close() {
        RPsiLet e = firstOfType(parseCode("[@react.component] let make = () => { <A><B><span>\"X\"->React.string</span></B><C></C></A>; };"), RPsiLet.class);

        RPsiTag tag = PsiTreeUtil.findChildOfType(e, RPsiTag.class);
        List<RPsiTag> innerTags = ORUtil.findImmediateChildrenOfClass(tag.getBody(), RPsiTag.class);
        assertSize(2, innerTags);
        assertEquals("<B><span>\"X\"->React.string</span></B>", innerTags.get(0).getText());
        assertEquals("<C></C>", innerTags.get(1).getText());
    }
}
