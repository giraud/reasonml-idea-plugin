package com.reason.lang.ocaml;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiSwitch;
import com.reason.lang.core.psi.PsiType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AndParsingTest extends OclParsingTestCase {
  public void test_letChaining() {
    List<PsiLet> lets =
        new ArrayList<>(letExpressions(parseCode("let rec lx x = x + 1 and ly y = 3 + (lx y)")));

    assertSize(2, lets);
    assertEquals("lx", lets.get(0).getName());
    assertEquals("ly", lets.get(1).getName());
  }

  public void test_moduleChaining() {
    PsiFile file = parseCode("module rec X : sig end = struct end and Y : sig end = struct end");
    List<PsiModule> mods = new ArrayList<>(moduleExpressions(file));

    assertSize(2, mods);
    assertEquals("X", mods.get(0).getName());
    assertEquals("Y", mods.get(1).getName());
  }

  public void test_patternChaining() {
    PsiFile file = parseCode("match optsign with | Some sign -> let mtb1 = 1 and mtb2 = 2");
    Collection<PsiNamedElement> exps = expressions(file);

    assertInstanceOf(firstElement(file), PsiSwitch.class);
    assertEquals(0, exps.size());
  }

  public void test_typeChaining() {
    Collection<PsiType> types =
        typeExpressions(parseCode("type update = | NoUpdate and 'state self = {state: 'state;}"));

    assertSize(2, types);
    assertEquals("update", first(types).getName());
    assertEquals("self", second(types).getName());
  }

  // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/135
  public void test_GH_135() {
    List<PsiLet> lets =
        new ArrayList<>(letExpressions(parseCode("let f1 = function | _ -> ()\nand missing = ()")));

    assertSize(2, lets);
    assertEquals("f1", lets.get(0).getName());
    assertEquals("missing", lets.get(1).getName());
  }

  // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/175
  public void test_GH_175() {
    List<PsiLet> lets =
        new ArrayList<>(
            letExpressions(
                parseCode(
                    "let f1 = let f11 = function | _ -> \"\" in ()\n and f2 = let f21 = function | _ -> \"\" in ()\n and f3 = ()\n")));

    assertSize(3, lets);
    assertEquals("f1", lets.get(0).getName());
    assertEquals("f2", lets.get(1).getName());
    assertEquals("f3", lets.get(2).getName());
  }

  // https://github.com/reasonml-editor/reasonml-idea-plugin/issues/271
  public void test_GH_271() {
    List<PsiLet> lets =
        new ArrayList<>(
            letExpressions(
                parseCode(
                    "let parser_of_token_list a = \nlet loop x = () in \n() \nand parser_of_symbol b = ()")));

    assertSize(2, lets);
    assertEquals("parser_of_token_list", lets.get(0).getName());
    assertEquals("parser_of_symbol", lets.get(1).getName());
  }
}
