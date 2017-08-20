package com.reason.merlin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.reason.Platform;
import com.reason.ide.ReasonMLNotification;
import com.reason.merlin.types.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import static com.reason.merlin.MerlinProcess.NO_CONTEXT;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class MerlinServiceComponent implements MerlinService, com.intellij.openapi.components.ApplicationComponent {

    private static final TypeReference<List<MerlinError>> ERRORS_TYPE_REFERENCE = new TypeReference<List<MerlinError>>() {
    };
    private static final TypeReference<List<MerlinType>> TYPE_TYPE_REFERENCE = new TypeReference<List<MerlinType>>() {
    };
    private static final TypeReference<Boolean> BOOLEAN_TYPE_REFERENCE = new TypeReference<Boolean>() {
    };
    private static final TypeReference<MerlinVersion> VERSION_TYPE_REFERENCE = new TypeReference<MerlinVersion>() {
    };
    private static final TypeReference<List<String>> LIST_STRING_TYPE_REFERENCE = new TypeReference<List<String>>() {
    };
    private static final TypeReference<List<MerlinToken>> LIST_TOKEN_TYPE_REFERENCE = new TypeReference<List<MerlinToken>>() {
    };
    private static final TypeReference<MerlinCompletion> COMPLETION_TYPE_REFERENCE = new TypeReference<MerlinCompletion>() {
    };
    private static final TypeReference<Object> OBJECT_TYPE_REFERENCE = new TypeReference<Object>() {
    };
    private static final MerlinCompletion NO_COMPLETION = new MerlinCompletion();

    private MerlinProcess m_merlin;

    @NotNull
    @Override
    public String getComponentName() {
        return "ReasonMerlin";
    }


    @Override
    public void initComponent() {
        String merlinBin = Platform.getBinary("REASON_MERLIN_BIN", "reasonMerlin", "ocamlmerlin");

        try {
            m_merlin = new MerlinProcess(merlinBin);
        } catch (IOException e) {
            Notifications.Bus.notify(new ReasonMLNotification("Error locating merlin", "Can't find merlin, using '" + merlinBin + "'\n" + e.getMessage(), NotificationType.ERROR));
            return;
        }

        // Automatically select latest version
        try {
            MerlinVersion merlinVersion = selectVersion(3);
            Notifications.Bus.notify(new ReasonMLNotification("Merlin", "Found", merlinVersion.toString(), NotificationType.INFORMATION, null));
        } catch (UncheckedIOException e) {
            Notifications.Bus.notify(new ReasonMLNotification("Merlin", "Merlin not found", "Check that you have a REASON_MERLIN_BIN environment variable that contains the absolute path to the ocamlmerlin binary", NotificationType.ERROR, null));
            disposeComponent();
        }
    }

    @Override
    public void disposeComponent() {
        if (m_merlin != null) {
            try {
                m_merlin.close();
            } catch (IOException e) {
                // nothing to do
            } finally {
                m_merlin = null;
            }
        }
    }

    @Override
    public boolean isRunning() {
        return m_merlin != null;
    }

    @Override
    public List<MerlinError> errors(String filename) {
        List<MerlinError> merlinErrors = m_merlin.makeRequest(ERRORS_TYPE_REFERENCE, filename, "[\"errors\"]");
        return merlinErrors == null ? emptyList() : merlinErrors;
    }

    @Nullable
    @Override
    public MerlinVersion version() {
        return m_merlin.makeRequest(VERSION_TYPE_REFERENCE, NO_CONTEXT, "[\"protocol\", \"version\"]");
    }

    @Nullable
    @Override
    public MerlinVersion selectVersion(int version) {
        return m_merlin.makeRequest(VERSION_TYPE_REFERENCE, NO_CONTEXT, "[\"protocol\", \"version\", " + version + "]");
    }

    @Override
    public void sync(String filename, String buffer) {
        m_merlin.makeRequest(BOOLEAN_TYPE_REFERENCE, filename, "[\"tell\", \"start\", \"end\", " + m_merlin.writeValueAsString(buffer) + "]");
    }

    @Nullable
    @Override
    public Object dump(String filename, DumpFlag flag) {
        return m_merlin.makeRequest(OBJECT_TYPE_REFERENCE, filename, "[\"dump\", \"" + flag.name() + "\"]");
    }

    @Override
    public List<MerlinToken> dumpTokens(String filename) {
        List<MerlinToken> merlinTokens = m_merlin.makeRequest(LIST_TOKEN_TYPE_REFERENCE, filename, "[\"dump\", \"" + DumpFlag.tokens.name() + "\"]");
        return merlinTokens == null ? emptyList() : merlinTokens;
    }

    @Override
    public List<String> paths(String filename, Path path) {
        List<String> merlinPaths = m_merlin.makeRequest(LIST_STRING_TYPE_REFERENCE, filename, "[\"path\", \"list\", \"" + path.name() + "\"]");
        return merlinPaths == null ? emptyList() : merlinPaths;
    }

    @Override
    public List<String> listExtensions(String filename) {
        List<String> merlinExtensions = m_merlin.makeRequest(LIST_STRING_TYPE_REFERENCE, filename, "[\"extension\", \"list\"]");
        return merlinExtensions == null ? emptyList() : merlinExtensions;
    }

    @Override
    public void enableExtensions(String filename, List<String> extensions) {
        List<String> collect = extensions.stream().map(s -> m_merlin.writeValueAsString(s)).collect(toList());
        m_merlin.makeRequest(OBJECT_TYPE_REFERENCE, filename, "[\"extension\", \"enable\", [" + join(collect) + "]]");
    }

    @Nullable
    @Override
    public Object projectGet() {
        return m_merlin.makeRequest(OBJECT_TYPE_REFERENCE, "filename", "[\"project\", \"get\"]");
    }

    @Override
    public List<MerlinType> findType(String filename, MerlinPosition position) {
        List<MerlinType> merlinTypes = m_merlin.makeRequest(TYPE_TYPE_REFERENCE, filename, "[\"type\", \"enclosing\", \"at\", " + position + "]");
        return merlinTypes == null ? emptyList() : merlinTypes;
    }

    @Override
    public void outline(String filename) {
        m_merlin.makeRequest(OBJECT_TYPE_REFERENCE, filename, "[\"outline\"]");
    }

    @Override
    public MerlinCompletion completions(String filename, String prefix, MerlinPosition position) {
        if (m_merlin == null) {
            return NO_COMPLETION;
        }
        String query = "[\"complete\", \"prefix\", " + m_merlin.writeValueAsString(prefix) + ", \"at\", " + m_merlin.writeValueAsString(position) + ", \"with\", \"doc\"]";
        MerlinCompletion merlinCompletion = m_merlin.makeRequest(COMPLETION_TYPE_REFERENCE, filename, query);
        return merlinCompletion == null ? NO_COMPLETION : merlinCompletion;
    }

    private String join(Iterable<String> items) {
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (String item : items) {
            if (!first) {
                sb.append(",");
            }
            sb.append(item);
            first = false;
        }
        return sb.toString();
    }
}
