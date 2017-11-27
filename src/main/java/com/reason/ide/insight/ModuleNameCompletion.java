package com.reason.ide.insight;

import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.LookupElementRenderer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.reason.Platform;
import com.reason.RmlFile;
import com.reason.icons.Icons;
import com.reason.ide.files.OclFileType;
import com.reason.ide.files.RmlFileType;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiModuleFile;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.reason.lang.core.RmlPsiUtil.fileNameToModuleName;
import static com.reason.lang.core.RmlPsiUtil.findFileModules;

class ModuleNameCompletion {

    static void addModules(Project project, PsiModuleFile currentModule, String modulePrefix,/*@NotNull CompletionParameters parameters, ProcessingContext context,*/ @NotNull CompletionResultSet resultSet) {
        // First find all potential modules of current file
        PsiModule[] currentModules = currentModule.getModules();
        for (PsiModule module : currentModules) {
            String moduleName = module.getName();
            LookupElementBuilder lookupModule = LookupElementBuilder.createWithSmartPointer(moduleName, module).
                    withRenderer(new LookupElementRenderer<LookupElement>() {
                        @Override
                        public void renderElement(LookupElement element, LookupElementPresentation presentation) {
                            presentation.setItemText(moduleName);
                            presentation.setItemTextBold(true);
                            presentation.setIcon(Icons.MODULE);
                        }
                    });
            resultSet.addElement(lookupModule);
        }

        List<PsiFile> modules = new ArrayList<>();
        modules.addAll(findFileModules(project, RmlFileType.INSTANCE.getDefaultExtension(), modulePrefix));
        modules.addAll(findFileModules(project, OclFileType.INSTANCE.getDefaultExtension(), modulePrefix));

        if (!modules.isEmpty()) {
            for (PsiFile module : modules) {
                String moduleName = fileNameToModuleName(module);
                LookupElementBuilder lookupModule = LookupElementBuilder.
                        createWithSmartPointer(moduleName, module).
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
                resultSet.addElement(lookupModule);
            }
        }
    }

}
