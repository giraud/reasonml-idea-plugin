type domProps = {
  key?: string,
  children?: JsxU.element,
  ref?: domRef,
  @as("aria-current")
  ariaCurrent?: [#page | #step | #location | #date | #time | #"true" | #"false"],
  className?: string /* substitute for "class" */,
  @as("data-testid") dataTestId?: string,
  onClick?: JsxEventU.Mouse.t => unit,
}
