package com.reason.ide.hints;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.util.*;
import com.reason.ide.*;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.*;

// just an experiment, cancelled for now
public class SignatureProvider /*implements InlayParameterHintsProvider*/ {

    public static class InferredTypesWithLines {
        private final InferredTypes m_types;
        private final @NotNull EditorPosition m_editorPosition;
        //        private InferredTypes types;

        InferredTypesWithLines(InferredTypes types, @NotNull String[] lines) {
            m_types = types;
            m_editorPosition = new EditorPosition(lines);
        }

        public @Nullable PsiSignature getSignatureByOffset(int textOffset) {
            LogicalPosition elementPosition = m_editorPosition.getPositionFromOffset(textOffset);
            return elementPosition == null ? null : m_types.getSignatureByPosition(elementPosition);
        }

        public InferredTypes getTypes() {
            return m_types;
        }
    }

    public static final Key<InferredTypesWithLines> SIGNATURE_CONTEXT =
            Key.create("REASONML_SIGNATURE_CONTEXT");

    // @NotNull
    // @Override
    // public List<InlayInfo> getParameterHints(PsiElement element) {
    //        if (element instanceof PsiLet) {
    //            PsiLet letStatement = (PsiLet) element;
    //            if (!letStatement.getLetBinding().isFunction()) {
    //                if (letStatement.hasInferredType()) {
    //                    return singletonList(new InlayInfo(letStatement.getInferredType(),
    // 14/*letStatement.getLetBinding().getValueName().getTextOffset()*/));
    //                }
    //            }
    //        }

    // return emptyList();
    // }
    //
    // @Nullable
    // @Override
    // public HintInfo getHintInfo(PsiElement psiElement) {
    //    return null;
    // }

  /*
      @Nullable
      @Override
      public MethodInfo getMethodInfo(PsiElement element) {
          System.out.println("getMethodInfo " + element);
          if (element instanceof PsiLet) {
              MethodInfo methodInfo = new MethodInfo("fqn", asList("p1", "p2"));
              return methodInfo;
          }
          return null;
      }
  */

    // @NotNull
    // @Override
    // public Set<String> getDefaultBlackList() {
    //    return emptySet();
    // }
    //
    // @Nullable
    // @Override
    // public Language getBlackListDependencyLanguage() {
    //    return null;
    // }
    //
    // @NotNull
    // @Override
    // public List<Option> getSupportedOptions() {
    //    return emptyList();
    // }
    //
    // @Override
    // public boolean isBlackListSupported() {
    //    return false;
    // }
    //
    // @Override
    // public String getInlayPresentation(@NotNull String inlayText) {
    //    return inlayText;
    // }
}
