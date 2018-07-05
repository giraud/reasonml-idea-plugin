package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.ide.files.FileBase;
import com.reason.lang.MlTypes;
import com.reason.lang.core.ModulePath;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiUpperSymbol;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.reason.lang.core.MlFileType.implementationOnly;
import static com.reason.lang.core.MlFileType.interfaceOrImplementation;
import static com.reason.lang.core.MlScope.inBsconfig;

public class ModuleCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final MlTypes m_types;

    public ModuleCompletionProvider(MlTypes types) {
        m_types = types;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        //System.out.println("»» Module completion");

        Project project = parameters.getOriginalFile().getProject();
        PsiElement originalPosition = parameters.getOriginalPosition();

        PsiElement cursorElement = originalPosition;

        // PsiWhite after a DOT
        //if (originalPosition instanceof PsiWhiteSpace) {
        //    PsiElement prevSibling = originalPosition.getPrevSibling();
        //    if (prevSibling instanceof PsiOpen) {
        //        cursorElement = prevSibling.getLastChild();
        //    }
        //}
        // from UIDENT node to PsiSymbolName
        //else if (originalPosition != null && originalPosition.getNode().getElementType() == m_types.UIDENT) {
        if (originalPosition != null) {
            cursorElement = originalPosition.getParent();
        }

        // Compute module path (all module names before the last dot)
        List<PsiUpperSymbol> moduleNames = new ArrayList<>();
        PsiElement previousSibling = cursorElement == null ? null : cursorElement.getPrevSibling();
        if (previousSibling != null) {
            IElementType previousElementType = previousSibling.getNode().getElementType();
            while (previousElementType == m_types.DOT || previousElementType == m_types.UPPER_SYMBOL) {
                if (previousSibling instanceof PsiUpperSymbol) {
                    moduleNames.add((PsiUpperSymbol) previousSibling);
                }
                previousSibling = previousSibling == null ? null : previousSibling.getPrevSibling();
                previousElementType = previousSibling == null ? null : previousSibling.getNode().getElementType();
            }
        }
        Collections.reverse(moduleNames);
        ModulePath modulePath = new ModulePath(moduleNames);

        PsiFinder psiFinder = PsiFinder.getInstance();
        if (modulePath.isEmpty()) {
            // First module to complete, use the list of files
            Collection<FileBase> files = psiFinder.findFileModules(project, interfaceOrImplementation);
            if (!files.isEmpty()) {
                for (FileBase file : files) {
                    resultSet.addElement(LookupElementBuilder.
                            create(file.asModuleName()).
                            withTypeText(file.shortLocation(project)).
                            withIcon(PsiIconUtil.getProvidersIcon(file, 0))
                    );
                }
            }
        } else {
            String latestModuleName = modulePath.getLatest();
            Collection<PsiModule> modules = psiFinder.findModules(project, latestModuleName, implementationOnly, inBsconfig);
            if (!modules.isEmpty()) {
                for (PsiModule module : modules) {
                    for (PsiModule expression : module.getModules()) {
                        resultSet.addElement(LookupElementBuilder.
                                create(expression).
                                withIcon(PsiIconUtil.getProvidersIcon(expression, 0))
                        );
                    }
                }
            }
        }

    }
}
