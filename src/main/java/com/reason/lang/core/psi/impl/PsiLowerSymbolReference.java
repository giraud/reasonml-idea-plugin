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

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;
import static com.reason.lang.core.MlScope.all;
import static java.util.stream.Collectors.toList;

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

        Project project = myElement.getProject();
        Collection<PsiLet> lets = PsiFinder.getInstance().findLets(project, m_referenceName, interfaceOrImplementation, all);

        //System.out.println("  lets: " + lets.size());
        //for (PsiLet let : lets) {
        //    System.out.println("    " + let.getContainingFile().getVirtualFile().getCanonicalPath() + " " + let.getQualifiedName());
        //}

        if (!lets.isEmpty()) {
            Collection<PsiLet> filteredLets = lets;
            if (1 < lets.size()) {
                // Find potential paths of current element
                List<String> potentialPaths = modulePathFinder.extractPotentialPaths(myElement).stream().map(item -> item + "." + m_referenceName).collect(toList());
                //System.out.println("  potential paths: [" + Joiner.join(", ", potentialPaths) + "]");

                // Filter the modules, keep the ones with the same qualified name
                filteredLets = lets.stream().
                        filter(module -> {
                            String moduleQn = module.getQualifiedName();
                            return m_referenceName.equals(moduleQn) || potentialPaths.contains(moduleQn);
                        }).
                        collect(toList());

                //System.out.println("  filtered lets: " + filteredLets.size());
                //for (PsiLet module : filteredLets) {
                //    System.out.println("    " + module.getContainingFile().getVirtualFile().getCanonicalPath() + " " + module.getQualifiedName());
                //}
            }

            if (filteredLets.isEmpty()) {
                return null;
            }

            PsiLet letReference = filteredLets.iterator().next();
            //System.out.println("»» " + letReference.getName());
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
