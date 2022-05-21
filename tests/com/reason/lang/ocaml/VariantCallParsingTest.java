package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import jpsplugin.com.reason.*;

import java.util.*;
import java.util.stream.*;

@SuppressWarnings("ConstantConditions")
public class VariantCallParsingTest extends OclParsingTestCase {
    public void test_basic() {
        PsiLetBinding binding = firstOfType(parseCode("let x = Var"), PsiLet.class).getBinding();

        assertEquals("Var", binding.getText());
        assertNull(ORUtil.findImmediateFirstChildOfClass(binding, PsiVariantDeclaration.class));
        assertEquals(m_types.A_VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, PsiUpperSymbol.class).getNode().getElementType());
    }

    public void test_params() {
        PsiLetBinding binding = firstOfType(parseCode("let x = Var(a, b, c)"), PsiLet.class).getBinding();

        assertEquals("Var(a, b, c)", binding.getText());
        assertNull(PsiTreeUtil.findChildOfType(binding, PsiVariantDeclaration.class));
        assertNull(PsiTreeUtil.findChildOfType(binding, PsiSignatureItem.class));
        assertEquals(m_types.A_VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, PsiUpperSymbol.class).getNode().getElementType());
    }

    public void test_with_param() {
        PsiLetBinding binding = firstOfType(parseCode("let x = Var(1)"), PsiLet.class).getBinding();

        assertEquals("Var(1)", binding.getText());
        assertNull(ORUtil.findImmediateFirstChildOfClass(binding, PsiVariantDeclaration.class));
        assertEquals(m_types.A_VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, PsiUpperSymbol.class).getNode().getElementType());
    }

    public void test_pattern_match() {
        PsiSwitch e = firstOfType(parseCode(
                "let _ = match action with | UpdateDescription(desc) -> let open ReasonReact.SideEffects in (fun _self -> onDescriptionChange desc)"), PsiSwitch.class);

        PsiPatternMatchBody body = PsiTreeUtil.findChildOfType(e, PsiPatternMatchBody.class);
        assertEquals("let open ReasonReact.SideEffects in (fun _self -> onDescriptionChange desc)", body.getText());
        Collection<PsiUpperSymbol> uppers = PsiTreeUtil.findChildrenOfType(body, PsiUpperSymbol.class);
        assertEquals("ReasonReact, SideEffects", Joiner.join(", ", uppers.stream().map(PsiElement::getText).collect(Collectors.toList())));
    }
}
