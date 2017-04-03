package com.reason.merlin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Joiner;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.project.Project;
import com.reason.ide.ReasonMLNotification;
import com.reason.merlin.types.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;

import static com.reason.merlin.MerlinProcess.NO_CONTEXT;
import static java.util.stream.Collectors.toList;

public class MerlinServiceComponent extends AbstractProjectComponent implements MerlinService {

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
    private static final TypeReference<Object> OBJECT_TYPE_REFERENCE = new TypeReference<Object>() {
    };

    private MerlinProcess merlin;

    protected MerlinServiceComponent(Project project) {
        super(project);
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ReasonMerlin";
    }

    @Override
    public void projectOpened() {
        String merlinBin = System.getenv("MERLIN_BIN"); // ocamlmerlin
        if (merlinBin == null) {
            merlinBin = "ocamlmerlin";
        }

        try {
            this.merlin = new MerlinProcess(merlinBin, this.myProject.getBasePath());
        } catch (IOException e) {
            Notifications.Bus.notify(new ReasonMLNotification("Error locating merlin", "Can't find merlin, using '" + merlinBin + "'\n" + e.getMessage(), NotificationType.ERROR));
            return;
        }

        // Automatically select latest version
        MerlinVersion merlinVersion = selectVersion(3);
        Notifications.Bus.notify(new ReasonMLNotification("version", merlinVersion.toString(), NotificationType.INFORMATION));
    }

    @Override
    public void projectClosed() {
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
}
