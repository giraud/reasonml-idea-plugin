package com.reason.bs;

import com.intellij.execution.process.ProcessEvent;
import com.intellij.execution.process.ProcessListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.reason.bs.console.ConsoleBus;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class BsbOutputListener implements ProcessListener {
    private final ConsoleBus m_consoleNotifier;
    private final BucklescriptErrorsManager m_errorsManager;
    private String m_fileProcessed = "";
    private boolean m_building = false;
    private boolean m_failed;
    private BsbError m_bsbError;

    public BsbOutputListener(ConsoleBus consoleNotifier, Project project) {
        m_consoleNotifier = consoleNotifier;
        m_errorsManager = BucklescriptErrorsManager.getInstance(project);
    }

    @Override
    public void startNotified(ProcessEvent event) {
    }

    @Override
    public void processWillTerminate(ProcessEvent event, boolean willBeDestroyed) {
    }

    @Override
    public void processTerminated(ProcessEvent event) {
        m_consoleNotifier.processTerminated();
    }

    @Override
    public void onTextAvailable(ProcessEvent event, Key outputType) {
        String text = event.getText().trim();
        if (text.startsWith(">>>> Finish compiling")) {
            reset();
        } else if (text.startsWith("Building")) {
            reset();
        } else if (m_building && !text.isEmpty()) {
            m_fileProcessed = text.split(" ")[0].replace(".cmj", ".re").replace("\\", "/");
            m_building = false;
            m_errorsManager.clearErrors(m_fileProcessed);
        } else if (!m_fileProcessed.isEmpty()) {
            if (text.startsWith("FAILED")) {
                m_failed = true;
            } else if (m_failed && text.startsWith("File ")) {
                Pattern pattern = Pattern.compile(".*, line (\\d+), characters (\\d+)-(\\d+).*");
                Matcher matcher = pattern.matcher(text);
                if (matcher.matches()) {
                    m_bsbError = new BsbError();
                    m_bsbError.line = parseInt(matcher.group(1));
                    m_bsbError.colStart = parseInt(matcher.group(2));
                    m_bsbError.colEnd = parseInt(matcher.group(3));
                }
            } else if (m_bsbError != null && text.startsWith("Error")) {
                m_bsbError.message = text;
                m_errorsManager.setError(m_fileProcessed, m_bsbError);
            }
        }
    }

    private void reset() {
        m_fileProcessed = "";
        m_building = false;
        m_failed = false;
        m_bsbError = null;
    }
}
