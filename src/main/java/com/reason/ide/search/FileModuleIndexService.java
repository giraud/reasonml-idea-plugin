package com.reason.ide.search;

import com.intellij.openapi.application.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.util.indexing.*;
import com.reason.ide.search.index.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class FileModuleIndexService {
    private static final Log LOG = Log.create("index.fileservice");

    private final @Nullable FileModuleIndex m_index;

    public FileModuleIndexService() {
        m_index = FileModuleIndex.getInstance();
    }

    public static FileModuleIndexService getService() {
        return ApplicationManager.getApplication().getService(FileModuleIndexService.class);
    }

    @NotNull
    public List<FileModuleData> getTopModules(@NotNull Project project, @NotNull GlobalSearchScope scope) {
        List<FileModuleData> result = new ArrayList<>();

        ID<String, FileModuleData> indexId = m_index == null ? null : m_index.getName();
        if (indexId != null) {
            FileBasedIndex index = FileBasedIndex.getInstance();
            for (String key : index.getAllKeys(indexId, project)) {
                result.addAll(getTopModuleData(key, scope));
            }
        }

        return result;
    }

    public @NotNull List<FileModuleData> getTopModuleData(@NotNull String name, @NotNull GlobalSearchScope scope) {
        ID<String, FileModuleData> indexId = m_index == null ? null : m_index.getName();
        return indexId == null ? Collections.emptyList() : FileBasedIndex.getInstance().getValues(indexId, name, scope);
    }

    @NotNull
    public Collection<String> getNamespaces(@NotNull Project project, @NotNull GlobalSearchScope scope) {
        Set<String> result = new HashSet<>();

        ID<String, FileModuleData> indexId = m_index == null ? null : m_index.getName();
        if (indexId != null) {
            for (String key : FileBasedIndex.getInstance().getAllKeys(indexId, project)) {
                getTopModuleData(key, scope).stream()
                        .filter(FileModuleData::hasNamespace)
                        .findFirst().map(FileModuleData::getNamespace)
                        .ifPresent(result::add);
            }
        }

        LOG.debug("namespaces", result);
        return result;
    }
}
