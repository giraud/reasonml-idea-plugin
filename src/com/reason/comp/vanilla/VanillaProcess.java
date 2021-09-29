package com.reason.comp.vanilla;

import com.intellij.execution.configurations.*;
import com.reason.comp.vanilla.tools.*;
import jpsplugin.com.reason.*;

import java.util.*;

/**
 * in the folder {sdk_home}/bin,
 * we got a lot of binaries (ex: ocaml, ocamlc, ...).
 * This class is extended by each "process" which
 * is corresponding to the execution of a binary.
 */
public abstract class VanillaProcess {
    protected static final Log LOG = Log.create("compiler.vanilla");

    // COMMANDS (a lot of classes may use the same command, with different arguments)
    // and for different purposes
    protected static final String OCAML = "ocaml";

    // Attributes
    /** value fro {sdk_home} **/
    protected final String mySdkHome;
    /** redirect errors on out **/
    protected final boolean myRedirectOnErrors;

    public VanillaProcess(String sdkHome) {
        mySdkHome = sdkHome + "/bin/";
        myRedirectOnErrors = false;
    }

    // behavior

    /**
     * Get the arguments, the executable is included.
     * Ex: ["ocaml", "-version"]
     * which means the command
     * {sdk_home}/bin/ocaml -version
     **/
    protected abstract ArrayList<String> getArguments();

    /**
     * call the command, returns the output as a String.
     * You may only have to call {@link #run()} inside, and maybe parse the result.
     **/
    public abstract String call();

    // run GeneralCommandLine

    /** create the process with {@link #makeProcess()} and return the result **/
    protected String run() {
        GeneralCommandLine cli = makeProcess();

        // start and fetch result
        return ProcessUtils.parseOutputFromCommandLine(cli, LOG);
    }

    /** create the process, but don't run it **/
    protected GeneralCommandLine makeProcess() {
        // make command
        ArrayList<String> args = getArguments();
        args.set(0, mySdkHome + args.get(0));

        // create command line
        GeneralCommandLine cli;
        if (PortableOpamUtils.isPortableOpam(mySdkHome)) {
            cli = PortableOpamUtils.makeGeneralCommandLine(args);
        } else {
            cli = new GeneralCommandLine(args);
        }
        cli.setWorkDirectory(mySdkHome);
        cli.setRedirectErrorStream(myRedirectOnErrors);
        return cli;
    }
}
