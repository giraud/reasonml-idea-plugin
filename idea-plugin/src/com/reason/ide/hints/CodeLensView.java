package com.reason.ide.hints;

import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import gnu.trove.THashMap;
import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CodeLensView {
  public static final Key<CodeLensInfo> CODE_LENS = Key.create("reasonml.codelens");

  private CodeLensView() {}

  public static class CodeLensInfo {
    @NotNull
    final Map<VirtualFile, Map<Integer, InferredTypes.LogicalPositionSignature>> m_signatures =
        new THashMap<>();

    @Nullable
    public synchronized String get(@NotNull VirtualFile file, int line) {
      Map<Integer, InferredTypes.LogicalPositionSignature> result = m_signatures.get(file);
      InferredTypes.LogicalPositionSignature sig = result == null ? null : result.get(line);
      return sig == null ? null : sig.signature;
    }

    synchronized void putAll(
        @NotNull VirtualFile file,
        @NotNull Map<Integer, InferredTypes.LogicalPositionSignature> signatures) {
      Map<Integer, InferredTypes.LogicalPositionSignature> signaturesPerLine =
          m_signatures.get(file);
      if (signaturesPerLine == null) {
        signaturesPerLine = new THashMap<>();
        m_signatures.put(file, signaturesPerLine);
      } else {
        signaturesPerLine.clear();
      }
      signaturesPerLine.putAll(signatures);
    }

    public synchronized void move(
        @NotNull VirtualFile file, @NotNull LogicalPosition cursorPosition, int direction) {
      Map<Integer, InferredTypes.LogicalPositionSignature> signaturesByLine =
          m_signatures.get(file);
      if (signaturesByLine != null) {
        int startLine = cursorPosition.line;
        Map<Integer, InferredTypes.LogicalPositionSignature> signatures = new THashMap<>();
        for (Map.Entry<Integer, InferredTypes.LogicalPositionSignature> signatureEntry :
            signaturesByLine.entrySet()) {
          Integer line = signatureEntry.getKey();
          InferredTypes.LogicalPositionSignature value = signatureEntry.getValue();
          if (startLine == line) {
            if (cursorPosition.column < value.colEnd) {
              signatures.put(line + direction, value);
            } else {
              signatures.put(line, value);
            }
          } else if (startLine < line) {
            signatures.put(line + direction, value);
          } else {
            signatures.put(line, value);
          }
        }
        m_signatures.put(file, signatures);
      }
    }
  }
}
