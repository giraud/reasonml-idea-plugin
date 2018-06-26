package com.reason.ocaml;

import com.intellij.psi.util.PsiTreeUtil;
import com.reason.BaseParsingTestCase;
import com.reason.lang.core.psi.PsiFunction;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import com.reason.lang.ocaml.OclParserDefinition;

public class MatchParsingTest extends BaseParsingTestCase {
    public MatchParsingTest() {
        super("", "ml", new OclParserDefinition());
    }

    public void testMatch() {
        PsiFileModuleImpl psiFileModule = parseCode("let path_of_dirpath dir = match DirPath.repr dir with [] -> failwith \"path_of_dirpath\"", true);
        assertEquals(2, psiFileModule.getChildren().length);

        PsiLet let = first(psiFileModule.getLetExpressions());
        assertTrue(let.isFunction());
        PsiFunction function = PsiTreeUtil.findChildOfType(let, PsiFunction.class);

    }

}
