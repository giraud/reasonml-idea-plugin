package com.reason.ide.hints;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.bs.Bucklescript;
import com.reason.bs.BucklescriptProjectComponent;
import com.reason.bs.hints.BsQueryTypesServiceComponent;
import io.methvin.watcher.DirectoryChangeEvent;
import io.methvin.watcher.DirectoryChangeListener;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

class CmiDirectoryChangeListener implements DirectoryChangeListener {

    private final Logger m_log;
    private final Project m_project;
    private final Path m_pathToWatch;
    private final Bucklescript m_bucklescript;

    CmiDirectoryChangeListener(@NotNull Project project, @NotNull Path pathToWatch) {
        m_log = Logger.getInstance("ReasonML.types");
        m_project = project;
        m_pathToWatch = pathToWatch;
        m_bucklescript = BucklescriptProjectComponent.getInstance(project);
    }

    @Override
    public void onEvent(DirectoryChangeEvent event) {
        if (event.eventType() == DirectoryChangeEvent.EventType.DELETE) {
            return;
        }

        Path path = event.path();
        if (path.toString().endsWith(".cmi")) {
            Path relativeCmi = m_pathToWatch.relativize(path);
            m_log.info("Detected change on file " + relativeCmi + ", reading types");

            VirtualFile sourceFile = CmiFileManager.toSource(m_project, relativeCmi);
            if (sourceFile == null) {
                m_log.info("can't convert " + relativeCmi + " to " + CmiFileManager.toRelativeSource(path, relativeCmi));
                return;
            }

            BsQueryTypesServiceComponent.InferredTypes inferredTypes = m_bucklescript.queryTypes(path);
            InferredTypesService.annotateFile(m_project, inferredTypes, sourceFile);
        }
    }

    @Override
    public void onException(Exception e) {
        m_log.error(e);
    }
}
