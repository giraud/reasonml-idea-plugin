package com.reason.ide.insight.provider;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.util.*;
import com.reason.ide.search.reference.*;
import com.reason.lang.core.*;
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

        if (previousElement instanceof RPsiLowerSymbol previousLowerSymbol) {
            LOG.debug(" -> lower symbol", previousLowerSymbol);

            PsiElement resolvedElement = previousLowerSymbol.getReference().resolveInterface();
            if (LOG.isDebugEnabled()) {
                LOG.debug(" -> resolved to", resolvedElement == null ? null : resolvedElement.getParent());
            }

            if (resolvedElement != null) {
                Collection<RPsiObjectField> fields = getFields(resolvedElement);

                if (fields == null) {
                    LOG.debug("  -> Not a js object/record");
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

    static @Nullable Collection<RPsiObjectField> getFields(@Nullable PsiElement resolvedElement) {
        if (resolvedElement instanceof RPsiLet let) {
            if (let.isJsObject()) {
                RPsiJsObject jsObject = ORUtil.findImmediateFirstChildOfClass(let.getBinding(), RPsiJsObject.class);
                return jsObject == null ? null : jsObject.getFields();
            } else {
                RPsiSignature letSignature = let.getSignature();
                if (letSignature != null && !letSignature.isFunction()) {
                    LOG.debug("Testing let signature", letSignature.getText());

                    RPsiLowerSymbol sigTerm = ORUtil.findImmediateLastChildOfClass(letSignature.getItems().get(0), RPsiLowerSymbol.class);
                    ORPsiLowerSymbolReference sigReference = sigTerm == null ? null : sigTerm.getReference();
                    PsiElement resolvedSignature = sigReference == null ? null : sigReference.resolve();

                    if (resolvedSignature instanceof RPsiType && ((RPsiType) resolvedSignature).isJsObject()) {
                        return ((RPsiType) resolvedSignature).getJsObjectFields();
                    }
                }
            }
        } else if (resolvedElement instanceof RPsiObjectField resolvedObjectField) {
            RPsiFieldValue value = resolvedObjectField.getValue();
            PsiElement valueElement = value != null ? value.getFirstChild() : null;
            if (valueElement instanceof RPsiJsObject valueObject) {
                return valueObject.getFields();
            } else if (valueElement instanceof RPsiLowerSymbol valueLowerSymbol) {
                // Must be an object defined outside
                PsiElement valueResolvedElement = valueLowerSymbol.getReference().resolveInterface();
                return valueResolvedElement != null ? getFields(valueResolvedElement) : null;
            } else if (valueElement instanceof RPsiUpperSymbol) {
                // Must be a path of an object defined outside
                PsiElement lSymbol = ORUtil.nextSiblingWithTokenType(valueElement, ORUtil.getTypes(resolvedElement.getLanguage()).LIDENT);
                ORPsiLowerSymbolReference valueReference = lSymbol == null ? null : (ORPsiLowerSymbolReference) lSymbol.getReference();
                PsiElement valueResolvedElement = valueReference == null ? null : valueReference.resolveInterface();
                return valueResolvedElement == null ? null : getFields(valueResolvedElement);
            }
        }
        return null;
    }
}
