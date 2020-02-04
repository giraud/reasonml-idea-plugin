package com.reason.ide;

import java.util.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.json.JsonFileType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import com.intellij.util.containers.ContainerUtil;
import com.reason.hints.InsightManager;
import com.reason.ide.files.CmtFileType;

/**
 * Listener that detects all modifications on project files
 */
class ORVirtualFileListener implements AsyncFileListener {

    @Nullable
    @Override
    public ChangeApplier prepareChange(@NotNull List<? extends VFileEvent> events) {
        List<? extends VFileEvent> relevantEvents = ContainerUtil.filter(events, this::isRelevantEvent);
        return relevantEvents.isEmpty() ? null : new ChangeApplier() {
            @Override
            public void afterVfsChange() {
                for (VFileEvent event : relevantEvents) {
                    VirtualFile file = event.getFile();
                    FileType fileType = file == null ? null : file.getFileType();
                    if (fileType instanceof JsonFileType && "bsconfig.json".equals(file.getName())) {
                        for (Project project : ProjectManager.getInstance().getOpenProjects()) {
                            Module module = ModuleUtil.findModuleForFile(file, project);
                            if (module != null) {
                                ServiceManager.getService(project, InsightManager.class).downloadRincewindIfNeeded(file);
                               OREditorTracker.getInstance(project).updateQueues();
                            }
                        }
                    }
                }
            }
        };
    }

    private boolean isRelevantEvent(@NotNull VFileEvent event) {
        if (event instanceof VFileContentChangeEvent) {
            VirtualFile file = event.getFile();
            FileType fileType = file == null ? null : file.getFileType();
            return fileType instanceof CmtFileType || (fileType instanceof JsonFileType && "bsconfig.json".equals(file.getName()));
        }
        return false;
    }
}
