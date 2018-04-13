package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.psi.*;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PsiTagStartImpl extends MlAstWrapperPsiElement implements PsiTagStart {
    public PsiTagStartImpl(@NotNull ASTNode node) {
        super(RmlTypes.INSTANCE, node);
    }

    @Override
    public Map<String, String> getAttributes() {
        Map<String, String> result = Collections.emptyMap();

        Project project = getProject();

        // find tag 'make' expression
        PsiElement tagName = findChildByClass(PsiUpperSymbol.class);
        if (tagName == null) {
            // no tag name, it's not a custom tag
            tagName = findChildByClass(PsiLowerSymbol.class);
            if (tagName != null) {
                Collection<PsiModule> reactModules = StubIndex.getElements(IndexKeys.MODULES, "ReactDOMRe", project, GlobalSearchScope.allScope(project), PsiModule.class);
                if (!reactModules.isEmpty()) {
                    PsiModule reactDomRe = reactModules.iterator().next();
                    PsiExternal props = reactDomRe.getExternalExpression("props");
                    if (props != null) {
                        PsiSignature signature = PsiTreeUtil.getStubChildOfType(props, PsiSignature.class);
                        if (signature != null) {
                            Collection<PsiNamedSymbol> namedSymbols = PsiTreeUtil.findChildrenOfType(signature, PsiNamedSymbol.class);
                            if (!namedSymbols.isEmpty()) {
                                result = new HashMap<>();
                                for (PsiNamedSymbol namedSymbol : namedSymbols) {
                                    PsiSignature symbolSignature = PsiTreeUtil.getNextSiblingOfType(namedSymbol, PsiSignature.class);
                                    String type = symbolSignature == null ? "" : symbolSignature.getText();
                                    result.put(namedSymbol.getName(), type == null ? "" : type);
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // The tag is a custom component
            PsiModule module = PsiFinder.findFileModule(project, tagName.getText());
            if (module != null) {
                Collection<PsiLet> expressions = module.getLetExpressions();
                for (PsiLet expression : expressions) {
                    if ("make".equals(expression.getName())) {
                        return expression.getParameters();
                    }
                }
            }
        }

        return result;
    }
}
