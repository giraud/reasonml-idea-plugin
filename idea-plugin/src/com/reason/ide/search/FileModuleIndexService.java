package com.reason.ide.search;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.util.indexing.*;
import com.reason.ide.search.index.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class FileModuleIndexService {
  private final @Nullable FileModuleIndex m_index;
  private final @Nullable NamespaceIndex m_nsIndex;

  public FileModuleIndexService() {
    m_index = FileModuleIndex.getInstance();
    m_nsIndex = NamespaceIndex.getInstance();
  }

  public static FileModuleIndexService getService() {
    return ServiceManager.getService(FileModuleIndexService.class);
  }

  @NotNull
  public Collection<String> getNamespaces(@NotNull Project project) {
    return m_nsIndex == null ? Collections.emptyList() : FileBasedIndex.getInstance().getAllKeys(m_nsIndex.getName(), project);
  }

  public boolean isNamespace(@Nullable String name, @NotNull Project project) {
    return name != null && m_nsIndex != null && FileBasedIndex.getInstance().getAllKeys(m_nsIndex.getName(), project).contains(name);
  }

  @NotNull
  Collection<IndexedFileModule> getFilesForNamespace(@NotNull String namespace, @NotNull GlobalSearchScope scope) {
    Collection<IndexedFileModule> result = new ArrayList<>();

    FileBasedIndex fileIndex = FileBasedIndex.getInstance();

    if (m_index != null && scope.getProject() != null) {
      for (String key : fileIndex.getAllKeys(m_index.getName(), scope.getProject())) {
        List<FileModuleData> values = fileIndex.getValues(m_index.getName(), key, scope);
        int valuesSize = values.size();
        if (valuesSize > 2) {
          System.out.println("ERROR, size of " + key + " is " + valuesSize);
        } else {
          for (FileModuleData value : values) {
            if (valuesSize == 1 || value.isInterface()) {
              if (namespace.equals(value.getNamespace())) {
                result.add(value);
              }
            }
          }
        }
      }
    }

    return result;
  }
}
