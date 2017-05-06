package com.reason.bs;

import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.execution.ui.ConsoleViewContentType.ERROR_OUTPUT;
import static java.lang.Integer.parseInt;

class BsbOutputListener implements ProcessListener {
    private final ConsoleView console;
    private final ActionToolbar toolbar;
    private final BucklescriptErrorsManager errorsManager;
    private String fileProcessed = "";
    private boolean building = false;
    private boolean failed;
    private BsbError bsbError;

    BsbOutputListener(ConsoleView console, ActionToolbar toolbar, Project project) {
        this.console = console;
        this.toolbar = toolbar;
//        this.baseDir = project.getBaseDir().getCanonicalPath();
        this.errorsManager = BucklescriptErrorsManager.getInstance(project);
    }

    @Override
    public void startNotified(ProcessEvent event) {
    }

    @Override
    public void processTerminated(ProcessEvent event) {
        console.print("\nProcess has terminated, fix the problem before restarting it.", ERROR_OUTPUT);
        ((BucklescriptToolWindowFactory.BucklescriptConsole.StartAction) toolbar.getActions().get(2)).setEnable(true);
    }

    @Override
    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
    }

    @Override
    public void onTextAvailable(ProcessEvent event, Key outputType) {
        String text = event.getText().trim();
        if (text.startsWith(">>>> Finish compiling")) {
            reset();
        } else if (text.startsWith("Building")) {
            reset();
        } else if (building && !text.isEmpty()) {
            fileProcessed = text.split(" ")[0].replace(".cmj", ".re").replace("\\", "/");
            building = false;
            this.errorsManager.clearErrors(fileProcessed);
        } else if (!fileProcessed.isEmpty()) {
            if (text.startsWith("FAILED")) {
                failed = true;
            } else if (failed && text.startsWith("File ")) {
                Pattern pattern = Pattern.compile(".*, line (\\d+), characters (\\d+)-(\\d+).*");
                Matcher matcher = pattern.matcher(text);
                if (matcher.matches()) {
                    bsbError = new BsbError();
                    bsbError.line = parseInt(matcher.group(1));
                    bsbError.colStart = parseInt(matcher.group(2));
                    bsbError.colEnd = parseInt(matcher.group(3));
                }
            } else if (this.bsbError != null && text.startsWith("Error")) {
                this.bsbError.message = text;
                this.errorsManager.setError(fileProcessed, bsbError);
            }
        }
    }

    private void reset() {
        fileProcessed = "";
        building = false;
        failed = false;
        bsbError = null;
    }
}
