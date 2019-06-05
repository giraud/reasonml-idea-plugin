package com.reason.lang.core.signature;

import com.intellij.lang.Language;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.reason.RmlLanguage;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Unified signature between OCaml and Reason.
 * Hindley-Milner
 */
public class ORSignature {

    public static final ORSignature EMPTY = new ORSignature("");
    private static final String REASON_SEPARATOR = " => ";
    private static final String OCAML_SEPARATOR = " -> ";

    @NotNull
    private final SignatureType[] m_types;

    public static class SignatureType {
        String value;
        boolean mandatory = false;
        @NotNull
        String defaultValue = "";

        @NotNull
        @Override
        public String toString() {
            return value + (defaultValue.isEmpty() ? "" : "=" + defaultValue);
        }
    }

    public ORSignature(boolean isOcaml, @NotNull Collection<PsiSignatureItem> items) {
        m_types = new SignatureType[items.size()];
        int i = 0;
        for (PsiSignatureItem item : items) {
            String[] tokens = item.getText().split("=");
            String normalizedValue = tokens[0].
                    replaceAll("\\s+", " ").
                    replaceAll("\\( ", "\\(").
                    replaceAll(", \\)", "\\)").
                    replaceAll("=>", "->");
            SignatureType signatureType = new SignatureType();
            signatureType.value = normalizedValue; //(isOcaml && item.isNamedItem()) ? "~" + normalizedValue : normalizedValue;
            signatureType.mandatory = !tokens[0].contains("option") && tokens.length == 1;
            signatureType.defaultValue = 2 == tokens.length ? tokens[1] : "";

            m_types[i] = signatureType;
            i++;
        }
    }

    public ORSignature(Collection<PsiParameter> parameters) {
        m_types = new SignatureType[parameters.size() + 1];
        int i = 0;
        for (PsiParameter item : parameters) {
            String[] tokens = item.getText().split("=");
            String normalizedValue = tokens[0];

            SignatureType signatureType = new SignatureType();
            signatureType.value = normalizedValue;
            signatureType.mandatory = false /* we don't know */;
            signatureType.defaultValue = 2 == tokens.length ? tokens[1] : "";

            m_types[i] = signatureType;
            i++;
        }


        SignatureType signatureType = new SignatureType();
        signatureType.value = "'a";
        signatureType.mandatory = false /* we don't know */;
        signatureType.defaultValue = "";
        m_types[i] = signatureType;

        StringBuilder sb = new StringBuilder();
        sb.append("(");
        for (int j = 0; j < m_types.length - 1; j++) {
            SignatureType m_type = m_types[j];
            if (0 < j) {
                sb.append(", ");
            }
            sb.append(m_type);
        }
        sb.append(")").append(REASON_SEPARATOR).append("'a");
        sb.toString(); /* TODO */
    }

    public ORSignature(@NotNull String signature) {
        if (signature.isEmpty()) {
            m_types = new SignatureType[0];
        } else {
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
        }
    }

    @NotNull
    @Override
    public String toString() {
        return asString(OclLanguage.INSTANCE);
    }

    public String asString(Language lang) {
        String sig = buildSignature(lang == RmlLanguage.INSTANCE);
        if (sig.length() > 1000) {
            return sig.substring(0, 1000) + "...";
        }
        return sig;
    }

    public String asParameterInfo(Language lang) {
        String sig = buildParameters();
        if (sig.length() > 1000) {
            return sig.substring(0, 1000) + "...";
        }
        return sig;
    }

    public boolean isFunctionSignature() {
        return 1 < m_types.length;
    }

    public boolean isEmpty() {
        return m_types.length == 0;
    }

    public boolean isMandatory(int index) {
        return m_types.length <= index || m_types[index].mandatory;
    }

    @NotNull
    public SignatureType[] getTypes() {
        return m_types;
    }

    @NotNull
    private String buildSignature(boolean reason) {
        StringBuilder sb = new StringBuilder();

        if (reason && 2 < m_types.length) {
            sb.append("(");
        }

        String inputSeparator = reason ? ", " : OCAML_SEPARATOR;
        for (int i = 0; i < m_types.length - 1; i++) {
            if (0 < i) {
                sb.append(inputSeparator);
            }
            SignatureType type = m_types[i];
            sb.append(type.value);
            if (!type.mandatory && !type.defaultValue.isEmpty()) {
                sb.append("=").append(type.defaultValue);
            }
        }

        if (reason && 2 < m_types.length) {
            sb.append(")");
        }
        if (1 < m_types.length) {
            sb.append(reason ? REASON_SEPARATOR : OCAML_SEPARATOR);
        }
        if (0 < m_types.length) {
            sb.append(m_types[m_types.length - 1]);
        }

        return sb.toString();
    }

    @NotNull
    private String buildParameters() {
        StringBuilder sb = new StringBuilder();

        String inputSeparator = ", ";
        for (int i = 0; i < m_types.length - 1; i++) {
            if (0 < i) {
                sb.append(inputSeparator);
            }
            sb.append(m_types[i].value);
        }

        return sb.toString();
    }
}
