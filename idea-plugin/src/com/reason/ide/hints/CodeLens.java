package com.reason.ide.hints;

import com.intellij.openapi.editor.*;
import com.intellij.openapi.util.*;
import gnu.trove.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CodeLens {
  public static final Key<CodeLens> CODE_LENS = Key.create("reasonml.codelens");

  @NotNull
  final Map<Integer, InferredTypes.LogicalPositionSignature> m_signatures = new THashMap<>();

  public synchronized @Nullable String get(int line) {
    InferredTypes.LogicalPositionSignature signature = m_signatures.get(line);
    return signature == null ? null : signature.signature;
  }

  public synchronized void putAll(@NotNull Map<Integer, InferredTypes.LogicalPositionSignature> signatures) {
    m_signatures.clear();
    m_signatures.putAll(signatures);
  }

  public synchronized void move(@NotNull LogicalPosition cursorPosition, int direction) {
    Map<Integer, InferredTypes.LogicalPositionSignature> newSignatures = new THashMap<>();

    int startLine = cursorPosition.line;
    for (Map.Entry<Integer, InferredTypes.LogicalPositionSignature> signatureEntry : m_signatures.entrySet()) {
      Integer line = signatureEntry.getKey();
      InferredTypes.LogicalPositionSignature value = signatureEntry.getValue();
      if (startLine == line) {
        if (cursorPosition.column < value.colEnd) {
          newSignatures.put(line + direction, value);
        } else {
          newSignatures.put(line, value);
        }
      } else if (startLine < line) {
        newSignatures.put(line + direction, value);
      } else {
        newSignatures.put(line, value);
      }
    }

    m_signatures.clear();
    m_signatures.putAll(newSignatures);
  }

  public void remove(int line) {
    m_signatures.remove(line);
  }
}
