package com.reason.merlin;

import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.reason.Platform;
import com.reason.ide.RmlNotification;
import com.reason.merlin.types.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class MerlinServiceComponent implements MerlinService, com.intellij.openapi.components.ApplicationComponent {

    private MerlinProcess3 m_merlin3;

    @NotNull
    @Override
    public String getComponentName() {
        return "ReasonMerlin";
    }

    @Override
    public void initComponent() {
        String merlinBin = Platform.getBinary("REASON_MERLIN_BIN", "reasonMerlin", "ocamlmerlin");

        try {
            m_merlin3 = new MerlinProcess3(merlinBin);
        } catch (IOException e) {
            Notifications.Bus.notify(new RmlNotification("Error locating merlin", "Can't find merlin, using '" + merlinBin + "'\n" + e.getMessage(), NotificationType.ERROR));
            return;
        }

        // Automatically select latest version
        try {
            MerlinVersion merlinVersion = selectVersion(3);
            Notifications.Bus.notify(new RmlNotification("Merlin", "Found", merlinVersion.toString(), NotificationType.INFORMATION, null));
        } catch (UncheckedIOException e) {
            Notifications.Bus.notify(new RmlNotification("Merlin", "Merlin not found", "Check that you have a REASON_MERLIN_BIN environment variable that contains the absolute path to the ocamlmerlin binary", NotificationType.ERROR, null));
            disposeComponent();
        }
    }

    @Override
    public List<MerlinError> errors(String filename, String source) {
        final List<MerlinError> merlinErrors = new ArrayList<>();

        JsonNode valueNode = m_merlin3.execute(filename, source, singletonList("errors"));
        valueNode.elements().forEachRemaining(element -> merlinErrors.add(new MerlinError(element)));

        return merlinErrors;
    }

    @Override
    public MerlinVersion selectVersion(int version) {
        return m_merlin3.version();
    }

    @Override
    public List<MerlinType> typeExpression(String filename, String source, MerlinPosition position) {
        List<MerlinType> result = new ArrayList<>();

        JsonNode valueNode = m_merlin3.execute(filename, source, asList("type-enclosing", "-position", position.toShortString()));
        valueNode.elements().forEachRemaining(element -> result.add(new MerlinType(element)));

        return result;
    }

    @Override
    public MerlinCompletion completions(String filename, String source, MerlinPosition position, String prefix) {
        final MerlinCompletion merlinCompletion = new MerlinCompletion();

        JsonNode valueNode = m_merlin3.execute(filename, source, asList("complete-prefix", "-position", position.toShortString(), "-prefix", prefix, "-doc", "true"));
        valueNode.get("entries").elements().forEachRemaining(element -> merlinCompletion.entries.add(new MerlinCompletionEntry(element)));

        return merlinCompletion;
    }

}
