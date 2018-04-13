package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.lang.MlTypes;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiObjectField;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ObjectCompletionProvider extends CompletionProvider<CompletionParameters> {
    public ObjectCompletionProvider(MlTypes types) {

    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        //System.out.println("» ObjectCompletionProvider");

        Project project = parameters.getOriginalFile().getProject();
        PsiElement cursorElement = parameters.getPosition();
        PsiElement sharpsharpElement = cursorElement.getPrevSibling();
        PsiElement previousElement = sharpsharpElement == null ? null : sharpsharpElement.getPrevSibling();

        if (previousElement instanceof PsiLowerSymbol) {
            // TODO: Find the correct symbol...
            // Use type if possible
            String lowerName = ((PsiNamedElement) previousElement).getName();
            if (lowerName != null) {
                PsiLet let = null;

                Collection<PsiLet> lets = PsiFinder.findLets(project, lowerName);
                //Collection<PsiLet> filteredLets = lets;
                if (!lets.isEmpty()) {
                    // TODO: Find the correct module path...
//                    for (PsiLet filteredLet : filteredLets) {
//                        System.out.println(" " + filteredLet.getContainingFile().getVirtualFile().getCanonicalPath());
//                    }
                    let = lets.iterator().next();
                }

                if (let != null && let.isObject()) {
                    Collection<PsiObjectField> fields = let.getObjectFields();
                    for (PsiObjectField field : fields) {
                        resultSet.addElement(
                                LookupElementBuilder.
                                        create(field.getName()).
                                        withIcon(PsiIconUtil.getProvidersIcon(field, 0))
                        );
                    }
                }
            }
        }
    }
}
