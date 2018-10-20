package com.reason.ide;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.reason.icons.Icons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ORNotification extends Notification {

    private static final String REASON_ML_GROUP_DISPLAY = "Reason";

    public ORNotification(@Nullable String title, @Nullable String subtitle, @Nullable String content, @NotNull NotificationType type, @Nullable NotificationListener listener) {
        super(REASON_ML_GROUP_DISPLAY, /*getIcon(type), */"reason: " + title, /*subtitle, */content, type, listener);
    }

    public ORNotification(@NotNull String title, @NotNull String content, @NotNull NotificationType type) {
        super(REASON_ML_GROUP_DISPLAY, /*getIcon(type), */"reason: " + title, /*null, */content, type, null);
    }

    public ORNotification(@NotNull String title, @NotNull String content, @NotNull NotificationType type, NotificationListener listener) {
        super(REASON_ML_GROUP_DISPLAY, /*getIcon(type), */"reason: " + title, /*null, */content, type, listener);
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return getIcon(getType());
    }

    @NotNull
    private static Icon getIcon(@NotNull NotificationType type) {
        return type == NotificationType.INFORMATION ? Icons.BLUE_FILE : (type == NotificationType.WARNING ? Icons.YELLOW_FILE : Icons.RML_FILE);
    }

}
