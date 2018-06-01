package com.reason.ide.insight.provider;

import java.io.*;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.Joiner;
import com.reason.Platform;
import com.reason.ide.Debug;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.MlScope;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiSignatureUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiNamedElement;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;

public class FreeExpressionCompletionProvider extends CompletionProvider<CompletionParameters> {

    private final Debug m_debug;
    private final ModulePathFinder m_modulePathFinder;

    public FreeExpressionCompletionProvider(ModulePathFinder modulePathFinder) {
        m_modulePathFinder = modulePathFinder;
        m_debug = new Debug(Logger.getInstance("ReasonML.insight.free"));
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        m_debug.debug("FREE expression completion");

        PsiFinder psiFinder = PsiFinder.getInstance();
        Project project = parameters.getOriginalFile().getProject();
        PsiElement cursorElement = parameters.getPosition();

        // Add file modules
        Collection<PsiModule> fileModules = psiFinder.findFileModules(project, interfaceOrImplementation);
        for (PsiModule module : fileModules) {
            if (!module.isComponent()) {
                resultSet.addElement(LookupElementBuilder.create(module).
                        withTypeText(
                                Platform.removeProjectDir(project, module.getContainingFile().getVirtualFile()).replace("node_modules" + File.separator, "")).
                        withIcon(PsiIconUtil.getProvidersIcon(module, 0)));
            }
        }

        // Add paths (opens and local opens for ex)
        List<String> paths = m_modulePathFinder.extractPotentialPaths(cursorElement);
        for (String path : paths) {
            PsiModule fileModule = psiFinder.findFileModule(project, path);
            if (fileModule != null) {
                for (PsiNamedElement expression : fileModule.getExpressions()) {
                    resultSet.addElement(LookupElementBuilder.create(expression).
                            withTypeText(PsiSignatureUtil.getProvidersType(expression)).
                            withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
                }
            }
        }

        // Add all local expressions (let and module name)
        PsiElement item = cursorElement.getPrevSibling();
        while (item != null) {
            if (item instanceof PsiModule) {
                if (!(item instanceof PsiFileModuleImpl)) {
                    resultSet.addElement(LookupElementBuilder.create(item).
                            withTypeText(PsiSignatureUtil.getProvidersType((PsiModule) item)).
                            withIcon(PsiIconUtil.getProvidersIcon(item, 0)));
                }
            } else if (item instanceof PsiLet) {
                PsiLet let = (PsiLet) item;
                resultSet.addElement(LookupElementBuilder.create(let).
                        withTypeText(PsiSignatureUtil.getProvidersType(let)).
                        withIcon(PsiIconUtil.getProvidersIcon(let, 0)));
            }

            PsiElement prevItem = item.getPrevSibling();
            if (prevItem == null) {
                PsiElement parent = item.getParent();
                item = parent instanceof PsiModule ? parent.getPrevSibling() : parent;
            } else {
                item = prevItem;
            }
        }

        // Add pervasives expressions
        PsiModule pervasives = psiFinder.findModule(project, "Pervasives", interfaceOrImplementation, MlScope.all);
        if (pervasives != null) {
            for (PsiNamedElement expression : pervasives.getExpressions()) {
                resultSet.addElement(LookupElementBuilder.create(expression).
                        withTypeText(PsiSignatureUtil.getProvidersType(expression)).
                        withIcon(PsiIconUtil.getProvidersIcon(expression, 0)));
            }
        }
    }
}
