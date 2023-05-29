package com.reason.lang.ocaml;

import com.intellij.extapi.psi.*;
import com.intellij.lang.*;
import com.intellij.lexer.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.*;
import com.intellij.psi.tree.*;
import com.reason.ide.files.*;
import com.reason.lang.core.psi.ocamllex.*;
import com.reason.lang.core.stub.type.*;
import org.jetbrains.annotations.*;

public class OclParserDefinition implements ParserDefinition {
    private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    private static final TokenSet COMMENTS = TokenSet.create(OclTypes.INSTANCE.MULTI_COMMENT);
    private static final TokenSet STRINGS = TokenSet.create(OclTypes.INSTANCE.STRING_VALUE);

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new OclLexer();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return STRINGS;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new OclParser(false);
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return OclFileStubElementType.INSTANCE;
    }

    @NotNull
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return viewProvider.getFileType() instanceof OclInterfaceFileType
                ? new OclInterfaceFile(viewProvider)
                : new OclFile(viewProvider);
    }

    @NotNull
    public SpaceRequirements spaceExistenceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(@NotNull ASTNode node) {
        IElementType type = node.getElementType();
        if (type instanceof ORStubElementType) {
            //noinspection rawtypes
            return ((ORStubElementType) type).createPsi(node);
        }
        throw new IllegalArgumentException("Not an OCaml stub node: " + node + " (" + type + ", " + type.getLanguage() + "): " + node.getText());
    }
}
