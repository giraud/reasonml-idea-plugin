package com.reason.lang.core.psi.reference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.Log;
import com.reason.ide.search.FileModuleIndexService;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.ORElementFactory;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.psi.PsiInnerModule;
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

import static com.reason.lang.core.ORFileType.interfaceOrImplementation;
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
    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
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

        PsiInnerModule parent = PsiTreeUtil.getParentOfType(myElement, PsiInnerModule.class);

        // If name is used in a definition, it's a declaration not a usage: ie, it's not a reference
        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_references.html
        if (parent != null && parent.getNameIdentifier() == myElement) {
            return null;
        }

        Project project = myElement.getProject();
        PsiFinder psiFinder = PsiFinder.getInstance();
        LOG.debug("Find reference for upper symbol", m_referenceName);

        // Find potential paths of current element
        List<String> potentialPaths = getPotentialPaths();

        // Might be a file module, try that first

        PsiElement prevSibling = myElement.getPrevSibling();
        if (prevSibling == null || prevSibling.getNode().getElementType() != m_types.DOT) {
            VirtualFile fileWithName = FileModuleIndexService.getService().getFileWithName(m_referenceName, GlobalSearchScope.allScope(project));
            if (fileWithName != null) {
                LOG.debug("  file", fileWithName);
                return PsiManager.getInstance(project).findFile(fileWithName);
            }
            // else it might be an inner module
        }

        // Try to find a module from dependencies

        Collection<PsiInnerModule> modules = psiFinder.findModules(project, m_referenceName, interfaceOrImplementation);

        LOG.debug("  modules", modules);
        if (LOG.isDebugEnabled()) {
            for (PsiInnerModule module : modules) {
                LOG.debug("    " + module.getContainingFile().getVirtualFile().getCanonicalPath() + " " + module.getQualifiedName());
            }
        }

        if (!modules.isEmpty()) {
            // Filter the modules, keep the ones with the same qualified name
            Collection<PsiInnerModule> filteredModules = modules.stream().
                    filter(module -> {
                        String moduleQn = module.getQualifiedName();
                        return m_referenceName.equals(moduleQn) || potentialPaths.contains(moduleQn);
                    }).
                    collect(toList());
            LOG.debug("  filtered modules: ", filteredModules);

            if (filteredModules.isEmpty()) {
                return null;
            }

            PsiInnerModule moduleReference = filteredModules.iterator().next();
            String moduleAlias = moduleReference.getAlias();
            if (moduleAlias != null) {
                PsiQualifiedNamedElement moduleFromAlias = PsiFinder.getInstance().findModuleFromQn(project, moduleAlias);
                if (moduleFromAlias != null) {
                    LOG.debug("    module alias resolved to file", moduleAlias);
                    return moduleFromAlias;
                }
            }

            if (LOG.isDebugEnabled()) {
                LOG.debug("»» " + moduleReference.getQualifiedName() + " / " + moduleReference.getAlias());
            }

            return moduleReference.getNameIdentifier();
        }

        return null;
    }

    private List<String> getPotentialPaths() {
        ModulePathFinder modulePathFinder = m_types instanceof RmlTypes ? new RmlModulePathFinder() : new OclModulePathFinder();

        Project project = myElement.getProject();
        PsiFinder psiFinder = PsiFinder.getInstance();

        List<String> paths = modulePathFinder.extractPotentialPaths(myElement, true);
        List<String> potentialPaths = paths.stream().
                map(s -> {
                    PsiQualifiedNamedElement moduleAlias = psiFinder.findModuleAlias(project, s);
                    return moduleAlias == null ? psiFinder.findModuleFromQn(project, s) : moduleAlias;
                }).
                filter(Objects::nonNull).
                map(item -> item.getQualifiedName() + "." + m_referenceName).
                collect(toList());
        LOG.debug("  potential paths", potentialPaths);

        return potentialPaths;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
