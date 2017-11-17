package reason.psi;

import com.intellij.extapi.psi.ASTWrapperPsiElement;
import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiWhiteSpace;
import reason.icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class PsiLet extends ASTWrapperPsiElement implements PsiInferredType {
    private String inferredType = "";

    public PsiLet(ASTNode node) {
        super(node);
    }

    @Nullable
    public PsiValueName getLetName() {
        return findChildByClass(PsiValueName.class);
    }

    @Nullable
    public PsiFunBody getFunctionBody() {
        return findChildByClass(PsiFunBody.class);
    }

    @Nullable
    public PsiLetBinding getLetBinding() {
        return findChildByClass(PsiLetBinding.class);
    }

    private boolean isFunction() {
        return findChildByClass(PsiFunBody.class) != null;
    }

    private boolean isRecursive() {
        // Find first element after the LET
        PsiElement firstChild = getFirstChild();
        PsiElement sibling = firstChild.getNextSibling();
        if (sibling != null && sibling instanceof PsiWhiteSpace) {
            sibling = sibling.getNextSibling();
        }

        return sibling != null && "rec".equals(sibling.getText());
    }

    @Override
    public void setInferredType(String inferredType) {
        this.inferredType = inferredType;
    }

    @Override
    public String getInferredType() {
        return inferredType;
    }

    @Override
    public boolean hasInferredType() {
        return inferredType != null && !inferredType.isEmpty();
    }

    @Nullable
    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                PsiValueName letValueName = getLetName();
                if (letValueName == null) {
                    return "_";
                }

                String letName = letValueName.getText();
                if (isFunction()) {
                    return letName + "(..)" + (isRecursive() ? ": rec" : "");
                }

                return letName + (hasInferredType() ? ": " + getInferredType() : "");
            }

            @Nullable
            @Override
            public String getLocationString() {
                return null;
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                return isFunction() ? Icons.FUNCTION : Icons.LET;
            }
        };
    }
}
