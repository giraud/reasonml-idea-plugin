package com.reason.ide.search;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.intellij.util.indexing.ID;
import com.reason.Joiner;
import com.reason.Log;
import com.reason.ide.files.FileBase;
import com.reason.ide.search.index.FileModuleIndex;
import com.reason.ide.search.index.ModuleIndex;
import com.reason.ide.search.index.NamespaceIndex;
import com.reason.lang.core.psi.PsiModule;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FileModuleIndexService {

    private static final Log LOG = Log.create("index.fileservice");

    @NotNull
    private final FileModuleIndex m_index;
    @NotNull
    private final NamespaceIndex m_nsIndex;

    public FileModuleIndexService() {
        m_index = FileModuleIndex.getInstance();
        m_nsIndex = NamespaceIndex.getInstance();
    }

    public static FileModuleIndexService getService() {
        return ServiceManager.getService(FileModuleIndexService.class);
    }

    @NotNull
    public Collection<String> getNamespaces(@NotNull Project project) {
        return FileBasedIndex.getInstance().getAllKeys(m_nsIndex.getName(), project);
    }

    @NotNull
    public boolean isNamespace(@Nullable String name, @NotNull Project project) {
        return name != null && FileBasedIndex.getInstance().getAllKeys(m_nsIndex.getName(), project).contains(name);
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
                Collection<PsiModule> psiModules = ModuleIndex.getInstance().get(key, project, scope);
                for (PsiModule psiModule : psiModules) {
                    result.add((FileBase) psiModule.getContainingFile());
                }
            }
        }

        return result;
    }

    @NotNull
    Collection<IndexedFileModule> getFilesForNamespace(@NotNull String namespace, @NotNull GlobalSearchScope scope) {
        Collection<IndexedFileModule> result = new ArrayList<>();

        FileBasedIndex fileIndex = FileBasedIndex.getInstance();

        if (scope.getProject() != null) {
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
