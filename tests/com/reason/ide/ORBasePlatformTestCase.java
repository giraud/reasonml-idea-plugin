package com.reason.ide;

import org.jetbrains.annotations.NotNull;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.LightPlatformCodeInsightFixtureTestCase;
import com.reason.ide.files.FileBase;

public abstract class ORBasePlatformTestCase extends LightPlatformCodeInsightFixtureTestCase {

    @NotNull
    @SuppressWarnings("UnusedReturnValue")
    protected FileBase configureCode(@NotNull String fileName, @NotNull String code) {
        return configureCode(fileName, code, false);
    }

    @NotNull
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    protected FileBase configureCode(@NotNull String fileName, @NotNull String code, boolean debug) {
        PsiFile file = myFixture.configureByText(fileName, code);
        if (debug) {
            System.out.println("» " + fileName + " " + this.getClass());
            System.out.println(DebugUtil.psiToString(file, true, true));
        }
        return (FileBase) file;
    }
}
