let component = ReasonReact.statelessComponent("Alert");

let make = (~_type, ~onClose=?, ~dismissAfter=?, children) => {
  ...component,
  render: _self => <div />,
};