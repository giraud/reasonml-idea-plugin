package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.search.index.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.RPsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
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

        if (previousElement instanceof RPsiLowerSymbol) {
            LOG.debug(" -> lower symbol", previousElement);

            PsiLowerSymbolReference reference = (PsiLowerSymbolReference) previousElement.getReference();
            PsiElement resolvedElement = reference == null ? null : reference.resolveInterface();
            if (LOG.isDebugEnabled()) {
                LOG.debug(" -> resolved to", resolvedElement == null ? null : resolvedElement.getParent());
            }

            if (resolvedElement != null) {
                Collection<RPsiObjectField> fields = getFields(qnameFinder, resolvedElement);

                if (fields == null) {
                    LOG.debug("  -> Not a js object");
                } else {
                    for (RPsiObjectField field : fields) {
                        String fieldName = field.getName();
                        resultSet.addElement(LookupElementBuilder.create(fieldName)
                                .withIcon(PsiIconUtil.getProvidersIcon(field, 0)));
                    }
                }
            }
        }

        LOG.debug("  -> Nothing found");
    }

    private static @Nullable Collection<RPsiObjectField> getFields(@NotNull QNameFinder qnameFinder, @NotNull PsiElement resolvedElement) {
        if (resolvedElement instanceof RPsiLet) {
            RPsiLet let = (RPsiLet) resolvedElement;
            if (let.isJsObject()) {
                RPsiJsObject jsObject = ORUtil.findImmediateFirstChildOfClass(let.getBinding(), RPsiJsObject.class);
                return jsObject == null ? null : jsObject.getFields();
            } else {
                RPsiType type = getType(let, qnameFinder);
                if (type != null && type.isJsObject()) {
                    RPsiJsObject jsObject = ORUtil.findImmediateFirstChildOfClass(type.getBinding(), RPsiJsObject.class);
                    return jsObject == null ? null : jsObject.getFields();
                }
            }
        } else if (resolvedElement instanceof RPsiObjectField) {
            PsiElement value = ((RPsiObjectField) resolvedElement).getValue();
            if (value instanceof RPsiJsObject) {
                return ((RPsiJsObject) value).getFields();
            } else if (value instanceof RPsiLowerSymbol) {
                // Must be an object defined outside
                PsiLowerSymbolReference valueReference = (PsiLowerSymbolReference) value.getReference();
                PsiElement valueResolvedElement = valueReference == null ? null : valueReference.resolveInterface();
                return valueResolvedElement == null ? null : getFields(qnameFinder, valueResolvedElement);
            } else if (value instanceof RPsiUpperSymbol) {
                // Must be a path of an object defined outside
                PsiElement lSymbol = ORUtil.nextSiblingWithTokenType(value, ORUtil.getTypes(resolvedElement.getLanguage()).LIDENT);
                PsiLowerSymbolReference valueReference = lSymbol == null ? null : (PsiLowerSymbolReference) lSymbol.getReference();
                PsiElement valueResolvedElement = valueReference == null ? null : valueReference.resolveInterface();
                return valueResolvedElement == null ? null : getFields(qnameFinder, valueResolvedElement);
            }
        }
        return null;
    }

    private static @Nullable RPsiType getType(@NotNull RPsiLet let, @NotNull QNameFinder qnameFinder) {
        GlobalSearchScope scope = GlobalSearchScope.allScope(let.getProject());
        RPsiSignature letSignature = let.getSignature();
        if (letSignature != null) {
            LOG.debug("Testing let signature", letSignature.getText());

            Set<String> paths = qnameFinder.extractPotentialPaths(let);
            LOG.debug("  Paths found", paths);

            Project project = let.getProject();
            String signatureName = "." + letSignature.getText();
            for (String path : paths) {
                Collection<RPsiType> types = TypeFqnIndex.getElements((path + signatureName).hashCode(), project, scope);
                if (!types.isEmpty()) {
                    RPsiType type = types.iterator().next();
                    LOG.debug("  -> Found", type);
                    return type;
                }
            }
        }

        return null;
    }
}
