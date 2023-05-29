package com.reason.lang;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.impl.*;
import com.intellij.psi.tree.*;
import com.intellij.psi.util.*;
import com.intellij.testFramework.*;
import com.reason.ide.files.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.*;
import org.junit.runner.*;
import org.junit.runners.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static com.intellij.psi.util.PsiTreeUtil.*;

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
        return ORUtil.findImmediateChildrenOfClass(file, PsiNamedElement.class);
    }

    @NotNull
    protected Collection<RPsiInclude> includeExpressions(@NotNull PsiFile file) {
        return getStubChildrenOfTypeAsList(file, RPsiInclude.class);
    }

    @NotNull
    protected List<RPsiType> typeExpressions(@NotNull PsiFile file) {
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(file, RPsiType.class));
    }

    @NotNull
    protected Collection<RPsiExternal> externalExpressions(@NotNull PsiFile file) {
        return getStubChildrenOfTypeAsList(file, RPsiExternal.class);
    }

    @NotNull
    protected List<RPsiInnerModule> moduleExpressions(@NotNull PsiFile file) {
        return getStubChildrenOfTypeAsList(file, RPsiInnerModule.class);
    }

    @NotNull
    protected Collection<RPsiFunctor> functorExpressions(@NotNull PsiFile file) {
        return getStubChildrenOfTypeAsList(file, RPsiFunctor.class);
    }

    @NotNull
    protected Collection<RPsiClass> classExpressions(@NotNull PsiFile file) {
        return getStubChildrenOfTypeAsList(file, RPsiClass.class);
    }

    @NotNull
    protected List<RPsiLet> letAllExpressions(@NotNull PsiFile file) {
        return new ArrayList<>(PsiTreeUtil.findChildrenOfType(file, RPsiLet.class));
    }

    @NotNull
    protected List<RPsiOpen> openExpressions(@NotNull PsiFile file) {
        return new ArrayList<>(getStubChildrenOfTypeAsList(file, RPsiOpen.class));
    }

    @NotNull
    protected Collection<RPsiVal> valExpressions(@NotNull PsiElement root) {
        return findChildrenOfType(root, RPsiVal.class);
    }

    protected RPsiExternal externalExpression(@NotNull PsiFile file, @NotNull String name) {
        return getStubChildrenOfTypeAsList(file, RPsiExternal.class)
                .stream()
                .filter(psiExternal -> name.equals(psiExternal.getName()))
                .findFirst()
                .orElse(null);
    }

    protected @Nullable PsiElement firstElement(@NotNull PsiFile fileModule) {
        return fileModule.getFirstChild();
    }

    public static int childrenCount(@NotNull FileBase file) {
        return file.getChildren().length;
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

    protected <T extends PsiElement> List<T> childrenOfType(PsiElement element, @NotNull Class<T> aClass) {
        return new ArrayList<>(findChildrenOfType(element, aClass));
    }

    @NotNull protected List<IElementType> extractUpperSymbolTypes(PsiElement e) {
        Collection<RPsiUpperSymbol> symbols = findChildrenOfType(e, RPsiUpperSymbol.class);
        return symbols
                .stream()
                .map(psi -> psi.getNode().getElementType())
                .collect(Collectors.toList());
    }

    @NotNull protected List<IElementType> extractLowerSymbolTypes(PsiElement e) {
        return PsiTreeUtil.findChildrenOfType(e, RPsiLowerSymbol.class)
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
