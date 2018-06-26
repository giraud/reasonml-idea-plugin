package com.reason.ocaml;

import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.ocaml.OclParserDefinition;

public class MatchTryParsingTest extends BaseParsingTestCase {
    public MatchTryParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testMatch() {
        PsiFileModuleImpl psiFileModule = parseCode("let path_of_dirpath dir = match DirPath.repr dir with [] -> failwith \"path_of_dirpath\"");
        assertEquals(2, psiFileModule.getChildren().length);

        PsiLet let = first(psiFileModule.getLetExpressions());
        assertTrue(let.isFunction());
        PsiFunction function = PsiTreeUtil.findChildOfType(let, PsiFunction.class);
    }

    public void testTryIn() {
        PsiFileModuleImpl psiFileModule = parseCode("try x with Not_found -> assert false in otherExpression");
        PsiElement[] children = psiFileModule.getChildren();
        assertEquals(2 + 1, children.length); // in is token
    }

    public void testTryLet() {
        PsiFileModuleImpl psiFileModule = parseCode("let e = try let t = 6 with Not_found -> ()", true);
        PsiElement[] children = psiFileModule.getChildren();
        assertEquals(1 + 1, children.length);
    }
}
