package com.reason.lang.core.psi.reference;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiUpperSymbolReference extends ORMultiSymbolReference<PsiUpperSymbol> {
    private static final Log LOG = Log.create("ref.upper");
    private static final Log LOG_PERF = Log.create("ref.perf.upper");

    public PsiUpperSymbolReference(@NotNull PsiUpperSymbol element, @NotNull ORTypes types) {
        super(element, types);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        if (myReferenceName == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        // If name is used in a definition, it's a declaration not a usage: ie, it's not a reference
        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_references.html
        if (myElement instanceof PsiUpperIdentifier) {
            return ResolveResult.EMPTY_ARRAY;
        }

        long startAll = System.currentTimeMillis();

        LOG.debug("Find reference for upper symbol", myReferenceName);

        // Gather instructions from element up to the file root
        Deque<PsiElement> instructions = ORReferenceAnalyzer.createInstructions(myElement, myTypes);
        instructions.addLast(myElement);

        if (LOG.isTraceEnabled()) {
            LOG.trace("  Instructions", Joiner.join(" -> ", instructions));
        }

        long endInstructions = System.currentTimeMillis();

        // Resolve aliases in the stack of instructions, this time from file down to element
        Deque<CodeInstruction> resolvedInstructions = ORReferenceAnalyzer.resolveInstructions(instructions, myElement.getProject());

        if (LOG.isTraceEnabled()) {
            LOG.trace("  Resolved instructions: " + Joiner.join(" -> ", resolvedInstructions));
        }

        long endResolvedInstructions = System.currentTimeMillis();

        // Find all elements by name and create a list of paths
        Project project = myElement.getProject();
        //Module module = Platform.getModule(project, myElement.getContainingFile().getVirtualFile());
        //GlobalSearchScope scope = module == null ? GlobalSearchScope.projectScope(project) : GlobalSearchScope.moduleScope(module);
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        Collection<PsiModule> modules = ModuleIndex.getElements(myReferenceName, project, scope);
        Collection<PsiVariantDeclaration> variants = VariantIndex.getElements(myReferenceName, project, scope);
        Collection<PsiException> exceptions = ExceptionIndex.getElements(myReferenceName, project, scope);

        long endIndexes = System.currentTimeMillis();

        ORElementResolver.Resolutions resolutions = project.getService(ORElementResolver.class).getComputation();
        resolutions.add(modules, true);
        resolutions.add(variants, false);
        resolutions.add(exceptions, false);

        //if (LOG.isTraceEnabled()) {
        //    LOG.trace("  Resolutions", resolutions.myResolutions.values());
        //}

        long endAddResolutions = System.currentTimeMillis();

        resolutions.addIncludesEquivalence();

        long endAddIncludes = System.currentTimeMillis();

        // Now that everything is resolved, we can use the stack of instructions to add weight to the paths

        for (CodeInstruction instruction : resolvedInstructions) {
            if (instruction.mySource instanceof FileBase) {
                resolutions.udpateTerminalWeight(((FileBase) instruction.mySource).getModuleName());
            } else if (instruction.myValues != null) {
                for (String value : instruction.myValues) {
                    resolutions.updateWeight(value, instruction.myAlternateValues);
                }
            }
        }

        long endUpdateResolutions = System.currentTimeMillis();

        resolutions.removeIncomplete();
        Collection<PsiQualifiedPathElement> sortedResult = resolutions.resolvedElements();

        if (LOG.isDebugEnabled()) {
            LOG.debug("  => found", Joiner.join(", ", sortedResult,
                    element -> element.getQualifiedName()
                            + " [" + Platform.getRelativePathToModule(element.getContainingFile()) + "]"));
        }

        long endSort = System.currentTimeMillis();

        ResolveResult[] resolveResults = new ResolveResult[sortedResult.size()];

        int i = 0;
        PsiElement parent = myElement.getParent();
        for (PsiElement element : sortedResult) {
            resolveResults[i] = new UpperResolveResult(element, parent);
            i++;
        }

        long endAll = System.currentTimeMillis();
        if (LOG_PERF.isDebugEnabled()) {
            LOG_PERF.debug("Resolution of " + myReferenceName + " in " + (endAll - startAll) + "ms => " +
                    " in: " + (endInstructions - startAll) + "ms," +
                    " rI: " + (endResolvedInstructions - endInstructions) + "ms," +
                    " id: " + (endIndexes - endResolvedInstructions) + "ms, " +
                    " aR: " + (endAddResolutions - endIndexes) + "ms," +
                    " aI: " + (endAddIncludes - endAddResolutions) + "ms," +
                    " uR: " + (endUpdateResolutions - endAddIncludes) + "ms," +
                    " sort: " + (endSort - endUpdateResolutions) + "ms"
            );
        }

        return resolveResults;
    }


    @Override
    public PsiElement handleElementRename(@NotNull String newName) throws IncorrectOperationException {
        PsiUpperIdentifier newNameIdentifier = ORCodeFactory.createModuleName(myElement.getProject(), newName);

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

    private static class UpperResolveResult implements ResolveResult {
        private final PsiElement m_referencedIdentifier;

        public UpperResolveResult(@NotNull PsiElement referencedElement, @Nullable PsiElement sourceParent) {
            if (referencedElement instanceof PsiModule && ((PsiModule) referencedElement).isComponent() && sourceParent instanceof PsiTagStart) {
                PsiElement make = ((PsiModule) referencedElement).getComponentNavigationElement();
                PsiLowerIdentifier identifier = ORUtil.findImmediateFirstChildOfClass(make, PsiLowerIdentifier.class);
                m_referencedIdentifier = identifier == null ? referencedElement : identifier;
            } else if (referencedElement instanceof PsiFakeModule) {
                // A fake module resolve to its file
                m_referencedIdentifier = referencedElement.getContainingFile();
            } else if (referencedElement instanceof PsiNameIdentifierOwner) {
                m_referencedIdentifier = ((PsiNameIdentifierOwner) referencedElement).getNameIdentifier();
            } else {
                PsiUpperIdentifier identifier = ORUtil.findImmediateFirstChildOfClass(referencedElement, PsiUpperIdentifier.class);
                m_referencedIdentifier = identifier == null ? referencedElement : identifier;
            }
        }

        @Override
        public @Nullable PsiElement getElement() {
            return m_referencedIdentifier;
        }

        @Override
        public boolean isValidResult() {
            return true;
        }
    }
}
