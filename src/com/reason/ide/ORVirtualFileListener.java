package com.reason.ide;

import com.intellij.openapi.application.*;
import com.intellij.openapi.components.*;
import com.intellij.openapi.module.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.intellij.openapi.vfs.newvfs.events.*;
import com.reason.*;
import com.reason.comp.bs.*;
import com.reason.comp.esy.*;
import com.reason.hints.*;
import com.reason.ide.console.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.comp.ORConstants.*;

/**
 * Listener that detects all modifications on files.
 * This class receives events from all projects.
 */
class ORVirtualFileListener implements AsyncFileListener {
    private static final Log LOG = Log.create("VFSlistener");

    @Override
    public @Nullable ChangeApplier prepareChange(@NotNull List<? extends VFileEvent> events) {
        return ORChangeApplier.apply(events);
    }

    private static class ORChangeApplier implements ChangeApplier {
        private final @NotNull List<? extends VFileEvent> m_events;

        private ORChangeApplier(@NotNull List<? extends VFileEvent> events) {
            m_events = events;
        }

        public static @NotNull ChangeApplier apply(@NotNull List<? extends VFileEvent> events) {
            return new ORChangeApplier(events);
        }

        @Override
        public void afterVfsChange() {
            m_events.forEach(ORChangeApplier::handleEvent);
        }

        private static <E extends VFileEvent> void handleEvent(@NotNull E event) {
            if (event instanceof VFileContentChangeEvent) {
                handleFileContentChangeEvent((VFileContentChangeEvent) event);
            } else if (event instanceof VFileCreateEvent || event instanceof VFileDeleteEvent) {
                showHideToolWindowsForConfigurationFiles(event);
            } else if (event instanceof VFilePropertyChangeEvent && ((VFilePropertyChangeEvent) event).isRename()) {
                showHideToolWindowsForConfigurationFiles(event);
            }
        }

        private static <E extends VFileEvent> void showHideToolWindowsForConfigurationFiles(@NotNull E event) {
            VirtualFile modifiedFile = event.getFile();
            if (modifiedFile == null) {
                return;
            }

            String fileName = modifiedFile.getName();
            boolean potentialToolWindowUpdate = NODE_MODULES.equals(fileName) || RESCRIPT_DIR.equals(fileName) || BS_DIR.equals(fileName)
                    || BsConfigJson.isBsConfigJson(modifiedFile) || EsyPackageJson.isEsyPackageJson(modifiedFile);

            if (potentialToolWindowUpdate) {
                LOG.info("Update tool windows visibility");
                for (Project project : ProjectManager.getInstance().getOpenProjects()) {
                    ORToolWindowManager toolWindowManager = project.getService(ORToolWindowManager.class);
                    ApplicationManager.getApplication().invokeLater(toolWindowManager::showHideToolWindows);
                }
            }
        }

        private static void handleFileContentChangeEvent(@NotNull VFileContentChangeEvent event) {
            VirtualFile file = event.getFile();

            if (BsConfigJson.isBsConfigJson(file)) {
                handleBsConfigContentChange(file);
            } else if (FileHelper.isNinja(file)) {
                LOG.debug("Refresh ninja build", file);
                for (Project project : ProjectManager.getInstance().getOpenProjects()) {
                    ServiceManager.getService(project, BsCompiler.class).refreshNinjaBuild();
                }
            }

            showHideToolWindowsForConfigurationFiles(event);
        }

        private static void handleBsConfigContentChange(@NotNull VirtualFile bsConfigFile) {
            for (Project project : ProjectManager.getInstance().getOpenProjects()) {
                Module module = ModuleUtil.findModuleForFile(bsConfigFile, project);
                if (module != null) {
                    ServiceManager.getService(project, InsightManager.class).downloadRincewindIfNeeded(bsConfigFile);
                }
            }
        }
    }
}
