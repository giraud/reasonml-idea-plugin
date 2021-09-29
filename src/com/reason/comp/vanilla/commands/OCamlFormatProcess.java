package com.reason.comp.vanilla.commands;

import com.intellij.execution.configurations.*;
import com.intellij.openapi.util.*;
import com.reason.comp.vanilla.*;
import jpsplugin.com.reason.*;

import java.io.*;
import java.util.*;

/**
 * command: {sdk_home/bin/}ocamlformat - --name some/file/name.ml --enable-outside-detected-project
 * expected: read the non-formatted file on stdin
 * output: the formatted file
 * if error: null
 */
public class OCamlFormatProcess extends VanillaProcess {

    private final String myTextToFormat;
    private final String myFilename;

    public OCamlFormatProcess(String sdkHome, String textToFormat, String name) {
        super(sdkHome);
        myTextToFormat = textToFormat;
        myFilename = name;
    }

    @Override protected ArrayList<String> getArguments() {
        ArrayList<String> args = new ArrayList<>();
        args.add("ocamlformat");
        args.add("-"); // use stdin
        args.add("--name");
        args.add(myFilename);
        // todo: it would be good to make something better
        //  allowing the user to create a config file
        args.add("--enable-outside-detected-project");
        return args;
    }

    @Override public String call() {
        // check file exists
        String ext = SystemInfo.isWindows ? ".exe" : "";
        File f = new File((mySdkHome+"ocamlformat").replace("/", "\\")+ext);
        if (!f.exists()) return null;

        // start and fetch result
        GeneralCommandLine cli = makeProcess();
        return ProcessUtils.parseOutputFromCommandLine(cli, LOG, myTextToFormat);
    }
}
