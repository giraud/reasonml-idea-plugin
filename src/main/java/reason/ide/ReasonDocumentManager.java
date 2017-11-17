package reason.ide;

import com.intellij.AppTopics;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import reason.ide.format.ReformatOnSave;
import org.jetbrains.annotations.NotNull;

public class ReasonDocumentManager implements ApplicationComponent {

    @NotNull
    @Override
    public String getComponentName() {
        return "ReasonDocumentManager";
    }

    @Override
    public void initComponent() {
        Boolean reasonReformatOnSave = Boolean.valueOf(System.getProperty("reasonReformatOnSave"));
        if (reasonReformatOnSave) {
            Notifications.Bus.notify(new RmlNotification("Refmt", "reformat on save is enabled", NotificationType.INFORMATION));
            ReformatOnSave handler = new ReformatOnSave();
            ApplicationManager.getApplication().getMessageBus().connect().subscribe(AppTopics.FILE_DOCUMENT_SYNC, handler);
        }
    }

    @Override
    public void disposeComponent() {
    }
}
