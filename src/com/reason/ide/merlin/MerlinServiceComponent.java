package com.reason.ide.merlin;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class MerlinServiceComponent implements MerlinService {

    private final AtomicBoolean tracking = new AtomicBoolean(false);
    private final Project project;

    public MerlinServiceComponent(Project project) {
        this.project = project;
    }

    @NotNull
    public static MerlinService getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, MerlinService.class);
    }

    public void startIfNeeded() {
        if (this.tracking.compareAndSet(false, true)) {
            System.out.println("Merlin service started");
        }
    }
}
