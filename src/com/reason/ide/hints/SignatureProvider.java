package com.reason.ide.hints;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.util.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

// just an experiment, cancelled for now
public class SignatureProvider /*implements InlayParameterHintsProvider*/ {
    private SignatureProvider() {
    }

    public static class InferredTypesWithLines {
        private final InferredTypes myTypes;
        private final EditorPosition myEditorPosition;

        InferredTypesWithLines(@NotNull InferredTypes types, String @NotNull [] lines) {
            myTypes = types;
            myEditorPosition = new EditorPosition(lines);
        }

        public @Nullable PsiSignature getSignatureByOffset(int textOffset) {
            LogicalPosition elementPosition = myEditorPosition.getPositionFromOffset(textOffset);
            return elementPosition == null ? null : myTypes.getSignatureByPosition(elementPosition);
        }

        public @NotNull InferredTypes getTypes() {
            return myTypes;
        }
    }

    public static final Key<InferredTypesWithLines> SIGNATURES_CONTEXT = Key.create("REASONML_SIGNATURES_CONTEXT");
}
