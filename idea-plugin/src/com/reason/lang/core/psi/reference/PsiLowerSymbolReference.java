package com.reason.lang.core.psi.reference;

import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.Log;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.QNameFinder;
import com.reason.lang.core.ORCodeFactory;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.ExpressionScope;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiLowerSymbol;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiQualifiedElement;
import com.reason.lang.core.psi.PsiRecordField;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVal;
import com.reason.lang.core.psi.impl.PsiLowerIdentifier;
import com.reason.lang.core.type.ORTypes;

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;
import static java.util.stream.Collectors.*;

public class PsiLowerSymbolReference extends PsiPolyVariantReferenceBase<PsiLowerSymbol> {

    private final Log LOG = Log.create("ref.lower");

    @Nullable
    private final String m_referenceName;

    public PsiLowerSymbolReference(@NotNull PsiLowerSymbol element, @NotNull ORTypes _types) {
        super(element, TextRange.create(0, element.getTextLength()));
        m_referenceName = element.getText();
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        if (m_referenceName == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        // If name is used in a definition, it's a declaration not a usage: ie, it's not a reference
        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_references.html
        PsiLowerIdentifier parent = PsiTreeUtil.getParentOfType(myElement, PsiLowerIdentifier.class);
        if (parent != null && parent.getNameIdentifier() == myElement) {
            return null;
        }

        PsiFinder psiFinder = PsiFinder.getInstance(myElement.getProject());

        PsiElement result = null;
        int resultPosition = Integer.MAX_VALUE;

        // Find potential paths of current element
        OrderedPaths potentialPaths = getPotentialPaths();
        LOG.debug("  potential paths", potentialPaths.getValues());

        // Try to find val items

        Collection<PsiVal> vals = psiFinder.findVals(m_referenceName, interfaceOrImplementation);
        LOG.debug("  vals", vals);

        if (!vals.isEmpty()) {
            // Filter the vals, keep the ones with the same qualified name
            List<PsiVal> filteredVals = vals.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            LOG.debug("  filtered vals", filteredVals);

            for (PsiVal valResult : filteredVals) {
                Integer valPosition = potentialPaths.getPosition(valResult.getQualifiedName());
                if (valPosition != null && valPosition < resultPosition) {
                    result = valResult;
                    resultPosition = valPosition;
                    LOG.debug("  Found intermediate result", valResult, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", valResult, valPosition == null ? -1 : valPosition);
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
                        letPosition = potentialPaths.getPosition(qname);
                        if (letPosition != null) {
                            letIdentifier = deconstructedElement;
                            break;
                        }
                    }
                } else {
                    letIdentifier = letResult;
                    letPosition = potentialPaths.getPosition(letResult.getQualifiedName());
                }

                if (letPosition != null && letPosition < resultPosition) {
                    result = letIdentifier;
                    resultPosition = letPosition;
                    LOG.debug("  Found intermediate result", letResult, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", letResult, letPosition == null ? -1 : letPosition);
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
                Integer parameterPosition = potentialPaths.getPosition(parameter.getQualifiedName());
                if (parameterPosition != null && parameterPosition < resultPosition) {
                    result = parameter;
                    resultPosition = parameterPosition;
                    LOG.debug("  Found intermediate result", parameter, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", parameter, parameterPosition == null ? -1 : parameterPosition);
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
                Integer externalPosition = potentialPaths.getPosition(externalResult.getQualifiedName());
                if (externalPosition != null && externalPosition < resultPosition) {
                    result = externalResult;
                    resultPosition = externalPosition;
                    LOG.debug("  Found intermediate result", externalResult, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", externalResult, externalPosition == null ? -1 : externalPosition);
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
                Integer typePosition = potentialPaths.getPosition(typeResult.getQualifiedName());
                if (typePosition != null && typePosition < resultPosition) {
                    result = typeResult;
                    resultPosition = typePosition;
                    LOG.debug("  Found intermediate result", typeResult, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", typeResult, typePosition == null ? -1 : typePosition);
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
                return m_referenceName.equals(qp) || potentialPaths.contains(qp);
            }).collect(toList());
            LOG.debug("  filtered fields", filteredFields);

            for (PsiRecordField fieldResult : filteredFields) {
                Integer fieldPosition = potentialPaths.getPosition(fieldResult.getPath());
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

        // If module and inclusion is used, try to resolve included expressions from module
        List<PsiQualifiedElement> resolvedElements = potentialPaths.getResolvedElements();
        for (int i = 0; i < resolvedElements.size(); i++) {
            PsiQualifiedElement resolvedElement = resolvedElements.get(i);
            if (i < resultPosition && resolvedElement instanceof PsiModule) {
                Collection<PsiNamedElement> expressions = ((PsiModule) resolvedElement)
                        .getExpressions(ExpressionScope.pub, element -> m_referenceName.equals(element.getName()));
                if (!expressions.isEmpty()) {
                    result = expressions.iterator().next();
                    resultPosition = i;
                }
            }
        }

        if (result != null && LOG.isDebugEnabled()) {
            LOG.debug("-> " + result + " (identifier=" + (result instanceof PsiNameIdentifierOwner ? ((PsiNameIdentifierOwner) result).getNameIdentifier() :
                    result) + ")");
        }

        PsiElement referencedElement = result == null ? new ORFakeResolvedElement(getElement()) : result;
        ResolveResult[] resolveResults = new ResolveResult[]{new LowerResolveResult(referencedElement)};
        return resolveResults;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        if (resolveResults.length > 1) {
            LOG.debug("Can't resolve element because too many results", resolveResults);
        }
        return resolveResults.length >= 1 ? resolveResults[0].getElement() : null;
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

    @NotNull
    private Predicate<? super PsiQualifiedElement> getPathPredicate(@NotNull OrderedPaths paths) {
        return element -> {
            if (element instanceof PsiLet && ((PsiLet) element).isDeconsruction()) {
                for (PsiElement deconstructedElement : ((PsiLet) element).getDeconstructedElements()) {
                    String qn = element.getPath() + "." + deconstructedElement.getText();
                    if ((m_referenceName != null && m_referenceName.equals(qn)) || paths.contains(qn)) {
                        return true;
                    }
                }
                return false;
            }

            String qn = element.getQualifiedName();
            return (m_referenceName != null && m_referenceName.equals(qn)) || paths.contains(qn);
        };
    }

    @NotNull
    private OrderedPaths getPotentialPaths() {
        QNameFinder qnameFinder = PsiFinder.getQNameFinder(myElement.getLanguage());
        GlobalSearchScope scope = GlobalSearchScope.allScope(myElement.getProject()); // in api

        PsiFinder psiFinder = PsiFinder.getInstance(myElement.getProject());

        OrderedPaths result = new OrderedPaths();

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

        for (PsiQualifiedElement element : resolvedPaths) {
            if (element != null && m_referenceName != null) {
                result.add(element, m_referenceName);
            }
        }

        return result;
    }

    static class OrderedPaths {
        final List<PsiQualifiedElement> m_elements = new ArrayList<>();
        final List<String> m_paths = new ArrayList<>();
        final Map<String, Integer> m_elementIndices = new HashMap<>();
        final Map<String, Integer> m_pathIndices = new HashMap<>();

        void add(@NotNull PsiQualifiedElement element, @NotNull String name) {
            String value = element.getQualifiedName() + (element instanceof PsiParameter ? "" : "." + name);

            m_elements.add(element);
            m_elementIndices.put(value, m_elements.size() - 1);
            m_paths.add(value);
            m_pathIndices.put(value, m_paths.size() - 1);
        }

        @NotNull
        public List<PsiQualifiedElement> getResolvedElements() {
            return m_elements;
        }

        @NotNull
        public List<String> getValues() {
            return m_paths;
        }

        public boolean contains(@Nullable String value) {
            return value != null && m_pathIndices.containsKey(value);
        }

        @Nullable
        public Integer getPosition(@NotNull String value) {
            return m_pathIndices.get(value);
        }
    }

    private static class LowerResolveResult implements ResolveResult {
        private final PsiElement m_referencedIdentifier;

        public LowerResolveResult(PsiElement referencedElement) {
            PsiLowerIdentifier identifier = ORUtil.findImmediateFirstChildOfClass(referencedElement, PsiLowerIdentifier.class);
            m_referencedIdentifier = identifier == null ? referencedElement : identifier;
        }

        @Nullable
        @Override
        public PsiElement getElement() {
            return m_referencedIdentifier;
        }

        @Override
        public boolean isValidResult() {
            return true;
        }
    }
}
