package com.reason.ide.search.reference;

import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.util.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.RPsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class PsiLowerSymbolReference extends ORMultiSymbolReference<RPsiLowerSymbol> {
    private static final Log LOG = Log.create("ref.lower");
    private static final Log LOG_PERF = Log.create("ref.perf.lower");

    public PsiLowerSymbolReference(@NotNull RPsiLowerSymbol element, @NotNull ORTypes types) {
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

        LOG.debug("Find reference for lower symbol", myReferenceName);

        // Gather instructions from element up to the file root
        Deque<PsiElement> instructions = ORReferenceAnalyzer.createInstructions(myElement, myTypes);

        if (LOG.isTraceEnabled()) {
            LOG.trace("  Instructions: ", Joiner.join(" -> ", instructions));
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
        ORElementResolver.Resolutions resolutions = project.getService(ORElementResolver.class).getComputation();
        GlobalSearchScope scope = GlobalSearchScope.allScope(project);

        Collection<RPsiType> types = TypeIndex.getElements(myReferenceName, project, scope);
        Collection<RPsiVal> vals = ValIndex.getElements(myReferenceName, project, scope);
        Collection<RPsiLet> lets = LetIndex.getElements(myReferenceName, project, scope);
        Collection<RPsiExternal> externals = ExternalIndex.getElements(myReferenceName, project, scope);
        Collection<RPsiRecordField> recordFields = RecordFieldIndex.getElements(myReferenceName, project, scope);
        Collection<RPsiObjectField> objectFields = ObjectFieldIndex.getElements(myReferenceName, project, scope);
        Collection<RPsiParameterDeclaration> parameters = ParameterIndex.getElements(myReferenceName, project, scope);

        if (LOG.isTraceEnabled()) {
            LOG.trace("  indexes: types=" + types.size() + ", vals=" + vals.size() + ", lets=" + lets.size() +
                    ", externals=" + externals.size() + ", fieds=" + (recordFields.size() + objectFields.size()) + ", params=" + parameters.size());
        }

        long endIndexes = System.currentTimeMillis();

        resolutions.add(types, false);
        resolutions.add(vals, false);
        resolutions.add(lets, false);
        resolutions.add(externals, false);
        resolutions.add(recordFields, false);
        resolutions.add(objectFields, false);
        resolutions.add(parameters, false);

        long endAddResolutions = System.currentTimeMillis();

        resolutions.addIncludesEquivalence();

        long endIncludes = System.currentTimeMillis();

        // Now that everything is resolved, we can use the stack of instructions to add weight to the paths

        List<String> modulesInScope = new ArrayList<>();
        for (CodeInstruction instruction : resolvedInstructions) {
            if (instruction.mySource instanceof RPsiInnerModule) {
                // if the module is not in scope, we skip that resolution
                if (!modulesInScope.contains(((RPsiInnerModule) instruction.mySource).getQualifiedName())) {
                    continue;
                }
            }

            if (instruction.mySource instanceof FileBase) {
                resolutions.udpateTerminalWeight(((FileBase) instruction.mySource).getModuleName());
            } else if (instruction.mySource instanceof RPsiLowerSymbol) {
                resolutions.removeUpper();
                resolutions.updateWeight(null, instruction.myAlternateValues);
            } else if (instruction.mySource instanceof RPsiUpperSymbol) {
                // We're in a path, must be exact
                String value = instruction.getFirstValue();
                resolutions.removeIfNotFound(value, instruction.myAlternateValues);
                resolutions.updateWeight(value, instruction.myAlternateValues);
            } else {
                if (instruction.mySource instanceof RPsiOpen) {
                    // adding a module in scope
                    modulesInScope.add(((RPsiOpen) instruction.mySource).getPath());
                }

                if (instruction.myValues != null) {
                    for (String value : instruction.myValues) {
                        resolutions.updateWeight(value, instruction.myAlternateValues);
                    }
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
            resolveResults[i] = new LowerResolveResult(element, myReferenceName);
            i++;
        }

        long endAll = System.currentTimeMillis();
        if (LOG_PERF.isDebugEnabled()) {
            LOG_PERF.debug("Resolution of " + myReferenceName + " in " + (endAll - startAll) + "ms => " +
                    " i:" + (endInstructions - startAll) + "," +
                    " rI:" + (endResolvedInstructions - endInstructions) + "," +
                    " id:" + (endIndexes - endResolvedInstructions) + "," +
                    " aR:" + (endAddResolutions - endIndexes) + "," +
                    " aI:" + (endIncludes - endAddResolutions) + "," +
                    " uR:" + (endUpdateResolutions - endIncludes) + "," +
                    " sort: " + (endSort - endUpdateResolutions) + ""
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

        public boolean isInterface() {
            return FileHelper.isInterface(myReferencedIdentifier.getContainingFile().getFileType());
        }
    }
}
