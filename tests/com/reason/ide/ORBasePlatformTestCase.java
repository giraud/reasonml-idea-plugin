package com.reason.ide;

import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.reason.ide.files.FileBase;

public class ORBasePlatformTestCase extends BasePlatformTestCase {

    @SuppressWarnings("UnusedReturnValue")
    protected FileBase configureCode(String fileName, String code) {
        return configureCode(fileName, code, false);
    }

    @SuppressWarnings("SameParameterValue")
    FileBase configureCode(String fileName, String code, boolean debug) {
        PsiFile file = myFixture.configureByText(fileName, code);
        if (debug) {
            System.out.println("» " + fileName + " " + this.getClass());
            System.out.println(DebugUtil.psiToString(file, true, true));
        }
        return (FileBase) file;
    }

}
