package com.reason.lang.core.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.lang.MlTypes;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.ocaml.OclModulePathFinder;
import com.reason.lang.reason.RmlModulePathFinder;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;
import static com.reason.lang.core.MlScope.inBsconfig;

public class PsiLowerSymbolReference extends PsiReferenceBase<PsiLowerSymbol> {

    @Nullable
    private final String m_referenceName;
    @NotNull
    private final MlTypes m_types;

    PsiLowerSymbolReference(@NotNull PsiLowerSymbol element, @NotNull MlTypes types) {
        super(element, PsiUtil.getTextRangeForReference(element));
        m_referenceName = element.getName();
        m_types = types;
    }


    @Nullable
    @Override
    public PsiElement resolve() {
        if (m_referenceName == null) {
            return null;
        }

        //System.out.println("resolving " + m_referenceName);
        PsiElement parent = PsiTreeUtil.getParentOfType(myElement, PsiLet.class);

        // If name is used in a let definition, it's already the reference
        // let <referenceName> = ...
        if (parent != null && ((PsiLet) parent).getNameIdentifier() == myElement) {
            return myElement;
        }

        ModulePathFinder modulePathFinder = m_types instanceof RmlTypes ? new RmlModulePathFinder() : new OclModulePathFinder();
        //System.out.println("  potential paths:");
        //List<String> paths = modulePathFinder.extractPotentialPaths(myElement);
        //for (String string : paths) {
        //    System.out.println("    " + string + "." + m_referenceName);
        //}

        Project project = myElement.getProject();
        Collection<PsiLet> lets = PsiFinder.getInstance().findLets(project, m_referenceName, interfaceOrImplementation, inBsconfig);

        if (!lets.isEmpty()) {
            Collection<PsiLet> filteredlets = lets;
            if (1 < lets.size()) {
                // Find potential paths of current element
                List<String> potentialPaths = modulePathFinder.extractPotentialPaths(myElement);

                if (!potentialPaths.isEmpty()) {
                    // Take the first for now
                    final String inPath = potentialPaths.get(0) + "." + m_referenceName;
                    filteredlets = lets.stream().
                            filter(let -> inPath.equals(let.getQualifiedName())).
                            collect(Collectors.toList());
                }

                //System.out.println("  filtered lets: " + filteredlets.size());
                //for (PsiLet let : filteredlets) {
                //   System.out.println("    " + let.getContainingFile().getVirtualFile().getCanonicalPath() + " " + let.getQualifiedName());
                //}
            }

            if (filteredlets.isEmpty()) {
                return null;
            }

            PsiLet letReference = filteredlets.iterator().next();
            return letReference.getNameIdentifier();
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }

}
