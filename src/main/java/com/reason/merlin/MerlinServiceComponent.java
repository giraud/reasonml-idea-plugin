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
        return MerlinServiceComponent.class.toString();
    }

    @Override
    public void initComponent() {
        String merlinBin = Platform.getBinary("REASON_MERLIN_BIN", "reasonMerlin", "ocamlmerlin");

        try {
            m_merlin3 = new MerlinProcess3(merlinBin);
        } catch (IOException e) {
            Notifications.Bus.notify(new RmlNotification("Merlin not found", "Can't find merlin, using '" + merlinBin + "', types inference will use bsc", NotificationType.INFORMATION));
            return;
        }

        // Automatically select latest version
        try {
            MerlinVersion merlinVersion = selectVersion(3);
            Notifications.Bus.notify(new RmlNotification("Merlin", "Found", merlinVersion.toString(), NotificationType.INFORMATION, null));
        } catch (UncheckedIOException e) {
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
    public boolean hasVersion() {
        return m_merlin3 != null && m_merlin3.version() != null;
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
        JsonNode entries = valueNode.get("entries");
        if (entries != null) {
            entries.elements().forEachRemaining(element -> merlinCompletion.entries.add(new MerlinCompletionEntry(element)));
        }

        return merlinCompletion;
    }

}
