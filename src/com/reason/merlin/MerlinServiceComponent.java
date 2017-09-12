package com.reason.merlin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.reason.Platform;
import com.reason.ide.RmlNotification;
import com.reason.merlin.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.List;

import static com.reason.merlin.MerlinProcess2.NO_CONTEXT;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;

public class MerlinServiceComponent implements MerlinService, com.intellij.openapi.components.ApplicationComponent {

    private static final TypeReference<List<MerlinError>> ERRORS_TYPE_REFERENCE = new TypeReference<List<MerlinError>>() {
    };
    private static final TypeReference<List<MerlinType>> TYPE_TYPE_REFERENCE = new TypeReference<List<MerlinType>>() {
    };
    private static final TypeReference<Boolean> BOOLEAN_TYPE_REFERENCE = new TypeReference<Boolean>() {
    };
    private static final TypeReference<MerlinVersion> VERSION_TYPE_REFERENCE = new TypeReference<MerlinVersion>() {
    };
    private static final TypeReference<MerlinCompletion> COMPLETION_TYPE_REFERENCE = new TypeReference<MerlinCompletion>() {
    };
    private static final MerlinCompletion NO_COMPLETION = new MerlinCompletion();

    private boolean m_useProtocol3 = true;
    private MerlinProcess2 m_merlin2;
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
            if (m_useProtocol3) {
                m_merlin3 = new MerlinProcess3(merlinBin);
            } else {
                m_merlin2 = new MerlinProcess2(merlinBin);
            }
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
    public void disposeComponent() {
        if (m_merlin2 != null) {
            try {
                m_merlin2.close();
            } catch (IOException e) {
                // nothing to do
            } finally {
                m_merlin2 = null;
            }
        }
    }

    @Override
    public boolean isRunning() {
        return m_useProtocol3 || m_merlin2 != null;
    }

    @Override
    public List<MerlinError> errors(String filename, String source) {
        final List<MerlinError> merlinErrors = new ArrayList<>();

        if (m_useProtocol3) {
            JsonNode valueNode = m_merlin3.execute(filename, source, singletonList("errors"));
            valueNode.elements().forEachRemaining(element -> merlinErrors.add(new MerlinError(element)));
        } else {
            merlinErrors.addAll(m_merlin2.makeRequest(ERRORS_TYPE_REFERENCE, filename, "[\"errors\"]"));
        }

        return merlinErrors;
    }

    @Nullable
    @Override
    public MerlinVersion selectVersion(int version) {
        if (m_useProtocol3) {
            return m_merlin3.version();
        }
        return m_merlin2.makeRequest(VERSION_TYPE_REFERENCE, NO_CONTEXT, "[\"protocol\", \"version\", " + version + "]");
    }

    @Override
    public void sync(String filename, String source) {
        if (!m_useProtocol3) {
            m_merlin2.makeRequest(BOOLEAN_TYPE_REFERENCE, filename, "[\"tell\", \"start\", \"end\", " + m_merlin2.writeValueAsString(source) + "]");
        }
    }

    @Override
    public List<MerlinType> typeExpression(String filename, String source, MerlinPosition position) {
        List<MerlinType> result = new ArrayList<>();

        // [ {"start":{"line":1,"col":4},"end":{"line":1,"col":6},"type":"float","tail":"no"} ]
        JsonNode valueNode = m_merlin3.execute(filename, source, asList("type-enclosing", "-position", position.toShortString()));
        valueNode.elements().forEachRemaining(element -> result.add(new MerlinType(element)));

        return result;
    }

    @Override
    public MerlinCompletion completions(String filename, String source, MerlinPosition position, String prefix) {
        final MerlinCompletion merlinCompletion = new MerlinCompletion();

        if (m_useProtocol3) {
            // {entries":[{"name":"append","kind":"Value","desc":"list 'a => list 'a => list 'a","info":""},...],"context":null}
            JsonNode valueNode = m_merlin3.execute(filename, source, asList("complete-prefix", "-position", position.toShortString(), "-prefix", prefix));
            valueNode.get("entries").elements().forEachRemaining(element -> merlinCompletion.entries.add(new MerlinCompletionEntry(element)));
        } else {
            if (m_merlin2 != null) {
                String query = "[\"complete\", \"prefix\", " + m_merlin2.writeValueAsString(prefix) + ", \"at\", " + m_merlin2.writeValueAsString(position) + ", \"with\", \"doc\"]";
//                merlinCompletion = m_merlin2.makeRequest(COMPLETION_TYPE_REFERENCE, filename, query);
            }
        }

        return merlinCompletion == null ? NO_COMPLETION : merlinCompletion;
    }

}
