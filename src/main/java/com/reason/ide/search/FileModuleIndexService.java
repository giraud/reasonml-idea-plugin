package com.reason.ide.search;

import com.intellij.openapi.application.*;
import com.intellij.openapi.project.*;
import com.intellij.psi.search.*;
import com.intellij.util.indexing.*;
import com.reason.ide.files.*;
import com.reason.ide.search.index.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class FileModuleIndexService {
    private static final Log LOG = Log.create("index.fileservice");

    private final @NotNull FileModuleIndex m_index;
    private final @NotNull NamespaceIndex m_nsIndex;

    public FileModuleIndexService() {
        m_index = FileModuleIndex.getInstance();
        m_nsIndex = NamespaceIndex.getInstance();
    }

    public static FileModuleIndexService getService() {
        return ApplicationManager.getApplication().getService(FileModuleIndexService.class);
    }

    @NotNull
    public Collection<String> getNamespaces(@NotNull Project project) {
        return FileBasedIndex.getInstance().getAllKeys(m_nsIndex.getName(), project);
    }

    public boolean isNamespace(@Nullable String name, @NotNull Project project) {
        return name != null
                && FileBasedIndex.getInstance().getAllKeys(m_nsIndex.getName(), project).contains(name);
    }

    @NotNull
    public List<FileBase> getFiles(@NotNull Project project, @NotNull GlobalSearchScope scope) {
        List<FileBase> result = new ArrayList<>();

        FileBasedIndex fileIndex = FileBasedIndex.getInstance();

        ID<String, FileModuleData> indexId = m_index.getName();
        Collection<String> allKeys = fileIndex.getAllKeys(indexId, project);
        LOG.debug("all keys (" + allKeys.size() + "): " + Joiner.join(", ", allKeys));
        for (String key : allKeys) {
            if (!"Pervasives".equals(key)) {
                Collection<RPsiModule> psiModules = ModuleIndex.getElements(key, project, scope);
                for (RPsiModule psiModule : psiModules) {
                    result.add((FileBase) psiModule.getContainingFile());
                }
            }
        }

        return result;
    }

    @NotNull Collection<IndexedFileModule> getFilesForNamespace(@NotNull String namespace, @NotNull GlobalSearchScope scope) {
        Collection<IndexedFileModule> result = new ArrayList<>();

        FileBasedIndex fileIndex = FileBasedIndex.getInstance();

        if (scope.getProject() != null) {
            for (String key : fileIndex.getAllKeys(m_index.getName(), scope.getProject())) {
                List<FileModuleData> values = fileIndex.getValues(m_index.getName(), key, scope);
                int valuesSize = values.size();
                if (valuesSize > 2) {
                    LOG.warn("getFilesForNamespace, key '" + key + "': found " + valuesSize + " items");
                    LOG.warn("  -> [" + Joiner.join(", ", values) + "]");
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
