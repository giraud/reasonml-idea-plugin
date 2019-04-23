package com.reason.lang.core.psi.reference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.Log;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.ORElementFactory;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclModulePathFinder;
import com.reason.lang.reason.RmlModulePathFinder;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.reason.lang.ModulePathFinder.includeALl;
import static java.util.stream.Collectors.toList;

public class PsiUpperSymbolReference extends PsiReferenceBase<PsiUpperSymbol> {

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

    @Override
    public PsiElement handleElementRename(@NotNull String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = ORElementFactory.createModuleName(myElement.getProject(), newName);

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

        // If name is used in a definition, it's a declaration not a usage: ie, it's not a reference
        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_references.html
        PsiNamedElement parent = PsiTreeUtil.getParentOfType(myElement, PsiInnerModule.class, PsiVariantDeclaration.class, PsiException.class);
        if (parent != null && parent.getNameIdentifier() == myElement) {
            return null;
        }

        LOG.debug("Find reference for upper symbol", m_referenceName);

        // Find potential paths of current element
        List<PsiQualifiedNamedElement> potentialPaths = getPotentialPaths();
        if (LOG.isDebugEnabled()) {
            for (PsiQualifiedNamedElement module : potentialPaths) {
                LOG.debug("    " + module.getContainingFile().getVirtualFile().getCanonicalPath() + " " + module.getQualifiedName());
            }
        }

        if (!potentialPaths.isEmpty()) {
            PsiQualifiedNamedElement elementReference = potentialPaths.iterator().next();
            boolean isInnerModule = elementReference instanceof PsiInnerModule;
            if (LOG.isDebugEnabled()) {
                LOG.debug("»» " + elementReference.getQualifiedName() + (isInnerModule ? " / " + ((PsiInnerModule) elementReference).getAlias() : ""));
            }

            return elementReference instanceof PsiNamedElement ? ((PsiNamedElement) elementReference).getNameIdentifier() : elementReference;
        }

        return null;
    }

    private List<PsiQualifiedNamedElement> getPotentialPaths() {
        Project project = myElement.getProject();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);
        PsiFinder psiFinder = PsiFinder.getInstance(project);

        ModulePathFinder modulePathFinder = m_types instanceof RmlTypes ? new RmlModulePathFinder() : new OclModulePathFinder();
        List<String> paths = modulePathFinder.extractPotentialPaths(myElement, includeALl, true);

        List<PsiQualifiedNamedElement> result = paths.stream().
                map(path -> path + "." + m_referenceName).
                map(qn -> {
                    PsiQualifiedNamedElement variant = psiFinder.findVariant(qn, scope);
                    if (variant != null) {
                        return variant;
                    }

                    PsiQualifiedNamedElement exception = psiFinder.findException(qn, scope);
                    if (exception != null) {
                        return exception;
                    }

                    PsiQualifiedNamedElement moduleAlias = psiFinder.findModuleAlias(qn);
                    return moduleAlias == null ? psiFinder.findModuleFromQn(qn) : moduleAlias;
                }).
                filter(Objects::nonNull).
                collect(toList());

        PsiQualifiedNamedElement moduleAlias = psiFinder.findModuleFromQn(m_referenceName);
        if (moduleAlias != null) {
            result.add(moduleAlias);
        }

        return result;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
