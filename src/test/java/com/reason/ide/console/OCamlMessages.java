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
        common.add(new String[]{//
                "File \"file.ml\", line 4, characters 6-7:\n",
                "Error: This expression has type int\n",
                "This is not a function; it cannot be applied."});
        common.add(new String[]{//
                "File \"file.ml\", line 3, characters 6-7:\n",
                "Warning 26: unused variable y."});
        common.add(new String[]{//
                "File \"file.ml\", line 6, characters 15-38:\n",
                "Error: Signature mismatch:\n",
                "       Modules do not match: sig val x : float end is not included in X\n",
                "       Values do not match: val x : float is not included in val x : int\n",
                "       File \"file.ml\", line 3, characters 2-13: Expected declaration\n",
                "       File \"file.ml\", line 7, characters 6-7: Actual declaration"
        });
        common.add(new String[]{//
                "File \"file.ml\", line 8, characters 6-7:\n",
                "Warning 32: unused value y."});

        // Since OCaml 4.08, the error messages have the following form.

        since408.add(new String[]{ // 0
                "File \"helloworld.ml\", line 2, characters 36-64:\n",
                "2 | module rec A: sig type t += A end = struct type t += A = B.A end\n",
                "                                        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^\n",
                "Error: Cannot safely evaluate the definition of the following cycle\n",
                "       of recursively-defined modules: A -> B -> A.\n",
                "       There are no safe modules in this cycle (see manual section 8.2)."
        });
        since408.add(new String[]{ // 1
                "File \"helloworld.ml\", lines 4-7, characters 6-3:\n",
                "4 | ......struct\n",
                "5 |   module F(X:sig end) = struct end\n",
                "6 |   let f () = B.value\n",
                "7 | end\n",
                "Error: Cannot safely evaluate the definition of the following cycle\n",
                "       of recursively-defined modules: A -> B -> A.\n",
                "       There are no safe modules in this cycle (see manual section 8.2)."
        });
        since408.add(new String[]{ // 2
                "File \"robustmatch.ml\", lines 33-37, characters 6-23:\n",
                " 9 | ......match t1, t2, x with\n",
                "10 |       | AB, AB, A -> ()\n",
                "11 |       | MAB, _, A -> ()\n",
                "12 |       | _,  AB, B -> ()\n",
                "13 |       | _, MAB, B -> ()\n",
                "Warning 8: this pattern-matching is not exhaustive.\n",
                "Here is an example of a case that is not matched:\n",
                "(AB, MAB, A)"
        });
        since408.add(new String[]{ // 3
                "File \"helloworld.ml\", line 2, characters 36-64:\n",
                "Error: Cannot safely evaluate the definition of the following cycle\n",
                "       of recursively-defined modules: A -> B -> A.\n",
                "       There are no safe modules in this cycle (see manual section 8.2)."
        });
        since408.add(new String[]{ // 4
                "File \"helloworld.ml\", line 2, characters 36-64:\n",
                "Warning 3: Cannot safely evaluate the definition of the following cycle\n",
                "       of recursively-defined modules: A -> B -> A.\n",
                "       There are no safe modules in this cycle (see manual section 8.2)."
        });
        since408.add(new String[]{ // 5
                "File \"helloworld.ml\", line 2, characters 36-64:\n",
                "2 | module rec A: sig type t += A end = struct type t += A = B.A end\n",
                "                                        ^^^^^^^^^^^^^^^^^^^^^^^^\n",
                "Warning: Cannot safely evaluate the definition of the following cycle\n",
                "       of recursively-defined modules: A -> B -> A.\n",
                "       There are no safe modules in this cycle (see manual section 8.2)."
        });
        since408.add(new String[]{ // 6
                "File \"main.ml\", line 3, characters 8-50:\n",
                "Error: This expression has type float but an expression was expected of type\n",
                "         int"
        });
        since408.add(new String[]{ // 7
                "File \"main.ml\", line 3, characters 8-50:\n",
                "Warning 3: This expression has type float but an expression was expected of type\n",
                "         int"
        });
        since408.add(new String[]{ // 8
                "File \"main.ml\", line 13, characters 34-35:\n",
                "13 |   let f : M.t -> M.t = fun M.C -> y\n",
                "                                       ^\n",
                "Error: This expression has type M/2.t but an expression was expected of type\n",
                "         M/1.t\n",
                "       File \"main.ml\", line 10, characters 2-41:\n",
                "         Definition of module M/1\n",
                "       File \"main.ml\", line 7, characters 0-32:\n",
                "         Definition of module M/2"
        });
        since408.add(new String[]{ // 9
                "File \"main.ml\", line 13, characters 34-35:\n",
                "13 |   let f : M.t -> M.t = fun M.C -> y\n",
                "                                       ^\n",
                "Error: This expression has type M/2.t but an expression was expected of type\n",
                "         M/1.t\n",
                "       File \"main.ml\", line 10, characters 2-41:\n",
                "         Definition of module M/1\n",
                "       File \"main.ml\", line 7, characters 0-32:\n",
                "         Definition of module M/2"
        });

        // Since OCaml 4.12, warnings come with mnemonics.

        since412.add(new String[]{ // 0
                "File \"moo.ml\", line 6, characters 6-10:\n",
                "6 |   let fish = 13 in\n",
                "          ^^^^\n",
                "Warning 26 [unused-var]: unused variable fish."
        });

        // Example of a warning with ancillary locations

        ancillary.add(new String[]{ // 0
                "File \"urk.ml\", line 1:\n",
                "Warning 63 [erroneous-printed-signature]: The printed interface differs from the inferred interface.\n",
                "The inferred interface contained items which could not be printed\n",
                "properly due to name collisions between identifiers.\n",
                "File \"urk.ml\", lines 23-25, characters 2-5:\n",
                "  Definition of module M/1\n",
                "File \"urk.ml\", lines 17-20, characters 0-3:\n",
                "  Definition of module M/2\n",
                "Beware that this warning is purely informational and will not catch\n",
                "all instances of erroneous printed interface.\n",
                "module M : sig type t val v : t end\n",
                "module F : sig module M : sig val v : M.t end val v : M/2.t end"
        });

        ancillary.add(new String[]{ // 1 Alert: treat like warning
                "File \"alrt.ml\", line 25, characters 9-10:\n",
                "25 |   val x: t [@@ocaml.deprecated]\n",
                "              ^\n",
                "Alert deprecated: t"
        });
    }

    private OCamlMessages() {
    }
}
