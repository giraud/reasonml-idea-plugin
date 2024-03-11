package com.reason.ide.search.reference;

import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.tree.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORPsiLiteralStringReference extends ORMultiSymbolReference<RPsiLiteralString> {
    private static final Log LOG = Log.create("ref.string");
    private static final Log LOG_PERF = Log.create("ref.perf.string");

    public ORPsiLiteralStringReference(@NotNull RPsiLiteralString element, @NotNull ORLangTypes types) {
        super(element, types);
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        // Ref of a js object in rescript : <lowerSymbol/> <array> "[" "string" "]" </array>
        PsiElement prevSibling = myElement.getPrevSibling();
        if (prevSibling == null || prevSibling.getNode().getElementType() != myTypes.LBRACKET) {
            return ResolveResult.EMPTY_ARRAY;
        }

        PsiElement parent = prevSibling.getParent();
        if (parent == null || parent.getNode().getElementType() != myTypes.C_ARRAY) {
            return ResolveResult.EMPTY_ARRAY;
        }

        PsiElement parentPrevSibling = parent.getPrevSibling();
        IElementType parentPrevSiblingType = parentPrevSibling != null ? parentPrevSibling.getNode().getElementType() : null;
        if (parentPrevSibling == null || (parentPrevSiblingType != myTypes.LIDENT && parentPrevSiblingType != myTypes.C_ARRAY)) {
            return ResolveResult.EMPTY_ARRAY;
        }

        // This is a reference to a JS object, can be resolved

        long startAll = System.currentTimeMillis();

        Project project = myElement.getProject();
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project); // ?

        LOG.debug("Find reference for string", myElement);
        if (LOG.isTraceEnabled()) {
            LOG.trace(" -> search scope: " + searchScope);
        }

        // Gather instructions from element up to the file root
        Deque<PsiElement> instructions = ORReferenceAnalyzer.createInstructions(myElement, true, myTypes);

        // Source element is part of an object chain
        instructions.addLast(new ORReferenceAnalyzer.SymbolField(myElement, false));

        if (LOG.isTraceEnabled()) {
            LOG.trace("  Instructions: [" + Joiner.join(" -> ", instructions) + "]");
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
            LOG.trace("  Resolved instructions: [" + Joiner.join(" -> ", resolvedInstructions) + "]");
        }

        long endResolvedInstructions = System.currentTimeMillis();

        if (LOG.isDebugEnabled()) {
            LOG.debug("  => found", Joiner.join(", ", resolvedInstructions,
                    element -> element.getQualifiedName()
                            + " [" + Platform.getRelativePathToModule(element.getContainingFile()) + "]"));
        }

        ResolveResult[] resolveResults = new ResolveResult[((Collection<RPsiQualifiedPathElement>) resolvedInstructions).size()];
        int i = 0;
        for (PsiElement element : resolvedInstructions) {
            resolveResults[i] = new ORPsiLowerSymbolReference.LowerResolveResult(element, myReferenceName);
            i++;
        }

        if (LOG_PERF.isDebugEnabled()) {
            long endAll = System.currentTimeMillis();
            LOG_PERF.debug("Resolution of " + myReferenceName + " in " + (endAll - startAll) + "ms => " +
                    " i:" + (endInstructions - startAll) + "," +
                    " r:" + (endResolvedInstructions - endInstructions)
            );
        }

        return resolveResults;

        // ...
    }
}
