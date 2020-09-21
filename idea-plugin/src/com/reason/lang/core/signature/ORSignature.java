package com.reason.lang.core.signature;

import com.intellij.lang.Language;
import com.intellij.util.ArrayUtil;
import com.reason.lang.core.psi.PsiParameter;
import com.reason.lang.core.psi.PsiSignatureItem;
import com.reason.lang.napkin.NsLanguage;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.reason.RmlLanguage;
import java.util.Collection;
import java.util.Collections;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Unified signature between OCaml and Reason. Hindley-Milner */
public class ORSignature {

  public static final ORSignature EMPTY =
      new ORSignature(RmlLanguage.INSTANCE, Collections.emptyList());
  public static final PsiSignatureItem[] EMPTY_ITEMS = new PsiSignatureItem[0];
  private static final String REASON_SEPARATOR = " => ";
  private static final String OCAML_SEPARATOR = " -> ";

  @NotNull private final SignatureType[] m_types;

  @Nullable private PsiSignatureItem[] m_items;

  public static class SignatureType {
    PsiSignatureItem item;
    String value;
    boolean mandatory = false;
    @NotNull String defaultValue = "";

    @NotNull
    @Override
    public String toString() {
      return value + (defaultValue.isEmpty() ? "" : "=" + defaultValue);
    }
  }

  public ORSignature(@NotNull Language language, @NotNull Collection<PsiSignatureItem> items) {
    m_items = items.isEmpty() ? null : ArrayUtil.toObjectArray(items, PsiSignatureItem.class);

    m_types = new SignatureType[items.size()];
    int i = 0;
    for (PsiSignatureItem item : items) {
      String text = item.getText();
      if (text.isEmpty()) {
        m_types[i] = new SignatureType();
      } else {
        String[] tokens = text.split("=");
        String normalizedValue =
            tokens[0]
                .replaceAll("\\s+", " ")
                .replaceAll("\\( ", "\\(")
                .replaceAll(", \\)", "\\)")
                .replaceAll("=>", "->");
        SignatureType signatureType = new SignatureType();
        signatureType.item = item;
        signatureType.value = normalizedValue;
        signatureType.mandatory = !tokens[0].contains("option") && tokens.length == 1;
        signatureType.defaultValue = 2 == tokens.length ? tokens[1] : "";

        m_types[i] = signatureType;
      }
      i++;
    }
  }

  public ORSignature(@NotNull Collection<PsiParameter> parameters) {
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

  @NotNull
  @Override
  public String toString() {
    return asString(OclLanguage.INSTANCE);
  }

  @NotNull
  public String asString(@NotNull Language lang) {
    String sig = buildSignature(lang);
    if (sig.length() > 1000) {
      return sig.substring(0, 1000) + "...";
    }
    return sig;
  }

  @NotNull
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
  public PsiSignatureItem[] getItems() {
    return m_items == null ? EMPTY_ITEMS : m_items;
  }

  @NotNull
  private String buildSignature(@NotNull Language lang) {
    StringBuilder sb = new StringBuilder();

    boolean reason = lang == RmlLanguage.INSTANCE || lang == NsLanguage.INSTANCE;
    String inputSeparator = reason ? ", " : OCAML_SEPARATOR;

    if (m_items != null) {
      if (reason && 2 < m_items.length) {
        sb.append("(");
      }

      for (int i = 0; i < m_items.length - 1; i++) {
        if (0 < i) {
          sb.append(inputSeparator);
        }
        PsiSignatureItem type = m_items[i];
        if (type != null) {
          sb.append(type.asText(lang).trim());
        }
      }

      if (reason && 2 < m_items.length) {
        sb.append(")");
      }
      if (1 < m_items.length) {
        sb.append(reason ? REASON_SEPARATOR : OCAML_SEPARATOR);
      }
      if (0 < m_items.length) {
        PsiSignatureItem type = m_items[m_items.length - 1];
        if (type != null) {
          sb.append(type.asText(lang));
        }
      }

      return sb.toString()
          .replaceAll("\\s+", " ")
          .replaceAll("\\( ", "\\(")
          .replaceAll(", \\)", "\\)");
    }

    if (reason && 2 < m_types.length) {
      sb.append("(");
    }

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
