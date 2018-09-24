package com.reason.lang.core;

import org.jetbrains.annotations.NotNull;

/**
 * Hindley-Milner signature
 */
public class HMSignature {

    public static final HMSignature EMPTY = new HMSignature("");

    public static class SignatureType {
        String value;
        boolean mandatory;
        String defaultValue;

        @Override
        public String toString() {
            return value;
        }
    }

    @NotNull
    private final SignatureType[] m_types;
    @NotNull
    private final String m_signature;

    public HMSignature(@NotNull String signature) {
        String normalized = signature.
                trim().
                replaceAll("\n", "").
                replaceAll("\\s+", " ").
                replaceAll("=>", "->");

        String[] items = normalized.split("->");
        m_types = new SignatureType[items.length];
        for (int i = 0; i < items.length; i++) {
            String[] tokens = items[i].trim().split("=");
            m_types[i] = new SignatureType();
            m_types[i].value = tokens[0];
            m_types[i].mandatory = !tokens[0].contains("option") && tokens.length == 1;
            m_types[i].defaultValue = 2 == tokens.length ? tokens[1] : "";
        }

        // Always use thin arrow
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < m_types.length; i++) {
            if (0 < i) {
                sb.append(" -> ");
            }
            SignatureType type = m_types[i];
            sb.append(type.value);
        }
        m_signature = sb.toString();
    }

    @Override
    public String toString() {
        return m_signature;
    }

    public boolean isFunctionSignature() {
        return 1 < m_types.length;
    }

    public boolean isEmpty() {
        return m_signature.isEmpty();
    }

    public boolean isMandatory(int index) {
        return m_types[index].mandatory;
    }

    public SignatureType[] getTypes() {
        return m_types;
    }
}
