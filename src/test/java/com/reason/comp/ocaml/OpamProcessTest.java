package com.reason.comp.ocaml;

import com.intellij.execution.process.*;
import com.reason.ide.*;
import jpsplugin.com.reason.*;
import org.junit.*;

import java.util.*;

public class OpamProcessTest extends ORBasePlatformTestCase {
    /*
    Windows:
    #  switch   compiler                                                                        description
    →  4.08.0                                                                                   ocaml-base-compiler = 4.08.0 | ocaml-system = 4.08.0
       default  arch-x86_64.1,ocaml-base-compiler.5.2.0,ocaml-options-vanilla.1,system-mingw.1  ocaml >= 4.05.0

    [WARNING] The environment is not in sync with the current switch.
    */
    @Test
    public void testListSwitches() {
        final List<OpamProcess.OpamSwitch> result = new ArrayList<>();
        ORProcessTerminated<List<OpamProcess.OpamSwitch>> listORProcessTerminated = data -> result.addAll(data);
        OpamProcess.ListProcessListener listProcessListener = new OpamProcess.ListProcessListener(listORProcessTerminated);

        listProcessListener.onTextAvailable(new ProcessEvent(OpamProcess.NULL_HANDLER, "#  switch   compiler                                                                        description"), ProcessOutputTypes.STDOUT);
        listProcessListener.onTextAvailable(new ProcessEvent(OpamProcess.NULL_HANDLER, "→  4.08.0                                                                                   ocaml-base-compiler = 4.08.0 | ocaml-system = 4.08.0"), ProcessOutputTypes.STDOUT);
        listProcessListener.onTextAvailable(new ProcessEvent(OpamProcess.NULL_HANDLER, "   default  arch-x86_64.1,ocaml-base-compiler.5.2.0,ocaml-options-vanilla.1,system-mingw.1  ocaml >= 4.05.0"), ProcessOutputTypes.STDOUT);
        listProcessListener.onTextAvailable(new ProcessEvent(OpamProcess.NULL_HANDLER, ""), ProcessOutputTypes.STDOUT);
        listProcessListener.onTextAvailable(new ProcessEvent(OpamProcess.NULL_HANDLER, "[WARNING] The environment is not in sync with the current switch."), ProcessOutputTypes.STDOUT);
        listProcessListener.processTerminated(new ProcessEvent(OpamProcess.NULL_HANDLER));

        assertSize(2, result);
        assertEquals(result.get(0).name(), "4.08.0");
        assertTrue(result.get(0).isSelected());
        assertEquals(result.get(1).name(), "default");
        assertFalse(result.get(1).isSelected());
    }
}
