package com.reason.comp.ocaml;

import com.intellij.openapi.project.*;
import com.reason.comp.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class OpamEnv {
    private final Project myProject;
    private final Map<String, Map<String, String>> myEnvs = new HashMap<>();

    public OpamEnv(@NotNull Project project) {
        myProject = project;
    }

    public @Nullable Map<String, String> getEnv(@Nullable String switchName) {
        return switchName == null ? null : myEnvs.get(switchName);
    }

    public void computeEnv(@Nullable String opamLocation, @Nullable String switchName, @Nullable String cygwinBash,
                           @Nullable ORProcessTerminated<Map<String, String>> onEnvTerminated) {
        if (opamLocation != null && switchName != null) {
            myProject.getService(OpamProcess.class).env(opamLocation, switchName, cygwinBash, data -> {
                myEnvs.put(switchName, data);
                if (onEnvTerminated != null) {
                    onEnvTerminated.run(data);
                }
            });
        }
    }
}
