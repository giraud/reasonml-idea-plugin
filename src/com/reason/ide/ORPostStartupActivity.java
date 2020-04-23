package com.reason.ide;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.reason.Log;
import org.jetbrains.annotations.NotNull;

public class ORPostStartupActivity implements StartupActivity, DumbAware {

    private static final Log LOG = Log.create("activity.startup");

    @Override
    public void runActivity(@NotNull Project project) {
        ORProjectRootListener.ensureSubscribed(project);
        ORFileDocumentListener.ensureSubscribed(project);
        LOG.debug("Subscribed project and document listeners.");
    }
}
