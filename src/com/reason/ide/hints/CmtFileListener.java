package com.reason.ide.hints;

import com.intellij.lang.Language;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.reason.Compiler;
import com.reason.Log;
import com.reason.Platform;
import com.reason.bs.Bucklescript;
import com.reason.hints.InsightManager;
import com.reason.ide.CompilerManager;
import com.reason.ide.FileManager;
import com.reason.ide.OREditorTracker;
import com.reason.ide.files.CmiFileType;
import com.reason.ide.files.FileHelper;
import com.reason.lang.ocaml.OclLanguage;
import com.reason.lang.reason.RmlLanguage;
import org.jetbrains.annotations.NotNull;

import java.nio.file.FileSystems;
import java.nio.file.Path;

public class CmtFileListener {

    private final static Log LOG = Log.create("hints.vfs");

    @NotNull
    private final Project m_project;

    private CmtFileListener(@NotNull Project project) {
        m_project = project;
    }

    public void onChange(@NotNull VirtualFile file) {
        if (file.getFileType() instanceof CmiFileType) {
            return;
        }

        InsightManager insightManager = ServiceManager.getService(m_project, InsightManager.class);

        Path path = FileSystems.getDefault().getPath(file.getPath());
        Path relativeCmti;

        Compiler compiler = CompilerManager.getInstance().getCompiler(m_project);
        if (compiler instanceof Bucklescript) {
            Path relativeRoot = FileSystems.getDefault().getPath("lib", "bs");
            Path pathToWatch = getPathToWatch(m_project, relativeRoot);
            relativeCmti = pathToWatch.relativize(path);
        } else {
            Path relativeRoot = FileSystems.getDefault().getPath("_build", "default");
            Path pathToWatch = getPathToWatch(m_project, relativeRoot);
            relativeCmti = pathToWatch.relativize(path);
        }

        LOG.info("Detected change on file " + relativeCmti + ", reading types");

        VirtualFile sourceFile = FileManager.toSource(m_project, file, relativeCmti);
        if (sourceFile == null) {
            LOG.warn("can't convert " + relativeCmti + " to " + FileManager.toRelativeSourceName(m_project, file, relativeCmti));
        } else if (OREditorTracker.getInstance(m_project).isOpen(sourceFile)) {
            Language lang = FileHelper.isReason(sourceFile.getFileType()) ? RmlLanguage.INSTANCE : OclLanguage.INSTANCE;
            insightManager.queryTypes(file, path, inferredTypes -> InferredTypesService.annotatePsiFile(m_project, lang, sourceFile, inferredTypes));
        }
    }

    @NotNull
    private Path getPathToWatch(@NotNull Project project, @NotNull Path relativeRoot) {
        VirtualFile baseRoot = Platform.findBaseRoot(project);
        Path basePath = FileSystems.getDefault().getPath(baseRoot.getPath());
        return basePath.resolve(relativeRoot);
    }
}
