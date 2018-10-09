package com.reason.lang.core;

import com.reason.Joiner;
import org.jetbrains.annotations.NotNull;

/**
 * Hindley-Milner signature
 */
public class HMSignature {

    public static final HMSignature EMPTY = new HMSignature(false, "");

    static class SignatureItem {
        String value;
        boolean mandatory;
    }

    @NotNull
    private final SignatureItem[] m_types;
    @NotNull
    private final String m_signature;

    public HMSignature(boolean isOCaml, @NotNull String signature) {
        String x = signature.
                replaceAll("\n", "").
                replaceAll("\\s+", " ");

        String[] tokens = x.split(isOCaml ? "->" : "=>");
        m_types = new SignatureItem[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            m_types[i] = new SignatureItem();
            m_types[i].value = token;
            m_types[i].mandatory = !token.contains("option");
        }

        // Always use thin arrow
        m_signature = Joiner.join(" -> ", tokens);
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
}
