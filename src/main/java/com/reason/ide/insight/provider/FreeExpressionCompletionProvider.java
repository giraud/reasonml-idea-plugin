package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.Platform;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiSignatureUtil;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiNamedElement;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;

public class FreeExpressionCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        //System.out.println("»» FREE expression completion");

        Project project = parameters.getOriginalFile().getProject();

        // Find all possible paths
        // todo

        // Add file modules
        List<PsiModule> fileModules = PsiFinder.getInstance().findFileModules(project, interfaceOrImplementation);
        for (PsiModule module : fileModules) {
            if (!module.isComponent()) {
                resultSet.addElement(
                        LookupElementBuilder.create(module).
                                withTypeText(Platform.removeProjectDir(project, module.getContainingFile().getVirtualFile()).replace("node_modules" + File.separator, "")).
                                withIcon(PsiIconUtil.getProvidersIcon(module, 0))
                );
            }
        }

        // Add all local expressions
        PsiModule currentFileModule = ((FileBase) parameters.getOriginalFile()).asModule();
        if (currentFileModule != null) {
            for (PsiNamedElement expression : currentFileModule.getExpressions()) {
                resultSet.addElement(
                        LookupElementBuilder.create(expression).
                                withTypeText(PsiSignatureUtil.getProvidersType(expression)).
                                withIcon(PsiIconUtil.getProvidersIcon(expression, 0))
                );
            }

            // Add open and includes
            //Collection<PsiOpen> openExpressions = currentFileModule.getOpenExpressions();
            //for (PsiOpen openExpression : openExpressions) {
            // todo
            //}

            //Collection<PsiInclude> includeExpressions = currentFileModule.getIncludeExpressions();
            //for (PsiInclude includeExpression : includeExpressions) {
            // todo
            //}
        }
    }

}
