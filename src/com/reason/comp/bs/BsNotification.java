package com.reason.comp.bs;

import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.intellij.notification.NotificationType.ERROR;

import com.intellij.notification.Notifications;
import jpsplugin.com.reason.ORNotification;
import org.jetbrains.annotations.Nls;

public class BsNotification {

  private BsNotification() {}

  @Nls
  public static void showBsbNotFound(String workingDirectory) {
    Notifications.Bus.notify(
        new ORNotification(
            "Bsb",
            "<html>"
                + "Can't find bsb.\n"
                + "The working directory is '"
                + workingDirectory
                + "'.\n"
                + "Be sure that bsb is installed and reachable from that directory, "
                + "see <a href=\"https://github.com/reasonml-editor/reasonml-idea-plugin#bucklescript\">github</a>."
                + "</html>",
            ERROR,
            URL_OPENING_LISTENER));
  }

  @Nls
  public static void showWorkingDirectoryNotFound() {
    Notifications.Bus.notify(
        new ORNotification(
            "BuckleScript",
            "<html>"
                + "Can't determine working directory.\n"
                + "Ensure your project contains a <b>bsconfig.json</b> file."
                + "</html>",
            ERROR,
            URL_OPENING_LISTENER));
  }
}
