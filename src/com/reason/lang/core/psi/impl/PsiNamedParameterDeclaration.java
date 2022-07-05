package com.reason.lang.core.psi.impl;

import com.intellij.lang.*;
import com.intellij.psi.stubs.*;
import com.intellij.psi.util.*;
import com.reason.lang.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.stub.*;
import com.reason.lang.core.type.*;
import com.reason.lang.ocaml.*;
import org.jetbrains.annotations.*;

public class PsiNamedParameterDeclaration extends PsiParameterDeclaration implements PsiLanguageConverter {
    // region Constructors
    public PsiNamedParameterDeclaration(@NotNull ORTypes types, @NotNull ASTNode node) {
        super(types, node);
    }

    public PsiNamedParameterDeclaration(@NotNull ORTypes types, @NotNull PsiParameterStub stub, @NotNull IStubElementType nodeType) {
        super(types, stub, nodeType);
    }
    // endregion

    public @Nullable PsiDefaultValue getDefaultValue() {
        return ORUtil.findImmediateFirstChildOfClass(this, PsiDefaultValue.class);
    }

    public @Nullable PsiSignature getSignature() {
        return PsiTreeUtil.findChildOfType(this, PsiSignature.class);
    }

    @Override
    public @NotNull String asText(@Nullable ORLanguageProperties toLang) {
        StringBuilder convertedText = null;
        Language fromLang = getLanguage();

        if (fromLang != toLang) {
            if (fromLang == OclLanguage.INSTANCE) {
                convertedText = new StringBuilder();
                convertedText.append("~").append(getName());
                PsiSignature signature = getSignature();
                if (signature != null) {
                    convertedText.append(":").append(signature.asText(toLang));
                }
            }
        }

        return convertedText == null ? getText() : convertedText.toString();
    }
}
