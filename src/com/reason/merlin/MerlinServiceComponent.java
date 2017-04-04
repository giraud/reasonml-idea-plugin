package com.reason.merlin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Joiner;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.reason.Platform;
import com.reason.ide.ReasonMLNotification;
import com.reason.merlin.types.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import static com.reason.merlin.MerlinProcess.NO_CONTEXT;
import static java.util.stream.Collectors.toList;

public class MerlinServiceComponent implements MerlinService, com.intellij.openapi.components.ApplicationComponent {

    private static final TypeReference<List<MerlinError>> ERRORS_TYPE_REFERENCE = new TypeReference<List<MerlinError>>() {
    };
    private static final TypeReference<String> STRING_TYPE_REFERENCE = new TypeReference<String>() {
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

    private MerlinProcess merlin;

    @NotNull
    @Override
    public String getComponentName() {
        return "ReasonMerlin";
    }


    @Override
    public void initComponent() {
        String merlinBin = Platform.getBinary("REASON_MERLIN_BIN", "reasonMerlin", "ocamlmerlin");

        try {
            this.merlin = new MerlinProcess(merlinBin);
        } catch (IOException e) {
            Notifications.Bus.notify(new ReasonMLNotification("Error locating merlin", "Can't find merlin, using '" + merlinBin + "'\n" + e.getMessage(), NotificationType.ERROR));
            return;
        }

        // Automatically select latest version
        try {
            MerlinVersion merlinVersion = selectVersion(3);
            Notifications.Bus.notify(new ReasonMLNotification("Merlin", "version", merlinVersion.toString(), NotificationType.INFORMATION, null));
        } catch (UncheckedIOException e) {
            Notifications.Bus.notify(new ReasonMLNotification("Merlin", "Merlin not found", "Check that you have a REASON_MERLIN_BIN environment variable that contains the absolute path to the ocamlmerlin binary", NotificationType.ERROR, null));
            disposeComponent();
        }
    }

    @Override
    public void disposeComponent() {
        if (merlin != null) {
            try {
                merlin.close();
            } catch (IOException e) {
                // nothing to do
            } finally {
                merlin = null;
            }
        }
    }

    @Override
    public boolean isRunning() {
        return this.merlin != null;
    }

    @Override
    public List<MerlinError> errors(String filename) {
        return this.merlin.makeRequest(ERRORS_TYPE_REFERENCE, filename, "[\"errors\"]");
    }

    @Override
    public MerlinVersion version() {
        return this.merlin.makeRequest(VERSION_TYPE_REFERENCE, NO_CONTEXT, "[\"protocol\", \"version\"]");
    }

    @Override
    public MerlinVersion selectVersion(int version) {
        return this.merlin.makeRequest(VERSION_TYPE_REFERENCE, NO_CONTEXT, "[\"protocol\", \"version\", " + version + "]");
    }

    @Override
    public void sync(String filename, String buffer) {
        this.merlin.makeRequest(BOOLEAN_TYPE_REFERENCE, filename, "[\"tell\", \"start\", \"end\", " + this.merlin.writeValueAsString(buffer) + "]");
    }

    @Override
    public Object dump(String filename, DumpFlag flag) {
        return this.merlin.makeRequest(OBJECT_TYPE_REFERENCE, filename, "[\"dump\", \"" + flag.name() + "\"]");
    }

    @Override
    public List<MerlinToken> dumpTokens(String filename) {
        return this.merlin.makeRequest(LIST_TOKEN_TYPE_REFERENCE, filename, "[\"dump\", \"" + DumpFlag.tokens.name() + "\"]");
    }

    @Override
    public List<String> paths(String filename, Path path) {
        return this.merlin.makeRequest(LIST_STRING_TYPE_REFERENCE, filename, "[\"path\", \"list\", \"" + path.name() + "\"]");
    }

    void addPath(Path path, List<String> paths) {
        List<String> jsonPaths = paths.stream().map(s -> this.merlin.writeValueAsString(s)).collect(toList());
        this.merlin.makeRequest(OBJECT_TYPE_REFERENCE, NO_CONTEXT, "[\"path\", \"add\", \"" + path + "\", [" + Joiner.on(", ").join(jsonPaths) + "]]");
    }

    @Override
    public List<String> listExtensions(String filename) {
        return this.merlin.makeRequest(LIST_STRING_TYPE_REFERENCE, filename, "[\"extension\", \"list\"]");
    }

    @Override
    public void enableExtensions(String filename, List<String> extensions) {
        List<String> collect = extensions.stream().map(s -> this.merlin.writeValueAsString(s)).collect(toList());
        this.merlin.makeRequest(OBJECT_TYPE_REFERENCE, filename, "[\"extension\", \"enable\", [" + Joiner.on(", ").join(collect) + "]]");
    }

    @Override
    public Object projectGet() {
        return this.merlin.makeRequest(OBJECT_TYPE_REFERENCE, "filename", "[\"project\", \"get\"]");
    }

    @Override
    public List<MerlinType> findType(String filename, MerlinPosition position) {
        return this.merlin.makeRequest(TYPE_TYPE_REFERENCE, filename, "[\"type\", \"enclosing\", \"at\", " + position + "]");
    }

    @Override
    public void outline(String filename) {
        this.merlin.makeRequest(OBJECT_TYPE_REFERENCE, filename, "[\"outline\"]");
    }

    @Override
    public MerlinCompletion completions(String filename, String prefix, MerlinPosition position) {
        String query = "[\"complete\", \"prefix\", " + this.merlin.writeValueAsString(prefix) + ", \"at\", " + this.merlin.writeValueAsString(position) + ", \"with\", \"doc\"]";
        return this.merlin.makeRequest(COMPLETION_TYPE_REFERENCE, filename, query);
    }
}
