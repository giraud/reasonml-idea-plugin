open TCoreLib;

let ro = ImmJs.Record.fromRecord; // temporary function to extract the type from the ImmJs.Record, making it a read-only (ro) structure

module Styles = {
  open CssJs;

  let title =
    style(. [|
      fontSize(Dimensions.typeScaleRaw.two->rem),
      fontWeight(bold),
      marginLeft(Dimensions.spacingRaw.small->px),
      flexGrow(1.),
    |]);
};

let rhythmObservationLabel = value => ("Over current {value}", {"value": value->getWithDefault("unknown rhythm")});
let durationObservationLabel = value => ("Over last {value}", {"value": value->Duration.toString});
let parameterObservationLabel = ("Over period parameter", {"value": ""});

type selection =
  | Last
  | Current
  | Period;

type period = {
  last: Duration.t,
  current: UUID.t,
  param: option(string),
};

let availableUnits = Duration.FieldType.([|Seconds, Minutes, Hours, Days, Weeks, Months, Years, WeekYears|]);

module type SelectorsIntf = {
  type store;
  // List all rhythms of application
  let getRhythms: (. store) => array(ModelStore.Views.Rhythm.t);
  // List all parameters of a dashboard
  let getParameters: (. store) => array((string, string));
  // Get current selected period, and current values of the time range
  let getPeriod: UUID.t => (. store) => (selection, period);
  // Get screen error
  let getError: (. store) => option(string);
};

module Selectors: SelectorsIntf with type store = DashboardReducers.AppStore.t = {
  type store = DashboardReducers.AppStore.t;
  let equivalence = Obj.magic; // Use it only for store equivalence

  let getRhythms = (. store: store) => ModelReducers.Selectors.getRhythms(. equivalence(store));

  let getParameters =
    (. store: store) =>
      store.dashboard
      ->DashboardStore.mapParameters(param =>
          param.parameterCast == DashboardParametersCast.periodParameter
            ? Some((param.parameterAlias, param.label == "" ? param.externalAlias : param.label)) : None
        );

  let getPeriod = pageletId =>
    (. store: store) => {
      let rhythms = getRhythms(. store);
      let defaultPeriod = {
        current: rhythms->Belt.Array.get(0)->mapWithDefault(UUID.random() /*never happens*/, r => r.uuid),
        last: Duration.make(1, Duration.FieldType.Hours),
        param: None,
      };

      switch (store.dashboard->DashboardStore.getTimeRangeValues(pageletId)) {
      | (Some(current), None, None) => (Current, {...defaultPeriod, current})
      | (None, Some(last), None) => (Last, {...defaultPeriod, last})
      | (None, None, Some(_) as param) => (Period, {...defaultPeriod, param})
      | _ => (Last, defaultPeriod) // should never happen
      };
    };

  let getError =
    (. store: store) =>
      store.dashboard.pageletErrors
      ->Belt.Map.String.get("observationPeriod")
      ->flatMap(errors => errors->FormValidation.getError("parameter"));
};

let findParamById = (params, id) => params->Belt.Array.getBy(((id', _name)) => id' == id);

/**
 Display the observation mode for the pagelet.

 @param dashboardUUID                 ID of the dashboard
 @param pageletUUID                   ID of the pagelet
 @param miniDashboardDisplayedHeight  Height of the mini dashboard if displayed, 0 otherwise
 @param disabledDone                  True if done button is disabled
 @param registerListener              Function to register a validator listener
 @param onUpdate                      Event fired when the observation mode is updated, so that menu can be updated
 @param onValidate                    Event fired when user clicks done button
 @param onCancel                      Event fired when user clicks cancel button
 @param selectors                     Store selectors (for storybooks, default to local selectors)
 */
[@react.component]
let make =
    (
      ~dashboardUUID: UUID.t,
      ~pageletUUID,
      ~miniDashboardDisplayedHeight=0,
      ~disabledDone,
      ~registerListener: Editor.ValidatorListener.t => Editor.ValidatorListener.unsubscribe,
      ~onUpdate=(. _) => (),
      ~onValidate,
      ~onCancel,
      ~selectors: (module SelectorsIntf)=(module Selectors),
    ) => {
  module S = (val selectors);

  let dispatch = Reselect.useRawDispatch();
  let parametersStatus = DashboardActions.useLoadParameters(dispatch, dashboardUUID, "edit");

  let paramError = Reselect.useSelector(S.getError);
  let rhythms = Reselect.useSelector(S.getRhythms);
  let parameters = Reselect.useSelector(S.getParameters);
  let (initialSelection, initialPeriod) = Reselect.useSelector(S.getPeriod(pageletUUID));

  let (selection, setSelection, _) = Hooks.useEditorValue(initialSelection);
  let (rhythm, setRhythm, _) = Hooks.useEditorValue(initialPeriod.current);
  let (last, setLast, _) = Hooks.useEditorValue(initialPeriod.last);
  let (param, setParam, _) = Hooks.useEditorValue(initialPeriod.param);

  let isRhythmSelected = selection->EditorValue.getValue == Current;
  let isLastSelected = selection->EditorValue.getValue == Last;
  let isParamSelected = selection->EditorValue.getValue == Period;

  let isParameterKnown =
    switch (param->EditorValue.getValue) {
    | Some(id) =>
      switch (parametersStatus, parameters->findParamById(id)) {
      | (Complete, None) => Some(false)
      | _ => Some(true)
      }
    | None => None
    };

  let inError = isParamSelected && !isParameterKnown->getWithDefault(false);

  React.useEffect3(
    () => {
      let error =
        switch (isParameterKnown) {
        | None => Belt.Result.Error([|"Select a value"|])
        | Some(false) => Belt.Result.Error([|"Parameter is unknown"|])
        | Some(true) => Belt.Result.Ok()
        };

      dispatch(.
        PageletEditorReducers.UpdateError(
          "observationPeriod",
          "parameter",
          isParamSelected ? error : Belt.Result.Ok(),
        ),
      );
      None;
    },
    (dispatch, isParamSelected, isParameterKnown),
  );

  // Register store update function when something is modified
  let updateStore =
    React.useCallback7(
      () => {
        open PageletEditorReducers;
        switch (selection->EditorValue.getValue) {
        | Current => dispatch(. UpdateObservationPeriodToCurrent(pageletUUID, rhythm->EditorValue.getValue))
        | Last => dispatch(. UpdateObservationPeriodToLast(pageletUUID, last->EditorValue.getValue))
        | Period => dispatch(. UpdateObservationPeriodToParam(pageletUUID, param->EditorValue.getValue, inError))
        };
        [] /* actions are created in reducer */;
      },
      (dispatch, pageletUUID, selection, rhythm, last, param, inError),
    );

  let isUpdated =
    selection->EditorValue.isUpdated
    || rhythm->EditorValue.isUpdated
    || last->EditorValue.isUpdated
    || param->EditorValue.isUpdated;

  React.useEffect3(
    () => isUpdated ? Some(registerListener(updateStore)) : None,
    (registerListener, isUpdated, updateStore),
  );

  <PageContentGrid height={Page.computePageHeight(miniDashboardDisplayedHeight)} title="Observation mode">
    <PageContentHeader>
      <Icon
        path=IconShapes.chrono
        height=Dimensions.IconSize.large
        width=Dimensions.IconSize.large
        colors=[|"white", Css.Types.Color.toString(Colors.Success.light)|]
      />
      <span className=Styles.title> "Observation mode"->React.string </span>
      <HBox space={Dimensions.spacingRaw.small->Css.px}>
        <Button text="Done" onClick={_ => onValidate()} disabled=disabledDone />
        <Button text="Cancel" secondary=true onClick={_ => onCancel()} />
      </HBox>
    </PageContentHeader>
    <PageContentBody>
      <Form>
        <Radio
          group="obs"
          id="last"
          label="Over last"
          onChange={(. _) => {
            setSelection(. Last);
            onUpdate(. durationObservationLabel(last->EditorValue.getValue));
          }}
          checked=isLastSelected
        />
        <EditorValue.FormInput input=last>
          {(. status, value) =>
             <DurationInput
               key="last-period"
               label="Over last duration"
               availableUnits
               value
               preventZeroValue=true
               maxScalar=9999.
               disabled={!isLastSelected}
               status={isLastSelected ? status : InputStatus.Normal}
               onChange={newValue => {
                 setLast(. newValue);
                 onUpdate(. durationObservationLabel(newValue));
               }}
             />}
        </EditorValue.FormInput>
        <Radio
          group="obs"
          id="current"
          label="Over current"
          onChange={(. _) => {
            setSelection(. Current);
            onUpdate(.
              rhythmObservationLabel(
                rhythms->ModelStore.Views.Rhythm.findByUUID(rhythm->EditorValue.getValue)->map(r => r.label),
              ),
            );
          }}
          checked=isRhythmSelected
        />
        <EditorValue.FormInput input=rhythm>
          {(. status, value) =>
             <Select
               key="current-period"
               options=rhythms
               value={rhythms->ModelStore.Views.Rhythm.findByUUID(value)}
               disabled={!isRhythmSelected}
               status={isRhythmSelected ? status : InputStatus.Normal}
               onItemSelection={(. newValue, _) =>
                 newValue->forEach(v => {
                   setRhythm(. v.uuid);
                   onUpdate(. rhythmObservationLabel(Some(v.label)));
                 })
               }
               itemRenderer={(. item, _, _) => item.label->React.string}
             />}
        </EditorValue.FormInput>
        <Radio
          group="obs"
          id="over"
          label="Over period parameter"
          checked=isParamSelected
          onChange={(. _) => {
            setSelection(. Period);
            onUpdate(. parameterObservationLabel);
          }}
        />
        <EditorValue.FormInput input=param error=?{isParamSelected ? paramError : None}>
          {(. status, value) =>
             <Select
               key="over-period"
               options=parameters
               value={value->flatMap(id => parameters->findParamById(id))}
               disabled={!isParamSelected}
               status={isParamSelected ? status : InputStatus.Normal}
               onItemSelection={(. newValue, _) =>
                 switch (newValue) {
                 | Some((id, _)) => setParam(. Some(id))
                 | _ => ()
                 }
               }
               itemRenderer={(. (_id, label), _, _) => label->React.string}
             />}
        </EditorValue.FormInput>
      </Form>
    </PageContentBody>
  </PageContentGrid>;
};
