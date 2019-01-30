package com.reason.ide.hints;

import com.intellij.lang.Language;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.util.containers.Stack;
import com.reason.lang.core.signature.ORSignature;
import gnu.trove.THashMap;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Set;

public class InferredTypesImplementation implements InferredTypes {

    private static final String OPEN = "Op";
    private static final String VALUE = "Va";
    private static final String MODULE_GHOST = "Mg";

    private final Map<String, Stack<OpenModule>> m_opens = new THashMap<>();
    private final Map<Integer, LogicalORSignature> m_pos = new THashMap<>();

    private final Map<Integer/*Line*/, Map<String/*ident*/, Map<LogicalPosition, ORSignature>>> m_idents = new THashMap<>();

    private final Map<String, ORSignature> m_let = new THashMap<>();
    private final Map<String, InferredTypesImplementation> m_modules = new THashMap<>();


    public void addTypes(@NotNull String type) {
        try {
            if (type.startsWith("val")) {
                int colonPos = type.indexOf(':');
                if (0 < colonPos && colonPos < type.length()) {
                    m_let.put(type.substring(4, colonPos - 1), new ORSignature(type.substring(colonPos + 1)));
                }
            } else if (type.startsWith("module")) {
                int colonPos = type.indexOf(':');
                if (0 <= colonPos) {
                    int sigPos = type.indexOf("sig");
                    InferredTypesImplementation moduleTypes = new InferredTypesImplementation();
                    m_modules.put(type.substring(7, colonPos - 1), moduleTypes);
                    int beginIndex = sigPos + 3;
                    int endIndex = type.length() - 3;
                    if (beginIndex < endIndex) {
                        String sigTypes = type.substring(beginIndex, endIndex);
                        String[] moduleSigTypes = sigTypes.trim().split("(?=module|val|type)");
                        for (String moduleSigType : moduleSigTypes) {
                            moduleTypes.addTypes(moduleSigType);
                        }
                    }
                }
            }
        } catch (RuntimeException e) {
            Logger.getInstance("ReasonML.types").error("Error while decoding type [" + type + "]", e);
        }
    }

    @NotNull
    public Map<Integer, String> listOpensByLines() {
        Map<Integer, String> result = new THashMap<>();
        for (Map.Entry<String, Stack<OpenModule>> entry : m_opens.entrySet()) {
            String moduleName = entry.getKey();
            Stack<OpenModule> stack = entry.getValue();
            for (OpenModule openModule : stack) {
                result.put(openModule.getLine(), openModule.getExposing());
            }
        }
        return result;
    }

    @NotNull
    public Map<Integer, String> signaturesByLines(Language lang) {
        Map<Integer, String> result = new THashMap<>();
        for (Map.Entry<Integer, LogicalORSignature> entry : m_pos.entrySet()) {
            result.put(entry.getKey(), entry.getValue().getSignature().asString(lang));
        }
        return result;
    }

    @NotNull
    public Map<Integer/*Line*/, Map<String/*ident*/, Map<LogicalPosition, ORSignature>>> listTypesByIdents() {
        return m_idents;
    }

    public void add(@NotNull String[] tokens) {
        if (OPEN.equals(tokens[0])) {
            LogicalPosition logicalPosition = extractLogicalPosition(tokens[1]);
            Stack<OpenModule> openStack = new Stack<>();
            openStack.push(new OpenModule(logicalPosition));
            m_opens.put(tokens[2], openStack);
        } else if (VALUE.equals(tokens[0])) {
            LogicalPosition logicalPosition = extractLogicalPosition(tokens[1]);
            addVisibleSignature(logicalPosition, new ORSignature(tokens[3]));
        } else if (MODULE_GHOST.equals(tokens[0])) {
            LogicalPosition logicalPosition = extractLogicalPosition(tokens[1]);
            String signature = tokens[3].startsWith("type t = ") ? tokens[3].substring(9) : tokens[3];
            addVisibleSignature(logicalPosition, new ORSignature(signature));
        }
    }

    private void addVisibleSignature(@NotNull LogicalPosition pos, @NotNull ORSignature signature) {
        LogicalORSignature savedSignature = m_pos.get(pos.line);
        if (savedSignature == null || pos.column < savedSignature.getLogicalPosition().column) {
            m_pos.put(pos.line, new LogicalORSignature(pos, signature));
        }
    }

    @NotNull
    private LogicalPosition extractLogicalPosition(@NotNull String value) {
        String[] loc = value.split(",");
        String[] pos = loc[0].split("\\.");
        int line = Integer.parseInt(pos[0]) - 1;
        int column = Integer.parseInt(pos[1]);
        return new LogicalPosition(line < 0 ? 0 : line, column < 0 ? 0 : column);
    }

    static class OpenModule {
        @NotNull
        private final LogicalPosition m_position;
        private final Set<String> m_values = new THashSet<>();

        OpenModule(LogicalPosition position) {
            m_position = position;
        }

        Integer getLine() {
            return m_position.line;
        }

        String getExposing() {
            return "exposing lot of stuff";
        }
    }

    static class LogicalORSignature {
        @NotNull
        private final LogicalPosition m_logicalPosition;
        @NotNull
        private final ORSignature m_signature;

        LogicalORSignature(@NotNull LogicalPosition position, @NotNull ORSignature signature) {
            m_logicalPosition = position;
            m_signature = signature;
        }

        @NotNull
        LogicalPosition getLogicalPosition() {
            return m_logicalPosition;
        }

        @NotNull
        public ORSignature getSignature() {
            return m_signature;
        }
    }

}
