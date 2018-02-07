/* This is a comment */

module <csModuleName>ModuleName</csModuleName> = {
  type t = { key: int };
  type tree 'a =
  | Node (tree 'a) (tree 'a)
  | Leaf;

  [<csAnnotation>@bs.deriving</csAnnotation> {accessors: accessors}]
  type t = [`Up | `Down | `Left | `Right];

  let add = (x y) => x + y;
  let myList = [ 1.0, 2.0, 3. ];
  let array = [| 1, 2, 3 |];
  let choice x = switch (myOption)
      | None => "nok"
      | Some(value) => "ok";
  let constant = "My constant";
  let numericConstant = 123;
};

React.createElement <csMarkupTag><div</csMarkupTag> <csMarkupAttribute>prop</csMarkupAttribute>=value<csMarkupTag>/></csMarkupTag> <csMarkupTag><Button></csMarkupTag> (ReactElement.toString "ok") <csMarkupTag></Button></csMarkupTag>;
