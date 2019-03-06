package com.reason.lang.ocamlyacc;

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
import com.reason.ide.files.MlyFile;
import com.reason.lang.ocaml.OclTypes;
import org.jetbrains.annotations.NotNull;

public class OclYaccParserDefinition implements ParserDefinition {
    private static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    private static final TokenSet COMMENTS = TokenSet.create(OclTypes.INSTANCE.COMMENT);
    private static final TokenSet STRINGS = TokenSet.create(OclTypes.INSTANCE.STRING_VALUE);

    private static final IFileElementType FILE = new IFileElementType(Language.findInstance(OclYaccLanguage.class));

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new OclYaccLexer();
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
    public PsiParser createParser(Project project) {
        return new OclYaccParser();
    }

    @NotNull
    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    @NotNull
    public PsiFile createFile(@NotNull FileViewProvider viewProvider) {
        return new MlyFile(viewProvider);
    }

    @NotNull
    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(@NotNull ASTNode node) {
        return OclYaccTypes.Factory.createElement(node);
    }
}
