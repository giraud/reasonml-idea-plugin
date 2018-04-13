package com.reason.ide;

import com.intellij.AppTopics;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBusConnection;
import com.reason.ide.format.ReformatOnSave;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReasonDocumentManager extends AbstractProjectComponent {

    @Nullable
    private MessageBusConnection m_busConnection;

    protected ReasonDocumentManager(@NotNull Project project) {
        super(project);
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ReasonDocumentManager";
    }

    @Override
    public void projectOpened() {
        Boolean reasonReformatOnSave = Boolean.valueOf(System.getProperty("reasonReformatOnSave"));
        if (reasonReformatOnSave) {
            Notifications.Bus.notify(new RmlNotification("Refmt", "reformat on save is enabled", NotificationType.INFORMATION));
            m_busConnection = ApplicationManager.getApplication().getMessageBus().connect();
            m_busConnection.subscribe(AppTopics.FILE_DOCUMENT_SYNC, new ReformatOnSave(myProject));
        }
    }

    @Override
    public void projectClosed() {
        if (m_busConnection != null) {
            m_busConnection.disconnect();
        }
    }
}
