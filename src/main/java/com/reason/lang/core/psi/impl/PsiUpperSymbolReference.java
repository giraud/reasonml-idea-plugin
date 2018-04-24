package com.reason.lang.core.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.lang.MlTypes;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.ocaml.OclModulePathFinder;
import com.reason.lang.reason.RmlModulePathFinder;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;
import static com.reason.lang.core.MlScope.all;

public class PsiUpperSymbolReference extends PsiReferenceBase<PsiUpperSymbol> {

    @Nullable
    private final String m_referenceName;
    @NotNull
    private final MlTypes m_types;

    PsiUpperSymbolReference(@NotNull PsiUpperSymbol element, @NotNull MlTypes types) {
        super(element, PsiUtil.getTextRangeForReference(element));
        m_referenceName = element.getName();
        m_types = types;
    }

    @Override
    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = RmlElementFactory.createModuleName(myElement.getProject(), newName);
        ASTNode newNameNode = newNameIdentifier == null ? null : newNameIdentifier.getFirstChild().getNode();

        if (newNameNode != null) {
            PsiElement nameIdentifier = myElement.getNameIdentifier();
            if (nameIdentifier == null) {
                myElement.getNode().addChild(newNameNode);
            } else {
                ASTNode oldNameNode = nameIdentifier.getNode();
                myElement.getNode().replaceChild(oldNameNode, newNameNode);
            }
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

        ModulePathFinder modulePathFinder = m_types instanceof RmlTypes ? new RmlModulePathFinder() : new OclModulePathFinder();
        //System.out.println("  potential paths:");
        //for (String string : strings) {
        //    System.out.println("    " + string + "." + m_referenceName);
        //}

        Project project = myElement.getProject();
        Collection<PsiModule> modules = PsiFinder.getInstance().findModules(project, m_referenceName, interfaceOrImplementation, all);

        //System.out.println("  modules: " + modules.size());
        //for (PsiModule module : modules) {
        //    System.out.println("    " + module.getContainingFile().getVirtualFile().getCanonicalPath() + " " + module.getQualifiedName());
        //}

        if (!modules.isEmpty()) {
            Collection<PsiModule> filteredModules = modules;
            if (1 < modules.size()) {
                // Find potential paths of current element
                List<String> potentialPaths = modulePathFinder.extractPotentialPaths(myElement);

                if (!potentialPaths.isEmpty()) {
                    // Take the first for now
                    final String inPath = potentialPaths.get(0) + "." + m_referenceName;
                    filteredModules = modules.stream().
                            filter(module -> inPath.equals(module.getQualifiedName())).
                            collect(Collectors.toList());
                }

                //System.out.println("  filetered modules: " + filteredModules.size());
                //for (PsiModule module : filteredModules) {
                //    System.out.println("    " + module.getContainingFile().getVirtualFile().getCanonicalPath() + " " + module.getQualifiedName());
                //}
            }

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
