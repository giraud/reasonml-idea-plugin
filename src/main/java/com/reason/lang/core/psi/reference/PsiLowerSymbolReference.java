package com.reason.lang.core.psi.reference;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.Debug;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.type.MlTypes;
import com.reason.lang.ocaml.OclModulePathFinder;
import com.reason.lang.reason.RmlModulePathFinder;
import com.reason.lang.reason.RmlTypes;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;
import static java.util.stream.Collectors.toList;

public class PsiLowerSymbolReference extends PsiReferenceBase<PsiLowerSymbol> {

    @Nullable
    private final String m_referenceName;
    @NotNull
    private final MlTypes m_types;

    private final Debug m_debug = new Debug(Logger.getInstance("ReasonML.ref.lower"));

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

        PsiNamedElement parent = PsiTreeUtil.getParentOfType(myElement, PsiLet.class, PsiExternal.class, PsiVal.class, PsiType.class);

        // If name is used in a definition, it's a declaration not a usage: ie, it's not a reference
        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_references.html
        if (parent != null && parent.getNameIdentifier() == myElement) {
            return null;
        }

        Project project = myElement.getProject();
        PsiFinder psiFinder = PsiFinder.getInstance();

        PsiNameIdentifierOwner result = null;
        int resultPosition = Integer.MAX_VALUE;

        // Find potential paths of current element
        Map<String, Integer> potentialPaths = getPotentialPaths();

        // Try to find let items

        Collection<PsiLet> lets = psiFinder.findLets(project, m_referenceName, interfaceOrImplementation);
        m_debug.debug("  lets", lets);

        if (!lets.isEmpty()) {
            // Filter the lets, keep the ones with the same qualified name
            List<PsiLet> filteredLets = lets.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            m_debug.debug("  filtered lets", filteredLets);

            if (!filteredLets.isEmpty()) {
                result = filteredLets.get(0);
                resultPosition = potentialPaths.get(((PsiLet) result).getQualifiedName());
                m_debug.debug("  Found intermediate result", (PsiLet) result, resultPosition);
            }
        }

        // Try to find val items

        Collection<PsiVal> vals = psiFinder.findVals(project, m_referenceName, interfaceOrImplementation);
        m_debug.debug("  vals", vals);

        if (!vals.isEmpty()) {
            // Filter the vals, keep the ones with the same qualified name
            List<PsiVal> filteredVals = vals.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            m_debug.debug("  filtered vals", filteredVals);

            if (!filteredVals.isEmpty()) {
                PsiVal valResult = filteredVals.get(0);
                Integer valPosition = potentialPaths.get(valResult.getQualifiedName());
                if (valPosition < resultPosition) {
                    result = valResult;
                    resultPosition = valPosition;
                    m_debug.debug("  Found intermediate result", valResult, resultPosition);
                } else {
                    m_debug.debug("  skip intermediate result", valResult, valPosition);
                }
            }
        }

        // Try to find external items

        Collection<PsiExternal> externals = psiFinder.findExternals(project, m_referenceName, interfaceOrImplementation);
        m_debug.debug("  externals", externals);

        if (!externals.isEmpty()) {
            // Filter the externals, keep the ones with the same qualified name
            List<PsiExternal> filteredExternals = externals.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            m_debug.debug("  filtered externals", filteredExternals);

            if (!filteredExternals.isEmpty()) {
                PsiExternal externalResult = filteredExternals.get(0);
                Integer externalPosition = potentialPaths.get(externalResult.getQualifiedName());
                if (externalPosition < resultPosition) {
                    result = externalResult;
                    resultPosition = externalPosition;
                    m_debug.debug("  Found intermediate result", externalResult, resultPosition);
                } else {
                    m_debug.debug("  skip intermediate result", externalResult, externalPosition);
                }
            }
        }

        // Try to find type items

        Collection<PsiType> types = psiFinder.findTypes(project, m_referenceName, interfaceOrImplementation);
        m_debug.debug("  types", types);

        if (!types.isEmpty()) {
            // Filter the types, keep the ones with the same qualified name
            List<PsiType> filteredTypes = types.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            m_debug.debug("  filtered types", filteredTypes);

            if (!filteredTypes.isEmpty()) {
                PsiType typeResult = filteredTypes.get(0);
                Integer typePosition = potentialPaths.get(typeResult.getQualifiedName());
                if (typePosition < resultPosition) {
                    result = typeResult;
                    m_debug.debug("  Found intermediate result", typeResult, resultPosition);
                } else {
                    m_debug.debug("  skip intermediate result", typeResult, typePosition);
                }
            }
        }

        if (result != null && m_debug.isDebugEnabled()) {
            m_debug.debug("»» " + result + " " + result.getNameIdentifier());
        }

        return result == null ? result : result.getNameIdentifier();
    }

    @NotNull
    private Predicate<? super PsiQualifiedNamedElement> getPathPredicate(@NotNull Map<String, Integer> potentialPaths) {
        return element -> {
            String qn = element.getQualifiedName();
            return (m_referenceName != null && m_referenceName.equals(qn)) || potentialPaths.containsKey(qn);
        };
    }

    @NotNull
    private Map<String, Integer> getPotentialPaths() {
        ModulePathFinder modulePathFinder = m_types instanceof RmlTypes ? new RmlModulePathFinder() : new OclModulePathFinder();

        Map<String, Integer> result = new THashMap<>();

        List<String> paths = modulePathFinder.extractPotentialPaths(myElement);
        m_debug.debug("  potential paths", paths);

        Integer position = 0;
        for (String qName : paths) {
            result.put(qName + "." + m_referenceName, position);
            position++;
        }

        return result;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }

}
