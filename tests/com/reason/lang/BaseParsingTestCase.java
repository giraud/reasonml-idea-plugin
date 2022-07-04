package com.reason.lang;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.*;
import com.intellij.psi.util.*;
import com.intellij.testFramework.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static com.intellij.psi.util.PsiTreeUtil.*;
import static com.reason.lang.core.ExpressionFilterConstants.*;

public abstract class BaseParsingTestCase extends ParsingTestCase {
    protected BaseParsingTestCase(@NotNull String dataPath, @NotNull String fileExt, @NotNull ParserDefinition... definitions) {
        super(dataPath, fileExt, definitions);
    }

    @Override
    protected @NotNull String getTestDataPath() {
        return "testData";
    }

    @NotNull
    protected List<PsiNamedElement> expressions(@NotNull PsiFile file) {
        return new ArrayList<>(PsiFileHelper.getExpressions(file, ExpressionScope.all, null));
    }

    @NotNull
    protected Collection<PsiInclude> includeExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getIncludeExpressions(file);
    }

    @NotNull
    protected List<PsiType> typeExpressions(@NotNull PsiFile file) {
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(file, PsiType.class));
    }

    @NotNull
    protected Collection<PsiExternal> externalExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getExternalExpressions(file);
    }

    @NotNull
    protected List<PsiModule> moduleExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getModuleExpressions(file);
    }

    @NotNull
    protected Collection<PsiFunctor> functorExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getFunctorExpressions(file);
    }

    @NotNull
    protected Collection<PsiKlass> classExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getClassExpressions(file);
    }

    @NotNull
    protected List<PsiLet> letAllExpressions(@NotNull PsiFile file) {
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(file, PsiLet.class));
    }

    @NotNull
    protected List<PsiLet> letExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getExpressions(file, ExpressionScope.all, FILTER_LET)
                .stream()
                .map(element -> (PsiLet) element)
                .collect(Collectors.toList());
    }

    @NotNull
    protected List<PsiOpen> openExpressions(@NotNull PsiFile file) {
        return new ArrayList<>(PsiFileHelper.getOpenExpressions(file));
    }

    @NotNull
    protected Collection<PsiVal> valExpressions(@NotNull PsiElement root) {
        return findChildrenOfType(root, PsiVal.class);
    }

    @NotNull
    protected PsiExternal externalExpression(@NotNull PsiFile file, @NotNull String name) {
        Collection<PsiExternal> externalExpressions = PsiFileHelper.getExternalExpressions(file);
        return externalExpressions
                .stream()
                .filter(psiExternal -> name.equals(psiExternal.getName()))
                .findFirst()
                .get();
    }

    protected @Nullable PsiElement firstElement(@NotNull PsiFile fileModule) {
        return ORUtil.findImmediateFirstChildWithoutClass(fileModule, PsiFakeModule.class);
    }

    public static int childrenCount(@NotNull FileBase file) {
        return file.getChildren().length - 1 /*PsiFakeModule*/;
    }

    public static <T extends PsiElement> T first(@NotNull Collection<T> collection) {
        return collection.iterator().next();
    }

    protected <T extends PsiElement> T second(@NotNull Collection<T> collection) {
        Iterator<T> iterator = collection.iterator();
        iterator.next();
        return iterator.next();
    }

    protected <T extends PsiElement> T firstOfType(PsiElement element, @NotNull Class<T> aClass) {
        return first(findChildrenOfType(element, aClass));
    }

    @NotNull
    protected PsiFile parseFile(String name) throws IOException {
        String text = loadFile(name + "." + myFileExt);
        return parseRawCode(text);
    }

    @NotNull
    protected FileBase parseCode(@NotNull String code) {
        parseRawCode(code);
        return (FileBase) myFile;
    }

    protected PsiFile parseRawCode(@NotNull String code) {
        myFile = createPsiFile("dummy", code);
        System.out.println("» " + this.getClass());
        System.out.println(DebugUtil.psiToString(myFile, false, true));
        return myFile;
    }

    @NotNull
    protected DuneFile parseDuneCode(@NotNull String code) {
        myFile = createFile("jbuild", code);
        System.out.println("» " + this.getClass());
        System.out.println(DebugUtil.psiToString(myFile, true, true));
        return (DuneFile) myFile;
    }

    @SuppressWarnings("unused")
    void debugPsiAst(@NotNull PsiElement element) {
        System.out.println(DebugUtil.psiToString(element, false, true));
    }
}
