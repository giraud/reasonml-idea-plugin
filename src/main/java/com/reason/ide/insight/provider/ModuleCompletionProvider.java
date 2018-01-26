package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import com.reason.lang.MlTypes;
import org.jetbrains.annotations.NotNull;

public class ModuleCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final MlTypes m_types;

    public ModuleCompletionProvider(MlTypes types) {
        m_types = types;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
        //System.out.println("»» Module completion");
        PsiElement originalPosition = parameters.getOriginalPosition();

        PsiElement cursorElement = originalPosition;

        // from VALUE_NAME to PsiName
        if (originalPosition != null && originalPosition.getNode().getElementType() == m_types.VALUE_NAME) {
            cursorElement = originalPosition.getParent();
        }

        // Compute qname
        String qname = "";
        PsiElement previousSibling = cursorElement == null ? null : cursorElement.getPrevSibling();
        if (previousSibling != null) {
            IElementType previousElementType = previousSibling.getNode().getElementType();
            while (previousElementType == m_types.DOT || previousElementType == m_types.MODULE_NAME) {
                if (previousElementType != m_types.DOT) {
                    qname = previousSibling.getText() + (qname.isEmpty() ? "" : "." + qname);
                }
                previousSibling = previousSibling.getPrevSibling();
                previousElementType = previousSibling == null ? null : previousSibling.getNode().getElementType();
            }
        }

        //System.out.println("list all modules with qname " + qname);
        //Collection<PsiModule> modules = StubIndex.getElements(IndexKeys.MODULES_QN, name1, project, GlobalSearchScope.allScope(project), PsiModule.class);
        //
        //if (!modules.isEmpty()) {
        //    for (PsiModule module : modules) {
        //        Collection<PsiNamedElement> expressions = module.getExpressions();
        //
        //        for (PsiNamedElement expression : expressions) {
        //            resultSet.addElement(
        //                    LookupElementBuilder.create(expression).
        //                            withIcon(PsiIconUtil.getProvidersIcon(expression, 0)).
        //                            withTypeText(PsiInferredTypeUtil.getTypeInfo(expression))
        //            );
        //        }
        //    }
        //}
    }
}
