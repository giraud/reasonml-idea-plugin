package com.reason.comp.bs;

import com.intellij.ide.browsers.*;
import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.net.*;

import static com.intellij.notification.NotificationType.*;

public class BsNotification {

    private BsNotification() {
    }

    @Nls
    public static void showBsbNotFound(String workingDirectory) {
        ORNotification notification = new ORNotification(
                "Bsb",
                "<html>"
                        + "Can't find bsb.\n"
                        + "The working directory is '"
                        + workingDirectory
                        + "'.\n"
                        + "Be sure that bsb is installed and reachable from that directory."
                        + "</html>",
                ERROR);
        notification.addAction(new DumbAwareAction("Open documentation") {
            @Override
            public void actionPerformed(@NotNull AnActionEvent e) {
                URI docURL = URI.create("https://giraud.github.io/reasonml-idea-plugin/docs/build-tools/bucklescript");
                BrowserLauncher.getInstance().browse(docURL);
            }
        });
        Notifications.Bus.notify(notification);
    }

    @Nls
    public static void showWorkingDirectoryNotFound() {
        Notifications.Bus.notify(
                new ORNotification(
                        "BuckleScript",
                        "<html>Can't determine working directory.\nEnsure your project contains a <b>bsconfig.json</b> file.</html>",
                        ERROR));
    }
}
