package com.reason.ide;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import com.reason.icons.ReasonMLIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ReasonMLNotification extends Notification {

    private static final String REASON_ML_GROUP_DISPLAY = "ReasonML";

    public ReasonMLNotification(@Nullable String title, @Nullable String subtitle, @Nullable String content, @NotNull NotificationType type, @Nullable NotificationListener listener) {
        super(REASON_ML_GROUP_DISPLAY, type == NotificationType.INFORMATION ? ReasonMLIcons.BLUE_FILE : (type == NotificationType.WARNING ? ReasonMLIcons.YELLOW_FILE : ReasonMLIcons.FILE), title, subtitle, content, type, listener);
    }

    public ReasonMLNotification(@NotNull String title, @NotNull String content, @NotNull NotificationType type) {
        super(REASON_ML_GROUP_DISPLAY, type == NotificationType.INFORMATION ? ReasonMLIcons.BLUE_FILE : (type == NotificationType.WARNING ? ReasonMLIcons.YELLOW_FILE : ReasonMLIcons.FILE), title, null, content, type, null);
    }
}
