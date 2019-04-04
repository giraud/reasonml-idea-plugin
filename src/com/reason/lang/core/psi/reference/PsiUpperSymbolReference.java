package com.reason.lang.core.psi.reference;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.Log;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.ORElementFactory;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.core.type.ORTypes;
import com.reason.lang.ocaml.OclModulePathFinder;
import com.reason.lang.reason.RmlModulePathFinder;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
        PsiInnerModule parent = PsiTreeUtil.getParentOfType(myElement, PsiInnerModule.class);
        if (parent != null && parent.getNameIdentifier() == myElement) {
            return null;
        }

        LOG.debug("Find reference for upper symbol", m_referenceName);

        // Find potential paths of current element
        List<PsiQualifiedNamedElement> potentialPaths = getPotentialPaths();
        LOG.debug("  potential paths", potentialPaths);
        if (LOG.isDebugEnabled()) {
            for (PsiQualifiedNamedElement module : potentialPaths) {
                LOG.debug("    " + module.getContainingFile().getVirtualFile().getCanonicalPath() + " " + module.getQualifiedName());
            }
        }

        if (!potentialPaths.isEmpty()) {
            PsiModule moduleReference = (PsiModule) ((Collection<PsiQualifiedNamedElement>) potentialPaths).iterator().next();
            if (LOG.isDebugEnabled()) {
                LOG.debug("»» " + moduleReference.getQualifiedName() + " / " + moduleReference.getAlias());
            }

            return moduleReference instanceof PsiInnerModule ? ((PsiInnerModule) moduleReference).getNameIdentifier() : moduleReference;
        }

        return null;
    }

    private List<PsiQualifiedNamedElement> getPotentialPaths() {
        PsiFinder psiFinder = PsiFinder.getInstance(myElement.getProject());

        ModulePathFinder modulePathFinder = m_types instanceof RmlTypes ? new RmlModulePathFinder() : new OclModulePathFinder();
        List<String> paths = modulePathFinder.extractPotentialPaths(myElement, true, true);

        List<PsiQualifiedNamedElement> result = paths.stream().
                map(path -> path + "." + m_referenceName).
                map(qn -> {
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
