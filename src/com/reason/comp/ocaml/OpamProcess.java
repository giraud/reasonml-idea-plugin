package com.reason.comp.ocaml;

import com.intellij.execution.*;
import com.intellij.execution.configurations.*;
import com.intellij.execution.process.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.reason.comp.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.regex.*;

public class OpamProcess {
    //private static final Log LOG = Log.create("opam");
    private static final Pattern SEXP = Pattern.compile("\\(\"([^\"]+)\" \"([^\"]+)\"\\)");

    private final @NotNull Project myProject;

    public OpamProcess(@NotNull Project project) {
        myProject = project;
    }

    public void list(@NotNull String opamRoot, @NotNull String version, @Nullable String cygwinBash, @NotNull ORProcessTerminated<List<String[]>> onProcessTerminated) {
        ArrayList<String[]> installedLibs = new ArrayList<>();

        GeneralCommandLine cli = new GeneralCommandLine("opam", "list", "--installed", "--safe", "--color=never", "--switch=" + version);
        cli.setRedirectErrorStream(true);

        OCamlExecutable executable = OCamlExecutable.getExecutable(opamRoot, cygwinBash);
        executable.patchCommandLine(cli, null, true, myProject);

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

    public void env(@Nullable String opamLocation, @Nullable String version, @Nullable String cygwinBash, @NotNull ORProcessTerminated<Map<String, String>> onProcessTerminated) {
        Map<String, String> result = new HashMap<>();

        GeneralCommandLine cli = new GeneralCommandLine("opam", "config", "env", "--sexp", "--switch=" + version);
        cli.setRedirectErrorStream(true);

        OCamlExecutable executable = OCamlExecutable.getExecutable(opamLocation, cygwinBash);
        executable.patchCommandLine(cli, null, true, myProject);

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

    public static class OpamSwitch {
        public final boolean isSelected;
        public final String name;

        public OpamSwitch(boolean isSelected, String name) {
            this.isSelected = isSelected;
            this.name = name;
        }

        @Override public String toString() {
            return (isSelected ? ">" : "") + name;
        }
    }

    public void listSwitch(@NotNull String opamRootPath, @Nullable String cygwinBash, @NotNull ORProcessTerminated<List<OpamSwitch>> onProcessTerminated) {
        List<OpamSwitch> result = new ArrayList<>();

        OCamlExecutable executable = OCamlExecutable.getExecutable(opamRootPath, cygwinBash);
        GeneralCommandLine cli = new GeneralCommandLine("opam", "switch", "list", "--color=never");
        executable.patchCommandLine(cli, null, true, myProject);

        KillableProcessHandler processHandler;
        try {
            processHandler = new KillableProcessHandler(cli);
            processHandler.addProcessListener(new ProcessListener() {
                boolean isHeader = true;
                boolean isFooter = false;

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
                        String text = event.getText();
                        if (isHeader) {
                            isHeader = false;
                        } else if (text.startsWith("[WARNING]")) {
                            isFooter = true;
                        } else if (!isFooter) {
                            String[] tokens = text.split("\\s+");
                            if (tokens.length == 4) {
                                result.add(new OpamSwitch(!tokens[0].isEmpty(), tokens[1]));
                            }
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
