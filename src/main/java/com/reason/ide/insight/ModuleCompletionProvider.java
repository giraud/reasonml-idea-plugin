package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.reason.icons.Icons;
import com.reason.psi.PsiLet;
import com.reason.psi.PsiModule;
import com.reason.psi.RmlPsiUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class ModuleCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        PsiElement position = parameters.getPosition(); //  IntellijIdeazzz
        PsiElement moduleName = position.getPrevSibling().getPrevSibling();

        List<PsiModule> modules = RmlPsiUtil.findModules(parameters.getOriginalFile().getProject(), moduleName.getText());
        if (!modules.isEmpty()) {
            for (PsiModule module : modules) {
                // list all let expressions
                Collection<PsiLet> lets = module.getLetExpressions();
                for (PsiLet let : lets) {
                    String inferredType = let.getInferredType();
                    resultSet.addElement(LookupElementBuilder.create(let.getName()).
                            withIcon(Icons.VALUE).
                            withTypeText(inferredType == null ? "unknown type" : inferredType));
                }
            }
        }
    }
}
