package com.reason.lang.core.psi.reference;

import java.util.*;
import java.util.function.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.Joiner;
import com.reason.Log;
import com.reason.Platform;
import com.reason.ide.files.FileHelper;
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

import static com.reason.lang.core.ORFileType.both;
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
        LOG.debug("Resolving", m_referenceName);

        List<PsiElement> result = new ArrayList<>();
        int resultPosition = Integer.MAX_VALUE;

        // Find potential paths of current element
        OrderedPaths potentialPaths = getPotentialPaths();
        LOG.debug("  potential paths", potentialPaths.getValues());

        // Try to find val items

        Collection<PsiVal> vals = psiFinder.findVals(m_referenceName, both);
        LOG.debug("  vals", vals);

        if (!vals.isEmpty()) {
            // Filter the vals, keep the ones with the same qualified name
            List<PsiVal> filteredVals = vals.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            LOG.debug("  filtered vals", filteredVals);

            for (PsiVal valResult : filteredVals) {
                int valPosition = potentialPaths.getPosition(valResult.getQualifiedName());
                if (-1 < valPosition && valPosition <= resultPosition) {
                    if (valPosition < resultPosition) {
                        result.clear();
                        resultPosition = valPosition;
                    }
                    result.add(valResult);
                    LOG.debug("  Found intermediate result", valResult, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", valResult, valPosition);
                }
            }
        }

        // Try to find let items

        Collection<PsiLet> lets = psiFinder.findLets(m_referenceName, both);
        LOG.debug("  lets", lets);

        if (!lets.isEmpty()) {
            // Filter the lets, keep the ones with the same qualified name
            List<PsiLet> filteredLets = lets.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            LOG.debug("  filtered lets", filteredLets);

            for (PsiLet letResult : filteredLets) {
                PsiElement letIdentifier = null;
                int letPosition = -1;
                if (letResult.isDeconsruction()) {
                    for (PsiElement deconstructedElement : letResult.getDeconstructedElements()) {
                        String qname = letResult.getPath() + "." + deconstructedElement.getText();
                        letPosition = potentialPaths.getPosition(qname);
                        if (letPosition != -1) {
                            letIdentifier = deconstructedElement;
                            break;
                        }
                    }
                } else {
                    letIdentifier = letResult;
                    letPosition = potentialPaths.getPosition(letResult.getQualifiedName());
                }

                if (-1 < letPosition && letPosition <= resultPosition) {
                    if (letPosition < resultPosition) {
                        result.clear();
                        resultPosition = letPosition;
                    }
                    result.add(letResult);
                    LOG.debug("  Found intermediate result", letResult, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", letResult, letPosition);
                }
            }
        }

        // Try to find parameter items

        Collection<PsiParameter> parameters = psiFinder.findParameters(m_referenceName, both);
        LOG.debug("  parameters", parameters);

        if (!parameters.isEmpty()) {
            // Filter the parameters, keep the ones with the same qualified name
            List<PsiParameter> filteredParameters = parameters.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            LOG.debug("  filtered parameters", filteredParameters);

            for (PsiParameter parameter : filteredParameters) {
                int parameterPosition = potentialPaths.getPosition(parameter.getQualifiedName());
                if (-1 < parameterPosition && parameterPosition <= resultPosition) {
                    if (parameterPosition < resultPosition) {
                        result.clear();
                        resultPosition = parameterPosition;
                    }
                    result.add(parameter);
                    LOG.debug("  Found intermediate result", parameter, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", parameter, parameterPosition);
                }
            }
        }

        // Try to find external items

        Collection<PsiExternal> externals = psiFinder.findExternals(m_referenceName, both);
        LOG.debug("  externals", externals);

        if (!externals.isEmpty()) {
            // Filter the externals, keep the ones with the same qualified name
            List<PsiExternal> filteredExternals = externals.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            LOG.debug("  filtered externals", filteredExternals);

            for (PsiQualifiedElement qualifiedElement : filteredExternals) {
                int externalPosition = potentialPaths.getPosition(qualifiedElement.getQualifiedName());
                if (-1 < externalPosition && externalPosition <= resultPosition) {
                    if (externalPosition < resultPosition) {
                        result.clear();
                        resultPosition = externalPosition;
                    }
                    result.add(qualifiedElement);
                    LOG.debug("  Found intermediate result", qualifiedElement, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", qualifiedElement, externalPosition);
                }
            }
        }

        // Try to find type items

        Collection<PsiType> types = psiFinder.findTypes(m_referenceName, both);
        LOG.debug("  types", types);

        if (!types.isEmpty()) {
            // Filter the types, keep the ones with the same qualified name
            List<PsiType> filteredTypes = types.stream().filter(getPathPredicate(potentialPaths)).collect(toList());
            LOG.debug("  filtered types", filteredTypes);

            for (PsiQualifiedElement qElement : filteredTypes) {
                int qPosition = potentialPaths.getPosition(qElement.getQualifiedName());
                if (-1 < qPosition && qPosition <= resultPosition) {
                    if (qPosition < resultPosition) {
                        result.clear();
                        resultPosition = qPosition;
                    }
                    result.add(qElement);
                    LOG.debug("  Found intermediate result", qElement, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", qElement, qPosition);
                }
            }
        }

        // Try to find type fields !

        Collection<PsiRecordField> recordFields = psiFinder.findRecordFields(m_referenceName, both);
        LOG.debug("  record fields", recordFields);

        if (!recordFields.isEmpty()) {
            // Filter the fields, keep the ones with the same qualified name
            List<PsiRecordField> filteredFields = recordFields.stream().filter(element -> {
                String qp = element.getPath();
                return m_referenceName.equals(qp) || potentialPaths.contains(qp);
            }).collect(toList());
            LOG.debug("  filtered fields", filteredFields);

            for (PsiQualifiedElement qElement : filteredFields) {
                int qPosition = potentialPaths.getPosition(qElement.getPath());
                if (-1 < qPosition && qPosition <= resultPosition) {
                    if (qPosition < resultPosition) {
                        result.clear();
                        resultPosition = qPosition;
                    }
                    result.add(qElement);
                    LOG.debug("  Found intermediate result", qElement, resultPosition);
                } else {
                    LOG.debug("  skip intermediate result", qElement, qPosition);
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
                    result.clear();
                    result.add(expressions.iterator().next());
                    resultPosition = i;
                }
            }
        }

        // return implementation if an interface file exists
        result.sort((item1, item2) -> FileHelper.isInterface(item1.getContainingFile().getFileType()) ? 1 :
                (FileHelper.isInterface(item2.getContainingFile().getFileType()) ? -1 : 0));

        if (LOG.isDebugEnabled()) {
            LOG.debug("  => found", Joiner.join(", ", result, item -> ((PsiQualifiedElement) item).getQualifiedName() + " [" + Platform
                    .removeProjectDir(item.getProject(), item.getContainingFile().getVirtualFile().getPath()) + "]"));
        }

        ResolveResult[] resolveResults = new ResolveResult[result.size()];
        int i = 0;
        for (PsiElement element : result) {
            resolveResults[i] = new LowerResolveResult(element, m_referenceName);
            i++;
        }

        return resolveResults;
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return 0 < resolveResults.length ? resolveResults[0].getElement() : null;
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
                Set<PsiModule> modulesFromQn = psiFinder.findModulesFromQn(pathName, true, both, scope);
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

            if (!m_paths.contains(value)) {
                m_paths.add(value);
                m_elements.add(element);
                m_elementIndices.put(value, m_elements.size() - 1);
                m_pathIndices.put(value, m_paths.size() - 1);
            }
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

        public int getPosition(@NotNull String value) {
            Integer pos = m_pathIndices.get(value);
            return pos == null ? -1 : pos;
        }
    }

    public static class LowerResolveResult implements ResolveResult {
        private final PsiElement m_referencedIdentifier;

        public LowerResolveResult(@NotNull PsiElement referencedElement, String sourceName) {
            if (referencedElement instanceof PsiLet && ((PsiLet) referencedElement).isDeconsruction()) {
                PsiElement identifierElement = referencedElement;
                for (PsiElement deconstructedElement : ((PsiLet) referencedElement).getDeconstructedElements()) {
                    if (deconstructedElement.getText().equals(sourceName)) {
                        identifierElement = deconstructedElement;
                        break;
                    }
                }
                m_referencedIdentifier = identifierElement;
            } else {
                PsiLowerIdentifier identifier = ORUtil.findImmediateFirstChildOfClass(referencedElement, PsiLowerIdentifier.class);
                m_referencedIdentifier = identifier == null ? referencedElement : identifier;
            }
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

        public boolean isInterface() {
            return FileHelper.isInterface(m_referencedIdentifier.getContainingFile().getFileType());
        }
    }
}
