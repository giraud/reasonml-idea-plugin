package com.reason.bs.hints;

import java.util.HashMap;
import java.util.Map;

public class InferredTypesImplementation implements BsQueryTypesService.InferredTypes {
    private final Map<String, String> m_let = new HashMap<>();
    private final Map<String, InferredTypesImplementation> m_modules = new HashMap<>();

    public void add(String type) {
        if (type.startsWith("val")) {
            int colonPos = type.indexOf(':');
            m_let.put(type.substring(4, colonPos - 1), type.substring(colonPos + 1).replaceAll("\\s+", " "));
        } else if (type.startsWith("module")) {
            int colonPos = type.indexOf(':');
            if (0 <= colonPos) {
                int sigPos = type.indexOf("sig");
                InferredTypesImplementation moduleTypes = new InferredTypesImplementation();
                m_modules.put(type.substring(7, colonPos - 1), moduleTypes);
                String sigTypes = type.substring(sigPos + 3, type.length() - 3);
                String[] moduleSigTypes = sigTypes.trim().split("(?=module|val|type)");
                for (String moduleSigType : moduleSigTypes) {
                    moduleTypes.add(moduleSigType);
                }
            }
        }
    }

    public String getLetType(String name) {
        return m_let.get(name);
    }

    public BsQueryTypesService.InferredTypes getModuleType(String name) {
        return m_modules.get(name);
    }
}
