package com.reason.merlin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;

import java.io.*;

public class MerlinProcess implements Closeable {

    static final String NO_CONTEXT = null;

    private ObjectMapper objectMapper;
    private Process merlin;
    private BufferedWriter writer;
    private BufferedReader reader;
    private BufferedReader errorReader;

    MerlinProcess(String merlinBin, String basePath) throws IOException {
        objectMapper = new ObjectMapper();
        ProcessBuilder processBuilder = new ProcessBuilder(merlinBin)
                .directory(new File(basePath))
                .redirectErrorStream(true);

        merlin = processBuilder.start();
        writer = new BufferedWriter(new OutputStreamWriter(merlin.getOutputStream()));
        reader = new BufferedReader(new InputStreamReader(merlin.getInputStream()));
        errorReader = new BufferedReader(new InputStreamReader(merlin.getErrorStream()));
    }

    @Override
    public void close() throws IOException {
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

    <R> R makeRequest(TypeReference<R> type, @Nullable String filename, String query) {
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

    String writeValueAsString(Object value) {
        try {
            return this.objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
