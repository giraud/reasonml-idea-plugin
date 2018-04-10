package com.reason;

import com.intellij.lang.ParserDefinition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.ParsingTestCase;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.psi.PsiModule;
import com.reason.lang.core.psi.impl.PsiFileModuleImpl;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public abstract class BaseParsingTestCase extends ParsingTestCase {
    protected BaseParsingTestCase(@NotNull String dataPath, @NotNull String fileExt, @NotNull ParserDefinition... definitions) {
        super(dataPath, fileExt, definitions);
    }

    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    protected <T> T first(Collection<T> collection) {
        return collection.iterator().next();
    }

    protected PsiFileModuleImpl parseCode(String code) {
        return parseCode(code, false);
    }

    protected PsiFileModuleImpl parseCode(String code, boolean print) {
        myFile = createPsiFile("dummy", code);
        FileBase file = (FileBase) myFile;
        if (print) {
            System.out.println(DebugUtil.psiToString(file, false, true));
        }
        return (PsiFileModuleImpl) file.asModule();
    }

    protected PsiModule doMlTest() {
        doTest(false);
        return ((FileBase) myFile).asModule();
    }

    @SuppressWarnings("unused")
    void debugPsiAst(PsiElement element) {
        System.out.println(DebugUtil.psiToString(element, false, true));
    }
}
