package com.reason.lang.reason;

import com.intellij.lang.*;
import com.intellij.lexer.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

public class RmlParserDefinition implements ParserDefinition {
    @Override
    public @NotNull Lexer createLexer(Project project) {
        return new RmlLexer();
    }

    public @NotNull TokenSet getWhitespaceTokens() {
        return TokenSet.create(TokenType.WHITE_SPACE);
    }

    public @NotNull TokenSet getCommentTokens() {
        return TokenSet.create(RmlTypes.INSTANCE.MULTI_COMMENT, RmlTypes.INSTANCE.SINGLE_COMMENT);
    }

    public @NotNull TokenSet getStringLiteralElements() {
        return TokenSet.create(RmlTypes.INSTANCE.STRING_VALUE);
    }

    public @NotNull PsiParser createParser(Project project) {
        return new RmlParser();
    }

    @Override
    public @NotNull IFileElementType getFileNodeType() {
        return RmlFileStubElementType.INSTANCE;
    }

    public @NotNull PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return viewProvider.getFileType() instanceof RmlInterfaceFileType
                ? new RmlInterfaceFile(viewProvider)
                : new RmlFile(viewProvider);
    }

    public @NotNull SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    public @NotNull PsiElement createElement(@NotNull ASTNode node) {
        IElementType type = node.getElementType();
        if (type instanceof ORStubElementType) {
            //noinspection rawtypes
            return ((ORStubElementType) node.getElementType()).createPsi(node);
        }

        throw new IllegalArgumentException("Not a ReasonML node: " + node + " (" + type + ", " + type.getLanguage() + ")");
    }
}
