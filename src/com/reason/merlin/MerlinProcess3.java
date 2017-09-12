package com.reason.merlin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.reason.Joiner;
import com.reason.ide.RmlNotification;
import com.reason.merlin.types.MerlinVersion;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

class MerlinProcess3 {

    static final List<String> DOCKER_COMMAND = asList("docker", "run", "-i", "ocamlu");
    static final List<String> MERLIN_COMMAND = asList("ocamlmerlin", "single");

    private File m_merlinBin;
    private ObjectMapper m_objectMapper;

    MerlinProcess3(String merlinBin) throws IOException {
        m_merlinBin = new File(merlinBin).getAbsoluteFile();
        m_objectMapper = new ObjectMapper();
    }

    String runCommand(String filename, String source, List<String> command) throws IOException {
        List<String> commands = new ArrayList<>();

        long start = System.currentTimeMillis();

        commands.addAll(DOCKER_COMMAND);
        commands.addAll(MERLIN_COMMAND);
        commands.addAll(command);
        if (filename != null) {
            commands.add("-filename");
            commands.add(filename);
        }
        System.out.println(">> " + Joiner.join(" ", commands));

        ProcessBuilder pb = new ProcessBuilder(commands);
        pb.redirectErrorStream(true);

        Process m_merlin = pb.start();
        BufferedWriter m_writer = new BufferedWriter(new OutputStreamWriter(m_merlin.getOutputStream()));
        BufferedReader m_reader = new BufferedReader(new InputStreamReader(m_merlin.getInputStream()));

        if (source != null) {
            m_writer.write(source);
            m_writer.close();
        }

        String content = m_reader.readLine();
        long end = System.currentTimeMillis();

        System.out.println("--- " + (end - start) + "ms");
        System.out.println("    <= " + content);

        return content;
    }

    <R> R execute(TypeReference<R> type, String filename, String source, List<String> command) {
        List<String> commands = new ArrayList<>();

        try {
            String content = runCommand(filename, source, command);

            JsonNode jsonNode = m_objectMapper.readTree(content);
            JsonNode responseNode = extractResponse(jsonNode);
            if (responseNode == null) {
                return null;
            }

            try {
                return m_objectMapper.convertValue(responseNode, type);
            } catch (RuntimeException e) {
                System.err.println("!! Request conversion error");
                System.err.println("        file: " + filename);
                System.err.println("     request: " + Joiner.join(" ", commands));
                System.err.println("     content: " + content);
                System.err.println("         msg: " + e.getMessage());
                throw e;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    MerlinVersion version() {
        try {
            MerlinVersion merlinVersion = new MerlinVersion();
            merlinVersion.merlin = runCommand(null, null, asList("-version"));
            return merlinVersion;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
            Notifications.Bus.notify(new RmlNotification("Merlin", responseType, value.toString(), NotificationType.ERROR, null));
        }

        // failure or error should not be reported to the user
        return null;
    }
}

