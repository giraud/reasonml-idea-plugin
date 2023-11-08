package com.reason.ide.annotations;

import com.intellij.execution.process.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.comp.rescript.*;
import com.reason.ide.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

import static com.reason.ide.annotations.ORErrorAnnotator.*;
import static java.util.Collections.*;

public class ResErrorAnnotator {
    protected static final Log LOG = Log.create("annotator.rescript");

    private ResErrorAnnotator() {
    }

    public static @Nullable ORErrorAnnotator.InitialInfo<ResResolvedCompiler> collectInformation(@NotNull ResResolvedCompiler compiler, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        BsConfig config = psiFile.getProject().getService(ORCompilerConfigManager.class).getConfig(compiler.getConfigFile());
        VirtualFile contentRoot = compiler.getContentRoot();
        VirtualFile libRoot = contentRoot != null ? contentRoot.findFileByRelativePath("lib/bs") : null;
        Ninja ninja = contentRoot != null ? compiler.readNinjaBuild() : null;

        if (ninja != null && config != null && libRoot != null) {
            VirtualFile virtualFile = ORFileUtils.getVirtualFile(psiFile);
            if (virtualFile != null) {
                List<String> args = ResPlatform.isDevSource(virtualFile, contentRoot, config) ? ninja.getArgsDev() : ninja.getArgs();
                return new ORErrorAnnotator.InitialInfo<>(compiler, psiFile, libRoot, null, editor, args, config.getJsxVersion(), config.getJsxMode(), config.isUncurried());
            }
        }

        return null;
    }

    public static @Nullable ORErrorAnnotator.AnnotationResult doAnnotate(@NotNull InitialInfo<? extends ORResolvedCompiler<?>> initialInfo) {
        PsiFile sourcePsiFile = initialInfo.sourcePsiFile;
        Project project = sourcePsiFile.getProject();
        VirtualFile sourceFile = ORFileUtils.getVirtualFile(sourcePsiFile);
        if (sourceFile == null) {
            return null;
        }

        String nameWithoutExtension = sourceFile.getNameWithoutExtension();

        // Create and clean temporary compilation directory
        File tempCompilationDirectory = getOrCreateTempDirectory(project);
        cleanTempDirectory(tempCompilationDirectory, nameWithoutExtension);

        // Creates a temporary file on disk with a copy of the current document.
        // It'll be used by bsc for a temporary compilation
        File sourceTempFile = copyToTempFile(tempCompilationDirectory, sourcePsiFile, nameWithoutExtension);
        if (sourceTempFile == null) {
            return null;
        }
        LOG.trace("Wrote contents to temporary file", sourceTempFile);

        File cmtFile = new File(tempCompilationDirectory, nameWithoutExtension + ".cmt");

        List<String> arguments = new ArrayList<>(initialInfo.arguments);

        // React/jsx flags
        String jsxVersion = initialInfo.jsxVersion;
        if (jsxVersion != null) {
            arguments.add("-bs-jsx");
            arguments.add(jsxVersion);
        }
        String jsxMode = initialInfo.jsxMode;
        if (jsxMode != null) {
            arguments.add("-bs-jsx-mode");
            arguments.add(jsxMode);
        }

        // new Rescript feature
        if (initialInfo.uncurried) {
            arguments.add("-uncurried");
        }

        arguments.add("-bin-annot");
        arguments.add("-o");
        arguments.add(cmtFile.getPath());
        arguments.add(sourceTempFile.getPath());

        long compilationStartTime = System.currentTimeMillis();

        // https://github.com/giraud/reasonml-idea-plugin/issues/362
        // lib directory can be invalidated after a clean
        if (!initialInfo.libRoot.isValid()) {
            LOG.debug("lib directory is invalid, skip compilation");
            return null;
        }

        List<OutputInfo> info = compile((ResResolvedCompiler) initialInfo.compiler, arguments, initialInfo.libRoot);

        if (LOG.isTraceEnabled()) {
            LOG.trace("Compilation done in " + (System.currentTimeMillis() - compilationStartTime) + "ms");
        }

        LOG.debug("Found info", info);

        File baseFile = new File(sourceTempFile.getParent(), com.intellij.openapi.util.io.FileUtil.getNameWithoutExtension(sourceTempFile));

        LOG.debug("Clear temporary files from base", baseFile);
        com.intellij.openapi.util.io.FileUtil.delete(sourceTempFile);
        com.intellij.openapi.util.io.FileUtil.delete(new File(baseFile.getPath() + ".cmi"));
        FileUtil.delete(new File(baseFile.getPath() + ".cmj"));
        // cmt is never deleted

        return new AnnotationResult(info, initialInfo.editor, cmtFile);
    }

    static @NotNull List<OutputInfo> compile(@NotNull ResResolvedCompiler compiler, @NotNull List<String> arguments, @NotNull VirtualFile workDir) {
        List<String> command = new ArrayList<>();
        command.add(compiler.getPath());
        command.addAll(arguments.stream().filter(s -> !"-bin-annot".equals(s)).toList());

        if (LOG.isTraceEnabled()) {
            LOG.trace(Joiner.join(" ", command.toArray(new String[0])));
            LOG.trace("  work dir" + (workDir.isValid() ? " [valid]" : " [not valid]"), workDir);
        }

        Process process = null;
        try {
            CompilerProcessListener processListener = new CompilerProcessListener(new RescriptOutputAnalyzer());

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File(workDir.getPath()));
            builder.environment().put("BS_VSCODE", "1");

            process = builder.start();
            try (BufferedReader stdErr = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                String line;
                while ((line = stdErr.readLine()) != null) {
                    processListener.onTextAvailable(line, ProcessOutputTypes.STDERR);
                }
            }
            return processListener.getOutputInfo();
        } catch (Exception e) {
            LOG.info("Execution error", e);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return emptyList();
    }
}
