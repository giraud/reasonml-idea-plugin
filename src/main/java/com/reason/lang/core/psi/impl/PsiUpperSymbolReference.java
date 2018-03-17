package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.MlTypes;
import com.reason.lang.core.RmlPsiUtil;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiUpperSymbol;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;
import static com.reason.lang.core.MlScope.all;

public class PsiUpperSymbolReference extends PsiReferenceBase<PsiUpperSymbol> {

    @Nullable
    private final String m_referenceName;
    @NotNull
    private final MlTypes m_types;

    PsiUpperSymbolReference(@NotNull PsiUpperSymbol element, @NotNull MlTypes types) {
        super(element, RmlPsiUtil.getTextRangeForReference(element));
        m_referenceName = element.getName();
        m_types = types;
    }

    @Override
    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = RmlElementFactory.createModuleName(myElement.getProject(), newName);
        ASTNode newNameNode = newNameIdentifier.getFirstChild().getNode();

        PsiElement nameIdentifier = myElement.getNameIdentifier();
        if (nameIdentifier == null) {
            myElement.getNode().addChild(newNameNode);
        } else {
            ASTNode oldNameNode = nameIdentifier.getNode();
            myElement.getNode().replaceChild(oldNameNode, newNameNode);
        }

        return myElement;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        if (m_referenceName == null) {
            return null;
        }

        PsiElement parent = PsiTreeUtil.getParentOfType(myElement, PsiModule.class);

        // If name is used in a module definition, it's already the reference
        // module <ReferenceName> = ...
        if (parent != null && ((PsiModule) parent).getNameIdentifier() == myElement) {
            return myElement;
        }

        Project project = myElement.getProject();
        Collection<PsiModule> modules = RmlPsiUtil.findModules(project, m_referenceName, interfaceOrImplementation, all);

        if (!modules.isEmpty()) {
            Collection<PsiModule> filteredModules = modules;
            //if (1 < modules.size()) {
            // TODO: find modulePath of current element
            //filteredModules = modules.stream().
            //filter(psiModule -> inPath != psiModule instanceof PsiFileModuleImpl).
            //collect(Collectors.toList());
            //}

            if (filteredModules.isEmpty()) {
                return null;
            }

            PsiModule moduleReference = filteredModules.iterator().next();
            return moduleReference.getNameIdentifier();
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
