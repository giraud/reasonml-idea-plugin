package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.PsiIconUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiInferredTypeUtil;
import com.reason.lang.core.psi.PsiNamedElement;

import java.util.Collection;

// The cursor is at file level
public class FileCompletionProvider {
    public static void complete(Project project, FileBase file, CompletionResultSet resultSet) {
        // Add all file expressions
        Collection<PsiNamedElement> fileExpressions = file.getExpressions();
        for (PsiNamedElement expression : fileExpressions) {
            resultSet.addElement(
                    LookupElementBuilder.create(expression).
                            withIcon(PsiIconUtil.getProvidersIcon(expression, 0)).
                            withTypeText(PsiInferredTypeUtil.getTypeInfo(expression))
            );
        }
    }
}
