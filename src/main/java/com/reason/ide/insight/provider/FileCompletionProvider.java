package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiInferredTypeUtil;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.reason.lang.core.MlFileType.interfaceOnly;
import static com.reason.lang.core.MlScope.all;

// The cursor is at file level
public class FileCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        //System.out.println("»» File completion");

        FileBase file = (FileBase) parameters.getOriginalFile();
        Project project = file.getProject();

        // Add all file expressions (local or from include)
        Collection<PsiNamedElement> fileExpressions = file.asModule().getExpressions();
        addExpressionsToResult(resultSet, fileExpressions);

        // Add all expressions from pervasives (only RmlFile ?)
        PsiModule pervasives = RmlPsiUtil.findModule(project, "Pervasives", interfaceOnly, all);
        if (pervasives != null) {
            addExpressionsToResult(resultSet, pervasives.getExpressions());
        }

        // Add all expressions from the open
        // TODO complex expressions
        //PsiOpen[] openExpressions = file.getOpenExpressions();
    }

    private static void addExpressionsToResult(@NotNull CompletionResultSet resultSet, Collection<PsiNamedElement> fileExpressions) {
        for (PsiNamedElement expression : fileExpressions) {
            resultSet.addElement(
                    LookupElementBuilder.create(expression).
                            withTailText(" (" + expression.getContainingFile().getVirtualFile().getPresentableName() + ")", true).
                            withIcon(PsiIconUtil.getProvidersIcon(expression, 0)).
                            withTypeText(PsiInferredTypeUtil.getTypeInfo(expression))
            );
        }
    }

}
