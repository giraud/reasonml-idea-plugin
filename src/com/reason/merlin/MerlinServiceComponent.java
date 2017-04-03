package com.reason.merlin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.util.List;

public class MerlinServiceComponent implements MerlinService, com.intellij.openapi.components.ApplicationComponent {

    private static final TypeReference<List<MerlinError>> ERRORS_TYPE_REFERENCE = new TypeReference<List<MerlinError>>() {
    };
    private static final TypeReference<String> STRING_TYPE_REFERENCE = new TypeReference<String>() {
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
        ProcessBuilder processBuilder = new ProcessBuilder(merlinBin).redirectErrorStream(true);

        try {
            merlin = processBuilder.start();
            writer = new BufferedWriter(new OutputStreamWriter(merlin.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(merlin.getInputStream()));
            errorReader = new BufferedReader(new InputStreamReader(merlin.getErrorStream()));
        } catch (IOException e) {
            Notifications.Bus.notify(new Notification("reasonML", "Error locating merlin", "Can't find merlin, using '" + merlinBin + "'\n" + e.getMessage(), NotificationType.ERROR));
            e.printStackTrace();
        }
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
    public List<MerlinError> errors() {
        return makeRequest(ERRORS_TYPE_REFERENCE, "filename", "[\"errors\"]");
    }

    @Override
    public String version() {
        return makeRequest(STRING_TYPE_REFERENCE, "filename", "[\"version\"]");
    }

    @Override
    public Object dump(DumpFlag flag) {
        return makeRequest(new TypeReference<Object>() {
        }, "filename", "[\"dump\", \"" + flag.name() + "\"]");
    }

    @Override
    public List<MerlinToken> dumpTokens() {
        return makeRequest(new TypeReference<List<MerlinToken>>() {
        }, "filename", "[\"dump\", \"" + DumpFlag.tokens.name() + "\"]");
    }

    @Override
    public List<String> paths(Path path) {
        return makeRequest(new TypeReference<List<String>>() {
        }, "filename", "[\"path\", \"list\", \"" + path.name() + "\"]");
    }

    @Override
    public List<String> listExtensions() {
        return makeRequest(new TypeReference<List<String>>() {
        }, "filename", "[\"extension\", \"list\"]");
    }

    private <R> R makeRequest(TypeReference<R> type, String filename, String request) {
//        System.out.println("make request " + request);
        if (writer == null) {
            return null;
        }

        try {
            writer.write(request);
            writer.flush();

            StringBuilder errorBuffer = new StringBuilder();
            errorReader.lines().forEach(l -> errorBuffer.append(l).append(System.lineSeparator()));
            if (0 < errorBuffer.length()) {
                throw new RuntimeException(errorBuffer.toString());
            } else {
                JsonNode jsonNode = objectMapper.readTree(reader.readLine());
                JsonNode responseNode = extractResponse(jsonNode);

//                System.out.println("Result found: >> " + responseNode.toString() + " <<");
                return objectMapper.convertValue(responseNode, type);
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private JsonNode extractResponse(JsonNode merlinResult) {
        String responseType = merlinResult.get(0).textValue();
        if ("return".equals(responseType)) {
            return merlinResult.get(1);
        }

        System.err.println("Request failed: " + merlinResult.get(0).asText() + " > " + merlinResult.get(1).toString());
        throw new RuntimeException(merlinResult.get(1).toString());
    }
}
