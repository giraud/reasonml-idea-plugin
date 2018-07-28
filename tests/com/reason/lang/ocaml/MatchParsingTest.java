package com.reason.lang.ocaml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.*;

public class MatchParsingTest extends BaseParsingTestCase {
    public MatchParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testMatch() {
        PsiFile psiFile = parseCode("let path_of_dirpath dir = match DirPath.repr dir with [] -> failwith \"path_of_dirpath\"");
        assertEquals(1, psiFile.getChildren().length);

        PsiLet let = first(letExpressions(psiFile));
        assertTrue(let.isFunction());
        PsiFunction function = PsiTreeUtil.findChildOfType(let, PsiFunction.class);
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
        PsiFile file = parseCode("begin match Repr.repr o with\n"+
                "    | BLOCK (0, [|id; o|]) ->\n"+
                "      [|(Int, id, 0 :: pos); (tpe, o, 1 :: pos)|]\n"+
                "    | _ -> raise Exit\n"+
                "    end", true);
        PsiElement[] children = file.getChildren();

        assertEquals(1, children.length);
        assertInstanceOf(children[0], PsiScopedExpr.class);
    }


}
