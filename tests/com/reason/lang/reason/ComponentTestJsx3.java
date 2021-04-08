package com.reason.lang.reason;

import com.reason.ide.files.*;
import com.reason.lang.core.psi.*;

import java.util.*;
import java.util.stream.*;

public class ComponentTestJsx3 extends RmlParsingTestCase {
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

        List<ComponentPropertyAdapter> params = e.getFunction().getParameters().stream().map(ComponentPropertyAdapter::new).collect(Collectors.toList());
        assertSize(1, params);
        assertFalse(params.get(0).isMandatory());
    }
}
