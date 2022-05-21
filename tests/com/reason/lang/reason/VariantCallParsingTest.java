package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import jpsplugin.com.reason.*;

import java.util.*;
import java.util.stream.*;

@SuppressWarnings("ConstantConditions")
public class VariantCallParsingTest extends RmlParsingTestCase {
    public void test_basic() {
        PsiLetBinding binding = firstOfType(parseCode("let x = Var;"), PsiLet.class).getBinding();

        assertEquals("Var", binding.getText());
        assertNull(PsiTreeUtil.findChildOfType(binding, PsiVariantDeclaration.class));
        assertEquals(m_types.A_VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, PsiUpperSymbol.class).getNode().getElementType());
    }

    public void test_params() {
        PsiLetBinding binding = firstOfType(parseCode("let x = Var(a, b, c);"), PsiLet.class).getBinding();

        assertEquals("Var(a, b, c)", binding.getText());
        assertNull(PsiTreeUtil.findChildOfType(binding, PsiVariantDeclaration.class));
        assertNull(PsiTreeUtil.findChildOfType(binding, PsiSignatureItem.class));
        assertEquals(m_types.A_VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, PsiUpperSymbol.class).getNode().getElementType());
    }

    public void test_withPath() {
        PsiLetBinding binding = firstOfType(parseCode("let x = A.Variant(1);"), PsiLet.class).getBinding();

        assertEquals("A.Variant(1)", binding.getText());
        assertNull(PsiTreeUtil.findChildOfType(binding, PsiVariantDeclaration.class));
        ArrayList<PsiUpperSymbol> symbols = new ArrayList<>(PsiTreeUtil.findChildrenOfType(binding, PsiUpperSymbol.class));
        assertEquals(m_types.A_VARIANT_NAME, symbols.get(1).getNode().getElementType());
    }

    public void test_withParam() {
        PsiLetBinding binding = firstOfType(parseCode("let x = Var(1);"), PsiLet.class).getBinding();

        assertEquals("Var(1)", binding.getText());
        assertNull(ORUtil.findImmediateFirstChildOfClass(binding, PsiVariantDeclaration.class));
        // assertEquals(m_types.VARIANT_NAME, PsiTreeUtil.findChildOfType(binding,
        // PsiUpperSymbol.class).getFirstChild().getNode().getElementType());
    }

    public void test_pattern_match() {
        PsiSwitch e = firstOfType(parseCode("switch (action) { | UpdateDescription(desc) => ReasonReact.SideEffects.(_self => onDescriptionChange(desc)) };"), PsiSwitch.class);

        PsiPatternMatchBody body = PsiTreeUtil.findChildOfType(e, PsiPatternMatchBody.class);
        assertEquals("ReasonReact.SideEffects.(_self => onDescriptionChange(desc))", body.getText());
        List<PsiUpperSymbol> uppers = ORUtil.findImmediateChildrenOfClass(body, PsiUpperSymbol.class);
        assertEquals("ReasonReact, SideEffects", Joiner.join(", ", uppers.stream().map(PsiElement::getText).collect(Collectors.toList())));
    }

    public void test_in_method() {
        PsiFunction e = firstOfType(parseCode("let _ = (. fileName, data) => self.send(SetErrorMessage(fileName, data##message))"), PsiFunction.class);

        PsiUpperSymbol upper = PsiTreeUtil.findChildOfType(e, PsiUpperSymbol.class);
        assertEquals("SetErrorMessage", upper.getText());
    }
}
