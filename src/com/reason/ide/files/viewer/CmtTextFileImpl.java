package com.reason.ide.files.viewer;

import com.intellij.lang.FileASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.psi.impl.PsiFileEx;
import com.intellij.psi.impl.source.SourceTreeToPsiMap;
import com.intellij.psi.impl.source.tree.TreeElement;
import com.intellij.psi.search.PsiElementProcessor;
import com.intellij.util.ArrayUtil;
import com.intellij.util.IncorrectOperationException;
import com.reason.ide.files.CmtFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CmtTextFileImpl extends PsiElementBase implements PsiFileEx, PsiFile, PsiCompiledFile {
    private final FileViewProvider m_viewProvider;

    CmtTextFileImpl(@NotNull FileViewProvider viewProvider) {
        m_viewProvider = viewProvider;
    }

    @Override
    public VirtualFile getVirtualFile() {
        return m_viewProvider.getVirtualFile();
    }

    @NotNull
    @Override
    public String getName() {
        return getVirtualFile().getName();
    }

    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("Can't edit this file");
    }

    @Override
    public boolean processChildren(PsiElementProcessor<PsiFileSystemItem> processor) {
        return true;
    }

    @Override
    public PsiDirectory getContainingDirectory() {
        VirtualFile parentFile = getVirtualFile().getParent();
        if (parentFile == null) {
            return null;
        }
        return getManager().findDirectory(parentFile);
    }

    @Override
    public PsiDirectory getParent() {
        return getContainingDirectory();
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @NotNull
    @Override
    public Language getLanguage() {
        return PlainTextLanguage.INSTANCE;
    }

    @Override
    public PsiManager getManager() {
        return m_viewProvider.getManager();
    }

    @NotNull
    @Override
    public PsiElement[] getChildren() {
        return new PsiElement[0];
    }

    @Override
    public int getStartOffsetInParent() {
        return 0;
    }

    @Override
    public TextRange getTextRange() {
        return TextRange.create(0, getTextLength());
    }

    @Nullable
    @Override
    public PsiElement findElementAt(int offset) {
        return null;
    }

    @Override
    public int getTextOffset() {
        return 0;
    }

    @Override
    public String getText() {
        VirtualFile file = getVirtualFile();
        Document document = FileDocumentManager.getInstance().getDocument(file);
        assert document != null : file.getUrl();
        return document.getText();
    }

    @Override
    public int getTextLength() {
        VirtualFile file = getVirtualFile();
        Document document = FileDocumentManager.getInstance().getDocument(file);
        assert document != null : file.getUrl();
        return document.getTextLength();
    }

    @NotNull
    @Override
    public char[] textToCharArray() {
        return ArrayUtil.EMPTY_CHAR_ARRAY;
    }

    @Override
    public long getModificationStamp() {
        return getVirtualFile().getModificationStamp();
    }

    @NotNull
    @Override
    public PsiFile getOriginalFile() {
        return this;
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return CmtFileType.INSTANCE;
    }

    @NotNull
    @Override
    public PsiFile[] getPsiRoots() {
        return new PsiFile[]{this};
    }

    @NotNull
    @Override
    public FileViewProvider getViewProvider() {
        return m_viewProvider;
    }

    @Override
    public FileASTNode getNode() {
        return null;
    }

    @Override
    public void subtreeChanged() {

    }

    @Override
    public void checkSetName(String name) throws IncorrectOperationException {
        throw new IncorrectOperationException("Can't edit this file");
    }

    @Override
    public boolean isContentsLoaded() {
        return true;
    }

    @Override
    public void onContentReload() {

    }

    @Override
    public void markInvalidated() {

    }

    @Override
    public PsiFile getDecompiledPsiFile() {
        return (PsiFile) getMirror();
    }

    @Override
    public PsiElement getMirror() {
        PsiFileFactory factory = PsiFileFactory.getInstance(getManager().getProject());
        PsiFile mirror = factory.createFileFromText("fileName", PlainTextLanguage.INSTANCE, "Cmt File decompiled", false, false);
        TreeElement mirrorTreeElement = SourceTreeToPsiMap.psiToTreeNotNull(mirror);
        return mirrorTreeElement.getPsi();
    }
}
