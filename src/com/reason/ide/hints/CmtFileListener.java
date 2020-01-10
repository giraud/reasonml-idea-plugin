package com.reason.ide.hints;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

        Path path = FileSystems.getDefault().getPath(file.getPath());
        @Nullable Path relativeCmt;

        Compiler compiler = CompilerManager.getInstance().getCompiler(m_project);
        if (compiler instanceof Bucklescript) {
            Path relativeRoot = FileSystems.getDefault().getPath("lib", "bs");
            VirtualFile baseRoot = Platform.findORPackageJsonContentRoot(m_project);
            Path pathToWatch = getPathToWatch(baseRoot, relativeRoot);
            relativeCmt = pathToWatch == null ? null : pathToWatch.relativize(path);
        } else {
            Path relativeRoot = FileSystems.getDefault().getPath("_build", "default");
            VirtualFile baseRoot = Platform.findORDuneContentRoot(m_project);
            Path pathToWatch = getPathToWatch(baseRoot, relativeRoot);
            relativeCmt = pathToWatch == null ? null : pathToWatch.relativize(path);
        }

        if (relativeCmt != null) {
            LOG.info("Detected change on file " + relativeCmt + ", reading types");
            VirtualFile sourceFile = FileManager.toSource(m_project, file, relativeCmt);
            if (sourceFile == null) {
                LOG.warn("can't convert " + relativeCmt + " to " + FileManager.toRelativeSourceName(m_project, file, relativeCmt));
            } else if (OREditorTracker.getInstance(m_project).isOpen(sourceFile)) {
                InsightManager insightManager = ServiceManager.getService(m_project, InsightManager.class);

                Language lang = FileHelper.isReason(sourceFile.getFileType()) ? RmlLanguage.INSTANCE : OclLanguage.INSTANCE;
                insightManager.queryTypes(file, path, inferredTypes -> InferredTypesService.annotatePsiFile(m_project, lang, sourceFile, inferredTypes));
            }
        }
    }

    @Nullable
    private Path getPathToWatch(@Nullable VirtualFile baseRoot, @NotNull Path relativeRoot) {
        return baseRoot == null ? null : FileSystems.getDefault().getPath(baseRoot.getPath()).resolve(relativeRoot);
    }
}
