package com.reason.ide.hints;

import com.intellij.lang.*;
import com.intellij.openapi.editor.*;
import com.reason.lang.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

import java.util.*;

public interface InferredTypes {
    class LogicalPositionSignature {
        String signature;
        int colStart;
        int colEnd;
    }

    @NotNull
    Map<Integer, LogicalPositionSignature> signaturesByLines(@Nullable ORLanguageProperties lang);

    @Nullable
    PsiSignature getSignatureByPosition(@NotNull LogicalPosition elementPosition);
}
