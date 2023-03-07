package com.reason.ide.console;

import java.util.*;

// Shows different OCaml error messages and how they are rendered.
// Copied from https://github.com/Chris00/tuareg/blob/master/compilation.txt
public class OCamlMessages {
    public static final List<String[]> common = new ArrayList<>();
    public static final List<String[]> since408 = new ArrayList<>();
    public static final List<String[]> since412 = new ArrayList<>();
    public static final List<String[]> ancillary = new ArrayList<>();

    static {
        common.add(new String[]{ // 0
                "File \"file.ml\", line 4, characters 6-7:",
                "Error: This expression has type int",
                "       This is not a function; it cannot be applied."});

        common.add(new String[]{ // 1
                "File \"file.ml\", line 3, characters 6-7:",
                "Warning 26: unused variable y."});

        common.add(new String[]{ // 2
                "File \"file.ml\", line 6, characters 15-38:",
                "Error: Signature mismatch:",
                "       Modules do not match: sig val x : float end is not included in X",
                "       Values do not match: val x : float is not included in val x : int",
                "       File \"file.ml\", line 3, characters 2-13: Expected declaration",
                "       File \"file.ml\", line 7, characters 6-7: Actual declaration"
        });

        // Since OCaml 4.08, the error messages have the following form.

        since408.add(new String[]{ // 0
                "File \"helloworld.ml\", line 2, characters 36-64:",
                "2 | module rec A: sig type t += A end = struct type t += A = B.A end",
                "                                        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^",
                "Error: Cannot safely evaluate the definition of the following cycle",
                "       of recursively-defined modules: A -> B -> A.",
                "       There are no safe modules in this cycle (see manual section 8.2)."
        });

        since408.add(new String[]{ // 1
                "File \"helloworld.ml\", lines 4-7, characters 6-3:",
                "4 | ......struct",
                "5 |   module F(X:sig end) = struct end",
                "6 |   let f () = B.value",
                "7 | end",
                "Error: Cannot safely evaluate the definition of the following cycle",
                "       of recursively-defined modules: A -> B -> A.",
                "       There are no safe modules in this cycle (see manual section 8.2)."
        });

        since408.add(new String[]{ // 2
                "File \"robustmatch.ml\", lines 33-37, characters 6-23:",
                " 9 | ......match t1, t2, x with",
                "10 |       | AB, AB, A -> ()",
                "11 |       | MAB, _, A -> ()",
                "12 |       | _,  AB, B -> ()",
                "13 |       | _, MAB, B -> ()",
                "Warning 8: this pattern-matching is not exhaustive.",
                "Here is an example of a case that is not matched:",
                "(AB, MAB, A)"
        });

        since408.add(new String[]{ // 3
                "File \"helloworld.ml\", line 2, characters 36-64:",
                "Error: Cannot safely evaluate the definition of the following cycle",
                "       of recursively-defined modules: A -> B -> A.",
                "       There are no safe modules in this cycle (see manual section 8.2)."
        });

        since408.add(new String[]{ // 4
                "File \"helloworld.ml\", line 2, characters 36-64:",
                "Warning 3: Cannot safely evaluate the definition of the following cycle",
                "       of recursively-defined modules: A -> B -> A.",
                "       There are no safe modules in this cycle (see manual section 8.2)."
        });

        since408.add(new String[]{ // 5
                "File \"helloworld.ml\", line 2, characters 36-64:",
                "2 | module rec A: sig type t += A end = struct type t += A = B.A end",
                "                                        ^^^^^^^^^^^^^^^^^^^^^^^^",
                "Warning: Cannot safely evaluate the definition of the following cycle",
                "       of recursively-defined modules: A -> B -> A.",
                "       There are no safe modules in this cycle (see manual section 8.2)."
        });

        since408.add(new String[]{ // 6
                "File \"main.ml\", line 3, characters 8-50:",
                "Error: This expression has type float but an expression was expected of type",
                "         int"
        });

        since408.add(new String[]{ // 7
                "File \"main.ml\", line 3, characters 8-50:",
                "Warning 3: This expression has type float but an expression was expected of type",
                "         int"
        });

        since408.add(new String[]{ // 8
                "File \"main.ml\", line 13, characters 34-35:",
                "13 |   let f : M.t -> M.t = fun M.C -> y",
                "                                       ^",
                "Error: This expression has type M/2.t but an expression was expected of type",
                "         M/1.t",
                "       File \"main.ml\", line 10, characters 2-41:",
                "         Definition of module M/1",
                "       File \"main.ml\", line 7, characters 0-32:",
                "         Definition of module M/2"
        });

        since408.add(new String[]{ // 9
                "File \"main.ml\", line 13, characters 34-35:",
                "13 |   let f : M.t -> M.t = fun M.C -> y",
                "                                       ^",
                "Error: This expression has type M/2.t but an expression was expected of type",
                "         M/1.t",
                "       File \"main.ml\", line 10, characters 2-41:",
                "         Definition of module M/1",
                "       File \"main.ml\", line 7, characters 0-32:",
                "         Definition of module M/2"
        });

        // Since OCaml 4.12, warnings come with mnemonics.

        since412.add(new String[]{ // 0
                "File \"moo.ml\", line 6, characters 6-10:",
                "6 |   let fish = 13 in",
                "          ^^^^",
                "Warning 26 [unused-var]: unused variable fish."
        });

        // Example of a warning with ancillary locations

        ancillary.add(new String[]{ // 0
                "File \"urk.ml\", line 1:",
                "Warning 63 [erroneous-printed-signature]: The printed interface differs from the inferred interface.",
                "The inferred interface contained items which could not be printed",
                "properly due to name collisions between identifiers.",
                "File \"urk.ml\", lines 23-25, characters 2-5:",
                "  Definition of module M/1",
                "File \"urk.ml\", lines 17-20, characters 0-3:",
                "  Definition of module M/2",
                "Beware that this warning is purely informational and will not catch",
                "all instances of erroneous printed interface.",
                "module M : sig type t val v : t end",
                "module F : sig module M : sig val v : M.t end val v : M/2.t end"
        });

        ancillary.add(new String[]{ // 1 Alert: treat like warning
                "File \"alrt.ml\", line 25, characters 9-10:",
                "25 |   val x: t [@@ocaml.deprecated]",
                "              ^",
                "Alert deprecated: t"
        });
    }

    private OCamlMessages() {
    }
}
