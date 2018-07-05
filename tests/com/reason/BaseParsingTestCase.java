package com.reason;

import com.intellij.lang.ParserDefinition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.ParsingTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.PsiFileHelper;
import com.reason.lang.core.psi.*;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;

import static com.intellij.psi.util.PsiTreeUtil.findChildrenOfType;

public abstract class BaseParsingTestCase extends ParsingTestCase {
    protected BaseParsingTestCase(@NotNull String dataPath, @NotNull String fileExt, @NotNull ParserDefinition... definitions) {
        super(dataPath, fileExt, definitions);
    }

    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    protected Collection<PsiNamedElement> expressions(@NotNull PsiFile file) {
        return PsiFileHelper.getExpressions(file);
    }

    protected Collection<PsiInclude> includeExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getIncludeExpressions(file);
    }

    protected Collection<PsiType> typeExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getTypeExpressions(file);
    }

    protected Collection<PsiModule> moduleExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getModuleExpressions(file);
    }

    protected Collection<PsiLet> letExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getLetExpressions(file);
    }

    protected PsiExternal externalExpression(@NotNull PsiFile file, @NotNull String name) {
        Collection<PsiExternal> externalExpressions = PsiFileHelper.getExternalExpressions(file);
        return externalExpressions.stream().filter(psiExternal -> name.equals(psiExternal.getName())).findFirst().get();
    }

    protected PsiElement firstElement(PsiFile fileModule) {
        return fileModule.getFirstChild();
    }

    protected <T extends PsiElement> T first(Collection<T> collection) {
        return collection.iterator().next();
    }

    protected <T extends PsiElement> T second(Collection<T> collection) {
        Iterator<T> iterator = collection.iterator();
        iterator.next();
        return iterator.next();
    }

    protected <T extends PsiElement> T firstOfType(PsiElement element, Class<T> aClass) {
        return first(findChildrenOfType(element, aClass));
    }

    protected PsiFile parseCode(String code) {
        return parseCode(code, false);
    }

    protected PsiFile parseCode(String code, boolean print) {
        myFile = createPsiFile("dummy", code);
        FileBase file = (FileBase) myFile;
        if (print) {
            System.out.println(DebugUtil.psiToString(file, false, true));
        }
        return file;
    }

    @SuppressWarnings("unused")
    void debugPsiAst(PsiElement element) {
        System.out.println(DebugUtil.psiToString(element, false, true));
    }
}
