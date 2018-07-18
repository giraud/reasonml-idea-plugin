package com.reason.ocaml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLetBinding;
import com.reason.lang.core.psi.PsiSwitch;
import com.reason.lang.ocaml.OclParserDefinition;

public class MatchTryParsingTest extends BaseParsingTestCase {
    public MatchTryParsingTest() {
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
        PsiLet let = first(letExpressions(parseCode("let _ = match c with | VtMeta -> let _ = x", true)));
        PsiLetBinding binding = let.getBinding();
        assertNotNull(PsiTreeUtil.findChildOfType(binding, PsiSwitch.class));
    }

    public void testTryIn() {
        PsiFile psiFile = parseCode("try x with Not_found -> assert false in otherExpression");
        PsiElement[] children = psiFile.getChildren();
        assertEquals(5, children.length); // in is token
    }

    public void testTryLet() {
        PsiFile psiFileModule = parseCode("let e = try let t = 6 with Not_found -> ()");
        PsiElement[] children = psiFileModule.getChildren();
        assertEquals(1, children.length);
    }
}
