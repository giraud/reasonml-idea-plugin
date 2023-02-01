package com.reason.lang.core.stub.type;

import com.intellij.lang.*;
import com.intellij.psi.*;
import com.intellij.psi.stubs.*;
import com.reason.lang.core.type.*;
import org.jetbrains.annotations.*;

/**
 Some causes of bugs include:

 <ul>
    <li>leaf/composite PSI mismatch (thanks Vladimir for pointing that out!)
    <li>LightStubBuilder not working in the same way as the normal one would (by building stubs by AST)
    <li>errors in incremental parsing (e.g. IReparseableElementType#isParsable incorrectly returning true on broken code, when full reparse would produce a different result)
    <li>asymmetric stub serialization/deserialization code
    <li>lexer/parser/AST/PSI/stubs depending on something more than the file's content: other file, project settings, etc
 </ul>
 */
public abstract class ORStubElementType<StubT extends StubElement<?>, PsiT extends PsiElement> extends IStubElementType<StubT, PsiT> implements ORCompositeType {
    static final String[] EMPTY_PATH = new String[0];

    ORStubElementType(@NotNull String debugName, @Nullable Language language) {
        super(debugName, language);
    }

    public abstract @NotNull PsiElement createPsi(ASTNode node);
}
