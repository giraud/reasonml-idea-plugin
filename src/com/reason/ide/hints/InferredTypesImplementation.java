package com.reason.ide.hints;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.LogicalPosition;
import com.reason.lang.core.HMSignature;
import com.reason.lang.core.LogicalHMSignature;
import gnu.trove.THashMap;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class InferredTypesImplementation implements InferredTypes {

    private final Map<Integer, LogicalHMSignature> m_pos = new THashMap<>();
    private final Map<Integer/*Line*/, Map<String/*ident*/, Map<LogicalPosition, HMSignature>>> m_idents = new THashMap<>();
    private final Map<LogicalPosition, String> m_opens = new THashMap<>();

    private final Map<String, HMSignature> m_let = new THashMap<>();
    private final Map<String, InferredTypesImplementation> m_modules = new THashMap<>();

    public void addTypes(String type) {
        try {
            if (type.startsWith("val")) {
                int colonPos = type.indexOf(':');
                if (0 < colonPos && colonPos < type.length()) {
                    m_let.put(type.substring(4, colonPos - 1), new HMSignature(type.substring(colonPos + 1)));
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

    public HMSignature getLetType(String name) {
        return m_let.get(name);
    }

    public InferredTypes getModuleType(String name) {
        return m_modules.get(name);
    }

    @NotNull
    public Map<LogicalPosition, String> listOpensByLines() {
        return new THashMap<>(m_opens);
    }

    @NotNull
    public Collection<LogicalHMSignature> listTypesByLines() {
        return m_pos.values();
    }

    @NotNull
    public Map<Integer/*Line*/, Map<String/*ident*/, Map<LogicalPosition, HMSignature>>> listTypesByIdents() {
        return m_idents;
    }

    public void add(@NotNull String[] tokens) {
        if ("O".equals(tokens[0])) {
            if (4 <= tokens.length) {
                LogicalPosition logicalPosition = extractLogicalPosition(tokens[1]);
                m_opens.put(logicalPosition, "exposing " + tokens[3].replaceAll(tokens[2] + ".", ""));
            }
        } else if (5 <= tokens.length) {
            LogicalPosition logicalPosition = extractLogicalPosition(tokens[1]);
            if ("I".equals(tokens[0])) {
                Map<String, Map<LogicalPosition, HMSignature>> lines = m_idents.get(logicalPosition.line);
                if (lines == null) {
                    lines = new THashMap<>();
                    m_idents.put(logicalPosition.line, lines);
                }

                Map<LogicalPosition, HMSignature> idents = lines.get(tokens[3]);
                if (idents == null) {
                    idents = new THashMap<>();
                    lines.put(tokens[3], idents);
                }

                idents.put(logicalPosition, new HMSignature(tokens[4]));
            } else {
                LogicalHMSignature signature = m_pos.get(logicalPosition.line);
                if (signature == null || logicalPosition.column < signature.getLogicalPosition().column) {
                    m_pos.put(logicalPosition.line, new LogicalHMSignature(logicalPosition, new HMSignature(tokens[4])));
                }
            }
        }
    }

    LogicalPosition extractLogicalPosition(@NotNull String encodedPos) {
        String[] codedPos = encodedPos.split("\\.");
        int line = Integer.parseInt(codedPos[0]);
        int column = Integer.parseInt(codedPos[1]);
        return new LogicalPosition(line < 0 ? 0 : line, column < 0 ? 0 : column);
    }
}
