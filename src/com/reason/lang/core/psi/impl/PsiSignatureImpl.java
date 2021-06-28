package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.tree.*;
import com.reason.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.type.*;
import com.reason.lang.reason.*;
import com.reason.lang.rescript.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.stream.*;

public class PsiSignatureImpl extends CompositeTypePsiElement<ORTypes> implements PsiSignature {
    private static final String REASON_SEPARATOR = " => ";
    private static final String OCAML_SEPARATOR = " -> ";

    PsiSignatureImpl(@NotNull ORTypes types, @NotNull IElementType elementType) {
        super(types, elementType);
    }

    @Override
    public boolean isFunction() {
        return getItems().size() > 1;
    }

    @Override
    public @NotNull String asText(@NotNull Language lang) {
        List<PsiSignatureItem> items = getItems();
        if (items.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        boolean isFunction = 1 < items.size();
        boolean reason = lang == RmlLanguage.INSTANCE || lang == ResLanguage.INSTANCE;
        String inputSeparator = reason ? ", " : OCAML_SEPARATOR;

        List<String> conversions = items.stream().map(item -> item.asText(lang)).collect(Collectors.toList());
        String result = conversions.remove(items.size() - 1);

        if (isFunction) {
            if (reason && 1 < conversions.size()) {
                sb.append("(");
            }
            sb.append(Joiner.join(inputSeparator, conversions));
            if (reason && 1 < conversions.size()) {
                sb.append(")");
            }
            sb.append(reason ? REASON_SEPARATOR : OCAML_SEPARATOR);
        }
        sb.append(result);

        String text = sb.toString().replaceAll("\\s+", " ");
        if (lang == ResLanguage.INSTANCE) {
            text = text
                    .replaceAll("< ", "<")
                    .replaceAll(", >", ">");
        }

        return text
                .replaceAll("\\( ", "\\(")
                .replaceAll(", \\)", "\\)");
    }

    @Override
    public @NotNull List<PsiSignatureItem> getItems() {
        return ORUtil.findImmediateChildrenOfClass(this, PsiSignatureItem.class);
    }

    @Override
    public @NotNull String toString() {
        return "PsiSignature";
    }
}
