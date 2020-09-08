package com.reason.lang.ocaml;

import java.util.*;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiPatternMatch;
import com.reason.lang.core.psi.PsiPatternMatchBody;
import com.reason.lang.core.psi.PsiScopedExpr;
import com.reason.lang.core.psi.PsiSwitch;
import com.reason.lang.core.psi.PsiUpperSymbol;

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
        assertNotNull(PsiTreeUtil.findChildOfType(binding, PsiSwitch.class));
    }

    public void test_matchWithException() {
        FileBase psiFile = parseCode("match x with | exception Failure -> Printf.printf");
        assertEquals(1, childrenCount(psiFile));
    }

    public void test_complexMatch() {
        FileBase file = parseCode("begin match Repr.repr o with\n" + "    | BLOCK (0, [|id; o|]) ->\n" + "      [|(Int, id, 0 :: pos); (tpe, o, 1 :: pos)|]\n"
                                          + "    | _ -> raise Exit\n" + "    end");
        PsiElement[] children = file.getChildren();

        assertEquals(1, childrenCount((file)));
        assertInstanceOf(children[0], PsiScopedExpr.class);
    }

    public void test_patternTokenType() {
        PsiFile psiFile = parseCode("let _ = match action with | Incr -> counter + 1");

        PsiSwitch switch_ = first(PsiTreeUtil.findChildrenOfType(psiFile, PsiSwitch.class));
        Collection<PsiPatternMatch> patterns = PsiTreeUtil.findChildrenOfType(switch_, PsiPatternMatch.class);
        assertSize(1, patterns);
        PsiPatternMatch patternMatch = patterns.iterator().next();
        PsiUpperSymbol variant = ORUtil.findImmediateFirstChildOfClass(patternMatch, PsiUpperSymbol.class);
        //assertTrue(variant.isVariant());
        assertEquals("Incr", variant.getText());
        assertEquals("counter + 1", patternMatch.getBody().getText());
    }

    public void test_patternMatch() {
        PsiFile psiFile = parseCode("let _ = match p with | Typedtree.Partial -> \"Partial\" | Total -> \"Total\"");

        PsiSwitch e = first(PsiTreeUtil.findChildrenOfType(psiFile, PsiSwitch.class));
        List<PsiPatternMatch> patterns = new ArrayList<>(PsiTreeUtil.findChildrenOfType(e, PsiPatternMatch.class));
        assertSize(2, patterns);

        PsiPatternMatch m1 = patterns.get(0);
        assertEquals("Typedtree", PsiTreeUtil.findChildOfType(m1, PsiUpperSymbol.class).getText());
        assertEquals("\"Partial\"", m1.getBody().getText());

        PsiPatternMatch m2 = patterns.get(1);
        assertEquals("\"Total\"", m2.getBody().getText());
    }

    public void test_functionShortcut() {
        PsiLet e = first(letExpressions(parseCode("let f x = function | Variant -> 1")));

        PsiFunction fun = (PsiFunction) e.getBinding().getFirstChild();
        assertNotNull(ORUtil.findImmediateFirstChildOfClass(fun.getBody(), PsiPatternMatch.class));
        assertEquals("1", PsiTreeUtil.findChildOfType(fun.getBody(), PsiPatternMatchBody.class).getText());
    }

    public void test_functionShortcutNoPipe() {
        PsiLet e = first(letExpressions(parseCode("let f x = function Variant -> 1")));

        PsiFunction fun = (PsiFunction) e.getBinding().getFirstChild();
        assertNotNull(ORUtil.findImmediateFirstChildOfClass(fun.getBody(), PsiPatternMatch.class));
        assertEquals("1", PsiTreeUtil.findChildOfType(fun.getBody(), PsiPatternMatchBody.class).getText());
    }
}
