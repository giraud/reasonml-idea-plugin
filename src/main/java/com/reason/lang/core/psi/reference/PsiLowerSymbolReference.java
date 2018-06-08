package com.reason.lang.core.psi.reference;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.Joiner;
import com.reason.lang.MlTypes;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.*;
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

    @SuppressWarnings("FieldCanBeLocal") private final boolean m_debug = false;

    public PsiLowerSymbolReference(@NotNull PsiLowerSymbol element, @NotNull MlTypes types) {
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

        if (m_debug) {
            System.out.println("resolving " + m_referenceName);
        }

        PsiNamedElement parent = PsiTreeUtil.getParentOfType(myElement, PsiLet.class, PsiExternal.class);

        // If name is used in a let definition, it's already the reference
        // let <referenceName> = ...
        if (parent != null && parent.getNameIdentifier() == myElement) {
            if (m_debug) {
                System.out.println("  Parent " + parent + ", stop here");
            }
            return myElement;
        }

        ModulePathFinder modulePathFinder = m_types instanceof RmlTypes ? new RmlModulePathFinder() : new OclModulePathFinder();

        Project project = myElement.getProject();
        Collection<PsiQualifiedNamedElement> namedElements = PsiFinder.getInstance().findLetsOrExternals(project, m_referenceName, interfaceOrImplementation, all);

        if (m_debug) {
            System.out.println("  elements: " + namedElements.size());
            for (PsiQualifiedNamedElement element : namedElements) {
                System.out.println("    " + element.getContainingFile().getVirtualFile().getCanonicalPath() + " " + element.getQualifiedName());
            }
        }

        if (!namedElements.isEmpty()) {
            Collection<PsiQualifiedNamedElement> filteredElements = namedElements;
            if (1 < namedElements.size()) {
                // Find potential paths of current element
                List<String> potentialPaths = modulePathFinder.extractPotentialPaths(myElement).
                        stream().
                        map(item -> {
                            PsiModule moduleAlias = PsiFinder.getInstance().findModuleAlias(project, item);
                            String qn = (moduleAlias == null) ? item : moduleAlias.getQualifiedName();
                            return qn + "." + m_referenceName;
                        }).
                        collect(toList());
                if (m_debug) {
                    System.out.println("  potential paths: [" + Joiner.join(", ", potentialPaths) + "]");
                }

                // Filter the modules, keep the ones with the same qualified name
                filteredElements = namedElements.stream().
                        filter(element -> {
                            String qn = element.getQualifiedName();
                            return m_referenceName.equals(qn) || potentialPaths.contains(qn);
                        }).
                        collect(toList());

                if (m_debug) {
                    System.out.println("  filtered elements: " + filteredElements.size());
                    for (PsiQualifiedNamedElement element : filteredElements) {
                        System.out.println("    " + element.getContainingFile().getVirtualFile().getCanonicalPath() + " " + element.getQualifiedName());
                    }
                }
            }

            if (filteredElements.isEmpty()) {
                return null;
            }

            PsiNamedElement elementReference = (PsiNamedElement) filteredElements.iterator().next();
            if (m_debug) {
                System.out.println("»» " + elementReference.getName());
            }
            return elementReference.getNameIdentifier();
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }

}
