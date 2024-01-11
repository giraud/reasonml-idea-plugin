package com.reason.ide;

import com.intellij.openapi.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.*;
import com.intellij.util.messages.*;
import com.reason.ide.format.*;
import org.jetbrains.annotations.*;

import static com.intellij.AppTopics.*;

public class ORFileDocumentListener implements Disposable {
    private final @NotNull MessageBusConnection m_messageBusConnection;

    public static void ensureSubscribed(@NotNull Project project) {
        project.getService(ORFileDocumentListener.class);
    }

    private ORFileDocumentListener(@NotNull Project project) {
        m_messageBusConnection = project.getMessageBus().connect(this);

        m_messageBusConnection.subscribe(
                FILE_DOCUMENT_SYNC,
                new FileDocumentManagerListener() {
                    @Override
                    public void beforeDocumentSaving(@NotNull Document document) {
                        ReformatOnSave.apply(project, document);
                    }
                });
    }

    @Override
    public void dispose() {
        m_messageBusConnection.disconnect();
    }
}
