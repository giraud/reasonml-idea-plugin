package com.reason.comp.vanilla;

import com.intellij.execution.configurations.*;
import com.reason.comp.vanilla.tools.*;
import jpsplugin.com.reason.*;

import java.util.*;

public abstract class VanillaProcess {
    // COMMANDS
    public static final String OCAML = "ocaml";
    private static final Log LOG = Log.create("compiler.vanilla");
    protected final boolean myRedirectOnErrors;
    // Attributes
    private final String mySdkHome;

    public VanillaProcess(String sdkHome) {
        mySdkHome = sdkHome + "/bin/";
        myRedirectOnErrors = false;
    }

    // behavior

    /**
     * Get the arguments, the executable is included.
     * Ex: ["ocaml", "-version"]
     * means the command
     * /path/to/sdk/bin/ocaml -version
     **/
    protected abstract ArrayList<String> getArguments();

    /**
     * call command, return output
     **/
    public abstract String call();

    // run GeneralCommandLine

    protected String run() {
        GeneralCommandLine cli = makeProcess();

        // start and fetch result
        return ProcessUtils.parseOutputFromCommandLine(cli, LOG);
    }

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
