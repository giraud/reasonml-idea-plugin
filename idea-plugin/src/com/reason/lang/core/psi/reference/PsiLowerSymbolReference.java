package com.reason.lang.core.psi.reference;

import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import com.reason.Log;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.QNameFinder;
import com.reason.lang.core.ORCodeFactory;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiQualifiedElement;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiTypeConstrName;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclQNameFinder;
import com.reason.lang.reason.RmlQNameFinder;
import com.reason.lang.reason.RmlTypes;
import gnu.trove.THashMap;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;
import static java.util.stream.Collectors.*;

public class PsiLowerSymbolReference extends PsiReferenceBase<PsiLowerSymbol> {

    private final Log LOG = Log.create("ref.lower");

    @Nullable
    private final String m_referenceName;
    @NotNull
    private final ORTypes m_types;

    public PsiLowerSymbolReference(@NotNull PsiLowerSymbol element, @NotNull ORTypes types) {
        super(element, ORUtil.getTextRangeForReference(element));
        m_referenceName = element.getName();
        m_types = types;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = ORCodeFactory.createLetName(myElement.getProject(), newName);

        ASTNode newNameNode = newNameIdentifier == null ? null : newNameIdentifier.getFirstChild().getNode();
        if (newNameNode != null) {
            PsiElement nameIdentifier = myElement.getFirstChild();
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

        PsiElement parent = myElement.getParent();
        if (parent instanceof PsiTypeConstrName) {
            parent = parent.getParent();
        }
        PsiNameIdentifierOwner namedParent = parent instanceof PsiNameIdentifierOwner ? (PsiNameIdentifierOwner) parent : null;

        // If name is used in a definition, it's a declaration not a usage: ie, it's not a reference
        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_references.html
        if (namedParent != null && namedParent.getNameIdentifier() == myElement) {
            return null;
        }

        PsiFinder psiFinder = PsiFinder.getInstance(myElement.getProject());

        PsiElement result = null;
        int resultPosition = Integer.MAX_VALUE;

        // Find potential paths of current element
        Map<String, Integer> potentialPaths = getPotentialPaths();
        LOG.debug("  potential paths", potentialPaths);

        // Try to find val items

        Collection<PsiVal> vals = psiFinder.findVals(m_referenceName, interfaceOrImplementation);
        LOG.debug("  vals", vals);

        if (!vals.isEmpty()) {
            // Filter the vals, keep the ones with the same qualified name
            List<PsiVal> filteredVals = vals.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            LOG.debug("  filtered vals", filteredVals);

            for (PsiVal valResult : filteredVals) {
                Integer valPosition = potentialPaths.get(valResult.getQualifiedName());
                if (valPosition < resultPosition) {
                    result = valResult;
                    resultPosition = valPosition;
                    LOG.debug("  Found intermediate result", valResult, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", valResult, valPosition);
                }
            }
        }

        // Try to find let items

        Collection<PsiLet> lets = psiFinder.findLets(m_referenceName, interfaceOrImplementation);
        LOG.debug("  lets", lets);

        if (!lets.isEmpty()) {
            // Filter the lets, keep the ones with the same qualified name
            List<PsiLet> filteredLets = lets.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            LOG.debug("  filtered lets", filteredLets);

            for (PsiLet letResult : filteredLets) {
                PsiElement letIdentifier = null;
                Integer letPosition = null;
                if (letResult.isDeconsruction()) {
                    for (PsiElement deconstructedElement : letResult.getDeconstructedElements()) {
                        String qname = letResult.getPath() + "." + deconstructedElement.getText();
                        letPosition = potentialPaths.get(qname);
                        if (letPosition != null) {
                            letIdentifier = deconstructedElement;
                            break;
                        }
                    }
                } else {
                    letIdentifier = letResult;
                    letPosition = potentialPaths.get(letResult.getQualifiedName());
                }

                if (letPosition != null && letPosition < resultPosition) {
                    result = letIdentifier;
                    resultPosition = letPosition;
                    LOG.debug("  Found intermediate result", letResult, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", letResult, letPosition);
                }
            }
        }

        // Try to find parameter items

        Collection<PsiParameter> parameters = psiFinder.findParameters(m_referenceName, interfaceOrImplementation);
        LOG.debug("  parameters", parameters);

        if (!parameters.isEmpty()) {
            // Filter the parameters, keep the ones with the same qualified name
            List<PsiParameter> filteredParameters = parameters.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            LOG.debug("  filtered parameters", filteredParameters);

            for (PsiParameter parameter : filteredParameters) {
                Integer parameterPosition = potentialPaths.get(parameter.getQualifiedName());
                if (parameterPosition < resultPosition) {
                    result = parameter;
                    resultPosition = parameterPosition;
                    LOG.debug("  Found intermediate result", parameter, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", parameter, parameterPosition);
                }
            }
        }

        // Try to find external items

        Collection<PsiExternal> externals = psiFinder.findExternals(m_referenceName, interfaceOrImplementation);
        LOG.debug("  externals", externals);

        if (!externals.isEmpty()) {
            // Filter the externals, keep the ones with the same qualified name
            List<PsiExternal> filteredExternals = externals.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            LOG.debug("  filtered externals", filteredExternals);

            for (PsiExternal externalResult : filteredExternals) {
                Integer externalPosition = potentialPaths.get(externalResult.getQualifiedName());
                if (externalPosition < resultPosition) {
                    result = externalResult;
                    resultPosition = externalPosition;
                    LOG.debug("  Found intermediate result", externalResult, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", externalResult, externalPosition);
                }
            }
        }

        // Try to find type items

        Collection<PsiType> types = psiFinder.findTypes(m_referenceName, interfaceOrImplementation);
        LOG.debug("  types", types);

        if (!types.isEmpty()) {
            // Filter the types, keep the ones with the same qualified name
            List<PsiType> filteredTypes = types.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            LOG.debug("  filtered types", filteredTypes);

            for (PsiType typeResult : filteredTypes) {
                Integer typePosition = potentialPaths.get(typeResult.getQualifiedName());
                if (typePosition < resultPosition) {
                    result = typeResult;
                    resultPosition = typePosition;
                    LOG.debug("  Found intermediate result", typeResult, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", typeResult, typePosition);
                }
            }
        }

        // Try to find type fields !

        Collection<PsiRecordField> recordFields = psiFinder.findRecordFields(m_referenceName, interfaceOrImplementation);
        LOG.debug("  record fields", types);

        if (!recordFields.isEmpty()) {
            // Filter the fields, keep the ones with the same qualified name
            List<PsiRecordField> filteredFields = recordFields.stream().filter(element -> {
                String qp = element.getPath();
                return m_referenceName.equals(qp) || potentialPaths.containsKey(qp);
            }).collect(toList());
            LOG.debug("  filtered fields", filteredFields);

            for (PsiRecordField fieldResult : filteredFields) {
                Integer fieldPosition = potentialPaths.get(fieldResult.getPath());
                if (fieldPosition != null) {
                    if (fieldPosition < resultPosition) {
                        result = fieldResult;
                        resultPosition = fieldPosition;
                        LOG.debug("  Found intermediate result", fieldResult, resultPosition);
                    } else {
                        LOG.debug("  skip intermediate result", fieldResult, fieldPosition);
                    }
                }
            }
        }

        if (result != null && LOG.isDebugEnabled()) {
            LOG.debug("-> " + result + " (identifier=" + (result instanceof PsiNameIdentifierOwner ? ((PsiNameIdentifierOwner) result).getNameIdentifier() :
                    result) + ")");
        }

        return result == null ? new ORFakeResolvedElement(getElement()) :
                result instanceof PsiNameIdentifierOwner ? ((PsiNameIdentifierOwner) result).getNameIdentifier() : result;
    }

    @NotNull
    private Predicate<? super PsiQualifiedElement> getPathPredicate(@NotNull Map<String, Integer> potentialPaths) {
        return element -> {
            if (element instanceof PsiLet && ((PsiLet) element).isDeconsruction()) {
                for (PsiElement deconstructedElement : ((PsiLet) element).getDeconstructedElements()) {
                    String qn = element.getPath() + "." + deconstructedElement.getText();
                    if ((m_referenceName != null && m_referenceName.equals(qn)) || potentialPaths.containsKey(qn)) {
                        return true;
                    }
                }
                return false;
            }

            String qn = element.getQualifiedName();
            return (m_referenceName != null && m_referenceName.equals(qn)) || potentialPaths.containsKey(qn);
        };
    }

    @NotNull
    private Map<String, Integer> getPotentialPaths() {
        QNameFinder qnameFinder = m_types instanceof RmlTypes ? new RmlQNameFinder() : new OclQNameFinder();
        GlobalSearchScope scope = GlobalSearchScope.allScope(myElement.getProject()); // in api

        PsiFinder psiFinder = PsiFinder.getInstance(myElement.getProject());

        Map<String, Integer> result = new THashMap<>();

        List<PsiQualifiedElement> resolvedPaths = new ArrayList<>();
        Set<String> potentialPaths = qnameFinder.extractPotentialPaths(myElement);
        for (String pathName : potentialPaths) {
            Set<PsiModule> moduleAlias = psiFinder.findModuleAlias(pathName, scope);
            if (moduleAlias.isEmpty()) {
                Set<PsiModule> modulesFromQn = psiFinder.findModulesFromQn(pathName, true, interfaceOrImplementation, scope);
                if (modulesFromQn.isEmpty()) {
                    // Not a module but maybe a let or a parameter
                    PsiLet let = psiFinder.findLetFromQn(pathName);
                    resolvedPaths.add(let == null ? psiFinder.findParamFromQn(pathName) : let);
                } else {
                    resolvedPaths.addAll(modulesFromQn);
                }
            } else {
                resolvedPaths.addAll(moduleAlias);
            }
        }

        Integer position = 0;
        for (PsiQualifiedElement element : resolvedPaths) {
            if (element != null) {
                result.put(element.getQualifiedName() + (element instanceof PsiParameter ? "" : "." + m_referenceName), position);
                position++;
            }
        }

        return result;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
