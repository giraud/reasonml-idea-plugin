package com.reason.lang.ocaml;

import com.intellij.lang.*;
import com.intellij.openapi.project.*;
import org.jetbrains.annotations.*;

public class OclSafeParserDefinition extends OclParserDefinition {
    @Override public @NotNull PsiParser createParser(Project project) {
        return new OclParser(true);
    }
}
