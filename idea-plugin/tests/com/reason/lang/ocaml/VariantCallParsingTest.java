package com.reason.lang.ocaml;

import java.util.*;
import java.util.stream.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.Joiner;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiPatternMatchBody;
import com.reason.lang.core.psi.PsiSwitch;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.PsiVariantDeclaration;

@SuppressWarnings("ConstantConditions")
public class VariantCallParsingTest extends BaseParsingTestCase {

    public VariantCallParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void test_basic() {
        PsiLetBinding binding = firstOfType(parseCode("let x = Var"), PsiLet.class).getBinding();

        assertEquals("Var", binding.getText());
        assertNull(ORUtil.findImmediateFirstChildOfClass(binding, PsiVariantDeclaration.class));
        assertEquals(OclTypes.INSTANCE.VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, PsiUpperSymbol.class).getFirstChild().getNode().getElementType());
    }

    public void test_withParam() {
        PsiLetBinding binding = firstOfType(parseCode("let x = Var(1)"), PsiLet.class).getBinding();

        assertEquals("Var(1)", binding.getText());
        assertNull(ORUtil.findImmediateFirstChildOfClass(binding, PsiVariantDeclaration.class));
        assertEquals(OclTypes.INSTANCE.VARIANT_NAME, PsiTreeUtil.findChildOfType(binding, PsiUpperSymbol.class).getFirstChild().getNode().getElementType());
    }

    public void testPatternMatch() {
        PsiSwitch e = firstOfType(
                parseCode("match action with | UpdateDescription(desc) -> let open ReasonReact.SideEffects in (fun _self -> onDescriptionChange desc)"),
                PsiSwitch.class);
        PsiPatternMatchBody body = PsiTreeUtil.findChildOfType(e, PsiPatternMatchBody.class);
        assertEquals("let open ReasonReact.SideEffects in (fun _self -> onDescriptionChange desc)", body.getText());
        Collection<PsiUpperSymbol> uppers = PsiTreeUtil.findChildrenOfType(body, PsiUpperSymbol.class);
        assertEquals("ReasonReact, SideEffects", Joiner.join(", ", uppers.stream().map(PsiElement::getText).collect(Collectors.toList())));
    }
}
