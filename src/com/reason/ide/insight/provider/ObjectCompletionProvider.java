package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.search.index.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import com.reason.lang.core.psi.reference.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class ObjectCompletionProvider {
    private static final Log LOG = Log.create("insight.object");

    private ObjectCompletionProvider() {
    }

    public static void addCompletions(@NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("OBJECT expression completion");

        PsiElement separator = PsiTreeUtil.prevVisibleLeaf(element);
        PsiElement previousElement = separator == null ? null : separator.getPrevSibling();

        QNameFinder qnameFinder = QNameFinderFactory.getQNameFinder(element.getLanguage());

        if (previousElement instanceof PsiLowerSymbol) {
            LOG.debug(" -> lower symbol", previousElement);

            PsiLowerSymbolReference reference = (PsiLowerSymbolReference) previousElement.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
            if (LOG.isDebugEnabled()) {
                LOG.debug(" -> resolved to", resolvedElement == null ? null : resolvedElement.getParent());
            }

            if (resolvedElement instanceof PsiLowerIdentifier) {
                Collection<PsiObjectField> fields = getFields(qnameFinder, resolvedElement);

                if (fields == null) {
                    LOG.debug("  -> Not a js object");
                } else {
                    for (PsiObjectField field : fields) {
                        String fieldName = field.getName();
                        resultSet.addElement(LookupElementBuilder.create(fieldName)
                                .withIcon(PsiIconUtil.getProvidersIcon(field, 0)));
                    }
                }
            }
        }

        LOG.debug("  -> Nothing found");
    }

    private static @Nullable Collection<PsiObjectField> getFields(@NotNull QNameFinder qnameFinder, @NotNull PsiElement resolvedElement) {
        PsiElement resolvedParent = resolvedElement.getParent();
        if (resolvedParent instanceof PsiLet) {
            PsiLet let = (PsiLet) resolvedParent;
            if (let.isJsObject()) {
                PsiJsObject jsObject = ORUtil.findImmediateFirstChildOfClass(let.getBinding(), PsiJsObject.class);
                return jsObject == null ? null : jsObject.getFields();
            } else {
                PsiType type = getType(let, qnameFinder);
                if (type != null && type.isJsObject()) {
                    PsiJsObject jsObject = ORUtil.findImmediateFirstChildOfClass(type.getBinding(), PsiJsObject.class);
                    return jsObject == null ? null : jsObject.getFields();
                }
            }
        } else if (resolvedParent instanceof PsiObjectField) {
            PsiObjectField field = (PsiObjectField) resolvedParent;
            PsiElement value = field.getValue();
            if (value instanceof PsiJsObject) {
                return ((PsiJsObject) value).getFields();
            } else {
                // Must be an object defined outside
                PsiLowerSymbol lSymbol = ORUtil.findImmediateLastChildOfClass(field, PsiLowerSymbol.class);
                PsiLowerSymbolReference valueReference = lSymbol == null ? null : (PsiLowerSymbolReference) lSymbol.getReference();
                PsiElement valueResolvedElement = valueReference == null ? null : valueReference.resolveInterface();
                return valueResolvedElement == null ? null : getFields(qnameFinder, valueResolvedElement);
            }
        }
        return null;
    }

    private static @Nullable PsiType getType(@NotNull PsiLet let, @NotNull QNameFinder qnameFinder) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(let.getProject());
        PsiSignature letSignature = let.getSignature();
        if (letSignature != null) {
            LOG.debug("Testing let signature", letSignature.getText());

            Set<String> paths = qnameFinder.extractPotentialPaths(let);
            LOG.debug("  Paths found", paths);

            Project project = let.getProject();
            String signatureName = "." + letSignature.getText();
            for (String path : paths) {
                Collection<PsiType> types = TypeFqnIndex.getElements((path + signatureName).hashCode(), project, scope);
                if (!types.isEmpty()) {
                    PsiType type = types.iterator().next();
                    LOG.debug("  -> Found", type);
                    return type;
                }
            }
        }

        return null;
    }
}
