package com.reason.lang.ocaml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import jpsplugin.com.reason.Joiner;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiSwitch;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.psi.PsiVariantDeclaration;
import com.reason.lang.core.psi.impl.PsiPatternMatchBody;
import java.util.*;
import java.util.stream.*;

@SuppressWarnings("ConstantConditions")
public class VariantCallParsingTest extends OclParsingTestCase {
  public void test_basic() {
    PsiLetBinding binding = firstOfType(parseCode("let x = Var"), PsiLet.class).getBinding();

    assertEquals("Var", binding.getText());
    assertNull(ORUtil.findImmediateFirstChildOfClass(binding, PsiVariantDeclaration.class));
    assertEquals(
        m_types.VARIANT_NAME,
        PsiTreeUtil.findChildOfType(binding, PsiUpperSymbol.class)
            .getFirstChild()
            .getNode()
            .getElementType());
  }

  public void test_withParam() {
    PsiLetBinding binding = firstOfType(parseCode("let x = Var(1)"), PsiLet.class).getBinding();

    assertEquals("Var(1)", binding.getText());
    assertNull(ORUtil.findImmediateFirstChildOfClass(binding, PsiVariantDeclaration.class));
    assertEquals(
        m_types.VARIANT_NAME,
        PsiTreeUtil.findChildOfType(binding, PsiUpperSymbol.class)
            .getFirstChild()
            .getNode()
            .getElementType());
  }

  public void test_patternMatch() {
    PsiSwitch e =
        firstOfType(
            parseCode(
                "match action with | UpdateDescription(desc) -> let open ReasonReact.SideEffects in (fun _self -> onDescriptionChange desc)"),
            PsiSwitch.class);

    PsiPatternMatchBody body = PsiTreeUtil.findChildOfType(e, PsiPatternMatchBody.class);
    assertEquals(
        "let open ReasonReact.SideEffects in (fun _self -> onDescriptionChange desc)",
        body.getText());
    Collection<PsiUpperSymbol> uppers = PsiTreeUtil.findChildrenOfType(body, PsiUpperSymbol.class);
    assertEquals(
        "ReasonReact, SideEffects",
        Joiner.join(", ", uppers.stream().map(PsiElement::getText).collect(Collectors.toList())));
  }
}
