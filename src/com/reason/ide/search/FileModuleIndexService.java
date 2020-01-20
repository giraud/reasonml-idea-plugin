package com.reason.ide.search;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import com.reason.Log;
import com.reason.ide.files.FileHelper;
import com.reason.ide.search.index.FileModuleIndex;
import com.reason.ide.search.index.NamespaceIndex;
import gnu.trove.THashSet;

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
    public Collection<String> getAllFileModules(@NotNull Project project) {
        return FileBasedIndex.getInstance().getAllKeys(m_index.getName(), project);
    }

    @NotNull
    private List<FileModuleData> getValues(@Nullable String moduleName, @NotNull GlobalSearchScope scope) {
        if (moduleName == null) {
            return Collections.emptyList();
        }

        return FileBasedIndex.getInstance().getValues(m_index.getName(), moduleName, scope);
    }

    @NotNull
    Collection<VirtualFile> getFilesWithName(@Nullable String moduleName, @NotNull GlobalSearchScope scope) {
        if (moduleName == null) {
            return Collections.emptyList();
        }

        return FileBasedIndex.getInstance().getContainingFiles(m_index.getName(), moduleName, scope);
    }

    @Nullable
    public VirtualFile getFile(@Nullable String moduleName, @NotNull GlobalSearchScope scope) {
        Collection<VirtualFile> files = getFilesWithName(moduleName, scope);
        if (1 < files.size()) {
            for (VirtualFile virtualFile : files) {
                if (FileHelper.isInterface(virtualFile.getFileType())) {
                    return virtualFile;
                }
            }
        }
        return files.isEmpty() ? null : files.iterator().next();
    }

    @NotNull
    public String getFilename(@Nullable String moduleName, @NotNull GlobalSearchScope scope) {
        List<FileModuleData> values = getValues(moduleName, scope);
        if (1 < values.size()) {
            for (FileModuleData value : values) {
                if (value.isInterface()) {
                    return value.getFullname();
                }
            }
        }

        if (values.isEmpty()) {
            return "<EMPTY>";
        }

        return values.iterator().next().getFullname();
    }

    @NotNull
    public Collection<VirtualFile> getInterfaceFilesWithName(@Nullable String moduleName, @NotNull GlobalSearchScope scope) {
        if (moduleName == null) {
            return Collections.emptyList();
        }

        Set<VirtualFile> interfaceFiles = new THashSet<>();

        FileBasedIndex.getInstance().processValues(m_index.getName(), moduleName, null, (file, value) -> {
            if (value.isInterface()) {
                interfaceFiles.add(file);
            }
            return true;
        }, scope);

        return interfaceFiles;
    }

    @NotNull
    public Collection<VirtualFile> getImplementationFilesWithName(@Nullable String moduleName, @NotNull GlobalSearchScope scope) {
        if (moduleName == null) {
            return Collections.emptyList();
        }

        Set<VirtualFile> files = new THashSet<>();

        FileBasedIndex.ValueProcessor<FileModuleData> valueProcessor = (file, value) -> {
            if (!value.isInterface()) {
                files.add(file);
            }
            return true;
        };
        FileBasedIndex.getInstance().processValues(m_index.getName(), moduleName, null, valueProcessor, scope);

        return files;
    }

    @NotNull
    public Collection<IndexedFileModule> getFilesWithoutNamespace(@NotNull Project project, @NotNull GlobalSearchScope scope) {
        Collection<IndexedFileModule> result = new ArrayList<>();

        FileBasedIndex fileIndex = FileBasedIndex.getInstance();

        for (String key : fileIndex.getAllKeys(m_index.getName(), project)) {
            List<FileModuleData> values = fileIndex.getValues(m_index.getName(), key, scope);
            int valuesSize = values.size();
            if (valuesSize > 2) {
                LOG.warn("ERROR, size of " + key + " is " + valuesSize);
            } else {
                boolean fileFound = false;
                for (FileModuleData value : values) {
                    if (valuesSize == 1 || value.isInterface()) {
                        if (value.getNamespace().isEmpty() && !value.isComponent()) {
                            fileFound = true;
                            result.add(value);
                        }
                    }
                }
                // 2 files and none of them are an interface ! We take the first one
                if (valuesSize > 1 && !fileFound) {
                    result.add(values.get(0));
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

    @NotNull
    Collection<VirtualFile> getComponents(@NotNull Project project, @NotNull GlobalSearchScope scope) {
        Set<VirtualFile> files = new THashSet<>();
        FileBasedIndex instance = FileBasedIndex.getInstance();

        Collection<String> keys = instance.getAllKeys(m_index.getName(), project);
        for (String key : keys) {
            instance.processValues(m_index.getName(), key, null, (file, value) -> {
                if (value.isComponent()) {
                    files.add(file);
                }
                return true;
            }, scope, null);
        }

        return files;
    }
}
