package com.reason.comp.dune;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class OpamEnv {
    private final Project m_project;
    private final Map<String, Map<String, String>> m_envs = new HashMap<>();

    @FunctionalInterface
    public interface EnvTerminated {
        void run(@Nullable Map<String, String> env);
    }

    public OpamEnv(@NotNull Project project) {
        m_project = project;
    }

    public @Nullable Map<String, String> getEnv(@NotNull Sdk odk) {
        return m_envs.get(odk.getVersionString());
    }

    public void computeEnv(@NotNull Sdk odk, @Nullable EnvTerminated onEnvTerminated) {
        ServiceManager.getService(m_project, OpamProcess.class).env(odk, data -> {
            m_envs.put(odk.getVersionString(), data);
            if (onEnvTerminated != null) {
                onEnvTerminated.run(data);
            }
        });
    }
}
