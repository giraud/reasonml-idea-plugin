package com.reason.lang.ocaml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.BaseParsingTestCase;
import com.reason.lang.core.psi.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@SuppressWarnings("ConstantConditions")
public class MatchParsingTest extends BaseParsingTestCase {
    public MatchParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testMatch() {
        PsiFile psiFile = parseCode("let path_of_dirpath dir = match DirPath.repr dir with [] -> failwith \"path_of_dirpath\"");
        assertEquals(1, psiFile.getChildren().length);

        PsiLet let = first(letExpressions(psiFile));
        assertTrue(let.isFunction());
        PsiTreeUtil.findChildOfType(let, PsiFunction.class);
    }

    public void testMatchExpr() {
        PsiFile psiFileModule = parseCode("let _ = match c with | VtMeta -> let _ = x");
        PsiElement[] children = psiFileModule.getChildren();
        assertEquals(1, children.length);

        PsiLet let = first(letExpressions(psiFileModule));
        PsiLetBinding binding = let.getBinding();
        assertNotNull(PsiTreeUtil.findChildOfType(binding, PsiSwitch.class));
    }

    public void testMatchWithException() {
        PsiFile psiFile = parseCode("match x with | exception Failure -> Printf.printf");
        assertEquals(1, psiFile.getChildren().length);
    }

    public void testComplexMatch() {
        PsiFile file = parseCode("begin match Repr.repr o with\n" +
                "    | BLOCK (0, [|id; o|]) ->\n" +
                "      [|(Int, id, 0 :: pos); (tpe, o, 1 :: pos)|]\n" +
                "    | _ -> raise Exit\n" +
                "    end");
        PsiElement[] children = file.getChildren();

        assertEquals(1, children.length);
        assertInstanceOf(children[0], PsiScopedExpr.class);
    }

    public void testPatternTokenType() {
        PsiFile psiFile = parseCode("let _ = match action with | Incr -> counter + 1");

        PsiSwitch switch_ = first(PsiTreeUtil.findChildrenOfType(psiFile, PsiSwitch.class));
        Collection<PsiPatternMatch> patterns = PsiTreeUtil.findChildrenOfType(switch_, PsiPatternMatch.class);
        assertSize(1, patterns);
        PsiPatternMatch psiPatternMatch = patterns.iterator().next();
        PsiVariant variant = PsiTreeUtil.findChildOfType(psiPatternMatch, PsiVariant.class);
        assertEquals(OclTypes.INSTANCE.VARIANT_NAME, variant.getFirstChild().getNode().getElementType());
        assertEquals("Incr", variant.getText());
        assertEquals("counter + 1", psiPatternMatch.getBody().getText());
    }

    public void testPatternMatch() {
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
}
