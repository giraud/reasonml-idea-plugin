package com.reason.merlin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.reason.ide.ReasonMLNotification;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;
import java.util.stream.Collectors;

public class MerlinServiceComponent implements MerlinService, com.intellij.openapi.components.ApplicationComponent {

    private static final TypeReference<List<MerlinError>> ERRORS_TYPE_REFERENCE = new TypeReference<List<MerlinError>>() {
    };
    private static final TypeReference<String> STRING_TYPE_REFERENCE = new TypeReference<String>() {
    };
    private static final TypeReference<List<MerlinType>> TYPE_TYPE_REFERENCE = new TypeReference<List<MerlinType>>() {
    };
    public static final TypeReference<Boolean> BOOLEAN_TYPE_REFERENCE = new TypeReference<Boolean>() {
    };
    private static final String NO_CONTEXT = null;
    public static final TypeReference<MerlinVersion> VERSION_TYPE_REFERENCE = new TypeReference<MerlinVersion>() {
    };

    private ObjectMapper objectMapper;
    private Process merlin;
    private BufferedWriter writer;
    private BufferedReader reader;
    private BufferedReader errorReader;

    @NotNull
    @Override
    public String getComponentName() {
        return "ReasonMerlin";
    }

    @Override
    public void initComponent() {
        System.out.println("Init merlin component");
        String merlinBin = System.getenv("MERLIN_BIN"); // ocamlmerlin
        if (merlinBin == null) {
            merlinBin = "ocamlmerlin";
        }

        objectMapper = new ObjectMapper();
        ProcessBuilder processBuilder = new ProcessBuilder(merlinBin)
                .directory(new File("V:\\sources\\reason\\ReasonProject"))
                .redirectErrorStream(true);

        try {
            merlin = processBuilder.start();
            writer = new BufferedWriter(new OutputStreamWriter(merlin.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(merlin.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(merlin.getErrorStream()));
        } catch (IOException e) {
            Notifications.Bus.notify(new ReasonMLNotification("Error locating merlin", "Can't find merlin, using '" + merlinBin + "'\n" + e.getMessage(), NotificationType.ERROR));
            return;
        }

        MerlinVersion merlinVersion = selectVersion(3);
        Notifications.Bus.notify(new ReasonMLNotification("version", merlinVersion.toString(), NotificationType.INFORMATION));
    }

    @Override
    public void disposeComponent() {
        System.out.println("Dispose merlin component");
        if (merlin != null && merlin.isAlive()) {
            try {
                writer.close();
                reader.close();
                errorReader.close();
            } catch (IOException e) {
                // nothing to do
            }
            merlin.destroyForcibly();
            merlin = null;
        }
    }

    @Override
    public List<MerlinError> errors(String filename) {
        return makeRequest(ERRORS_TYPE_REFERENCE, filename, "[\"errors\"]");
    }

    @Override
    public MerlinVersion version() {
        return makeRequest(VERSION_TYPE_REFERENCE, NO_CONTEXT, "[\"protocol\", \"version\"]");
    }

    @Override
    public MerlinVersion selectVersion(int version) {
        return makeRequest(VERSION_TYPE_REFERENCE, NO_CONTEXT, "[\"protocol\", \"version\", " + version + "]");
    }

    @Override
    public void sync(String filename, String buffer) {
        try {
            makeRequest(BOOLEAN_TYPE_REFERENCE, filename, "[\"tell\", \"start\", \"end\", " + this.objectMapper.writeValueAsString(buffer) + "]");
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object dump(String filename, DumpFlag flag) {
        return makeRequest(new TypeReference<Object>() {
        }, filename, "[\"dump\", \"" + flag.name() + "\"]");
    }

    @Override
    public List<MerlinToken> dumpTokens(String filename) {
        return makeRequest(new TypeReference<List<MerlinToken>>() {
        }, filename, "[\"dump\", \"" + DumpFlag.tokens.name() + "\"]");
    }

    @Override
    public List<String> paths(String filename, Path path) {
        return makeRequest(new TypeReference<List<String>>() {
        }, filename, "[\"path\", \"list\", \"" + path.name() + "\"]");
    }

    @Override
    public List<String> listExtensions(String filename) {
        return makeRequest(new TypeReference<List<String>>() {
        }, filename, "[\"extension\", \"list\"]");
    }

    @Override
    public void enableExtensions(String filename, List<String> extensions) {
        List<String> collect = extensions.stream().map(s -> {
            try {
                return this.objectMapper.writeValueAsString(s);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());

        makeRequest(new TypeReference<Object>() {
        }, filename, "[\"extension\", \"enable\", [" + Joiner.on("'").join(collect) + "]]");

    }

    @Override
    public Object projectGet() {
        return makeRequest(new TypeReference<Object>() {
        }, "filename", "[\"project\", \"get\"]");
    }

    @Override
    public List<MerlinType> findType(String filename, MerlinPosition position) {
        return makeRequest(TYPE_TYPE_REFERENCE, filename, "[\"type\", \"enclosing\", \"at\", " + position + "]");
    }

    private <R> R makeRequest(TypeReference<R> type, @Nullable String filename, String query) {
        if (writer == null) {
            return null;
        }

        try {
            String request;
            if (filename == NO_CONTEXT) {
                request = query;
            } else {
                request = "{\"context\": [\"auto\", " + this.objectMapper.writeValueAsString(filename) + "], " +
                        "\"query\": " + query + "}";
                //System.out.println("make request " + request);
            }

            writer.write(request);
            writer.flush();

            StringBuilder errorBuffer = new StringBuilder();
            errorReader.lines().forEach(l -> errorBuffer.append(l).append(System.lineSeparator()));
            if (0 < errorBuffer.length()) {
                throw new RuntimeException(errorBuffer.toString());
            } else {
//                System.out.println(reader.lines().count());
                String content = reader.readLine();
//                System.out.println("  »» " + content);
                JsonNode jsonNode = this.objectMapper.readTree(content);
                JsonNode responseNode = extractResponse(jsonNode);

//                System.out.println("Result found: >> " + responseNode.toString() + " <<");
                return this.objectMapper.convertValue(responseNode, type);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (RuntimeException e) {
            System.err.println("ERROR");
            e.printStackTrace();
            throw e;
        }
    }

    private JsonNode extractResponse(JsonNode merlinResult) {
        JsonNode classField = merlinResult.get("class");
        String responseType = classField.textValue();
        if ("return".equals(responseType)) {
            return merlinResult.get("value");
        }

        System.err.println("Request failed: " + classField.asText() + " > " + merlinResult.get(1).toString());
        throw new RuntimeException(merlinResult.get(1).toString());
    }
}
