package com.reason.comp.dune;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.util.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;

public class OpamProcess {

    private static final Log LOG = Log.create("dune");
    private static final Pattern SEXP = Pattern.compile("\\(\"([^\"]+)\" \"([^\"]+)\"\\)");

    @FunctionalInterface
    public interface ProcessTerminated<T> {
        void run(@Nullable T data);
    }

    private final @NotNull Project m_project;

    public OpamProcess(@NotNull Project project) {
        m_project = project;
    }

    public void list(@NotNull Sdk odk, @NotNull ProcessTerminated<List<String[]>> onProcessTerminated) {
        ArrayList<String[]> installedLibs = new ArrayList<>();

        GeneralCommandLine cli =
                new GeneralCommandLine("opam", "list", "--installed", "--safe", "--color=never", "--switch=" + odk.getVersionString());
        cli.setRedirectErrorStream(true);

        OCamlExecutable executable = OCamlExecutable.getExecutable(odk);
        executable.patchCommandLine(cli, null, true, m_project);

        KillableProcessHandler processHandler;
        try {
            processHandler = new KillableProcessHandler(cli);
            processHandler.addProcessListener(new ProcessListener() {
                @Override
                public void startNotified(@NotNull ProcessEvent event) {
                }

                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                    onProcessTerminated.run(installedLibs);
                }

                @Override
                public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                    if (ProcessOutputType.isStdout(outputType)) {
                        String text = event.getText().trim();
                        if (text.charAt(0) != '#') {
                            String[] split = text.split("\\s+", 3);
                            installedLibs.add(new String[]{split[0].trim(), split.length >= 2 ? split[1].trim() : "unknown", split.length >= 3 ? split[2].trim() : ""});
                        }
                    }
                }
            });
            processHandler.startNotify();
        } catch (ExecutionException e) {
            ORNotification.notifyError("Dune", "Can't run opam", e.getMessage(), null);
            installedLibs.add(new String[]{"Error", e.getMessage()});
            onProcessTerminated.run(installedLibs);
        }
    }

    public void env(@NotNull Sdk odk, @NotNull ProcessTerminated<Map<String, String>> onProcessTerminated) {
        Map<String, String> result = new HashMap<>();

        GeneralCommandLine cli = new GeneralCommandLine("opam", "config", "env", "--sexp", "--switch=" + odk.getVersionString());
        cli.setRedirectErrorStream(true);

        OCamlExecutable executable = OCamlExecutable.getExecutable(odk);
        executable.patchCommandLine(cli, null, true, m_project);

        KillableProcessHandler processHandler;
        try {
            processHandler = new KillableProcessHandler(cli);
            processHandler.addProcessListener(new ProcessListener() {
                @Override
                public void startNotified(@NotNull ProcessEvent event) {
                }

                @Override
                public void processTerminated(@NotNull ProcessEvent event) {
                    onProcessTerminated.run(result);
                }

                @Override
                public void onTextAvailable(@NotNull ProcessEvent event, @NotNull Key outputType) {
                    if (ProcessOutputType.isStdout(outputType)) {
                        String text = event.getText().trim();
                        Matcher matcher = SEXP.matcher(text);
                        if (matcher.matches()) {
                            String key = matcher.group(1);
                            String value = matcher.group(2);
                            result.put(key, value);
                        }
                    }
                }
            });
            processHandler.startNotify();
        } catch (ExecutionException e) {
            ORNotification.notifyError("Dune", "Can't run opam", e.getMessage(), null);
            onProcessTerminated.run(result);
        }
    }
}
