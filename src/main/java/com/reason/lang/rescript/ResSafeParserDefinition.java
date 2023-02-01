package com.reason.lang.rescript;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import org.jetbrains.annotations.*;

public class ResSafeParserDefinition extends ResParserDefinition {
    @Override
    public @NotNull PsiParser createParser(Project project) {
        return new ResParser(true);
    }
}
