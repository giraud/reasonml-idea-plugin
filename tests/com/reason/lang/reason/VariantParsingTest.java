package com.reason.lang.reason;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.Joiner;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiPatternMatchBody;
import com.reason.lang.core.psi.PsiSwitch;
import com.reason.lang.core.psi.PsiUpperSymbol;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ConstantConditions")
public class VariantParsingTest extends BaseParsingTestCase {

    public VariantParsingTest() {
        super("", "re", new RmlParserDefinition());
    }

    public void testBasic() {
        PsiSwitch e = firstOfType(parseCode("switch (action) { | UpdateDescription(desc) => ReasonReact.SideEffects(_self => onDescriptionChange(desc)) };"), PsiSwitch.class);
        PsiPatternMatchBody body = PsiTreeUtil.findChildOfType(e, PsiPatternMatchBody.class);
        assertEquals("ReasonReact.SideEffects(_self => onDescriptionChange(desc))", body.getText());
        List<PsiUpperSymbol> uppers = ORUtil.findImmediateChildrenOfClass(body, PsiUpperSymbol.class);
        assertEquals("ReasonReact, SideEffects", Joiner.join(", ", uppers.stream().map(PsiElement::getText).collect(Collectors.toList())));
    }

    public void testInMethod() {
        PsiFunction e = firstOfType(parseCode("(. fileName, data) => self.send(SetErrorMessage(fileName, data##message))"), PsiFunction.class);
        PsiUpperSymbol upper = PsiTreeUtil.findChildOfType(e, PsiUpperSymbol.class);
        assertEquals("SetErrorMessage", upper.getText());
    }

}
