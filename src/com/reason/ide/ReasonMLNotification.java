package com.reason.ide;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationListener;
import com.intellij.notification.NotificationType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ReasonMLNotification extends Notification {

    public static final String REASON_ML_GROUP_DISPLAY = "ReasonML";

    public ReasonMLNotification(@NotNull Icon icon, @NotNull NotificationType type) {
        super(REASON_ML_GROUP_DISPLAY, icon, type);
    }

    public ReasonMLNotification(@NotNull Icon icon, @Nullable String title, @Nullable String subtitle, @Nullable String content, @NotNull NotificationType type, @Nullable NotificationListener listener) {
        super(REASON_ML_GROUP_DISPLAY, icon, title, subtitle, content, type, listener);
    }

    public ReasonMLNotification(@NotNull String title, @NotNull String content, @NotNull NotificationType type) {
        super(REASON_ML_GROUP_DISPLAY, title, content, type);
    }

    public ReasonMLNotification( @NotNull String title, @NotNull String content, @NotNull NotificationType type, @Nullable NotificationListener listener) {
        super(REASON_ML_GROUP_DISPLAY, title, content, type, listener);
    }
}
