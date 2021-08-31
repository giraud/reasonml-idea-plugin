package com.reason.ide.annotations;

import com.intellij.execution.process.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.vfs.*;
import com.reason.comp.bs.*;
import com.reason.comp.rescript.*;
import com.reason.ide.*;
import com.reason.ide.settings.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.io.*;
import java.util.*;
import java.util.stream.*;

import static com.reason.comp.ORConstants.*;
import static java.util.Collections.*;
import static jpsplugin.com.reason.Platform.*;

public class ResErrorAnnotator extends ORErrorAnnotator {
    @Override
    @Nullable VirtualFile getContentRoot(Project project, VirtualFile sourceFile) {
        VirtualFile bsConfig = ORFileUtils.findAncestor(project, BS_CONFIG_FILENAME, sourceFile);
        return bsConfig == null ? null : bsConfig.getParent();
    }

    @Override
    Ninja readNinja(@NotNull Project project, @NotNull VirtualFile contentRoot) {
        return project.getService(ResCompiler.class).readNinjaBuild(contentRoot);
    }

    @Override
    @NotNull List<OutputInfo> compile(@NotNull Project project, @NotNull VirtualFile sourceFile, @NotNull ArrayList<String> arguments, @NotNull VirtualFile workDir) {
        VirtualFile bsc = ResPlatform.findBscExecutable(project, sourceFile);
        if (bsc == null) {
            bsc = BsPlatform.findBscExecutable(project, sourceFile); // a res file inside a ReasonML project ?
            if (bsc == null) {
                LOG.warn("Unable to find bsc.exe for " + sourceFile);
                return emptyList();
            }
        }

        ORSettings settings = project.getService(ORSettings.class);

        List<String> command = new ArrayList<>();
        command.add(bsc.getPath());
        command.addAll(arguments.stream().filter(s -> !"-bin-annot".equals(s)).collect(Collectors.toList()));

        if (LOG.isTraceEnabled()) {
            LOG.trace(bsc.getPath() + " " + Joiner.join(" ", arguments));
            LOG.trace("  work dir", workDir);
        }

        String[] environment = null;
        //if (!settings.isUseSuperErrors()) {
            environment = new String[]{"BS_VSCODE=1"};
        //}

        try (InputStream errorStream = Runtime.getRuntime().exec(command.toArray(new String[0]), environment, new File(workDir.getPath())).getErrorStream()) {
            //BsLineProcessor bsLineProcessor = new BsLineProcessor(LOG);
            RescriptOutputAnalyzer lineProcessor = new RescriptOutputAnalyzer();
            AnsiEscapeDecoder m_ansiEscapeDecoder = new AnsiEscapeDecoder();
            //exec.waitFor(500, TimeUnit.MILLISECONDS);

            BufferedReader errReader = new BufferedReader(new InputStreamReader(errorStream, UTF8));
            errReader.lines().forEach(text -> {
                StringBuilder sb = new StringBuilder();
                m_ansiEscapeDecoder.escapeText(text, ProcessOutputType.STDERR, (chunk, attributes) -> sb.append(chunk));
                lineProcessor.onTextAvailable(sb.toString());
            });

            return lineProcessor.getOutputInfo();
        } catch (IOException e) {
            LOG.warn(e);
            return emptyList();
        }
    }
}
