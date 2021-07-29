package com.reason.comp.esy;

import static com.intellij.notification.NotificationType.ERROR;

import com.intellij.notification.Notifications;
import jpsplugin.com.reason.ORNotification;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;

public class EsyNotification {

  private EsyNotification() {}

  @Nls
  public static void showEsyNotFound() {
    Notifications.Bus.notify(
        new ORNotification("Esy Missing", "Unable to find esy executable in system PATH.", ERROR));
  }

  @Nls
  public static void showEsyProjectNotFound() {
    Notifications.Bus.notify(
        new ORNotification(
            "Esy Project Not Found", "Unable to find esy project. Have you run esy yet?", ERROR));
  }

  @Nls
  public static void showExecutionException(@NotNull Exception e) {
    Notifications.Bus.notify(
        new ORNotification(
            "Esy Exception", "Failed to execute esy command.\n" + e.getMessage(), ERROR));
  }
}
