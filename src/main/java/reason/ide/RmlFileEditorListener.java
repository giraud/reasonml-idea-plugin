package reason.ide;

import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import reason.ide.hints.RmlQueryTypes;
import org.jetbrains.annotations.NotNull;

/**
 * Listen to editor events and query merlin for types when editor get the focus.
 */
public class RmlFileEditorListener implements FileEditorManagerListener {
    private final Project m_project;

    RmlFileEditorListener(Project project) {
        m_project = project;
    }

    @Override
    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        RmlQueryTypes.queryForSelectedTextEditor(m_project);
    }
}
