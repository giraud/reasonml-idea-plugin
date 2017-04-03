package com.reason.ide.hints;

import com.google.common.base.Joiner;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.reason.Platform;
import com.reason.merlin.types.MerlinError;
import com.reason.merlin.types.MerlinPosition;
import com.reason.merlin.MerlinService;
import com.reason.merlin.types.MerlinType;
import com.reason.psi.ReasonMLLetStatement;
import com.reason.psi.ReasonMLValueName;
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
                debounce(200, TimeUnit.MILLISECONDS).
                subscribe(event -> EventQueue.invokeLater(() -> {
                    Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                    if (selectedTextEditor != null) {
                        Document document = selectedTextEditor.getDocument();
                        PsiFile psiFile = PsiDocumentManager.getInstance(project).getPsiFile(document);
                        if (psiFile != null) {
                            PsiElement element = psiFile.findElementAt(event.getOffset());
                            if (element != null) {
                                // every time for all statements ??

                                ReasonMLLetStatement letStatement = PsiTreeUtil.getParentOfType(element, ReasonMLLetStatement.class);
                                if (letStatement != null) {
                                    // Found a let statement, try to get its type
                                    ReasonMLValueName valueName = letStatement.getLetBinding().getValueName();
                                    int nameOffset = valueName.getTextOffset();
                                    LogicalPosition namePosition = selectedTextEditor.offsetToLogicalPosition(nameOffset);

                                    QueryMerlinTask merlinTask = new QueryMerlinTask(psiFile, letStatement, namePosition);
                                    ApplicationManager.getApplication().executeOnPooledThread(merlinTask); // Let statement has been modified
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

        private final ReasonMLLetStatement letStatement;
        private final String buffer;
        private final LogicalPosition position;
        private final PsiFile psiFile;

        public QueryMerlinTask(PsiFile psiFile, ReasonMLLetStatement letStatement, LogicalPosition position) {
            this.psiFile = psiFile;
            this.letStatement = letStatement;
            this.buffer = psiFile.getText(); // ?
            this.position = position;
        }

        @Override
        public void run() {
            MerlinService service = ServiceManager.getService(MerlinService.class);
            if (!service.isRunning()) {
                System.err.println("Can't find merlin service, abort");
            } else {
                String filename = psiFile.getVirtualFile().getCanonicalPath();
                // BIG HACK
                if (Platform.isWindows()) {
                    filename = "file:///mnt/v/sources/reason/ReasonProject/src/" + psiFile.getVirtualFile().getName();
                }

                service.sync(filename, this.buffer);

                List<MerlinType> types = service.findType(filename, new MerlinPosition(this.position));
                if (!types.isEmpty()) {
                    this.letStatement.setInferredType(types.get(0).type);
                }

                boolean debug = false;
                if (debug) {
                    System.out.println("-------------- Running a merlin task");
//                    System.out.println("            type: " + types.get(0));
//                    System.out.println("          ------- ");
//                    System.out.println("        dump env: " + service.dump(DumpFlag.env).toString());
//                    System.out.println("      dump flags: " + service.dump(DumpFlag.flags).toString());
//                System.out.println("            dump: " + service.dump(DumpFlag.parser).toString());
//                System.out.println("            dump: " + service.dump(DumpFlag.recover).toString());
//                    System.out.println("     dump tokens: [" + Joiner.on("'").join(service.dumpTokens()) + "]");
//                    System.out.println("          source: [" + Joiner.on("'").join(service.paths(filename, Path.source)) + "]");
//                    System.out.println("           build: [" + Joiner.on("'").join(service.paths(filename, Path.build)) + "]");
//                    System.out.println("      extensions: [" + Joiner.on("'").join(service.listExtensions(filename)) + "]");
//                    System.out.println("  Merlin version: " + service.version());
//                    System.out.println("         Project: [" + service.projectGet() + "]");
                }
            }
        }
    }
}
