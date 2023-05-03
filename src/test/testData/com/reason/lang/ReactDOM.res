module Props = {
  @deriving(abstract)
  type domProps = {
    @optional
    key: string,
    @optional
    ref: domRef,
    @optional @as("aria-details")
    ariaDetails: string,
    @optional
    className: string /* substitute for "class" */,
    @optional
    onClick: ReactEvent.Mouse.t => unit,
  }
}

include Props
