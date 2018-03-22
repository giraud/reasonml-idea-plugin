package com.reason.lang.core;

import com.reason.Joiner;
import org.jetbrains.annotations.NotNull;

/**
 * Hindley-Milner signature
 */
public class HMSignature {

    public static final HMSignature EMPTY = new HMSignature(false, "");

    private final String[] m_types;
    private final String m_signature;

    public HMSignature(boolean isOcaml, @NotNull String signature) {
        String x = signature.
                replaceAll("\n", "").
                replaceAll("\\s+", " ");

        String[] tokens = x.split(isOcaml ? "->" : "=>");
        m_types = new String[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i].trim();
            if (isOcaml) {
                // Transform ocaml to reason syntax
                /*
                String[] items = token.split(" ");
                if (1 < items.length) {
                    token = items[0];
                    for (int j = 1; j < items.length; j++) {
                        String item = items[j];
                        token = item + "(" + token + ")";
                    }
                }
                */
            }
            m_types[i] = token;
        }

        // Always use fat arrow
        m_signature = Joiner.join(" => ", m_types);
    }

    @Override
    public String toString() {
        return m_signature;
    }

    public boolean isFunctionSignature() {
        return 1 < m_types.length;
    }
}
