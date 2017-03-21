package com.reason.ide.hints;

import com.google.common.base.Joiner;
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
import com.reason.merlin.DumpFlag;
import com.reason.merlin.MerlinError;
import com.reason.merlin.MerlinService;
import com.reason.merlin.Path;
import com.reason.psi.ReasonMLLetStatement;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

import java.awt.*;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class ReasonMLDocumentListener implements DocumentListener {

    private final Subject<DocumentEvent> documentEventStream;
    private Disposable subscriber;

    public ReasonMLDocumentListener(Project project) {
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

    void projectClosed() {
        this.subscriber.dispose();
    }

    private static class QueryMerlinTask implements Runnable {
        @Override
        public void run() {
            MerlinService service = ServiceManager.getService(MerlinService.class);
            if (service == null) {
                System.out.println("Can't find merlin service, abort");
            } else {
                System.out.println("-------------- Running a merlin task");
                String version = service.version();
                List<MerlinError> errors = service.errors();
                System.out.println("        dump env: " + service.dump(DumpFlag.env).toString());
                System.out.println("      dump flags: " + service.dump(DumpFlag.flags).toString());
//                System.out.println("            dump: " + service.dump(DumpFlag.parser).toString());
//                System.out.println("            dump: " + service.dump(DumpFlag.recover).toString());
                System.out.println("     dump tokens: [" + Joiner.on("'").join(service.dumpTokens()) + "]");
                System.out.println("          source: [" + Joiner.on("'").join(service.paths(Path.source)) + "]");
                System.out.println("           build: [" + Joiner.on("'").join(service.paths(Path.build)) + "]");
                System.out.println("      extensions: [" + Joiner.on("'").join(service.extensions()) + "]");
                System.out.println("  Merlin version: " + version);
                System.out.println("          Errors: [" + Joiner.on("'").join(errors) + "]");
            }
        }
    }
}
