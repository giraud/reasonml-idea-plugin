package com.reason.merlin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.reason.Platform;
import com.reason.ide.ReasonMLNotification;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class MerlinProcess implements Closeable {

    static final String NO_CONTEXT = null;

    private ObjectMapper m_objectMapper;
    private Process m_merlin;
    private BufferedWriter m_writer;
    private BufferedReader m_reader;
    private BufferedReader m_errorReader;

    MerlinProcess(String merlinBin) throws IOException {
        List<String> commands;

        if (Platform.isWindows()) {
            commands = singletonList(merlinBin);
        } else {
            String absolutePath = new File(merlinBin).getAbsoluteFile().getParent();
            commands = asList("bash", "-c", "export PATH=" + absolutePath + ":$PATH && ocamlmerlin");
        }

        ProcessBuilder processBuilder = new ProcessBuilder(commands).redirectErrorStream(true);

        m_merlin = processBuilder.start();
        m_writer = new BufferedWriter(new OutputStreamWriter(m_merlin.getOutputStream()));
        m_reader = new BufferedReader(new InputStreamReader(m_merlin.getInputStream()));
        m_errorReader = new BufferedReader(new InputStreamReader(m_merlin.getErrorStream()));

        m_objectMapper = new ObjectMapper();
    }

    @Override
    public void close() throws IOException {
        if (m_merlin != null && m_merlin.isAlive()) {
            try {
                m_writer.close();
                m_reader.close();
                m_errorReader.close();
            } catch (IOException e) {
                // nothing to do
            }
            m_merlin.destroyForcibly();
            m_merlin = null;
        }
    }

    @Nullable
    synchronized <R> R makeRequest(TypeReference<R> type, @Nullable String filename, String query) {
        if (m_merlin == null) {
            return null;
        }

        try {
            String request;
            if (filename == NO_CONTEXT) {
                request = query;
            } else {
                request = "{\"context\": [\"auto\", " + m_objectMapper.writeValueAsString(filename) + "], " +
                        "\"query\": " + query + "}";
            }
            // System.out.println("=> " + request);

            m_writer.write(request);
            m_writer.flush();

            if (m_errorReader.ready()) {
                StringBuilder errorBuffer = new StringBuilder();
                m_errorReader.lines().forEach(l -> errorBuffer.append(l).append(System.lineSeparator()));
                throw new RuntimeException(errorBuffer.toString());
            } else {
                String content = m_reader.readLine();
                // System.out.println("<= content: " + content);
                JsonNode jsonNode = m_objectMapper.readTree(content);
                JsonNode responseNode = extractResponse(jsonNode);
                if (responseNode == null) {
                    return null;
                }
                //System.out.println("<= " + responseNode);

                try {
                    return m_objectMapper.convertValue(responseNode, type);
                } catch (RuntimeException e) {
                    System.err.println("!! Request conversion error");
                    System.err.println("        file: " + filename);
                    System.err.println("     request: " + query);
                    System.err.println("     content: " + content);
                    System.err.println("         msg: " + e.getMessage());
                    throw e;
                }
            }
        } catch (IOException e) {
            System.err.println("!! Request IO error");
            System.err.println("        file: " + filename);
            System.err.println("     request: " + query);
            System.err.println("         msg: " + e.getMessage());
            throw new UncheckedIOException(e);
        }
    }

    @Nullable
    private JsonNode extractResponse(JsonNode merlinResult) {
        JsonNode classField = merlinResult.get("class");
        if (classField == null) {
            return null;
        }
        JsonNode value = merlinResult.get("value");

        String responseType = classField.textValue();
        if ("return".equals(responseType)) {
            return value;
        }

        // Something went wrong with merlin, it can be: failure|error|exception
        // https://github.com/ocaml/merlin/blob/master/doc/dev/PROTOCOL.md#answers
        if ("error".equals(responseType)) {
            Notifications.Bus.notify(new ReasonMLNotification("Merlin", responseType, value.toString(), NotificationType.ERROR, null));
        }

        // failure or error should not be reported to the user
        return null;
    }

    String writeValueAsString(Object value) {
        try {
            return m_objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
