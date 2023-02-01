package com.reason.lang.reason;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import org.jetbrains.annotations.*;

public class RmlSafeParserDefinition extends RmlParserDefinition {
    @Override public @NotNull PsiParser createParser(Project project) {
        return new RmlParser(true);
    }
}
