[@react.component] let make = () => {
  <A>
      <Error>
        {showAttributeSelector
           ? <ModalOverlay>
                 <QueryAttributeSelectionDialog
                   onSelect={(indicator: GlobalStateTypes.AttributeEntity.t) => {
                     dispatch(. QueriesReducer.AddValue(queryId, indicator.uuid));
                     closeAttributeSelector();
                   }}
                   onCancel={(.) => closeAttributeSelector()}
                 />
             </ModalOverlay>
           : React.null}
      </Error>
      {switch (querySaveStatus) {
       | Loading => <SegmentSpinner isLoading=true />
       | _ => React.null
       }}
  </A>;
};
