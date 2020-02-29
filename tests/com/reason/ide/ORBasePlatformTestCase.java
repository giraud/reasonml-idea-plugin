package com.reason.ide;

import java.io.*;
import org.jetbrains.annotations.NotNull;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.CharsetToolkit;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.DebugUtil;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.reason.ide.files.FileBase;

public abstract class ORBasePlatformTestCase extends BasePlatformTestCase {

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

    @NotNull
    protected String toJson(@NotNull String value) {
        return value.replaceAll("'", "\"").replaceAll("@", "\n");
    }

    protected String loadJson(@NotNull String filename) throws IOException {
        return FileUtil.loadFile(new File(getTestDataPath(), filename), CharsetToolkit.UTF8, true).trim();
    }
}
