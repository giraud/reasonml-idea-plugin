package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.LookupElementRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.ProcessingContext;
import com.reason.Platform;
import com.reason.RmlFile;
import com.reason.icons.Icons;
import com.reason.ide.files.OclFileType;
import com.reason.ide.files.RmlFileType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.reason.ide.insight.CompletionConstants.INTELLIJ_IDEA_RULEZZZ_LENGTH;
import static com.reason.lang.core.RmlPsiUtil.fileNameToModuleName;
import static com.reason.lang.core.RmlPsiUtil.findFileModules;

public class ModuleNameCompletionProvider extends CompletionProvider<CompletionParameters> {

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        String tokenName = parameters.getPosition().getText(); //  <UIDENT>IntellijIdeaRulezzz

        Project project = parameters.getOriginalFile().getProject();

        String moduleStartName = tokenName.substring(0, tokenName.length() - INTELLIJ_IDEA_RULEZZZ_LENGTH).toLowerCase(Locale.getDefault());

        List<PsiFile> modules = new ArrayList<>();
        modules.addAll(findFileModules(project, RmlFileType.INSTANCE.getDefaultExtension(), moduleStartName));
        modules.addAll(findFileModules(project, OclFileType.INSTANCE.getDefaultExtension(), moduleStartName));

        if (!modules.isEmpty()) {
            for (PsiFile module : modules) {
                String moduleName = fileNameToModuleName(module);
                LookupElementBuilder lookupModule = LookupElementBuilder.createWithSmartPointer(moduleName, module).
                        withRenderer(new LookupElementRenderer<LookupElement>() {
                            @Override
                            public void renderElement(LookupElement element, LookupElementPresentation presentation) {
                                presentation.setItemText(moduleName);
                                presentation.setItemTextBold(true);

                                PsiFile psiElement = (PsiFile) element.getPsiElement();
                                presentation.setIcon(psiElement instanceof RmlFile ? Icons.RML_FILE : Icons.OCL_FILE);
                                presentation.setTypeText(Platform.removeProjectDir(project, psiElement.getVirtualFile().getPath()));
                                presentation.setTypeGrayed(true);
                            }
                        });
                /*
                LookupElementBuilder.
                        create(module).
                        withTypeText(module.getName()).
                        withIcon(module instanceof RmlFile ? Icons.RML_FILE : Icons.OCL_FILE)
                 */

                resultSet.addElement(lookupModule);
            }
        }
    }

}
