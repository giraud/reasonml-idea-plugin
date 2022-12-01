package com.reason.ide.annotations;

import com.intellij.execution.process.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.reason.comp.*;
import com.reason.comp.bs.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;

import static com.reason.comp.rescript.ResPlatform.*;
import static com.reason.ide.annotations.ORErrorAnnotator.*;
import static java.util.Collections.*;

public class BsErrorAnnotator {
    protected static final Log LOG = Log.create("annotator.bucklescript");

    private BsErrorAnnotator() {
    }

    public static @Nullable ORErrorAnnotator.InitialInfo<BsResolvedCompiler> collectInformation(@NotNull BsResolvedCompiler compiler, @NotNull Editor editor, @NotNull PsiFile psiFile) {
        VirtualFile contentRoot = compiler.getContentRoot();
        BsConfig config = contentRoot == null ? null : BsPlatform.readConfig(contentRoot);
        if (config == null) {
            LOG.info("No bsconfig.json found for content root: " + contentRoot);
            return null;
        }

        VirtualFile libRoot = contentRoot.findFileByRelativePath("lib/bs");
        if (libRoot == null) {
            LOG.info("Unable to find BuckleScript lib root.");
            return null;
        }

        Project project = psiFile.getProject();
        Ninja ninja = compiler.readNinja();
        VirtualFile sourceFile = psiFile.getVirtualFile();

        if (ninja != null && ninja.isRescriptFormat()) {
            List<String> args = isDevSource(sourceFile, contentRoot, config) ? ninja.getArgsDev() : ninja.getArgs();
            return new ORErrorAnnotator.InitialInfo<>(compiler, psiFile, libRoot, null, editor, args, config.getJsxVersion());
        }

        // create temporary compilation directory
        File tempCompilationDirectory = ORErrorAnnotator.getOrCreateTempDirectory(project);
        cleanTempDirectory(tempCompilationDirectory, sourceFile.getNameWithoutExtension());

        String jsxVersion = config.getJsxVersion();
        String namespace = config.getNamespace();

        // If a directory is marked as dev-only, it won't be built and exposed to other "dev"
        // directories in the same project
        // https://bucklescript.github.io/docs/en/build-configuration#sources
        for (String devSource : config.getDevSources()) {
            VirtualFile devFile = contentRoot.findFileByRelativePath(devSource);
            if (ninja != null && devFile != null && FileUtil.isAncestor(devFile.getPath(), sourceFile.getPath(), true)) {
                ninja.addInclude(devSource);
            }
        }

        // Creates a temporary file on disk with a copy of the current document.
        // It'll be used by bsc for a temporary compilation

        File sourceTempFile = ORErrorAnnotator.copyToTempFile(tempCompilationDirectory, psiFile, sourceFile.getNameWithoutExtension());
        if (sourceTempFile == null || ninja == null) {
            return null;
        }

        LOG.trace("Wrote contents to temporary file", sourceTempFile);

        String tempNameWithoutExtension = FileUtil.getNameWithoutExtension(sourceTempFile);
        File cmtFile = new File(sourceTempFile.getParent(), tempNameWithoutExtension + ".cmt");

        List<String> arguments = new ArrayList<>();
        arguments.add("-bs-super-errors");
        arguments.add("-color");
        arguments.add("never");
        arguments.addAll(ninja.getPkgFlags());
        arguments.addAll(ninja.getBscFlags());
        for (String ppxPath : ninja.getPpxIncludes()) {
            arguments.add("-ppx");
            arguments.add(ppxPath);
        }
        if (!namespace.isEmpty()) {
            arguments.add("-bs-ns");
            arguments.add(namespace);
        }
        if (jsxVersion != null) {
            arguments.add("-bs-jsx");
            arguments.add(jsxVersion);
        }
        for (String bscInclude : ninja.getIncludes()) {
            arguments.add("-I");
            arguments.add(bscInclude);
        }
        arguments.add("-bin-annot");
        arguments.add("-o");
        arguments.add(cmtFile.getPath());
        arguments.add(sourceTempFile.getPath());

        return new ORErrorAnnotator.InitialInfo<>(compiler, psiFile, libRoot, sourceTempFile, editor, arguments, jsxVersion);
    }

    public static @Nullable AnnotationResult doAnnotate(@NotNull InitialInfo<? extends ORResolvedCompiler<?>> initialInfo) {
        File cmtFile;
        List<OutputInfo> info;

        // https://github.com/giraud/reasonml-idea-plugin/issues/362
        // lib directory can be invalidated after a clean
        if (!initialInfo.libRoot.isValid()) {
            LOG.debug("lib directory is invalid, skip compilation");
            return null;
        }

        if (initialInfo.oldFormat) {
            assert initialInfo.tempFile != null;
            File baseFile = new File(initialInfo.tempFile.getParent(), FileUtil.getNameWithoutExtension(initialInfo.tempFile));
            cmtFile = new File(baseFile + ".cmt");

            long compilationStartTime = System.currentTimeMillis();

            info = compile((BsResolvedCompiler) initialInfo.compiler, initialInfo.arguments, initialInfo.libRoot);
            if (LOG.isTraceEnabled()) {
                LOG.trace("Compilation done in " + (System.currentTimeMillis() - compilationStartTime) + "ms");
            }

            LOG.debug("Clear temporary files from base", baseFile);
            FileUtil.delete(initialInfo.tempFile);
            FileUtil.delete(new File(baseFile.getPath() + ".cmi"));
            FileUtil.delete(new File(baseFile.getPath() + ".cmj"));
            // cmt is never deleted
        } else {
            Project project = initialInfo.sourcePsiFile.getProject();
            VirtualFile sourceFile = initialInfo.sourcePsiFile.getVirtualFile();

            // new info format
            String nameWithoutExtension = sourceFile.getNameWithoutExtension();

            // Create and clean temporary compilation directory
            File tempCompilationDirectory = getOrCreateTempDirectory(project);
            cleanTempDirectory(tempCompilationDirectory, nameWithoutExtension);

            // Creates a temporary file on disk with a copy of the current document.
            // It'll be used by bsc for a temporary compilation
            File sourceTempFile = copyToTempFile(tempCompilationDirectory, initialInfo.sourcePsiFile, nameWithoutExtension);
            if (sourceTempFile == null) {
                return null;
            }
            LOG.trace("Wrote contents to temporary file", sourceTempFile);

            VirtualFile contentRoot = initialInfo.libRoot.getParent().getParent();
            VirtualFile bsConfigFile = contentRoot.findFileByRelativePath(ORConstants.BS_CONFIG_FILENAME);
            BsConfig config = bsConfigFile == null ? null : BsConfigReader.read(bsConfigFile);
            if (config == null) {
                LOG.info("No bsconfig.json found for content root: " + contentRoot);
                return null;
            }

            cmtFile = new File(tempCompilationDirectory, nameWithoutExtension + ".cmt");

            ArrayList<String> arguments = new ArrayList<>(initialInfo.arguments);
            String jsxVersion = initialInfo.jsxVersion;
            if (jsxVersion != null) {
                arguments.add("-bs-jsx");
                arguments.add(jsxVersion);
            }
            arguments.add("-bin-annot");
            arguments.add("-o");
            arguments.add(cmtFile.getPath());
            arguments.add(sourceTempFile.getPath());

            info = compile((BsResolvedCompiler) initialInfo.compiler, arguments, initialInfo.libRoot);

            File baseFile = new File(sourceTempFile.getParent(), FileUtil.getNameWithoutExtension(sourceTempFile));

            LOG.debug("Clear temporary files from base", baseFile);
            FileUtil.delete(sourceTempFile);
            FileUtil.delete(new File(baseFile.getPath() + ".cmi"));
            FileUtil.delete(new File(baseFile.getPath() + ".cmj"));
            // cmt is never deleted
        }

        LOG.debug("Found info", info);

        return new ORErrorAnnotator.AnnotationResult(info, initialInfo.editor, cmtFile);
    }

    public static @NotNull List<OutputInfo> compile(@NotNull BsResolvedCompiler compiler, @NotNull List<String> arguments, @NotNull VirtualFile workDir) {
        List<String> command = new ArrayList<>();
        command.add(compiler.getPath());
        command.addAll(arguments);

        if (LOG.isTraceEnabled()) {
            LOG.trace(Joiner.join(" ", command.toArray(new String[0])));
            LOG.trace("  work dir", workDir);
        }

        Process process = null;
        try {
            ORErrorAnnotator.CompilerProcessListener processListener = new ORErrorAnnotator.CompilerProcessListener(new BsLineProcessor(LOG));

            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(new File(workDir.getPath()));

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
                process.destroyForcibly();
            }
        }

        return emptyList();
    }
}
