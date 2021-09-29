package com.reason.comp.vanilla.commands;

import com.intellij.execution.configurations.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.*;
import com.reason.comp.vanilla.*;

import java.util.*;

/**
 * command: {sdk_home/bin/}ocaml
 * return the GeneralCommandLine
 */
public final class OCamlProcess extends VanillaProcess {

    public static GeneralCommandLine makeProcess(Project project) {
        Sdk sdk = ProjectRootManager.getInstance(project).getProjectSdk();
        if (sdk == null) return null;
        OCamlProcess ocaml = new OCamlProcess(sdk.getHomePath());
        return ocaml.makeProcess();
    }

    // OCAML command related stuff

    private OCamlProcess(String sdkHome) {
        super(sdkHome);
    }

    @Override public String call() {
        throw new IllegalCallerException("not supposed to be call");
    }

    @Override protected ArrayList<String> getArguments() {
        ArrayList<String> args = new ArrayList<>();
        args.add(VanillaProcess.OCAML);
        return args;
    }

    @Override protected GeneralCommandLine makeProcess() {
        return super.makeProcess();
    }
}
