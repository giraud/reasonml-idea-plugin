package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class MatchParsingTest extends OclParsingTestCase {
    public void test_match() {
        FileBase psiFile = parseCode("let path_of_dirpath dir = match DirPath.repr dir with [] -> failwith \"path_of_dirpath\"");
        assertEquals(1, childrenCount(psiFile));

        PsiLet let = first(letExpressions(psiFile));
        assertTrue(let.isFunction());
        PsiTreeUtil.findChildOfType(let, PsiFunction.class);
    }

    public void test_matchExpr() {
        FileBase psiFileModule = parseCode("let _ = match c with | VtMeta -> let _ = x");
        assertEquals(1, childrenCount(psiFileModule));

        PsiLet let = first(letExpressions(psiFileModule));
        PsiLetBinding binding = let.getBinding();
        PsiSwitch match = PsiTreeUtil.findChildOfType(binding, PsiSwitch.class);
        assertEquals("VtMeta -> let _ = x", match.getPatterns().get(0).getText());
    }

    public void test_match_with_exception() {
        FileBase psiFile = parseCode("match x with | exception Failure -> Printf.printf");
        assertEquals(1, childrenCount(psiFile));
    }

    public void test_complex_match() {
        FileBase file =
                parseCode(
                        "begin match Repr.repr o with\n"
                                + "    | BLOCK (0, [|id; o|]) ->\n"
                                + "      [|(Int, id, 0 :: pos); (tpe, o, 1 :: pos)|]\n"
                                + "    | _ -> raise Exit\n"
                                + "    end");
        PsiElement[] children = file.getChildren();

        assertEquals(1, childrenCount((file)));
        assertInstanceOf(children[0], PsiScopedExpr.class);
    }

    public void test_pattern_token_type() {
        PsiFile psiFile = parseCode("let _ = match action with | Incr -> counter + 1");

        PsiSwitch switch_ = first(PsiTreeUtil.findChildrenOfType(psiFile, PsiSwitch.class));
        Collection<PsiPatternMatch> patterns = PsiTreeUtil.findChildrenOfType(switch_, PsiPatternMatch.class);
        assertSize(1, patterns);
        PsiPatternMatch patternMatch = patterns.iterator().next();
        PsiUpperSymbol variant = ORUtil.findImmediateFirstChildOfClass(patternMatch, PsiUpperSymbol.class);
        // assertTrue(variant.isVariant());
        assertEquals("Incr", variant.getText());
        assertEquals("counter + 1", patternMatch.getBody().getText());
    }

    public void test_pattern_match() {
        PsiFile psiFile = parseCode("let _ = match p with | Typedtree.Partial -> \"Partial\" | Total -> \"Total\"");

        PsiSwitch e = first(PsiTreeUtil.findChildrenOfType(psiFile, PsiSwitch.class));
        List<PsiPatternMatch> patterns = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiPatternMatch.class));
        assertSize(2, patterns);

        PsiPatternMatch m1 = patterns.get(0);
        assertEquals("Typedtree", PsiTreeUtil.findChildOfType(m1, PsiUpperSymbol.class).getText());
        assertEquals(myTypes.A_VARIANT_NAME, ORUtil.findImmediateLastChildOfClass(m1, PsiUpperSymbol.class).getNode().getElementType());
        assertEquals("\"Partial\"", m1.getBody().getText());

        PsiPatternMatch m2 = patterns.get(1);
        assertEquals("\"Total\"", m2.getBody().getText());
    }

    public void test_function_shortcut() {
        PsiLet e = first(letExpressions(parseCode("let f x = function | Variant -> 1")));

        PsiFunction fun = (PsiFunction) e.getBinding().getFirstChild();
        PsiSwitch shortcut = ORUtil.findImmediateFirstChildOfClass(fun.getBody(), PsiSwitch.class);
        assertNotNull(ORUtil.findImmediateFirstChildOfClass(shortcut, PsiPatternMatch.class));
        assertEquals("1", PsiTreeUtil.findChildOfType(shortcut, PsiPatternMatchBody.class).getText());
    }

    public void test_function_shortcut_no_pipe() {
        PsiLet e = first(letExpressions(parseCode("let f x = function Variant -> 1")));

        PsiFunction fun = (PsiFunction) e.getBinding().getFirstChild();
        PsiSwitch shortcut = ORUtil.findImmediateFirstChildOfClass(fun.getBody(), PsiSwitch.class);
        assertNotNull(ORUtil.findImmediateFirstChildOfClass(shortcut, PsiPatternMatch.class));
        assertEquals("1", PsiTreeUtil.findChildOfType(shortcut, PsiPatternMatchBody.class).getText());
    }

    public void test_function_shortcut_many() {
        PsiLet e = first(letExpressions(
                parseCode("let rec db_output_prodn = function "
                        + " | Sterm s -> if cond then first else second\n"
                        + " | Sedit2 (\"FILE\", file) -> let file_suffix_regex = a in printf \"i\"\n"
                        + " | Snterm s -> sprintf \"(Snterm %s) \" s")));

        PsiSwitch shortcut = PsiTreeUtil.findChildOfType(e, PsiSwitch.class);
        List<PsiPatternMatch> patterns = shortcut.getPatterns();
        assertSize(3, patterns);
        assertEquals("Sterm s -> if cond then first else second", patterns.get(0).getText());
        assertEquals("Sedit2 (\"FILE\", file) -> let file_suffix_regex = a in printf \"i\"", patterns.get(1).getText());
        assertEquals("Snterm s -> sprintf \"(Snterm %s) \" s", patterns.get(2).getText());
    }

    public void test_group() {
        PsiFile psiFile = parseCode("let _ = match x with | V1(y) -> Some y | Empty | Unknown -> None");

        PsiSwitch e = first(PsiTreeUtil.findChildrenOfType(psiFile, PsiSwitch.class));
        List<PsiPatternMatch> patterns = e.getPatterns();

        assertSize(3, patterns);
        assertEquals("V1(y) -> Some y", patterns.get(0).getText());
        assertEquals("Empty", patterns.get(1).getText());
        assertEquals("Unknown -> None", patterns.get(2).getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/312
    public void test_GH_312() {
        PsiFile f = parseCode("match fn ?arg with |None -> false |Some f -> true");

        PsiSwitch e = first(PsiTreeUtil.findChildrenOfType(f, PsiSwitch.class));
        List<PsiPatternMatch> patterns = e.getPatterns();

        assertSize(2, patterns);
        assertEquals("None -> false", patterns.get(0).getText());
        assertEquals("Some f -> true", patterns.get(1).getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/340
    // Not an object !
    public void test_GH_340() {
        List<PsiLet> es = letExpressions(parseCode("let fn cond i j = match cond with | Some i, Some j -> i < j\n let fn2 s = ()"));

        assertSize(2, es);
        assertEquals("let fn cond i j = match cond with | Some i, Some j -> i < j", es.get(0).getText());
        assertEquals("let fn2 s = ()", es.get(1).getText());
    }
}
