package reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;

public class PsiToken extends ASTWrapperPsiElement {

    public PsiToken(ASTNode node) {
        super(node);
    }

}
