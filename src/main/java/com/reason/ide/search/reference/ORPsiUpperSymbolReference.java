package com.reason.ide.search.reference;

import com.intellij.openapi.project.*;
import com.intellij.openapi.util.text.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.util.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.ide.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORPsiUpperSymbolReference extends ORMultiSymbolReference<RPsiUpperSymbol> {
    private static final Log LOG = Log.create("ref.upper");
    private static final Log LOG_PERF = Log.create("ref.perf.upper");

    public ORPsiUpperSymbolReference(@NotNull RPsiUpperSymbol element, @NotNull ORLangTypes types) {
        super(element, types);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        if (myReferenceName == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        // If name is used in a definition, it's a declaration not a usage: ie, it's not a reference
        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_references.html
        PsiElement parent = myElement.getParent();
        if (parent instanceof RPsiInnerModule) {
            if (!(parent.getParent() instanceof RPsiModuleSignature)) {
                LOG.debug("Declaration found (inner module), skip reference resolution", myElement);
                return ResolveResult.EMPTY_ARRAY;
            }
        } else if (parent instanceof RPsiFunctor || parent instanceof RPsiException || parent instanceof RPsiVariantDeclaration) {
            LOG.debug("Declaration found, skip reference resolution", myReferenceName);
            return ResolveResult.EMPTY_ARRAY;
        }

        long startAll = System.currentTimeMillis();

        Project project = myElement.getProject();
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project); // ?

        LOG.debug("Find reference for upper symbol", myReferenceName);
        if (LOG.isTraceEnabled()) {
            LOG.trace(" -> search scope: " + searchScope);
        }

        // Gather instructions from element up to the file root
        Deque<PsiElement> instructions = ORReferenceAnalyzer.createInstructionsBackward(myElement, false, myTypes);
        boolean inPath = ORReferenceAnalyzer.isInPath(myElement, myTypes);
        instructions.addLast(inPath ? myElement : new ORReferenceAnalyzer.FirstInPath(myElement));

        if (LOG.isTraceEnabled()) {
            LOG.trace("  Instructions", Joiner.join(" -> ", instructions));
        }

        long endInstructions = System.currentTimeMillis();

        BsConfig config = project.getService(ORCompilerConfigManager.class).getNearestConfig(myElement.getContainingFile());
        Set<String> openedModules = config == null ? null : config.getOpenedDeps();
        if (LOG.isTraceEnabled()) {
            LOG.trace("  virtual file", ORFileUtils.getVirtualFile(myElement.getContainingFile()));
        }

        // Resolve aliases in the stack of instructions, this time from file down to element
        List<RPsiQualifiedPathElement> resolvedInstructions = ORReferenceAnalyzer.resolveInstructions(instructions, openedModules, project, searchScope);

        if (LOG.isTraceEnabled()) {
            LOG.trace("  Resolutions", Joiner.join(", ", resolvedInstructions));
        }

        long endResolvedInstructions = System.currentTimeMillis();

        resolvedInstructions.sort((e1, e2) -> {
            if (e1 instanceof FileBase && ((FileBase) e1).isInterface()) {
                return 1;
            }
            if (e2 instanceof FileBase && ((FileBase) e2).isInterface()) {
                return -1;
            }
            return NaturalComparator.INSTANCE.compare(e1.getQualifiedName(), e2.getQualifiedName());
        });

        if (LOG.isDebugEnabled()) {
            LOG.debug("  => found", Joiner.join(", ", resolvedInstructions,
                    element -> element.getQualifiedName()
                            + " [" + Platform.getRelativePathToModule(element.getContainingFile()) + "]"));
        }

        ResolveResult[] resolveResults = new ResolveResult[((Collection<RPsiQualifiedPathElement>) resolvedInstructions).size()];

        int i = 0;
        for (PsiElement element : resolvedInstructions) {
            resolveResults[i] = new UpperResolveResult(element);
            i++;
        }

        long endAll = System.currentTimeMillis();
        if (LOG_PERF.isDebugEnabled()) {
            LOG_PERF.debug("Resolution of " + myReferenceName + " in " + (endAll - startAll) + "ms => " +
                    " instructions: " + (endInstructions - startAll) + "ms," +
                    " resolutions: " + (endResolvedInstructions - endInstructions) + "ms,"
            );
        }

        return resolveResults;
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newName) throws IncorrectOperationException {
        PsiElement newId = ORCodeFactory.createModuleName(myElement.getProject(), newName);
        return newId != null ? myElement.replace(newId) : myElement;
    }

    private static class UpperResolveResult implements ResolveResult {
        private final PsiElement m_referencedIdentifier;

        public UpperResolveResult(@NotNull PsiElement referencedElement) {
            m_referencedIdentifier = referencedElement;
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
