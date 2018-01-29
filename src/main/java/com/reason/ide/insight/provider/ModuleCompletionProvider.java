package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StubIndex;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.ProcessingContext;
import com.intellij.util.PsiIconUtil;
import com.reason.Platform;
import com.reason.icons.Icons;
import com.reason.ide.files.OclFile;
import com.reason.ide.search.IndexKeys;
import com.reason.lang.MlTypes;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiModuleName;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ModuleCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final MlTypes m_types;

    public ModuleCompletionProvider(MlTypes types) {
        m_types = types;
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet resultSet) {
        //System.out.println("»» Module completion");
        Project project = parameters.getOriginalFile().getProject();
        PsiElement originalPosition = parameters.getOriginalPosition();

        PsiElement cursorElement = originalPosition;

        // from VALUE_NAME to PsiName
        if (originalPosition != null && originalPosition.getNode().getElementType() == m_types.VALUE_NAME) {
            cursorElement = originalPosition.getParent();
        }

        // Compute qname (ModulePath of ModuleName ?)
        String qname = "";
        List<PsiModuleName> path = new ArrayList<>();
        PsiElement previousSibling = cursorElement == null ? null : cursorElement.getPrevSibling();
        if (previousSibling != null) {
            IElementType previousElementType = previousSibling.getNode().getElementType();
            while (previousElementType == m_types.DOT || previousElementType == m_types.MODULE_NAME) {
                if (previousElementType != m_types.DOT) {
                    qname = previousSibling.getText() + (qname.isEmpty() ? "" : "." + qname);
                }
                if (previousSibling instanceof PsiModuleName) {
                    path.add((PsiModuleName) previousSibling);
                }
                previousSibling = previousSibling.getPrevSibling();
                previousElementType = previousSibling == null ? null : previousSibling.getNode().getElementType();
            }
        }
        Collections.reverse(path);

        if (path.isEmpty()) {
            List<PsiModule> modules = RmlPsiUtil.findFileModules(project);
            if (!modules.isEmpty()) {
                for (PsiModule module : modules) {
                    resultSet.addElement(
                            LookupElementBuilder.create(module).
                                    withTypeText(Platform.removeProjectDir(project, module.getContainingFile().getVirtualFile())).
                                    withIcon(module.getContainingFile() instanceof OclFile ? Icons.OCL_FILE : Icons.RML_FILE)
                    );
                }
            }
        } else {
            PsiModuleName latestModuleName = path.get(path.size() - 1);
            Collection<PsiModule> modules = StubIndex.getElements(IndexKeys.MODULES, latestModuleName.getName(), project, GlobalSearchScope.allScope(project), PsiModule.class);
            if (!modules.isEmpty()) {
                for (PsiModule module : modules) {
                    for (PsiModule expression : module.getModules()) {
                        String modulePath = expression.getPath().toString();
                        System.out.println(expression.getQualifiedName() + " ? " + modulePath);
                        if (qname.equals(modulePath)) {
                            resultSet.addElement(
                                    LookupElementBuilder.create(expression).
                                            withIcon(PsiIconUtil.getProvidersIcon(expression, 0))
                            );
                        }
                    }
                }
            }
        }

    }
}
