package com.reason.lang;

import java.io.*;
import java.util.*;
import java.util.stream.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.lang.ParserDefinition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.ParsingTestCase;
import com.reason.ide.files.DuneFile;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.ORUtil;
import com.reason.lang.core.PsiFileHelper;
import com.reason.lang.core.psi.ExpressionScope;
import com.reason.lang.core.psi.PsiClass;
import com.reason.lang.core.psi.PsiExternal;
import com.reason.lang.core.psi.PsiFakeModule;
import com.reason.lang.core.psi.PsiFunctor;
import com.reason.lang.core.psi.PsiInclude;
import com.reason.lang.core.psi.PsiLet;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.PsiOpen;
import com.reason.lang.core.psi.PsiType;
import com.reason.lang.core.psi.PsiVal;

import static com.intellij.psi.util.PsiTreeUtil.findChildrenOfType;
import static com.reason.lang.core.ExpressionFilterConstants.FILTER_LET;

public abstract class BaseParsingTestCase extends ParsingTestCase {
    protected BaseParsingTestCase(@NotNull String dataPath, @NotNull String fileExt, @NotNull ParserDefinition... definitions) {
        super(dataPath, fileExt, definitions);
    }

    @NotNull
    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    @NotNull
    protected Collection<PsiNameIdentifierOwner> expressions(@NotNull PsiFile file) {
        return PsiFileHelper.getExpressions(file, ExpressionScope.all, null);
    }

    @NotNull
    protected Collection<PsiInclude> includeExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getIncludeExpressions(file);
    }

    @NotNull
    protected Collection<PsiType> typeExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getTypeExpressions(file);
    }

    @NotNull
    protected Collection<PsiExternal> externalExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getExternalExpressions(file);
    }

    @NotNull
    protected Collection<PsiModule> moduleExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getModuleExpressions(file);
    }

    @NotNull
    protected Collection<PsiFunctor> functorExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getFunctorExpressions(file);
    }

    @NotNull
    protected Collection<PsiClass> classExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getClassExpressions(file);
    }

    @NotNull
    protected Collection<PsiLet> letExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getExpressions(file, ExpressionScope.all, FILTER_LET).stream().map(element -> (PsiLet) element).collect(Collectors.toList());
    }

    @NotNull
    protected Collection<PsiOpen> openExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getOpenExpressions(file);
    }

    @NotNull
    protected Collection<PsiVal> valExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getValExpressions(file);
    }

    @NotNull
    protected PsiExternal externalExpression(@NotNull PsiFile file, @NotNull String name) {
        Collection<PsiExternal> externalExpressions = PsiFileHelper.getExternalExpressions(file);
        return externalExpressions.stream().filter(psiExternal -> name.equals(psiExternal.getName())).findFirst().get();
    }

    protected PsiElement firstElement(@NotNull PsiFile fileModule) {
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
        return parseFile(name, false);
    }

    @NotNull
    protected PsiFile parseFile(String name, @SuppressWarnings("SameParameterValue") boolean print) throws IOException {
        String text = loadFile(name + "." + myFileExt);
        return parseCode(text, print);
    }

    @NotNull
    protected FileBase parseCode(@NotNull String code) {
        return parseCode(code, false);
    }

    @NotNull
    protected FileBase parseCode(@NotNull String code, boolean print) {
        parseRawCode(code, print);
        return (FileBase) myFile;
    }

    protected PsiFile parseRawCode(@NotNull String code, boolean print) {
        myFile = createPsiFile("dummy", code);
        if (print) {
            System.out.println("» " + this.getClass());
            System.out.println(DebugUtil.psiToString(myFile, true, true));
        }
        return myFile;
    }

    @NotNull
    protected DuneFile parseDuneCode(@NotNull String code) {
        return parseDuneCode(code, false);
    }

    @NotNull
    protected DuneFile parseDuneCode(@NotNull String code, boolean print) {
        myFile = createFile("jbuild", code);
        if (print) {
            System.out.println("» " + this.getClass());
            System.out.println(DebugUtil.psiToString(myFile, true, true));
        }
        return (DuneFile) myFile;
    }

    @SuppressWarnings("unused")
    void debugPsiAst(@NotNull PsiElement element) {
        System.out.println(DebugUtil.psiToString(element, false, true));
    }
}
