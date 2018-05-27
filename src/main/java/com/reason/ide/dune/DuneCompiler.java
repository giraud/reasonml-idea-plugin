package com.reason.ide.dune;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.reason.Compiler;

public class DuneCompiler implements Compiler, ProjectComponent {

    public static Compiler getInstance(Project project) {
        return project.getComponent(DuneCompiler.class);
    }

    @Override
    public void refresh() {
        // Nothing to do
    }

    @Override
    public void run(FileType fileType) {
        // Run dune
        System.out.println("build dune");
    }
}
