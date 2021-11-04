package com.reason.ide.annotations;

import com.intellij.execution.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import com.reason.comp.rescript.*;
import jpsplugin.com.reason.*;
import org.apache.tools.ant.taskdefs.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static com.reason.ide.annotations.ORErrorAnnotator.*;
import static java.util.Collections.*;

public class ResErrorAnnotator {
    protected static final Log LOG = Log.create("annotator.rescript");

    private ResErrorAnnotator() {
    }

    public static @Nullable ORErrorAnnotator.InitialInfo<ResResolvedCompiler> collectInformation(@NotNull ResResolvedCompiler compiler, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        VirtualFile contentRoot = compiler.getContentRoot();
        BsConfig config = contentRoot == null ? null : ResPlatform.readConfig(contentRoot);
        VirtualFile libRoot = contentRoot == null ? null : contentRoot.findFileByRelativePath("lib/bs");
        Ninja ninja = contentRoot == null ? null : compiler.readNinjaBuild();

        if (ninja != null && config != null && libRoot != null) {
            List<String> args = ResPlatform.isDevSource(psiFile.getVirtualFile(), contentRoot, config) ? ninja.getArgsDev() : ninja.getArgs();
            return new ORErrorAnnotator.InitialInfo<>(compiler, psiFile, libRoot, null, editor, args, config.getJsxVersion());
        }

        return null;
    }

    public static @Nullable ORErrorAnnotator.AnnotationResult doAnnotate(@NotNull InitialInfo<? extends ORResolvedCompiler<?>> initialInfo) {
        PsiFile sourcePsiFile = initialInfo.sourcePsiFile;
        Project project = sourcePsiFile.getProject();
        VirtualFile sourceFile = sourcePsiFile.getVirtualFile();

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
        String jsxVersion = initialInfo.jsxVersion;
        if (jsxVersion != null) {
            arguments.add("-bs-jsx");
            arguments.add(jsxVersion);
        }
        arguments.add("-bin-annot");
        arguments.add("-o");
        arguments.add(cmtFile.getPath());
        arguments.add(sourceTempFile.getPath());

        long compilationStartTime = System.currentTimeMillis();

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

    @NotNull static List<OutputInfo> compile(@NotNull ResResolvedCompiler compiler, @NotNull List<String> arguments, @NotNull VirtualFile workDir) {
        List<String> command = new ArrayList<>();
        command.add(compiler.getPath());
        command.addAll(arguments.stream().filter(s -> !"-bin-annot".equals(s)).collect(Collectors.toList()));

        if (LOG.isTraceEnabled()) {
            LOG.trace(Joiner.join(" ", command.toArray(new String[0])));
            LOG.trace("  work dir", workDir);
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
        } catch (IOException e) {
            ORNotification.notifyError("Rescript", "Execution exception", e.getMessage(), null);
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        return emptyList();
    }
}
