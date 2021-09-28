package com.reason.comp.vanilla.commands;

import com.reason.comp.vanilla.*;

import java.util.*;

/**
 * command: {sdk_home/bin/}ocaml -version
 * expected: "The OCaml toplevel, version 4.12.0"
 * output: "4.12.0"
 * if error: "unknown version"
 */
public final class OCamlVersionProcess extends VanillaProcess {

    public static final String UNKNOWN_VERSION = "unknown version";

    public OCamlVersionProcess(String sdkHome) {
        super(sdkHome);
    }

    @Override public String call() {
        String output = run();
        if (output == null) {
            return UNKNOWN_VERSION;
        }
        // remove the sentence "The OCaml toplevel, version "
        return output.replace("The OCaml toplevel, version ", "");
    }

    @Override protected ArrayList<String> getArguments() {
        ArrayList<String> args = new ArrayList<>();
        args.add(VanillaProcess.OCAML);
        args.add("-version");
        return args;
    }
}
