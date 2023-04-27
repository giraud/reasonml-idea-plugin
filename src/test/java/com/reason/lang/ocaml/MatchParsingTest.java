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

        RPsiLet let = firstOfType(psiFile, RPsiLet.class);
        assertTrue(let.isFunction());
        PsiTreeUtil.findChildOfType(let, RPsiFunction.class);
    }

    @Test
    public void test_match_01() {
        FileBase psiFileModule = parseCode("let _ = match c with | VtMeta -> let _ = x");
        assertEquals(1, childrenCount(psiFileModule));

        RPsiLet let = firstOfType(psiFileModule, RPsiLet.class);
        RPsiLetBinding binding = let.getBinding();
        RPsiSwitch match = PsiTreeUtil.findChildOfType(binding, RPsiSwitch.class);
        assertEquals("VtMeta -> let _ = x", match.getPatterns().get(0).getText());
    }

    @Test
    public void test_match_with_exception_01() {
        RPsiPatternMatch e = firstOfType(parseCode("match x with | exception Failure -> Printf.printf"), RPsiPatternMatch.class);

        assertNoParserError(e);
        assertEquals("exception Failure -> Printf.printf", e.getText());
    }

    @Test
    public void test_match_with_exception_02() {
        RPsiPatternMatch e = firstOfType(parseCode("match x with | (* comment *) exception Failure -> Printf.printf"), RPsiPatternMatch.class);

        assertNoParserError(e);
        assertEquals("exception Failure -> Printf.printf", e.getText());
    }

    @Test
    public void test_complex_match() {
        FileBase file = parseCode("begin match Repr.repr o with\n"
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
        RPsiLet e = firstOfType(parseCode("let f x = function | Variant -> 1"), RPsiLet.class);

        RPsiFunction fun = (RPsiFunction) e.getBinding().getFirstChild();
        RPsiSwitch shortcut = ORUtil.findImmediateFirstChildOfClass(fun.getBody(), RPsiSwitch.class);
        assertNotNull(ORUtil.findImmediateFirstChildOfClass(shortcut, RPsiPatternMatch.class));
        assertEquals("1", PsiTreeUtil.findChildOfType(shortcut, RPsiPatternMatchBody.class).getText());
    }

    @Test
    public void test_function_shortcut_no_pipe() {
        RPsiLet e = firstOfType(parseCode("let f x = function Variant -> 1"), RPsiLet.class);

        RPsiFunction fun = (RPsiFunction) e.getBinding().getFirstChild();
        RPsiSwitch shortcut = ORUtil.findImmediateFirstChildOfClass(fun.getBody(), RPsiSwitch.class);
        assertNotNull(ORUtil.findImmediateFirstChildOfClass(shortcut, RPsiPatternMatch.class));
        assertEquals("1", PsiTreeUtil.findChildOfType(shortcut, RPsiPatternMatchBody.class).getText());
    }

    @Test
    public void test_function_shortcut_many() {
        RPsiLet e = firstOfType(
                parseCode("let rec db_output_prodn = function "
                        + " | Sterm s -> if cond then first else second\n"
                        + " | Sedit2 (\"FILE\", file) -> let file_suffix_regex = a in printf \"i\"\n"
                        + " | Snterm s -> sprintf \"(Snterm %s) \" s"), RPsiLet.class);

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

    @Test
    public void test_semi() {
        RPsiPatternMatchBody e = firstOfType(parseCode("let _ = match x with | X -> let y = 1 in fn (y); print y"), RPsiPatternMatchBody.class);

        assertEquals("let y = 1 in fn (y); print y", e.getText());
    }

    @Test // coq/analyze.ml
    public void test_begin_end() {
        RPsiPatternMatchBody e = firstOfType(parseCode("let _ = match (Obj.magic data) with\n" +
                "| CODE_CUSTOM_FIXED ->\n" +
                "    begin match input_cstring chan with\n" +
                "    | \"_j\" -> Rint64 (input_intL chan)\n" +
                "    | s -> Printf.eprintf \"Unhandled custom code: %s\" s; assert false\n" +
                "    end"), RPsiPatternMatchBody.class);

        assertEquals("begin match input_cstring chan with\n" +
                "    | \"_j\" -> Rint64 (input_intL chan)\n" +
                "    | s -> Printf.eprintf \"Unhandled custom code: %s\" s; assert false\n" +
                "    end", e.getText());
        RPsiSwitch m = PsiTreeUtil.findChildOfType(e, RPsiSwitch.class);
        assertEquals("input_cstring chan", m.getCondition().getText());
        assertSize(2, m.getPatterns());
    }

    @Test // coq/checker/checkInductive.ml
    public void test_match_or() {
        RPsiPatternMatch e = firstOfType(parseCode("let check_arity env ar1 ar2 = match ar1, ar2 with | (RegularArity _ | TemplateArity _), _ -> assert false"), RPsiPatternMatch.class);

        assertEquals("(RegularArity _ | TemplateArity _), _ -> assert false", e.getText());
        assertEquals("assert false", e.getBody().getText());
    }

    @Test // coq/checker/checkTypes.ml
    public void test_match_no_pipe() {
        RPsiPatternMatch e = firstOfType(parseCode("let _ = match pl, params with Some u::pl, LocalAssum (na,ty)::params -> check_kind env ty u"), RPsiPatternMatch.class);

        assertEquals("Some u::pl, LocalAssum (na,ty)::params -> check_kind env ty u", e.getText());
        assertEquals("check_kind env ty u", e.getBody().getText());
    }

    @Test // coq/checker/validate.ml
    public void test_match_02() {
        RPsiPatternMatch e = firstOfType(parseCode("let _ = match v with | List v0 -> val_sum \"list\" 1 [|[|Annot(\"elem\",v0);v|]|] mem ctx o"), RPsiPatternMatch.class);

        assertEquals("List v0 -> val_sum \"list\" 1 [|[|Annot(\"elem\",v0);v|]|] mem ctx o", e.getText());
        assertEquals("val_sum \"list\" 1 [|[|Annot(\"elem\",v0);v|]|] mem ctx o", e.getBody().getText());
        RPsiFunctionCall fc = ORUtil.findImmediateFirstChildOfClass(e.getBody(), RPsiFunctionCall.class);
        assertSize(6, fc.getParameters());
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
        List<RPsiLet> es = ORUtil.findImmediateChildrenOfClass(parseCode("let fn cond i j = match cond with | Some i, Some j -> i < j\n let fn2 s = ()"), RPsiLet.class);

        assertSize(2, es);
        assertEquals("let fn cond i j = match cond with | Some i, Some j -> i < j", es.get(0).getText());
        assertEquals("let fn2 s = ()", es.get(1).getText());
    }
}
