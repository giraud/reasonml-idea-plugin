package reason.psi;

import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.*;
import reason.icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PsiModuleReference extends PsiReferenceBase<PsiElement> implements PsiPolyVariantReference {
    private final String myName;

    PsiModuleReference(@NotNull PsiElement element, String name) {
        super(element);
        myName = name;
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> results = new ArrayList<>();

        List<PsiFile> files = PsiUtil.findFileModules(myElement.getProject(), myName);
        for (PsiFile file : files) {
            results.add(new PsiElementResolveResult(file));
        }

        List<PsiModule> modules = PsiUtil.findModules(myElement.getProject(), myName);
        for (PsiModule module : modules) {
            results.add(new PsiElementResolveResult(module));
        }

        return results.toArray(new ResolveResult[results.size()]);
    }

    @Nullable
    @Override
    public PsiElement resolve() {
        ResolveResult[] results = multiResolve(false);
        return results.length == 1 ? results[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        List<LookupElement> result = new ArrayList<>();

        List<PsiModule> modules = PsiUtil.findModules(myElement.getProject());
        for (PsiModule module : modules) {
            result.add(LookupElementBuilder.create(module).withIcon(Icons.MODULE).withTypeText("VARIANTS:" + module.getContainingFile().getName()));
        }

        return result.toArray();
    }
}
