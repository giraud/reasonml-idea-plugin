package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionResultSet;
import org.jetbrains.annotations.NotNull;

class ModuleDotCompletionProvider {
    static void complete(/*@NotNull CompletionParameters parameters, ProcessingContext context,*/ @NotNull CompletionResultSet resultSet) {
        //PsiElement position = parameters.getPosition(); //  IntellijIdeazzz
        //PsiElement moduleName = position.getPrevSibling().getPrevSibling();

        //List<PsiModule> modules = RmlPsiUtil.findModules(parameters.getOriginalFile().getProject(), moduleName.getText());
        //if (!modules.isEmpty()) {
        //    for (PsiModule module : modules) {
        //         list all let expressions
        //Collection<PsiLet> lets = module.getLetExpressions();
        //for (PsiLet let : lets) {
        //    String inferredType = let.getInferredType();
        //    resultSet.addElement(
        //            LookupElementBuilder.create(let.getName()).
        //                    withIcon(Icons.VALUE).
        //                    withTypeText(inferredType == null ? "unknown type" : inferredType));
        //}
        //}
        //}
    }
}
