/* Reason comment /*nest*/ */

/* tuples */
let tup = (4, 5);
let tup = (1: int, 2:int);
fun (a:int, b:int) => a


/* local scope */
{
  let msg = "Hello";
  print_string msg;
  let msg2 = "Goodbye";
  print_string msg2
};

/* records */
let myRec = {x: 0, y: 10};
let myFuncs = {
  myFun: fun x => x + 1,
  your: fun a b => a + b
};

/* lists */
let list = [1, 2, 3];
let list = [hd, ...tl];

/* type defs */
type tuple = (int, int);
type r = {x: int, y: int};
type func = int => int;

/* functions */
let x = fun a => fun b => e;
let x = fun a b => e;
let x a b => e;
