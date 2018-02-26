package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.reason.ide.files.RmlFile;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiTagStart;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public class PsiTagStartImpl extends MlAstWrapperPsiElement implements PsiTagStart {
    public PsiTagStartImpl(@NotNull ASTNode node) {
        super(RmlTypes.INSTANCE, node);
    }

    @Override
    public Map<String, String> getAttributes() {
        Project project = getProject();

        // find tag 'make' expression
        PsiUpperSymbol tagName = findChildByClass(PsiUpperSymbol.class);
        PsiFile fileModule = RmlPsiUtil.findFileModule(project, tagName.getText());
        if (fileModule != null) {
            Collection<PsiLet> expressions = ((RmlFile) fileModule).asModule().getLetExpressions();
            for (PsiLet expression : expressions) {
                if ("make".equals(expression.getName())) {
                    return expression.getParameters();
                }
            }
        }

        return Collections.emptyMap();
    }
}
