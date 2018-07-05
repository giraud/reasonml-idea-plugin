package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.psi.*;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PsiTagStartImpl extends MlAstWrapperPsiElement implements PsiTagStart {
    public PsiTagStartImpl(@NotNull ASTNode node) {
        super(RmlTypes.INSTANCE, node);
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return getFirstChild().getNextSibling();
    }

    @Nullable
    @Override
    public String getName() {
        PsiElement nameIdentifier = getNameIdentifier();
        return nameIdentifier == null ? null : nameIdentifier.getText();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return null;
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
                    PsiNamedElement props = reactDomRe.getTypeExpression("props");
                    if (props != null) {
                        PsiTypeBinding binding = PsiTreeUtil.getStubChildOfType(props, PsiTypeBinding.class);
                        if (binding != null) {
                            PsiRecord object = PsiTreeUtil.getStubChildOfType(binding, PsiRecord.class);
                            if (object != null) {
                                Collection<PsiRecordField> fields = PsiTreeUtil.findChildrenOfType(object, PsiRecordField.class);
                                if (!fields.isEmpty()) {
                                    result = new HashMap<>();
                                    for (PsiRecordField field : fields) {
                                        PsiSignature fieldSignature = field.getSignature();
                                        String type = fieldSignature == null ? "" : fieldSignature.getText();
                                        result.put(field.getName(), type == null ? "" : type);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } else {
            // The tag is a custom component
            PsiModule module = PsiFinder.getInstance().findRealFileModule(project, tagName.getText());
            if (module != null) {
                Collection<PsiLet> expressions = module.getLetExpressions();
                for (PsiLet expression : expressions) {
                    if ("make".equals(expression.getName())) {
                        PsiFunction function = expression.getFunction();
                        if (function != null) {
                            return function.getParameters();
                        }
                    }
                }
            }
        }

        return result;
    }

}
