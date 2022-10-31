package com.reason.lang;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.testFramework.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.RPsiType;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static com.intellij.psi.util.PsiTreeUtil.*;
import static com.reason.lang.core.ExpressionFilterConstants.*;

@RunWith(JUnit4.class)
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
    protected Collection<RPsiInclude> includeExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getIncludeExpressions(file);
    }

    @NotNull
    protected List<RPsiType> typeExpressions(@NotNull PsiFile file) {
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(file, RPsiType.class));
    }

    @NotNull
    protected Collection<RPsiExternal> externalExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getExternalExpressions(file);
    }

    @NotNull
    protected List<RPsiModule> moduleExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getModuleExpressions(file);
    }

    @NotNull
    protected Collection<RPsiFunctor> functorExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getFunctorExpressions(file);
    }

    @NotNull
    protected Collection<RPsiClass> classExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getClassExpressions(file);
    }

    @NotNull
    protected List<RPsiLet> letAllExpressions(@NotNull PsiFile file) {
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(file, RPsiLet.class));
    }

    @NotNull
    protected List<RPsiLet> letExpressions(@NotNull PsiFile file) {
        return PsiFileHelper.getExpressions(file, ExpressionScope.all, FILTER_LET)
                .stream()
                .map(element -> (RPsiLet) element)
                .collect(Collectors.toList());
    }

    @NotNull
    protected List<RPsiOpen> openExpressions(@NotNull PsiFile file) {
        return new ArrayList<>(PsiFileHelper.getOpenExpressions(file));
    }

    @NotNull
    protected Collection<RPsiVal> valExpressions(@NotNull PsiElement root) {
        return findChildrenOfType(root, RPsiVal.class);
    }

    @NotNull
    protected RPsiExternal externalExpression(@NotNull PsiFile file, @NotNull String name) {
        Collection<RPsiExternal> externalExpressions = PsiFileHelper.getExternalExpressions(file);
        return externalExpressions
                .stream()
                .filter(psiExternal -> name.equals(psiExternal.getName()))
                .findFirst()
                .get();
    }

    protected @Nullable PsiElement firstElement(@NotNull PsiFile fileModule) {
        return ORUtil.findImmediateFirstChildWithoutClass(fileModule, RPsiFakeModule.class);
    }

    public static int childrenCount(@NotNull FileBase file) {
        return file.getChildren().length - 1 /*RPsiFakeModule*/;
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

    protected <T extends PsiElement> List<T> children(PsiElement element, @NotNull Class<T> aClass) {
        return new ArrayList<>(findChildrenOfType(element, aClass));
    }

    @NotNull protected List<IElementType> extractUpperSymbolTypes(PsiElement e) {
        return PsiTreeUtil.findChildrenOfType(e, RPsiUpperSymbol.class)
                .stream()
                .map(psi -> psi.getNode().getElementType())
                .collect(Collectors.toList());
    }

    protected void assertNoParserError(PsiElement e) {
        assertNull(PsiTreeUtil.findChildOfType(e, PsiErrorElement.class));
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
        System.out.println(DebugUtil.psiToString(myFile, false, true));
        return (DuneFile) myFile;
    }

    @SuppressWarnings("unused")
    void debugPsiAst(@NotNull PsiElement element) {
        System.out.println(DebugUtil.psiToString(element, false, true));
    }
}
