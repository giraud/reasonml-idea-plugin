package com.reason.ide.search.reference;

import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiUpperSymbolReference extends ORMultiSymbolReference<RPsiUpperSymbol> {
    private static final Log LOG = Log.create("ref.upper");
    private static final Log LOG_PERF = Log.create("ref.perf.upper");

    public PsiUpperSymbolReference(@NotNull RPsiUpperSymbol element, @NotNull ORLangTypes types) {
        super(element, types);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        if (myReferenceName == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        // If name is used in a definition, it's a declaration not a usage: ie, it's not a reference
        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_references.html
        //if (myElement instanceof PsiUpperIdentifier) {
        PsiElement parent = myElement.getParent();
        if (parent instanceof RPsiModule) {
            if (!(parent.getParent() instanceof RPsiModuleType)) {
                return ResolveResult.EMPTY_ARRAY;
            }
        } else if (parent instanceof RPsiException || parent instanceof RPsiVariantDeclaration) {
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
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        Collection<RPsiModule> modules = ModuleIndex.getElements(myReferenceName, project, scope);
        Collection<RPsiVariantDeclaration> variants = VariantIndex.getElements(myReferenceName, project, scope);
        Collection<RPsiException> exceptions = ExceptionIndex.getElements(myReferenceName, project, scope);

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
        if (!(parent instanceof RPsiFunctorCall)) {
            resolutions.addFunctorsEquivalence();
        }

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
        Collection<RPsiQualifiedPathElement> sortedResult = resolutions.resolvedElements();

        if (LOG.isDebugEnabled()) {
            LOG.debug("  => found", Joiner.join(", ", sortedResult,
                    element -> element.getQualifiedName()
                            + " [" + Platform.getRelativePathToModule(element.getContainingFile()) + "]"));
        }

        long endSort = System.currentTimeMillis();

        ResolveResult[] resolveResults = new ResolveResult[sortedResult.size()];

        int i = 0;
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
        //myElement.replace(new RPsiUpperTagName(myTypes, myElement.getElementType(), newName));
        return myElement;
    }

    private static class UpperResolveResult implements ResolveResult {
        private final PsiElement m_referencedIdentifier;

        public UpperResolveResult(@NotNull PsiElement referencedElement, @Nullable PsiElement sourceParent) {
            if (referencedElement instanceof RPsiModule && ((RPsiModule) referencedElement).isComponent() && sourceParent instanceof RPsiTagStart) {
                PsiElement make = ((RPsiModule) referencedElement).getComponentNavigationElement();
                m_referencedIdentifier = make == null ? referencedElement : make;
            } else if (referencedElement instanceof RPsiFakeModule) {
                // A fake module resolve to its file
                m_referencedIdentifier = referencedElement.getContainingFile();
            } else /*if (referencedElement instanceof PsiNameIdentifierOwner)*/ {
                m_referencedIdentifier = referencedElement;
            }
            //else {
            //    RPsiUpperSymbol identifier = ORUtil.findImmediateFirstChildOfClass(referencedElement, RPsiUpperSymbol.class);
            //    m_referencedIdentifier = identifier == null ? referencedElement : identifier;
            //}
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
