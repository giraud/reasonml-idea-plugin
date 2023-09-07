package com.reason.lang.ocaml;

import com.intellij.lang.*;
import com.intellij.lexer.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

public class OclParserDefinition implements ParserDefinition {
    private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    private static final TokenSet COMMENTS = TokenSet.create(OclTypes.INSTANCE.MULTI_COMMENT);
    private static final TokenSet STRINGS = TokenSet.create(OclTypes.INSTANCE.STRING_VALUE);

    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new OclLexer();
    }

    @Override
    public @NotNull TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @Override
    public @NotNull TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @Override
    public @NotNull TokenSet getStringLiteralElements() {
        return STRINGS;
    }

    @Override
    public @NotNull PsiParser createParser(final Project project) {
        return new OclParser(false);
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return OclFileStubElementType.INSTANCE;
    }

    @Override
    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return viewProvider.getFileType() instanceof OclInterfaceFileType
                ? new OclInterfaceFile(viewProvider)
                : new OclFile(viewProvider);
    }

    @Override
    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @Override
    public @NotNull PsiElement createElement(@NotNull ASTNode node) {
        IElementType type = node.getElementType();
        if (type instanceof ORStubElementType) {
            //noinspection rawtypes
            return ((ORStubElementType) type).createPsi(node);
        }
        throw new IllegalArgumentException("Not an OCaml stub node: " + node + " (" + type + ", " + type.getLanguage() + "): " + node.getText());
    }
}
