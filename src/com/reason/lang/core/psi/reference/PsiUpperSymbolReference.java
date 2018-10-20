package com.reason.lang.core.psi.reference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.Debug;
import com.reason.ide.files.FileBase;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.RmlElementFactory;
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

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;
import static java.util.stream.Collectors.toList;

public class PsiUpperSymbolReference extends PsiReferenceBase<PsiUpperSymbol> {

    @Nullable
    private final String m_referenceName;
    @NotNull
    private final ORTypes m_types;

    private final Debug m_debug = new Debug(Logger.getInstance("ReasonML.ref.upper"));

    public PsiUpperSymbolReference(@NotNull PsiUpperSymbol element, @NotNull ORTypes types) {
        super(element, ORUtil.getTextRangeForReference(element));
        m_referenceName = element.getName();
        m_types = types;
    }

    @Override
    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = RmlElementFactory.createModuleName(myElement.getProject(), newName);

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

        PsiElement parent = PsiTreeUtil.getParentOfType(myElement, PsiModule.class);

        // If name is used in a definition, it's a declaration not a usage: ie, it's not a reference
        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_references.html
        if (parent != null && ((PsiModule) parent).getNameIdentifier() == myElement) {
            return null;
        }

        Project project = myElement.getProject();
        PsiFinder psiFinder = PsiFinder.getInstance();
        m_debug.debug("Find reference for upper symbol", m_referenceName);

        // Find potential paths of current element
        List<String> potentialPaths = getPotentialPaths();

        // Might be a file module, try that first

        PsiElement prevSibling = myElement.getPrevSibling();
        if (prevSibling == null || prevSibling.getNode().getElementType() != m_types.DOT) {
            FileBase fileModule = psiFinder.findFileModule(project, m_referenceName);
            if (fileModule != null) {
                m_debug.debug("  file", fileModule);
                return fileModule;
            }
            // else it might be an inner module
        }

        // Try to find a module from dependencies

        Collection<PsiModule> modules = psiFinder.findModules(project, m_referenceName, interfaceOrImplementation);

        m_debug.debug("  modules", modules);
        if (m_debug.isDebugEnabled()) {
            for (PsiModule module : modules) {
                m_debug.debug("    " + module.getContainingFile().getVirtualFile().getCanonicalPath() + " " + module.getQualifiedName());
            }
        }

        if (!modules.isEmpty()) {
            // Filter the modules, keep the ones with the same qualified name
            Collection<PsiModule> filteredModules = modules.stream().
                    filter(module -> {
                        String moduleQn = module.getQualifiedName();
                        return m_referenceName.equals(moduleQn) || potentialPaths.contains(moduleQn);
                    }).
                    collect(toList());
            m_debug.debug("  filtered modules: ", filteredModules);

            if (filteredModules.isEmpty()) {
                return null;
            }

            PsiModule moduleReference = filteredModules.iterator().next();
            String moduleAlias = moduleReference.getAlias();
            if (moduleAlias != null) {
                PsiQualifiedNamedElement moduleFromAlias = PsiFinder.getInstance().findModuleFromQn(project, moduleAlias);
                if (moduleFromAlias != null) {
                    m_debug.debug("    module alias resolved to file", moduleAlias);
                    return moduleFromAlias;
                }
            }

            if (m_debug.isDebugEnabled()) {
                m_debug.debug("»» " + moduleReference.getQualifiedName() + " / " + moduleReference.getAlias());
            }

            return moduleReference.getNameIdentifier();
        }

        return null;
    }

    private List<String> getPotentialPaths() {
        ModulePathFinder modulePathFinder = m_types instanceof RmlTypes ? new RmlModulePathFinder() : new OclModulePathFinder();

        List<String> potentialPaths = modulePathFinder.extractPotentialPaths(myElement).stream().
                map(item -> item + "." + m_referenceName).
                collect(toList());
        m_debug.debug("  potential paths", potentialPaths);

        return potentialPaths;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
