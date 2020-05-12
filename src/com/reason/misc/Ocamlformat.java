package com.reason.misc;

import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.reason.Platform;
import com.reason.esy.EsyProcess;
import com.reason.ide.console.CliType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.file.Path;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Ocamlformat {

    public static final String OCAMLFORMAT_EXECUTABLE_NAME = "ocamlformat";

    //    #
    //    # @opam/ocamlformat@opam:0.14.2@53883ae0
    //    #
    //    export CAML_LD_LIBRARY_PATH="/home/john/.esy/3_____________________________________________________________________/i/opam__s__ocamlformat-opam__c__0.14.2-1fb7429b/stublibs:/home/john/.esy/3_____________________________________________________________________/i/opam__s__ocamlformat-opam__c__0.14.2-1fb7429b/lib/stublibs:$CAML_LD_LIBRARY_PATH"
    //    export OCAMLPATH="/home/john/.esy/3_____________________________________________________________________/i/opam__s__ocamlformat-opam__c__0.14.2-1fb7429b/lib:$OCAMLPATH"
    //    export MAN_PATH="/home/john/.esy/3_____________________________________________________________________/i/opam__s__ocamlformat-opam__c__0.14.2-1fb7429b/man:$MAN_PATH"
    //    export PATH="/home/john/.esy/3_____________________________________________________________________/i/opam__s__ocamlformat-opam__c__0.14.2-1fb7429b/bin:$PATH"

    private Ocamlformat() {}

    public static Optional<Path> findExecutable(@NotNull Project project) {
        // first check system PATH for executable
        String systemPath = System.getenv("PATH");
        Optional<Path> executableInPath = Platform.findExecutableInPath(OCAMLFORMAT_EXECUTABLE_NAME, systemPath);
        if (executableInPath.isPresent()) {
            return executableInPath;
        }
        // then check esy environment
        return findEsyOcamlformatExecutable(project);
    }

    private static Optional<Path> findEsyOcamlformatExecutable(@NotNull Project project) {
        EsyProcess esyProcess = ServiceManager.getService(project, EsyProcess.class);
        ProcessHandler printEnvProcess = esyProcess.recreate(CliType.Esy.PRINT_ENV, null);
        if (printEnvProcess == null) {
            return Optional.empty();
        }

        EsyEnvListener esyEnvListener = new EsyEnvListener();
        printEnvProcess.addProcessListener(esyEnvListener);

        if (!printEnvProcess.waitFor()) {
            return Optional.empty();
        }
        Integer exitCode = printEnvProcess.getExitCode();
        if (exitCode == null || exitCode != 1) {
            return Optional.empty();
        }
        Optional<String> ocamlformatPath = esyEnvListener.getOcamlformatPath();
        if (!ocamlformatPath.isPresent()) {
           return Optional.empty();
        }
        File file = new File(ocamlformatPath.get());
        return Optional.of(file.toPath());
    }

    public static class EsyEnvListener implements ProcessListener {

        static final Pattern HEADER_PATTERN = Pattern.compile("^# @opam/ocamlformat@opam:.*");

        static final Pattern PATH_PATTERN = Pattern.compile("^export PATH=\"" + File.pathSeparator + ".*");

        private boolean isHeaderPresent;

        @Nullable
        private String executablePath;

        public EsyEnvListener() {
            this.isHeaderPresent = false;
            this.executablePath = null;
        }

        @Override
        public void startNotified(@NotNull ProcessEvent event) {}

        @Override
        public void processTerminated(@NotNull ProcessEvent event) {}

        @Override
        public void processWillTerminate(@NotNull ProcessEvent event, boolean willBeDestroyed) {}

        @Override
        public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
            String text = event.getText();
            if (!isHeaderPresent && HEADER_PATTERN.matcher(text).matches()) {
                isHeaderPresent = true;
                return;
            }
            if (isHeaderPresent) {
                Matcher matcher = PATH_PATTERN.matcher(text);
                if (matcher.matches()) {
                    this.executablePath = matcher.group();
                }
            }
        }

        public Optional<String> getOcamlformatPath() {
            return Optional.ofNullable(this.executablePath);
        }
    }
}
