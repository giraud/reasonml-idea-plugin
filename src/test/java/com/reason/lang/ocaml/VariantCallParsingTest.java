package com.reason.lang.ocaml;

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
public class VariantCallParsingTest extends OclParsingTestCase {
    @Test
    public void test_basic() {
        RPsiLetBinding binding = firstOfType(parseCode("let x = Var"), RPsiLet.class).getBinding();

        assertEquals("Var", binding.getText());
        assertNull(ORUtil.findImmediateFirstChildOfClass(binding, RPsiVariantDeclaration.class));
        assertEquals(myTypes.A_VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, RPsiUpperSymbol.class).getNode().getElementType());
    }

    @Test
    public void test_params() {
        RPsiLetBinding binding = firstOfType(parseCode("let x = Var(a, b, c)"), RPsiLet.class).getBinding();

        assertEquals("Var(a, b, c)", binding.getText());
        assertNull(PsiTreeUtil.findChildOfType(binding, RPsiVariantDeclaration.class));
        assertNull(PsiTreeUtil.findChildOfType(binding, RPsiSignatureItem.class));
        assertEquals(myTypes.A_VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, RPsiUpperSymbol.class).getNode().getElementType());
    }

    @Test
    public void test_with_param() {
        RPsiLetBinding binding = firstOfType(parseCode("let x = Var(1)"), RPsiLet.class).getBinding();

        assertEquals("Var(1)", binding.getText());
        assertNull(ORUtil.findImmediateFirstChildOfClass(binding, RPsiVariantDeclaration.class));
        assertEquals(myTypes.A_VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, RPsiUpperSymbol.class).getNode().getElementType());
    }

    @Test
    public void test_pattern_match() {
        RPsiSwitch e = firstOfType(parseCode(
                "let _ = match action with | UpdateDescription(desc) -> let open ReasonReact.SideEffects in (fun _self -> onDescriptionChange desc)"), RPsiSwitch.class);

        RPsiPatternMatchBody body = PsiTreeUtil.findChildOfType(e, RPsiPatternMatchBody.class);
        assertEquals("let open ReasonReact.SideEffects in (fun _self -> onDescriptionChange desc)", body.getText());
        Collection<RPsiUpperSymbol> uppers = PsiTreeUtil.findChildrenOfType(body, RPsiUpperSymbol.class);
        assertEquals("ReasonReact, SideEffects", Joiner.join(", ", uppers.stream().map(PsiElement::getText).collect(Collectors.toList())));
    }
}
