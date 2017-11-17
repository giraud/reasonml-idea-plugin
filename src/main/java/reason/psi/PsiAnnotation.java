package reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;

public class PsiAnnotation extends ASTWrapperPsiElement {

    public PsiAnnotation(ASTNode node) {
        super(node);
    }

    @NotNull
    PsiAnnotationName getAnnotationName() {
        return findNotNullChildByClass(PsiAnnotationName.class);
    }

    @Override
    public String toString() {
        return "Annotation '" + getAnnotationName() + "'";
    }
}
