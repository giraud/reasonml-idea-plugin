package com.reason.ide.search.index;

import com.intellij.openapi.application.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.search.*;
import com.reason.ide.search.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

@Service(Service.Level.APP)
public final class FileModuleIndexService {
    private static final Log LOG = Log.create("index.fileservice");

    private static FileModuleIndexService myInstance = null;

    public static @NotNull FileModuleIndexService getInstance() {
        if (myInstance == null) {
            myInstance = ApplicationManager.getApplication().getService(FileModuleIndexService.class);
        }
        return myInstance;
    }

    @NotNull
    public List<FileModuleData> getTopModules(@NotNull Project project, @NotNull GlobalSearchScope scope) {
        List<FileModuleData> result = new ArrayList<>();

        for (String key : getAllKeys(project)) {
            result.addAll(getTopModuleData(key, scope));
        }

        return result;
    }

    public @NotNull List<FileModuleData> getTopModuleData(@NotNull String name, @NotNull GlobalSearchScope scope) {
        return FileModuleIndex.getValues(name, scope);
    }

    @NotNull
    public Collection<String> getNamespaces(@NotNull Project project, @NotNull GlobalSearchScope scope) {
        Set<String> result = new HashSet<>();

        for (String key : getAllKeys(project)) {
            getTopModuleData(key, scope).stream()
                    .filter(FileModuleData::hasNamespace)
                    .findFirst().map(FileModuleData::getNamespace)
                    .ifPresent(result::add);
        }

        LOG.debug("namespaces", result);
        return result;
    }

    public @NotNull Collection<String> getAllKeys(@NotNull Project project) {
        return FileModuleIndex.getAllKeys(project);
    }

    public Collection<VirtualFile> getContainingFiles(@NotNull String name, @NotNull GlobalSearchScope scope) {
        return FileModuleIndex.getContainingFiles(name, scope);
    }
}
