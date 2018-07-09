package com.reason.lang.core.psi.reference;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
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
import java.util.function.Predicate;

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;
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
            System.out.println("resolving '" + m_referenceName + "'");
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
        List<String> potentialPaths = null;
        Project project = myElement.getProject();
        PsiFinder psiFinder = PsiFinder.getInstance();

        // Try to find let items

        Collection<PsiLet> lets = psiFinder.findLets(project, m_referenceName, interfaceOrImplementation);
        if (m_debug) {
            System.out.println("  lets: " + lets.size());
            for (PsiQualifiedNamedElement element : lets) {
                System.out.println("    " + element.getContainingFile().getVirtualFile().getCanonicalPath() + " " + element.getQualifiedName());
            }
        }

        if (!lets.isEmpty()) {
            // Filter the modules, keep the ones with the same qualified name
            potentialPaths = getPotentialPaths(modulePathFinder, project);
            Collection<PsiLet> filteredLets = lets.stream().filter(getPathPredicate(potentialPaths)).collect(toList());

            if (m_debug) {
                System.out.println("  filtered lets: " + filteredLets.size());
                for (PsiQualifiedNamedElement element : filteredLets) {
                    System.out.println("    " + element.getContainingFile().getVirtualFile().getCanonicalPath() + " " + element.getQualifiedName());
                }
            }

            if (!filteredLets.isEmpty()) {
                return extractNameIdentifier(filteredLets);
            }
        }

        // Try to find val items

        Collection<PsiVal> vals = psiFinder.findVals(project, m_referenceName, interfaceOrImplementation);
        if (m_debug) {
            System.out.println("  vals: " + vals.size());
            for (PsiQualifiedNamedElement element : vals) {
                System.out.println("    " + element.getContainingFile().getVirtualFile().getCanonicalPath() + " " + element.getQualifiedName());
            }
        }

        if (!vals.isEmpty()) {
            // Filter the modules, keep the ones with the same qualified name
            if (potentialPaths == null) {
                potentialPaths = getPotentialPaths(modulePathFinder, project);
            }
            Collection<PsiVal> filteredVals = vals.stream().filter(getPathPredicate(potentialPaths)).collect(toList());

            if (m_debug) {
                System.out.println("  filtered vals: " + filteredVals.size());
                for (PsiQualifiedNamedElement element : filteredVals) {
                    System.out.println("    " + element.getContainingFile().getVirtualFile().getCanonicalPath() + " " + element.getQualifiedName());
                }
            }

            if (!filteredVals.isEmpty()) {
                return extractNameIdentifier(filteredVals);
            }
        }

        // Try to find external items

        Collection<PsiExternal> externals = psiFinder.findExternals(project, m_referenceName, interfaceOrImplementation);
        if (m_debug) {
            System.out.println("  externals: " + externals.size());
            for (PsiQualifiedNamedElement element : externals) {
                System.out.println("    " + element.getContainingFile().getVirtualFile().getCanonicalPath() + " " + element.getQualifiedName());
            }
        }

        if (!externals.isEmpty()) {
            // Filter the modules, keep the ones with the same qualified name
            if (potentialPaths == null) {
                potentialPaths = getPotentialPaths(modulePathFinder, project);
            }
            Collection<PsiExternal> filteredExternals = externals.stream().filter(getPathPredicate(potentialPaths)).collect(toList());

            if (m_debug) {
                System.out.println("  filtered externals: " + filteredExternals.size());
                for (PsiQualifiedNamedElement element : filteredExternals) {
                    System.out.println("    " + element.getContainingFile().getVirtualFile().getCanonicalPath() + " " + element.getQualifiedName());
                }
            }

            if (!filteredExternals.isEmpty()) {
                return extractNameIdentifier(filteredExternals);
            }
        }

        // Try to find type items

        Collection<PsiType> types = psiFinder.findTypes(project, m_referenceName, interfaceOrImplementation);
        if (m_debug) {
            System.out.println("  types: " + types.size());
            for (PsiQualifiedNamedElement element : types) {
                System.out.println("    " + element.getContainingFile().getVirtualFile().getCanonicalPath() + " " + element.getQualifiedName());
            }
        }

        if (!types.isEmpty()) {
            // Filter the modules, keep the ones with the same qualified name
            if (potentialPaths == null) {
                potentialPaths = getPotentialPaths(modulePathFinder, project);
            }
            Collection<PsiType> filteredTypes = types.stream().filter(getPathPredicate(potentialPaths)).collect(toList());

            if (m_debug) {
                System.out.println("  filtered types: " + filteredTypes.size());
                for (PsiQualifiedNamedElement element : filteredTypes) {
                    System.out.println("    " + element.getContainingFile().getVirtualFile().getCanonicalPath() + " " + element.getQualifiedName());
                }
            }

            if (!filteredTypes.isEmpty()) {
                return extractNameIdentifier(filteredTypes);
            }
        }

        return null;
    }

    @NotNull
    private Predicate<? super PsiQualifiedNamedElement> getPathPredicate(List<String> potentialPaths) {
        return element -> {
            String qn = element.getQualifiedName();
            return (m_referenceName != null && m_referenceName.equals(qn)) || potentialPaths.contains(qn);
        };
    }

    private List<String> getPotentialPaths(ModulePathFinder modulePathFinder, Project project) {
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
        return potentialPaths;
    }

    @Nullable
    private PsiElement extractNameIdentifier(@NotNull Iterable<? extends PsiNameIdentifierOwner> filteredElements) {
        PsiNameIdentifierOwner elementReference = filteredElements.iterator().next();
        if (m_debug) {
            System.out.println("»» " + elementReference + " " + elementReference.getNameIdentifier());
        }
        return elementReference.getNameIdentifier();

    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }

}
