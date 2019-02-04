package com.reason.ide.search;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import gnu.trove.THashSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class FileModuleIndexService {
    private final FileModuleIndex m_index;

    public FileModuleIndexService(@NotNull FileModuleIndex index) {
        m_index = index;
    }

    public static FileModuleIndexService getService() {
        return ServiceManager.getService(FileModuleIndexService.class);
    }

    public Collection<VirtualFile> getFilesWithName(@Nullable String moduleName, @NotNull GlobalSearchScope scope) {
        if (moduleName == null) {
            return Collections.emptyList();
        }

        return FileBasedIndex.getInstance().getContainingFiles(m_index.getName(), moduleName, scope);
    }

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
}
