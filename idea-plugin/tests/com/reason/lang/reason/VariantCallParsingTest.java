package com.reason.lang.reason;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.Joiner;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ConstantConditions")
public class VariantCallParsingTest extends RmlParsingTestCase {
    public void test_basic() {
        PsiLetBinding binding = firstOfType(parseCode("let x = Var;"), PsiLet.class).getBinding();

        assertEquals("Var", binding.getText());
        assertNull(ORUtil.findImmediateFirstChildOfClass(binding, PsiVariantDeclaration.class));
        assertEquals(m_types.VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, PsiUpperSymbol.class).getFirstChild().getNode().getElementType());
    }

    public void test_withParam() {
        PsiLetBinding binding = firstOfType(parseCode("let x = Var(1);"), PsiLet.class).getBinding();

        assertEquals("Var(1)", binding.getText());
        assertNull(ORUtil.findImmediateFirstChildOfClass(binding, PsiVariantDeclaration.class));
        assertEquals(m_types.VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, PsiUpperSymbol.class).getFirstChild().getNode().getElementType());
    }

    public void test_patternMatch() {
        PsiSwitch e = firstOfType(parseCode("switch (action) { | UpdateDescription(desc) => ReasonReact.SideEffects.(_self => onDescriptionChange(desc)) };"), PsiSwitch.class);
        PsiPatternMatchBody body = PsiTreeUtil.findChildOfType(e, PsiPatternMatchBody.class);
        assertEquals("ReasonReact.SideEffects.(_self => onDescriptionChange(desc))", body.getText());
        List<PsiUpperSymbol> uppers = ORUtil.findImmediateChildrenOfClass(body, PsiUpperSymbol.class);
        assertEquals("ReasonReact, SideEffects", Joiner.join(", ", uppers.stream().map(PsiElement::getText).collect(Collectors.toList())));
    }

    public void test_inMethod() {
        PsiFunction e = firstOfType(parseCode("(. fileName, data) => self.send(SetErrorMessage(fileName, data##message))"), PsiFunction.class);
        PsiUpperSymbol upper = PsiTreeUtil.findChildOfType(e, PsiUpperSymbol.class);
        assertEquals("SetErrorMessage", upper.getText());
    }

}
