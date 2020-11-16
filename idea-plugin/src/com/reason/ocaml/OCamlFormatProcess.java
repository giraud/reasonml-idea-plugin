package com.reason.ocaml;

import com.intellij.execution.ExecutionException;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.process.ScriptRunnerUtil;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.reason.Log;
import com.reason.ide.settings.ORSettings;
import org.jetbrains.annotations.NotNull;

public class OCamlFormatProcess {

    private static final Log LOG = Log.create("ocamlformat.process");

    private final Project m_project;

    public static OCamlFormatProcess getInstance(@NotNull Project project) {
        return ServiceManager.getService(project, OCamlFormatProcess.class);
    }

    public OCamlFormatProcess(Project project) {
        m_project = project;
    }

    public String format(@NotNull PsiFile psiFile) {
        String ocamlformatExecutable = ORSettings.getInstance(m_project).getOcamlformatExecutable();
        if (!ocamlformatExecutable.isEmpty()) {
            VirtualFile virtualFile = psiFile.getVirtualFile();
            GeneralCommandLine cli = new GeneralCommandLine(ocamlformatExecutable, psiFile.getName());
            cli.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);
            cli.setWorkDirectory(virtualFile.getParent().getPath());
            cli.setRedirectErrorStream(true);
            try {
                return ScriptRunnerUtil.getProcessOutput(cli);
            } catch (ExecutionException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Failed to execute ocamlformat.");
                }
            }
        }
        return psiFile.getText();
    }
}