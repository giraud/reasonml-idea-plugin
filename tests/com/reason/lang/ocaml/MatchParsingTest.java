package com.reason.lang.ocaml;

import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.junit.*;

import java.util.*;

@SuppressWarnings("ConstantConditions")
public class MatchParsingTest extends OclParsingTestCase {
    @Test
    public void test_match() {
        FileBase psiFile = parseCode("let path_of_dirpath dir = match DirPath.repr dir with [] -> failwith \"path_of_dirpath\"");
        assertEquals(1, childrenCount(psiFile));

        RPsiLet let = first(letExpressions(psiFile));
        assertTrue(let.isFunction());
        PsiTreeUtil.findChildOfType(let, RPsiFunction.class);
    }

    @Test
    public void test_matchExpr() {
        FileBase psiFileModule = parseCode("let _ = match c with | VtMeta -> let _ = x");
        assertEquals(1, childrenCount(psiFileModule));

        RPsiLet let = first(letExpressions(psiFileModule));
        RPsiLetBinding binding = let.getBinding();
        RPsiSwitch match = PsiTreeUtil.findChildOfType(binding, RPsiSwitch.class);
        assertEquals("VtMeta -> let _ = x", match.getPatterns().get(0).getText());
    }

    @Test
    public void test_match_with_exception() {
        FileBase psiFile = parseCode("match x with | exception Failure -> Printf.printf");
        assertEquals(1, childrenCount(psiFile));
    }

    @Test
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
        assertInstanceOf(children[0], RPsiScopedExpr.class);
    }

    @Test
    public void test_pattern_token_type() {
        PsiFile psiFile = parseCode("let _ = match action with | Incr -> counter + 1");

        RPsiSwitch switch_ = first(PsiTreeUtil.findChildrenOfType(psiFile, RPsiSwitch.class));
        Collection<RPsiPatternMatch> patterns = PsiTreeUtil.findChildrenOfType(switch_, RPsiPatternMatch.class);
        assertSize(1, patterns);
        RPsiPatternMatch patternMatch = patterns.iterator().next();
        RPsiUpperSymbol variant = ORUtil.findImmediateFirstChildOfClass(patternMatch, RPsiUpperSymbol.class);
        // assertTrue(variant.isVariant());
        assertEquals("Incr", variant.getText());
        assertEquals("counter + 1", patternMatch.getBody().getText());
    }

    @Test
    public void test_pattern_match() {
        PsiFile psiFile = parseCode("let _ = match p with | Typedtree.Partial -> \"Partial\" | Total -> \"Total\"");

        RPsiSwitch e = first(PsiTreeUtil.findChildrenOfType(psiFile, RPsiSwitch.class));
        List<RPsiPatternMatch> patterns = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, RPsiPatternMatch.class));
        assertSize(2, patterns);

        RPsiPatternMatch m1 = patterns.get(0);
        assertEquals("Typedtree", PsiTreeUtil.findChildOfType(m1, RPsiUpperSymbol.class).getText());
        assertEquals(myTypes.A_VARIANT_NAME, ORUtil.findImmediateLastChildOfClass(m1, RPsiUpperSymbol.class).getNode().getElementType());
        assertEquals("\"Partial\"", m1.getBody().getText());

        RPsiPatternMatch m2 = patterns.get(1);
        assertEquals("\"Total\"", m2.getBody().getText());
    }

    @Test
    public void test_function_shortcut() {
        RPsiLet e = first(letExpressions(parseCode("let f x = function | Variant -> 1")));

        RPsiFunction fun = (RPsiFunction) e.getBinding().getFirstChild();
        RPsiSwitch shortcut = ORUtil.findImmediateFirstChildOfClass(fun.getBody(), RPsiSwitch.class);
        assertNotNull(ORUtil.findImmediateFirstChildOfClass(shortcut, RPsiPatternMatch.class));
        assertEquals("1", PsiTreeUtil.findChildOfType(shortcut, RPsiPatternMatchBody.class).getText());
    }

    @Test
    public void test_function_shortcut_no_pipe() {
        RPsiLet e = first(letExpressions(parseCode("let f x = function Variant -> 1")));

        RPsiFunction fun = (RPsiFunction) e.getBinding().getFirstChild();
        RPsiSwitch shortcut = ORUtil.findImmediateFirstChildOfClass(fun.getBody(), RPsiSwitch.class);
        assertNotNull(ORUtil.findImmediateFirstChildOfClass(shortcut, RPsiPatternMatch.class));
        assertEquals("1", PsiTreeUtil.findChildOfType(shortcut, RPsiPatternMatchBody.class).getText());
    }

    @Test
    public void test_function_shortcut_many() {
        RPsiLet e = first(letExpressions(
                parseCode("let rec db_output_prodn = function "
                        + " | Sterm s -> if cond then first else second\n"
                        + " | Sedit2 (\"FILE\", file) -> let file_suffix_regex = a in printf \"i\"\n"
                        + " | Snterm s -> sprintf \"(Snterm %s) \" s")));

        RPsiSwitch shortcut = PsiTreeUtil.findChildOfType(e, RPsiSwitch.class);
        List<RPsiPatternMatch> patterns = shortcut.getPatterns();
        assertSize(3, patterns);
        assertEquals("Sterm s -> if cond then first else second", patterns.get(0).getText());
        assertEquals("Sedit2 (\"FILE\", file) -> let file_suffix_regex = a in printf \"i\"", patterns.get(1).getText());
        assertEquals("Snterm s -> sprintf \"(Snterm %s) \" s", patterns.get(2).getText());
    }

    @Test
    public void test_group() {
        PsiFile psiFile = parseCode("let _ = match x with | V1(y) -> Some y | Empty | Unknown -> None");

        RPsiSwitch e = first(PsiTreeUtil.findChildrenOfType(psiFile, RPsiSwitch.class));
        List<RPsiPatternMatch> patterns = e.getPatterns();

        assertSize(3, patterns);
        assertEquals("V1(y) -> Some y", patterns.get(0).getText());
        assertEquals("Empty", patterns.get(1).getText());
        assertEquals("Unknown -> None", patterns.get(2).getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/312
    @Test
    public void test_GH_312() {
        PsiFile f = parseCode("match fn ?arg with |None -> false |Some f -> true");

        RPsiSwitch e = first(PsiTreeUtil.findChildrenOfType(f, RPsiSwitch.class));
        List<RPsiPatternMatch> patterns = e.getPatterns();

        assertSize(2, patterns);
        assertEquals("None -> false", patterns.get(0).getText());
        assertEquals("Some f -> true", patterns.get(1).getText());
    }

    // https://github.com/giraud/reasonml-idea-plugin/issues/340
    // Not an object !
    @Test
    public void test_GH_340() {
        List<RPsiLet> es = letExpressions(parseCode("let fn cond i j = match cond with | Some i, Some j -> i < j\n let fn2 s = ()"));

        assertSize(2, es);
        assertEquals("let fn cond i j = match cond with | Some i, Some j -> i < j", es.get(0).getText());
        assertEquals("let fn2 s = ()", es.get(1).getText());
    }
}
