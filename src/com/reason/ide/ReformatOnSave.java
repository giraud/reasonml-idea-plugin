package com.reason.ide;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

class ReformatOnSave extends FileDocumentManagerAdapter {

    private final RefmtProcess refmt;
    private final boolean useDoubleDash;

    ReformatOnSave() {
        this.refmt = new RefmtProcess();
        this.useDoubleDash = this.refmt.useDoubleDash();
    }

    /**
     * On save, reformat code using remft tool.
     * This method is only working on linux for now, refmt doesn't seem to work on my windows 64bits.
     * Might need more optimisation in the future, and could be a service like merlin (don't create processes
     * each time ?).
     *
     * @param document Document that is being saved
     */
    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        if (isReasonFile(document)) {
            String oldText = document.getText();
            String newText = this.refmt.run(this.useDoubleDash, oldText);
            if (!oldText.isEmpty() && !newText.isEmpty()) { // additional protection
                document.setText(newText);
            }
        }
    }

    private boolean isReasonFile(Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        String extension = file == null ? null : file.getExtension();
        return "re".equals(extension) || "rei".equals(extension);
    }
}
