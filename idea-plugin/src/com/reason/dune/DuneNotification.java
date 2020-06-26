package com.reason.dune;

import com.intellij.notification.Notifications;
import com.reason.ORNotification;
import org.jetbrains.annotations.Nls;

import static com.intellij.notification.NotificationListener.URL_OPENING_LISTENER;
import static com.intellij.notification.NotificationType.ERROR;

public class DuneNotification {

    private DuneNotification() {}

    @Nls
    public static void showOcamlSdkNotFound() {
        Notifications.Bus.notify(new ORNotification("Dune",
                "<html>Can't find sdk.\n"
                        + "When using a dune config file, you need to create an OCaml SDK and associate it to the project.\n"
                        + "see <a href=\"https://github.com/reasonml-editor/reasonml-idea-plugin#ocaml\">github</a>.</html>",
                ERROR, URL_OPENING_LISTENER));
    }
}
