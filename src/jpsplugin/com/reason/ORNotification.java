package jpsplugin.com.reason;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import icons.ORIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

import static com.intellij.notification.NotificationType.ERROR;
import static com.intellij.notification.NotificationType.INFORMATION;

public class ORNotification extends Notification {

    private static final String REASON_ML_GROUP_DISPLAY = "Reason";

    public static void notifyError(@Nullable String title, @Nullable String subtitle, @Nullable String content, @Nullable NotificationListener listener) {
        Notifications.Bus.notify(new ORNotification(title, subtitle, content, ERROR, listener));
    }

    public static void notifyInfo(@Nullable String title, @Nullable String subtitle, @Nullable String content,
            @Nullable NotificationListener listener) {
        Notifications.Bus.notify(new ORNotification(title, subtitle, content, INFORMATION, listener));
    }

    public ORNotification(@Nullable String title, @Nullable String subtitle, @Nullable String content, @NotNull NotificationType type, @Nullable NotificationListener listener) {
        super(REASON_ML_GROUP_DISPLAY, getIcon(type), title, subtitle, content, type, listener);
    }

    public ORNotification(@NotNull String title, @NotNull String content, @NotNull NotificationType type) {
        super(REASON_ML_GROUP_DISPLAY, getIcon(type), title, null, content, type, null);
    }

    public ORNotification(@NotNull String title, @NotNull String content, @NotNull NotificationType type, NotificationListener listener) {
        super(REASON_ML_GROUP_DISPLAY, getIcon(type), title, null, content, type, listener);
    }

    @Override
    public @Nullable Icon getIcon() {
        return getIcon(getType());
    }

    private static @NotNull Icon getIcon(@NotNull NotificationType type) {
        return type == NotificationType.INFORMATION
                ? ORIcons.RML_BLUE
                : (type == NotificationType.WARNING ? ORIcons.RML_YELLOW : ORIcons.RML_FILE);
    }
}
