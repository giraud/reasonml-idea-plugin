package com.reason.ide.search.reference;

import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiPropertyNameReference extends PsiPolyVariantReferenceBase<RPsiLeafPropertyName> {
    private static final Log LOG = Log.create("ref.params");
    private static final Log LOG_PERF = Log.create("ref.perf.params");

    private final @Nullable String myReferenceName;
    private final @NotNull ORLangTypes myTypes;

    public PsiPropertyNameReference(@NotNull RPsiLeafPropertyName element, @NotNull ORLangTypes types) {
        super(element, TextRange.from(0, element.getTextLength()));
        myReferenceName = element.getText();
        myTypes = types;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        if (myReferenceName == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        // If name is used in a definition, it's a declaration not a usage: ie, it's not a reference
        // http://www.jetbrains.org/intellij/sdk/docs/basics/architectural_overview/psi_references.html
        PsiElement parent = myElement.getParent();
        if (parent instanceof RPsiLet || parent instanceof RPsiVal || parent instanceof RPsiExternal) {
            return ResolveResult.EMPTY_ARRAY;
        }

        long startAll = System.currentTimeMillis();

        Project project = myElement.getProject();
        GlobalSearchScope searchScope = GlobalSearchScope.allScope(project); // ?

        LOG.debug("Find reference for propertyLeaf", myReferenceName);
        if (LOG.isTraceEnabled()) {
            LOG.trace(" -> search scope: " + searchScope);
        }

        // Gather instructions from element up to the file root
        Deque<PsiElement> instructions = ORReferenceAnalyzer.createInstructions(myElement, true, myTypes);
        instructions.addLast(parent);

        if (LOG.isTraceEnabled()) {
            LOG.trace("  Instructions: ", Joiner.join(" -> ", instructions));
        }

        long endInstructions = System.currentTimeMillis();

        BsConfigManager service = project.getService(BsConfigManager.class);
        BsConfig bsConfig = service.getNearest(myElement.getContainingFile());
        Set<String> openedModules = bsConfig == null ? null : bsConfig.getOpenedDeps();
        if (LOG.isTraceEnabled()) {
            LOG.trace("  virtual file", ORFileUtils.getVirtualFile(myElement.getContainingFile()));
        }

        // Resolve aliases in the stack of instructions, this time from file down to element
        List<RPsiQualifiedPathElement> resolvedInstructions = ORReferenceAnalyzer.resolveInstructions(instructions, openedModules, project, searchScope);

        if (LOG.isTraceEnabled()) {
            LOG.trace("  Resolved instructions: " + Joiner.join(" -> ", resolvedInstructions));
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
            resolveResults[i] = new JsxTagResolveResult(element, myReferenceName);
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
    public @Nullable PsiElement resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return 0 < resolveResults.length ? resolveResults[0].getElement() : null;
    }

    public static class JsxTagResolveResult implements ResolveResult {
        private @Nullable PsiElement myReferencedIdentifier = null;

        public JsxTagResolveResult(@NotNull PsiElement referencedElement, @NotNull String propertyName) {
            if (referencedElement instanceof RPsiLet) {
                RPsiFunction function = ((RPsiLet) referencedElement).getFunction();
                if (function != null) {
                    List<RPsiParameterDeclaration> parameters = function.getParameters();
                    for (RPsiParameterDeclaration parameter : parameters) {
                        if (propertyName.equals(parameter.getName())) {
                            myReferencedIdentifier = parameter;
                            break;
                        }
                    }
                }
            } else if (referencedElement instanceof RPsiParameterDeclaration) {
                myReferencedIdentifier = referencedElement;
            }
        }

        @Override
        public @Nullable PsiElement getElement() {
            return myReferencedIdentifier;
        }

        @Override
        public boolean isValidResult() {
            return myReferencedIdentifier != null;
        }
    }
}
