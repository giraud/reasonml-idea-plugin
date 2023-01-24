---
id: live-templates
title: Some Live Templates
sidebar_label: Live Templates
slug: /get-started/live-templates
---

List of templates that may help development.

from version: 0.103

# OCaml

**Scope**: `*.ml` and `*.mli`.

- `begin`

```ocaml
begin

end
```

- `class`

```ocaml
class name =
	object (self)
		contents
	end
```

- `for`

```ocaml
for i = starval to endval do
	code
done
```

- `fun`

```ocaml
(fun () -> body)
```

- `func`

```ocaml
(function
| patt1 -> expr1
| patt2 -> expr2)
```

- `if`

```ocaml
if (cond) then expr else expr2
```

- `let`

```ocaml
let var(s) = expr
```

- `lin`

```ocaml
let var(s) = expr in expr2
```

- `match`

```ocaml
match value with
| patt -> expr
| _ -> expr2
```

- `method`

```ocaml
method name = 
```

- `msig`

```ocaml
module Name : sig
	
end
```

- `mstruct`

```ocaml
module Name = struct
	
end
```

- `mtype`

```ocaml
module type Name = sig
	
end
```

- `thread`

```ocaml
ignore (Thread.create (fun () -> 
    
  ) ())
```

- `try`

```ocaml
try
	
with
    | _ -> failwith "Unknown"
```

- `type`

```ocaml
type name = expr
```

- `while`

```ocaml
while condition do
	
done
```
