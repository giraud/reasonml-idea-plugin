package reason.psi;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.AbstractElementManipulator;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;

public class PsiModuleNameManipulator extends AbstractElementManipulator<PsiModuleName> {
    @Override
    public PsiModuleName handleContentChange(@NotNull PsiModuleName element, @NotNull TextRange textRange, String s) throws IncorrectOperationException {
        // TODO: why
        return element;
    }
}
