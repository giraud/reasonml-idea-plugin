package com.reason.ide.annotations;

import com.intellij.openapi.util.Pair;
import java.util.Collection;
import java.util.Set;
import org.jetbrains.annotations.NotNull;

public interface ErrorsManager {

  void addAllInfo(@NotNull Collection<OutputInfo> bsbInfo);

  @NotNull
  Collection<OutputInfo> getInfo(@NotNull String moduleName);

  boolean hasErrors(@NotNull String moduleName, int lineNumber);

  void clearErrors();

  void clearErrors(@NotNull String moduleName);

  @NotNull
  Pair<Set<String>, Set<String>> getKeys();
}
