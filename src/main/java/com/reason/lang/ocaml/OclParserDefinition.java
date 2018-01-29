package com.reason.lang.ocaml;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.reason.ide.files.OclFile;
import com.reason.lang.LexerAdapter;
import com.reason.lang.PsiElementFactory;
import com.reason.lang.core.stub.type.OclFileStubElementType;
import org.jetbrains.annotations.NotNull;

public class OclParserDefinition implements ParserDefinition {
    private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    private static final TokenSet COMMENTS = TokenSet.create(OclTypes.INSTANCE.COMMENT);
    private static final TokenSet STRINGS = TokenSet.create(OclTypes.INSTANCE.STRING);

    private static final IFileElementType FILE = new IFileElementType(Language.findInstance(OclLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new LexerAdapter(OclTypes.INSTANCE);
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
        return new OclParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return OclFileStubElementType.INSTANCE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new OclFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return PsiElementFactory.createElement(OclTypes.INSTANCE, node);
    }
}
