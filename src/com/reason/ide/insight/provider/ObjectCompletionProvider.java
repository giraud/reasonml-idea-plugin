package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.Log;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.PsiRecordField;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;

public class ObjectCompletionProvider extends CompletionProvider<CompletionParameters> {

    private static final Log LOG = Log.create("insight.object");

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        LOG.debug("RECORD expression completion");

        Project project = parameters.getOriginalFile().getProject();
        PsiElement cursorElement = parameters.getPosition();
        PsiElement sharpSharpElement = cursorElement.getPrevSibling();
        PsiElement previousElement = sharpSharpElement == null ? null : sharpSharpElement.getPrevSibling();

        if (previousElement instanceof PsiLowerSymbol) {
            // TODO: Find the correct symbol...
            // Use type if possible
            String lowerName = ((PsiNamedElement) previousElement).getName();
            if (lowerName != null) {
                PsiLet let = null;

                Collection<? extends PsiQualifiedNamedElement> lets = PsiFinder.getInstance().findLets(project, lowerName, interfaceOrImplementation);
                //Collection<PsiLet> filteredLets = lets;
                if (!lets.isEmpty()) {
                    // TODO: Find the correct module path...
//                    for (PsiLet filteredLet : filteredLets) {
//                        System.out.println(" " + filteredLet.getContainingFile().getVirtualFile().getCanonicalPath());
//                    }
                    let = (PsiLet) lets.iterator().next();
                }

                if (let != null && let.isObject()) {
                    Collection<PsiRecordField> fields = let.getObjectFields();
                    for (PsiRecordField field : fields) {
                        String name = field.getName();
                        if (name != null) {
                            resultSet.addElement(LookupElementBuilder.
                                    create(name).
                                    withIcon(PsiIconUtil.getProvidersIcon(field, 0))
                            );
                        }
                    }
                }
            }
        }
    }
}
