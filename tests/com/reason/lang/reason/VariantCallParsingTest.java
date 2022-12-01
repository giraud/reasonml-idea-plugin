package com.reason.lang.reason;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import jpsplugin.com.reason.*;
import org.junit.*;

import java.util.*;
import java.util.stream.*;

@SuppressWarnings("ConstantConditions")
public class VariantCallParsingTest extends RmlParsingTestCase {
    @Test
    public void test_basic() {
        RPsiLetBinding binding = firstOfType(parseCode("let x = Var;"), RPsiLet.class).getBinding();

        assertEquals("Var", binding.getText());
        assertNull(PsiTreeUtil.findChildOfType(binding, RPsiVariantDeclaration.class));
        assertEquals(myTypes.A_VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, RPsiUpperSymbol.class).getNode().getElementType());
    }

    @Test
    public void test_params() {
        RPsiLetBinding binding = firstOfType(parseCode("let x = Var(a, b, c);"), RPsiLet.class).getBinding();

        assertEquals("Var(a, b, c)", binding.getText());
        assertNull(PsiTreeUtil.findChildOfType(binding, RPsiVariantDeclaration.class));
        assertNull(PsiTreeUtil.findChildOfType(binding, RPsiSignatureItem.class));
        assertEquals(myTypes.A_VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, RPsiUpperSymbol.class).getNode().getElementType());
    }

    @Test
    public void test_with_path() {
        RPsiLetBinding binding = firstOfType(parseCode("let x = A.Variant(1);"), RPsiLet.class).getBinding();

        assertEquals("A.Variant(1)", binding.getText());
        assertNull(PsiTreeUtil.findChildOfType(binding, RPsiVariantDeclaration.class));
        ArrayList<RPsiUpperSymbol> symbols = new ArrayList<>(PsiTreeUtil.findChildrenOfType(binding, RPsiUpperSymbol.class));
        assertEquals(myTypes.A_VARIANT_NAME, symbols.get(1).getNode().getElementType());
    }

    @Test
    public void test_pipe_first() {
        RPsiLetBinding e = firstOfType(parseCode("let _ = A.A1.(Variant->toString);"), RPsiLet.class).getBinding();

        RPsiLocalOpen l = PsiTreeUtil.findChildOfType(e, RPsiLocalOpen.class);
        assertEquals(myTypes.A_VARIANT_NAME, PsiTreeUtil.findChildOfType(l, RPsiUpperSymbol.class).getNode().getElementType());
    }

    @Test
    public void test_with_param() {
        RPsiLetBinding binding = firstOfType(parseCode("let x = Var(1);"), RPsiLet.class).getBinding();

        assertEquals("Var(1)", binding.getText());
        assertNull(ORUtil.findImmediateFirstChildOfClass(binding, RPsiVariantDeclaration.class));
        // assertEquals(m_types.VARIANT_NAME, PsiTreeUtil.findChildOfType(binding,
        // RPsiUpperSymbol.class).getFirstChild().getNode().getElementType());
    }

    @Test
    public void test_pattern_match() {
        RPsiSwitch e = firstOfType(parseCode("switch (action) { | UpdateDescription(desc) => ReasonReact.SideEffects.(_self => onDescriptionChange(desc)) };"), RPsiSwitch.class);

        RPsiPatternMatchBody body = PsiTreeUtil.findChildOfType(e, RPsiPatternMatchBody.class);
        assertEquals("ReasonReact.SideEffects.(_self => onDescriptionChange(desc))", body.getText());
        List<RPsiUpperSymbol> uppers = ORUtil.findImmediateChildrenOfClass(body, RPsiUpperSymbol.class);
        assertEquals("ReasonReact, SideEffects", Joiner.join(", ", uppers.stream().map(PsiElement::getText).collect(Collectors.toList())));
    }

    @Test
    public void test_in_method() {
        RPsiFunction e = firstOfType(parseCode("let _ = (. fileName, data) => self.send(SetErrorMessage(fileName, data##message))"), RPsiFunction.class);

        RPsiUpperSymbol upper = PsiTreeUtil.findChildOfType(e, RPsiUpperSymbol.class);
        assertEquals("SetErrorMessage", upper.getText());
    }
}
