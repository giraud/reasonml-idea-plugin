[@bs.module "react-intl"] external reactIntlJsReactClass: ReasonReact.reactClass = "FormattedMessage";

let make = (~id: string, ~defaultMessage: string, ~values: option(Js.t('a)), children) =>
    ReasonReact.wrapJsForReason(
      ~reactClass=reactIntlJsReactClass,
      ~props={"id": id, "defaultMessage": defaultMessage, "values": Js.Nullable.fromOption(values)},
      children,
);
