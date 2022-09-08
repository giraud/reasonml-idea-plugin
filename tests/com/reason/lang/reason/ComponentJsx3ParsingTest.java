package com.reason.lang.reason;

import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;
import java.util.stream.*;

@SuppressWarnings("ConstantConditions")
public class ComponentJsx3ParsingTest extends RmlParsingTestCase {
    public void test_file_component() {
        FileBase e = parseCode("[@react.component]\nlet make = () => { <div/> };");

        assertTrue(e.isComponent());
    }

    public void test_inner_component() {
        PsiInnerModule e = firstOfType(parseCode("module X = {\n  [@react.component]\n  let make = (~name) => { <div/> }\n};"), PsiInnerModule.class);

        assertTrue(e.isComponent());
    }

    public void test_mandatory_property() {
        PsiLet e = firstOfType(parseCode("[@react.component] let make = (~name, ~other:option(string)) => <div/>;"), PsiLet.class);

        List<ComponentPropertyAdapter> params = e.getFunction().getParameters().stream().map(ComponentPropertyAdapter::new).collect(Collectors.toList());
        assertSize(2, params);
        assertTrue(params.get(0).isMandatory());
        assertTrue(params.get(1).isMandatory());
    }

    public void test_optional_property() {
        PsiLet e = firstOfType(parseCode("[@react.component] let make = (~layout=?) => <div/>;"), PsiLet.class);

        List<PsiParameterDeclaration> parameters = e.getFunction().getParameters();
        List<ComponentPropertyAdapter> params = parameters.stream().map(ComponentPropertyAdapter::new).collect(Collectors.toList());
        assertSize(1, params);
        assertFalse(params.get(0).isMandatory());
    }

    public void test_close() {
        PsiLet e = firstOfType(parseCode("[@react.component] let make = () => { <A><B><span>\"X\"->React.string</span></B><C></C></A>; };"), PsiLet.class);

        PsiTag tag = PsiTreeUtil.findChildOfType(e, PsiTag.class);
        List<PsiTag> innerTags = ORUtil.findImmediateChildrenOfClass(tag.getBody(), PsiTag.class);
        assertSize(2, innerTags);
        assertEquals("<B><span>\"X\"->React.string</span></B>", innerTags.get(0).getText());
        assertEquals("<C></C>", innerTags.get(1).getText());
    }
}
