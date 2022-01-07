package com.reason.lang.core.psi.reference;

import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiPropertyNameReference extends PsiPolyVariantReferenceBase<PsiLeafPropertyName> {
    private static final Log LOG = Log.create("ref.params");

    private final @Nullable String myReferenceName;
    private final @NotNull ORTypes myTypes;

    public PsiPropertyNameReference(@NotNull PsiLeafPropertyName element, @NotNull ORTypes types) {
        super(element, TextRange.from(0, element.getTextLength()));
        myReferenceName = element.getText();
        myTypes = types;
    }

    @Override
    public ResolveResult @NotNull [] multiResolve(boolean incompleteCode) {
        if (myReferenceName == null) {
            return ResolveResult.EMPTY_ARRAY;
        }

        LOG.debug("Find reference for propertyLeaf", myReferenceName);

        Project project = myElement.getProject();
        ORElementResolver.Resolutions resolutions = project.getService(ORElementResolver.class).getComputation();

        // Comp.make function from module
        // Find all elements by name and create a list of paths
        resolutions.add(LetIndex.getElements("make", project, null), false);

        // Gather instructions from element up to the file root
        Deque<PsiElement> instructions = ORReferenceAnalyzer.createInstructions(myElement, myTypes);

        if (LOG.isTraceEnabled()) {
            LOG.trace("  Instructions: ", Joiner.join(" -> ", instructions));
        }

        // Resolve aliases in the stack of instructions, this time from file down to element
        Deque<CodeInstruction> resolvedInstructions = ORReferenceAnalyzer.resolveInstructions(instructions, myElement.getProject());

        if (LOG.isTraceEnabled()) {
            LOG.trace("  Resolved instructions: " + Joiner.join(" -> ", resolvedInstructions));
        }

        // Now that everything is resolved, we can use the stack of instructions to add weight to the paths

        for (CodeInstruction instruction : resolvedInstructions) {
            if (instruction.mySource instanceof FileBase) {
                resolutions.udpateTerminalWeight(((FileBase) instruction.mySource).getModuleName());
            } else if (instruction.mySource instanceof PsiLowerSymbol) {
                resolutions.removeUpper();
                resolutions.updateWeight(null, instruction.myAlternateValues);
            } else if (instruction.myValues != null) {
                for (String value : instruction.myValues) {
                    resolutions.updateWeight(value, instruction.myAlternateValues);
                }
            }
        }

        resolutions.removeIncomplete();
        Collection<PsiQualifiedPathElement> sortedResult = resolutions.resolvedElements();

        if (LOG.isDebugEnabled()) {
            LOG.debug("  => found", Joiner.join(", ", sortedResult,
                    element -> element.getQualifiedName()
                            + " [" + Platform.getRelativePathToModule(element.getContainingFile()) + "]"));
        }

        ResolveResult[] resolveResults = new ResolveResult[sortedResult.size()];
        int i = 0;
        for (PsiElement element : sortedResult) {
            resolveResults[i] = new JsxTagResolveResult(element, myReferenceName);
            i++;
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
            if (referencedElement instanceof PsiLet) {
                PsiFunction function = ((PsiLet) referencedElement).getFunction();
                if (function != null) {
                    List<PsiParameter> parameters = function.getParameters();
                    for (PsiParameter parameter : parameters) {
                        if (propertyName.equals(parameter.getName())) {
                            myReferencedIdentifier = parameter;
                            break;
                        }
                    }
                }
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
