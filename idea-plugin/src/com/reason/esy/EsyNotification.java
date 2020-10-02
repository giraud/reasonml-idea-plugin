package com.reason.esy;

import static com.intellij.notification.NotificationType.ERROR;

import com.intellij.notification.Notifications;
import com.reason.ORNotification;
import org.jetbrains.annotations.Nls;

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
  public static void showExecutionException(Exception e) {
    Notifications.Bus.notify(
        new ORNotification(
            "Esy Exception", "Failed to execute esy command.\n" + e.getMessage(), ERROR));
  }
}
