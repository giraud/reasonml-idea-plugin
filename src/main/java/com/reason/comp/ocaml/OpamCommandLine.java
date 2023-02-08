package com.reason.comp.ocaml;

import com.intellij.execution.configurations.*;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.*;
import com.intellij.openapi.roots.*;
import com.intellij.openapi.vfs.*;
import com.intellij.util.containers.*;
import com.reason.ide.settings.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

public abstract class OpamCommandLine {
    private static final Log LOG = Log.create("ocaml.opam");
    public static final VirtualFile[] EMPTY_VFILES = new VirtualFile[0];

    private final Project myProject;
    private final String myBinary;
    private final boolean myRedirectErrorStream;

    OpamCommandLine(@NotNull Project project, @NotNull String binary, boolean redirectErrorStream) {
        myProject = project;
        myBinary = binary;
        myRedirectErrorStream = redirectErrorStream;
    }

    protected OpamCommandLine(@NotNull Project project, @NotNull String binary) {
        this(project, binary, true);
    }

    protected abstract @NotNull List<String> getParameters();

    public @Nullable GeneralCommandLine create(@NotNull VirtualFile source) {
        ORSettings settings = myProject.getService(ORSettings.class);
        String opamLocation = settings.getOpamLocation();
        if (!opamLocation.isEmpty()) {
            String switchLocation = opamLocation + "/" + settings.getSwitchName();
            String binPath = switchLocation + "/bin";

            Module module = Platform.getModule(myProject, source);
            VirtualFile[] contentRoots = module == null ? EMPTY_VFILES : ModuleRootManager.getInstance(module).getContentRoots();
            if (contentRoots.length > 0) {
                GeneralCommandLine cli = new GeneralCommandLine(ContainerUtil.prepend(getParameters(), myBinary));
                cli.withParentEnvironmentType(GeneralCommandLine.ParentEnvironmentType.CONSOLE);
                cli.setWorkDirectory(contentRoots[0].getPath());
                cli.setRedirectErrorStream(myRedirectErrorStream);

                Map<String, String> env = myProject.getService(OpamEnv.class).getEnv(settings.getSwitchName());
                if (env != null) {
                    for (Map.Entry<String, String> entry : env.entrySet()) {
                        cli.withEnvironment(entry.getKey(), entry.getValue());
                    }
                }

                OCamlExecutable executable = OCamlExecutable.getExecutable(opamLocation, settings.getCygwinBash());
                return executable.patchCommandLine(cli, null, false, myProject);
            } else {
                LOG.debug("Content roots", contentRoots);
                LOG.debug("Binary directory", binPath);
            }
        }

        return null;
    }
}
