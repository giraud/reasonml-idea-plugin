package com.reason.lang.core.psi.reference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.ArrayListSet;
import com.reason.Joiner;
import com.reason.Log;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.QNameFinder;
import com.reason.lang.core.ORCodeFactory;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclQNameFinder;
import com.reason.lang.reason.RmlQNameFinder;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.reason.lang.core.ORFileType.both;

public class PsiUpperSymbolReference extends PsiPolyVariantReferenceBase<PsiUpperSymbol> {

    private static final Log LOG = Log.create("ref.upper");

    @Nullable
    private final String m_referenceName;
    @NotNull
    private final ORTypes m_types;

    public PsiUpperSymbolReference(@NotNull PsiUpperSymbol element, @NotNull ORTypes types) {
        super(element, ORUtil.getTextRangeForReference(element));
        m_referenceName = element.getName();
        m_types = types;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        if (m_referenceName == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        // If name is used in a definition, it's a declaration not a usage: ie, it's not a reference
        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_references.html
        PsiNameIdentifierOwner parent = PsiTreeUtil.getParentOfType(myElement, PsiInnerModule.class, PsiVariantDeclaration.class, PsiException.class);
        if (parent != null && parent.getNameIdentifier() == myElement) {
            return ResolveResult.EMPTY_ARRAY;
        }

        LOG.debug("Find reference for upper symbol", m_referenceName);

        // Find potential paths of current element
        Set<PsiQualifiedElement> potentialPaths = getPotentialPaths();
        if (potentialPaths.isEmpty()) {
            LOG.debug(" -> No potential path found");
        } else {
            ResolveResult[] resolveResults = new ResolveResult[potentialPaths.size()];

            int i = 0;
            for (PsiQualifiedElement referencedElement : potentialPaths) {
                if (LOG.isDebugEnabled()) {
                    boolean isInnerModule = referencedElement instanceof PsiInnerModule;
                    String alias = isInnerModule ? ((PsiInnerModule) referencedElement).getAlias() : null;
                    String source = referencedElement instanceof FileBase ? ((FileBase) referencedElement).shortLocation(referencedElement.getProject()) : referencedElement.getClass().getName();
                    LOG.debug(" -> " + referencedElement.getQualifiedName() + (alias == null ? "" : " / alias=" + alias) + " in file " + referencedElement
                            .getContainingFile() + " [" + source + "]");
                }

                // A fake module resolve to its file
                resolveResults[i] = new UpperResolveResult(referencedElement instanceof PsiFakeModule ? (FileBase) referencedElement.getContainingFile() : referencedElement);
                i++;
            }

            return resolveResults;
        }

        return ResolveResult.EMPTY_ARRAY;
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
        PsiElement newNameIdentifier = ORCodeFactory.createModuleName(myElement.getProject(), newName);

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

    private Set<PsiQualifiedElement> getPotentialPaths() {
        Project project = myElement.getProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        PsiFinder psiFinder = PsiFinder.getInstance(project);

        QNameFinder qnameFinder = m_types instanceof RmlTypes ? RmlQNameFinder.INSTANCE : OclQNameFinder.INSTANCE;
        Set<String> paths = qnameFinder.extractPotentialPaths(myElement);
        if (LOG.isTraceEnabled()) {
            LOG.trace(" -> Paths before resolution: " + Joiner.join(", ", paths));
        }

        Set<PsiQualifiedElement> resolvedElements = new ArrayListSet<>();
        for (String path : paths) {
            String qn = path + "." + m_referenceName;

            PsiQualifiedElement variant = psiFinder.findVariant(qn, scope);
            if (variant != null) {
                resolvedElements.add(variant);
            } else {
                // Trying to resolve variant from the name,
                // Variant might be locally open with module name only - and not including type name... qn can't be used
                Collection<PsiVariantDeclaration> variants = psiFinder.findVariantByName(path, m_referenceName, scope);
                if (!variants.isEmpty()) {
                    resolvedElements.addAll(variants);
                } else {
                    PsiQualifiedElement exception = psiFinder.findException(qn, both, scope);
                    if (exception != null) {
                        resolvedElements.add(exception);
                    } else {
                        // Don't resolve local module aliases to their real reference: this is needed for refactoring
                        Set<PsiModule> modulesFromQn = psiFinder.findModulesFromQn(qn, false, both, scope);
                        if (!modulesFromQn.isEmpty()) {
                            resolvedElements.addAll(modulesFromQn);
                        }
                    }
                }
            }
        }

        PsiElement prevSibling = myElement.getPrevSibling();
        if (prevSibling == null || prevSibling.getNode().getElementType() != m_types.DOT) {
            Set<PsiModule> modulesReference = psiFinder.findModulesFromQn(m_referenceName, true, both, scope);
            if (modulesReference.isEmpty()) {
                if (LOG.isTraceEnabled()) {
                    LOG.trace(" -> No module found for qn " + m_referenceName);
                }
            } else {
                resolvedElements.addAll(modulesReference);
            }
        }

        return resolvedElements;
    }

    private static class UpperResolveResult implements ResolveResult {
        private final PsiElement m_referencedIdentifier;

        public UpperResolveResult(PsiQualifiedElement referencedElement) {
            m_referencedIdentifier = referencedElement instanceof PsiNameIdentifierOwner ? ((PsiNameIdentifierOwner) referencedElement).getNameIdentifier() :
                    referencedElement;
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

        @Override
        public String toString() {
            return m_referencedIdentifier instanceof PsiNamedElement ? ((PsiNamedElement) m_referencedIdentifier).getName() : m_referencedIdentifier.getText();
        }
    }
}
