package com.reason.ide;

import java.util.*;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.vfs.newvfs.events.*;
import com.reason.bs.BsConfigJson;
import com.reason.esy.EsyPackageJson;
import com.reason.ide.console.ORToolWindowManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.AsyncFileListener;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.hints.InsightManager;

/**
 * Listener that detects all modifications on project files
 */
class ORVirtualFileListener implements AsyncFileListener {

    @Nullable
    @Override
    public ChangeApplier prepareChange(@NotNull List<? extends VFileEvent> events) {
        return ORChangeApplier.apply(events);
    }

    private static class ORChangeApplier implements ChangeApplier {

        private final List<? extends VFileEvent> m_events;

        public static ChangeApplier apply(List<? extends VFileEvent> events) {
            return new ORChangeApplier(events);
        }

        private ORChangeApplier(List<? extends VFileEvent> events) {
            this.m_events = events;
        }

        @Override
        public void afterVfsChange() {
            m_events.forEach(ORChangeApplier::handleEvent);
        }

        private static <E extends VFileEvent> void handleEvent(E event) {
            if (event instanceof VFileContentChangeEvent) {
                handleFileContentChangeEvent((VFileContentChangeEvent) event);
                return;
            }
            if (event instanceof VFileCreateEvent || event instanceof VFileDeleteEvent) {
                showHideToolWindowsForConfigurationFiles(event);
                return;
            }
            if (event instanceof VFilePropertyChangeEvent && ((VFilePropertyChangeEvent) event).isRename()) {
                showHideToolWindowsForConfigurationFiles(event);
            }
        }

        private static <E extends VFileEvent> void showHideToolWindowsForConfigurationFiles(E event) {
            VirtualFile modifiedFile = event.getFile();
            if (modifiedFile == null) {
                return;
            }
            if (BsConfigJson.isBsConfigJson(modifiedFile) || EsyPackageJson.isEsyPackageJson(modifiedFile)) {
                for (Project project : ProjectManager.getInstance().getOpenProjects()) {
                    ORToolWindowManager toolWindowManager = ORToolWindowManager.getInstance(project);
                    ApplicationManager.getApplication().invokeLater(toolWindowManager::showHideToolWindows);
                }
            }
        }

        private static void handleFileContentChangeEvent(VFileContentChangeEvent event) {
            VirtualFile file = event.getFile();
            if (BsConfigJson.isBsConfigJson(file)) {
                handleBsConfigContentChange(file);
            }
            showHideToolWindowsForConfigurationFiles(event);
        }

        private static void handleBsConfigContentChange(VirtualFile bsConfigFile) {
            for (Project project : ProjectManager.getInstance().getOpenProjects()) {
                Module module = ModuleUtil.findModuleForFile(bsConfigFile, project);
                if (module != null) {
                    ServiceManager.getService(project, InsightManager.class).downloadRincewindIfNeeded(bsConfigFile);
                    OREditorTracker.getInstance(project).updateQueues();
                }
            }
        }
    }
}
