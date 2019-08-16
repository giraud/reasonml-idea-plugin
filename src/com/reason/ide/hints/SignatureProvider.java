package com.reason.ide.hints;

import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.util.Key;
import com.reason.ide.EditorPosition;
import com.reason.lang.core.signature.ORSignature;
import org.jetbrains.annotations.Nullable;

// just an experiment, cancelled for now
public class SignatureProvider /*implements InlayParameterHintsProvider*/ {

    public static class InferredTypesWithLines {
        private InferredTypes m_types;
        private EditorPosition m_editorPosition;
        private InferredTypes types;

        InferredTypesWithLines(InferredTypes types, String[] lines) {
            m_types = types;
            m_editorPosition = new EditorPosition(lines);
        }

        @Nullable
        public ORSignature getSignatureByOffset(int textOffset) {
            LogicalPosition elementPosition = m_editorPosition.getPositionFromOffset(textOffset);
            return m_types.getSignatureByPosition(elementPosition);
        }

        public InferredTypes getTypes() {
            return m_types;
        }
    }

    public static final Key<InferredTypesWithLines> SIGNATURE_CONTEXT = Key.create("REASONML_SIGNATURE_CONTEXT");

    //@NotNull
    //@Override
    //public List<InlayInfo> getParameterHints(PsiElement element) {
//        if (element instanceof PsiLet) {
//            PsiLet letStatement = (PsiLet) element;
//            if (!letStatement.getLetBinding().isFunction()) {
//                if (letStatement.hasInferredType()) {
//                    return singletonList(new InlayInfo(letStatement.getInferredType(), 14/*letStatement.getLetBinding().getValueName().getTextOffset()*/));
//                }
//            }
//        }

    //return emptyList();
    //}
    //
    //@Nullable
    //@Override
    //public HintInfo getHintInfo(PsiElement psiElement) {
    //    return null;
    //}

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

    //@NotNull
    //@Override
    //public Set<String> getDefaultBlackList() {
    //    return emptySet();
    //}
    //
    //@Nullable
    //@Override
    //public Language getBlackListDependencyLanguage() {
    //    return null;
    //}
    //
    //@NotNull
    //@Override
    //public List<Option> getSupportedOptions() {
    //    return emptyList();
    //}
    //
    //@Override
    //public boolean isBlackListSupported() {
    //    return false;
    //}
    //
    //@Override
    //public String getInlayPresentation(@NotNull String inlayText) {
    //    return inlayText;
    //}
}
