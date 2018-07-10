package com.reason.lang.core.psi.reference;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.PsiReferenceBase;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.Joiner;
import com.reason.ide.files.FileBase;
import com.reason.lang.MlTypes;
import com.reason.lang.ModulePathFinder;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiUtil;
import com.reason.lang.core.RmlElementFactory;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiUpperSymbol;
import com.reason.lang.ocaml.OclModulePathFinder;
import com.reason.lang.reason.RmlModulePathFinder;
import com.reason.lang.reason.RmlTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

import static com.reason.lang.core.MlFileType.interfaceOrImplementation;
import static java.util.stream.Collectors.toList;

public class PsiUpperSymbolReference extends PsiReferenceBase<PsiUpperSymbol> {

    @Nullable
    private final String m_referenceName;
    @NotNull
    private final MlTypes m_types;

    @SuppressWarnings("FieldCanBeLocal") private final boolean m_debug = false;

    public PsiUpperSymbolReference(@NotNull PsiUpperSymbol element, @NotNull MlTypes types) {
        super(element, PsiUtil.getTextRangeForReference(element));
        m_referenceName = element.getName();
        m_types = types;
    }

    @Override
    public PsiElement handleElementRename(String newName) throws IncorrectOperationException {
        PsiElement newNameIdentifier = RmlElementFactory.createModuleName(myElement.getProject(), newName);
        ASTNode newNameNode = newNameIdentifier == null ? null : newNameIdentifier.getFirstChild().getNode();

        if (newNameNode != null) {
            PsiElement nameIdentifier = myElement.getNameIdentifier();
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

        // If name is used in a module definition, it's already the reference
        // module <ReferenceName> = ...
        if (parent != null && ((PsiModule) parent).getNameIdentifier() == myElement) {
            return null;
        }

        Project project = myElement.getProject();
        PsiFinder psiFinder = PsiFinder.getInstance();
        ModulePathFinder modulePathFinder = m_types instanceof RmlTypes ? new RmlModulePathFinder() : new OclModulePathFinder();

        // Might be a file module, try that
        PsiElement prevSibling = myElement.getPrevSibling();
        if (prevSibling == null || prevSibling.getNode().getElementType() != m_types.DOT) {
            FileBase fileModule = psiFinder.findFileModule(project, m_referenceName);
            if (m_debug) {
                System.out.println("  file: " + fileModule);
            }
            return fileModule;
        }

        Collection<PsiModule> modules = psiFinder.findModules(project, m_referenceName, interfaceOrImplementation);

        if (m_debug) {
            System.out.println("  modules: " + modules.size() + (modules.size() == 1 ? " (no filtering)" : ""));
            for (PsiModule module : modules) {
                System.out.println("    " + module.getContainingFile().getVirtualFile().getCanonicalPath() + " " + module.getQualifiedName());
            }
        }

        if (!modules.isEmpty()) {
            Collection<PsiModule> filteredModules = modules;
            if (1 < modules.size()) {
                // Find potential paths of current element
                List<String> potentialPaths = modulePathFinder.extractPotentialPaths(myElement).stream().map(item -> item + "." + m_referenceName).collect(toList());
                if (m_debug) {
                    System.out.println("  potential paths: [" + Joiner.join(", ", potentialPaths) + "]");
                }

                // Filter the modules, keep the ones with the same qualified name
                filteredModules = modules.stream().
                        filter(module -> {
                            String moduleQn = module.getQualifiedName();
                            return m_referenceName.equals(moduleQn) || potentialPaths.contains(moduleQn);
                        }).
                        collect(toList());

                if (m_debug) {
                    System.out.println("  filtered modules: " + filteredModules.size());
                    for (PsiModule module : filteredModules) {
                        System.out.println("    " + module.getContainingFile().getVirtualFile().getCanonicalPath() + " " + module.getQualifiedName());
                    }
                }
            }

            if (filteredModules.isEmpty()) {
                return null;
            }

            PsiModule moduleReference = filteredModules.iterator().next();
            String moduleAlias = moduleReference.getAlias();
            if (moduleAlias != null) {
                PsiQualifiedNamedElement moduleFromAlias = PsiFinder.getInstance().findModuleFromQn(project, moduleAlias);
                if (moduleFromAlias != null) {
                    if (m_debug) {
                        System.out.println("    module alias: " + moduleAlias + " resolved to file");
                    }
                    return moduleFromAlias;
                }
            }

            if (m_debug) {
                System.out.println("»» " + moduleReference.getQualifiedName() + " / " + moduleReference.getAlias());
            }


            return moduleReference.getNameIdentifier();
        }

        return null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        return EMPTY_ARRAY;
    }
}
