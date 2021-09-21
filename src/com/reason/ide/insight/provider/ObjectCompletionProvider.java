package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.search.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.type.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ObjectCompletionProvider {

    private static final Log LOG = Log.create("insight.object");

    private ObjectCompletionProvider() {
    }

    public static void addCompletions(@NotNull ORTypes types, @NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("OBJECT expression completion");

        Project project = element.getProject();

        QNameFinder qnameFinder = PsiFinder.getQNameFinder(element.getLanguage());
        PsiFinder psiFinder = project.getService(PsiFinder.class);

        Pair<PsiElement, List<String>> deepPath = extractDeepJsPath(types, element);
        List<String> jsPath = deepPath.second;
        PsiElement lSymbol = deepPath.first;
        String name = lSymbol.getText();

        Set<String> potentialPaths = qnameFinder.extractPotentialPaths(lSymbol);
        LOG.debug("Potential paths", potentialPaths);

        for (String path : potentialPaths) {
            PsiLet let = psiFinder.findLetFromQn(path + "." + name);
            if (let != null) {
                LOG.debug("  -> Found", let);

                Collection<PsiObjectField> fields = null;
                if (let.isJsObject()) {
                    PsiJsObject jsObject = ORUtil.findImmediateFirstChildOfClass(let.getBinding(), PsiJsObject.class);
                    fields = getJsObjectFieldsForPath(jsObject, jsPath, psiFinder, qnameFinder);
                } else {
                    PsiType type = getType(let, qnameFinder, psiFinder);
                    if (type != null && type.isJsObject()) {
                        PsiJsObject jsObject = ORUtil.findImmediateFirstChildOfClass(type.getBinding(), PsiJsObject.class);
                        fields = getJsObjectFieldsForPath(jsObject, jsPath, psiFinder, qnameFinder);
                    }
                }

                if (fields == null) {
                    LOG.debug("  -> Not a js object");
                } else {
                    for (PsiObjectField field : fields) {
                        String fieldName = field.getName();
                        if (fieldName != null) {
                            resultSet.addElement(LookupElementBuilder.create(fieldName)
                                    .withIcon(PsiIconUtil.getProvidersIcon(field, 0)));
                        }
                    }
                }

                return;
            }
        }

        LOG.debug("  -> Nothing found");
    }

    public static @NotNull Collection<PsiObjectField> getJsObjectFieldsForPath(@Nullable PsiJsObject root, @NotNull List<String> path,
                                                                               @NotNull PsiFinder psiFinder, @NotNull QNameFinder qnameFinder) {
        if (root != null) {
            PsiJsObject currentJsObject = root;
            int found = 0;

            for (String fieldName : path) {
                PsiObjectField field = currentJsObject.getField(fieldName);
                if (field != null) {
                    PsiJsObject nextJsObject = ORUtil.findImmediateFirstChildOfClass(field, PsiJsObject.class);
                    if (nextJsObject == null) {
                        // Must be an object defined outside
                        PsiElement value = field.getValue();
                        PsiElement childValue = findLet(value, qnameFinder, psiFinder);
                        if (childValue instanceof PsiJsObject) {
                            nextJsObject = (PsiJsObject) childValue;
                        } else {
                            break;
                        }
                    }
                    currentJsObject = nextJsObject;
                    found++;
                } else {
                    break;
                }
            }

            if (found == path.size()) {
                return currentJsObject.getFields();
            }
        }

        return Collections.emptyList();
    }

    private static @Nullable PsiElement findLet(@Nullable PsiElement fieldValue, @NotNull QNameFinder qnameFinder, @NotNull PsiFinder psiFinder) {
        String name = ORUtil.getLongIdent(fieldValue);

        Set<String> paths = qnameFinder.extractPotentialPaths(fieldValue);
        for (String path : paths) {
            PsiLet let = psiFinder.findLetFromQn((path.isEmpty() ? "" : path + ".") + name);
            if (let != null) {
                // val ?
                PsiLetBinding binding = let.getBinding();
                return binding == null ? null : binding.getFirstChild();
            }
        }

        return null;
    }

    private static @NotNull Pair<PsiElement, List<String>> extractDeepJsPath(@NotNull ORTypes types, @NotNull PsiElement element) {
        List<String> jsPath = new ArrayList<>();

        PsiElement lastElement = element;
        PsiElement previousLeaf = PsiTreeUtil.prevVisibleLeaf(element);
        if (previousLeaf != null) {
            IElementType previousElementType = previousLeaf.getNode().getElementType();
            while (previousLeaf != null && previousElementType == types.LIDENT || previousElementType == types.SHARPSHARP) {
                if (previousElementType == types.LIDENT) {
                    jsPath.add(previousLeaf.getText());
                    lastElement = previousLeaf.getParent();
                }
                previousLeaf = PsiTreeUtil.prevLeaf(previousLeaf);
                previousElementType = previousLeaf == null ? null : previousLeaf.getNode().getElementType();
            }

            Collections.reverse(jsPath);
        }

        if (!jsPath.isEmpty()) {
            jsPath.remove(0);
        }

        return Pair.create(lastElement, jsPath);
    }

    private static @Nullable PsiType getType(@NotNull PsiLet let, @NotNull QNameFinder qnameFinder, @NotNull PsiFinder psiFinder) {
        PsiSignature letSignature = let.getSignature();
        if (letSignature != null) {
            LOG.debug("Testing let signature", letSignature.getText());

            Set<String> paths = qnameFinder.extractPotentialPaths(let);
            LOG.debug("  Paths found", paths);

            GlobalSearchScope scope = GlobalSearchScope.allScope(let.getProject());
            String signatureName = "." + letSignature.getText();
            for (String path : paths) {
                PsiType type = psiFinder.findTypeFromQn(path + signatureName);
                if (type != null) {
                    LOG.debug("  -> Found", type);
                    return type;
                }
            }
        }

        return null;
    }
}
