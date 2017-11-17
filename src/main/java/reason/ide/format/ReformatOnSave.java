package reason.ide.format;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReformatOnSave extends FileDocumentManagerAdapter {

    /**
     * On save, reformat code using refmt tool.
     * Might need more optimisation in the future, and could be a service like merlin (don't create processes
     * each time ?).
     *
     * @param document Document that is being saved
     */
    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        // WriteCommandAction.writeCommandAction(project).run(() -> {
        //   RefmtManager.getInstance().refmt(project, document);
        // });
    }
}
