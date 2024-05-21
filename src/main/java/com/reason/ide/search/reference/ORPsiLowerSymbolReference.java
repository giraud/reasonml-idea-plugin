package com.reason.ide.search.reference;

import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.util.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.ide.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ORPsiLowerSymbolReference extends ORMultiSymbolReference<RPsiLowerSymbol> {
    private static final Log LOG = Log.create("ref.lower");
    private static final Log LOG_PERF = Log.create("ref.perf.lower");

    public ORPsiLowerSymbolReference(@NotNull RPsiLowerSymbol element, @NotNull ORLangTypes types) {
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
        if (parent instanceof RPsiLet || parent instanceof RPsiVal || parent instanceof RPsiType || parent instanceof RPsiExternal) {
            return ResolveResult.EMPTY_ARRAY;
        }

        long startAll = System.currentTimeMillis();

        Project project = myElement.getProject();
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project); // ?

        LOG.debug("Find reference for lower symbol", myReferenceName);
        if (LOG.isTraceEnabled()) {
            LOG.trace(" -> search scope: " + searchScope);
        }

        // Gather instructions from element up to the file root
        //Deque<PsiElement> instructionsOLD = ORReferenceAnalyzer.createInstructionsOLD(myElement, true, myTypes);
        Deque<PsiElement> instructions = ORReferenceAnalyzer.createInstructionsBackward(myElement, true, myTypes);

        // Test if source element is part of a record/object chain
        if (ORUtil.isPrevType(myElement, myTypes.SHARPSHARP)) { // ReasonML: JsObject field
            instructions.addLast(new ORReferenceAnalyzer.SymbolField(myElement, false));
        } else if (ORUtil.isPrevType(myElement, myTypes.DOT) && ORUtil.prevPrevSibling(myElement) instanceof RPsiLowerSymbol) { // Record field: a.b
            instructions.addLast(new ORReferenceAnalyzer.SymbolField(myElement, true));
        } else if (myElement.getParent() instanceof RPsiRecordField) {
            instructions.addLast(new ORReferenceAnalyzer.SymbolField(myElement, true));
        } else {
            instructions.addLast(myElement);
        }

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
            resolveResults[i] = new LowerResolveResult(element, myReferenceName);
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
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newName) throws IncorrectOperationException {
        PsiElement newId = ORCodeFactory.createLetName(myElement.getProject(), newName);
        return newId == null ? myElement : myElement.replace(newId);
    }

    public static class LowerResolveResult implements ResolveResult {
        private final @NotNull PsiElement myReferencedIdentifier;

        public LowerResolveResult(@NotNull PsiElement referencedElement, String sourceName) {
            if (referencedElement instanceof RPsiLet && ((RPsiLet) referencedElement).isDeconstruction()) {
                PsiElement identifierElement = referencedElement;
                for (PsiElement deconstructedElement : ((RPsiLet) referencedElement).getDeconstructedElements()) {
                    if (deconstructedElement.getText().equals(sourceName)) {
                        identifierElement = deconstructedElement;
                        break;
                    }
                }
                myReferencedIdentifier = identifierElement;
            } else {
                myReferencedIdentifier = referencedElement;
            }
        }

        @Override
        public @Nullable PsiElement getElement() {
            return myReferencedIdentifier;
        }

        @Override
        public boolean isValidResult() {
            return true;
        }

        public boolean inInterface() {
            return ORUtil.inInterface(myReferencedIdentifier);
        }
    }
}
