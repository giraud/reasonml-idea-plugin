package com.reason.ide;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.ide.merlin.MerlinService;
import com.reason.psi.ReasonMLLetStatement;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import java.awt.*;
import java.util.concurrent.TimeUnit;


public class ReasonMLDocumentListener implements DocumentListener {

    private final Subject<DocumentEvent> documentEventStream;
    private Disposable subscriber;

    ReasonMLDocumentListener(Project project) {
        this.documentEventStream = PublishSubject.create();

        subscriber = this.documentEventStream.
                debounce(300, TimeUnit.MILLISECONDS).
                subscribe(event -> EventQueue.invokeLater(() -> {
                    Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    if (selectedTextEditor != null) {
                        Document document1 = selectedTextEditor.getDocument();
                        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document1);
                        if (psiFile != null) {
                            PsiElement element = psiFile.findElementAt(event.getOffset());
                            if (element != null) {
                                ReasonMLLetStatement parentOfType = PsiTreeUtil.getParentOfType(element, ReasonMLLetStatement.class);
                                if (parentOfType != null) {
                                    ApplicationManager.getApplication().executeOnPooledThread(new QueryMerlinTask()); // Let statement has been modified
                                }
                            }
                        }
                    }
                }));
    }

    @Override
    public void beforeDocumentChange(DocumentEvent event) {
    }

    @Override
    public void documentChanged(DocumentEvent event) {
        this.documentEventStream.onNext(event);
    }

    public void projectClosed() {
        this.subscriber.dispose();
    }

    private static class QueryMerlinTask implements Runnable {
        @Override
        public void run() {
            MerlinService service = ServiceManager.getService(MerlinService.class);
            // Query let signature
        }
    }
}
