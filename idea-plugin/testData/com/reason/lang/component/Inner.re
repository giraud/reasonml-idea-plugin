
module Counter1 = {
  open GlobalState;

  let component = ReasonReact.statelessComponent("Counter1");
  let make = (~state, ~update, _children) => {
    ...component,
    render: _ =>
      <div>
        <button onClick=(_ => update(state => {...state, count1: state.count1 + 1}))>
          (ReasonReact.string("+"))
        </button>
        <button onClick=(_ => update(state => {...state, count1: state.count1 - 1}))>
          (ReasonReact.string("-"))
        </button>
        (ReasonReact.string(" counter:" ++ string_of_int(state.count1)))
      </div>,
  };
};