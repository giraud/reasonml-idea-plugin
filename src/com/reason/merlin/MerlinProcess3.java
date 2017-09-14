package com.reason.merlin;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.NullNode;
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
import static java.util.Collections.singletonList;

class MerlinProcess3 {

    // private static final List<String> DOCKER_COMMAND = asList("docker", "run", "-i", "ocamla");
    private final List<String> m_merlinCommand;

    private ObjectMapper m_objectMapper;

    MerlinProcess3(String merlinBin) throws IOException {
        File m_merlinBin = new File(merlinBin).getAbsoluteFile();
        m_objectMapper = new ObjectMapper();
        m_merlinCommand = asList(m_merlinBin.getName(), "server");
    }

    MerlinVersion version() {
        try {
            MerlinVersion merlinVersion = new MerlinVersion();
            merlinVersion.merlin = runCommand(null, null, singletonList("-version"));
            return merlinVersion;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    JsonNode execute(String filename, String source, List<String> command) {
        String content = "";
        try {
            content = runCommand(filename, source, command);
            JsonNode jsonNode = m_objectMapper.readTree(content);
            JsonNode valueNode = extractResponse(jsonNode);
            return valueNode == null ? NullNode.getInstance() : valueNode;
        } catch (Exception ex) {
            throw new RuntimeException("An error occurred when executing a command for the file " + filename + "\n"
            + "The command is: " + Joiner.join(" ", command) + "\n"
            + "The output from merlin is: " + content, ex);
        }
    }

    private String runCommand(String filename, String source, List<String> command) throws IOException {
        List<String> commands = new ArrayList<>();

        // long start = System.currentTimeMillis();

        commands.addAll(m_merlinCommand);
        commands.addAll(command);
        if (filename != null) {
            commands.add("-filename");
            commands.add(filename);
        }

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

        // long end = System.currentTimeMillis();
        // System.out.println((end - start) + "ms > " + Joiner.join(" ", commands) + " << " + content);

        return content;
    }

    @Nullable
    private JsonNode extractResponse(JsonNode merlinResult) {
        // !! handle notifications

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

