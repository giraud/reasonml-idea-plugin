package jpsplugin.com.reason;

import com.intellij.notification.*;
import com.reason.ide.*;
import org.jetbrains.annotations.*;

import javax.swing.*;

import static com.intellij.notification.NotificationType.*;

public final class ORNotification extends Notification {
    private static final String REASON_ML_GROUP_DISPLAY = "Reason";

    public static void notifyError(@NotNull String title, @Nullable String subtitle, @NotNull String content) {
        Notifications.Bus.notify(new ORNotification(title, subtitle, content, ERROR));
    }

    public ORNotification(@NotNull String title, @Nullable String subtitle, @NotNull String content, @NotNull NotificationType type) {
        super(REASON_ML_GROUP_DISPLAY, title, content, type);
        setIcon(getIcon(type));
        setSubtitle(subtitle);
    }

    public ORNotification(@NotNull String title, @NotNull String content, @NotNull NotificationType type) {
        super(REASON_ML_GROUP_DISPLAY, title, content, type);
        setIcon(getIcon(type));
    }

    @Override
    public @NotNull Icon getIcon() {
        return getIcon(getType());
    }

    private static @NotNull Icon getIcon(@NotNull NotificationType type) {
        return type == NotificationType.INFORMATION
                ? ORIcons.RML_BLUE
                : (type == NotificationType.WARNING ? ORIcons.RML_YELLOW : ORIcons.RML_FILE);
    }
}
