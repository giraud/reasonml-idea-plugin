package com.reason.ide.insight;

import java.util.*;
import org.jetbrains.annotations.NotNull;
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
import com.reason.lang.core.psi.Module;

import static com.reason.lang.core.RmlPsiUtil.*;

class ModuleNameCompletion {

    static void complete(Project project, RmlFile currentModule, String modulePrefix, @NotNull CompletionResultSet resultSet) {
        // First find all potential modules of current file
        Module[] currentModules = currentModule.getModules();
        for (Module module : currentModules) {
            String moduleName = module.getName();
            if (moduleName != null) {
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
                                if (psiElement != null) {
                                    presentation.setIcon(psiElement instanceof RmlFile ? Icons.RML_FILE : Icons.OCL_FILE);
                                    presentation.setTypeText(Platform.removeProjectDir(project, psiElement.getVirtualFile()));
                                    presentation.setTypeGrayed(true);
                                }
                            }
                        });
                resultSet.addElement(lookupModule);
            }
        }
    }
}
