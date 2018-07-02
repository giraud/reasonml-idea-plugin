package com.reason.lang.core.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PsiFunction extends ASTWrapperPsiElement {

    public PsiFunction(ASTNode node) {
        super(node);
    }

    @Override
    public boolean canNavigate() {
        return false;
    }

    public PsiFunctionBody getBody() {
        return PsiTreeUtil.findChildOfType(this, PsiFunctionBody.class);
    }

    @Override
    public String toString() {
        return "Function";
    }

    @NotNull
    public Map<String, String> getParameters() {
        PsiParameters parameters = findChildByClass(PsiParameters.class);
        Collection<PsiLowerSymbol> symbols = PsiTreeUtil.findChildrenOfType(parameters, PsiLowerSymbol.class);
        if (symbols.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, String> result = new THashMap<>();
        for (PsiLowerSymbol symbol : symbols) {
            String parameterName = symbol.getText();
            if (!"children".equals(parameterName) && !"_children".equals(parameterName)) {
                result.put(parameterName, "");
            }
        }

        return result;
    }
}