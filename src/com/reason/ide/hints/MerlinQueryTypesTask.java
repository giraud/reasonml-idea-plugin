package com.reason.ide.hints;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.LogicalPosition;
import com.intellij.psi.PsiFile;
import com.reason.Platform;
import com.reason.merlin.MerlinService;
import com.reason.merlin.types.MerlinPosition;
import com.reason.merlin.types.MerlinType;
import com.reason.psi.ReasonMLLetStatement;

import java.util.Collection;
import java.util.List;

class MerlinQueryTypesTask implements Runnable {

    private final Collection<ReasonMLLetStatement> letStatements;
    private final List<LogicalPosition> positions;
    private final PsiFile psiFile;

    MerlinQueryTypesTask(PsiFile psiFile, Collection<ReasonMLLetStatement> letStatements, List<LogicalPosition> positions) {
        this.psiFile = psiFile;
        this.letStatements = letStatements;
        this.positions = positions;
    }

    @Override
    public void run() {
        MerlinService merlin = ApplicationManager.getApplication().getComponent(MerlinService.class);
        if (merlin == null || !merlin.isRunning()) {
            return;
        }

        String filename = psiFile.getVirtualFile().getCanonicalPath();
        // BIG HACK
        if (Platform.isWindows()) {
            filename = Platform.toLinuxSubSystemPath(filename);
        }

        // Update merlin buffer
        merlin.sync(filename, this.psiFile.getText());

        int i = 0;
        for (ReasonMLLetStatement letStatement : this.letStatements) {
            List<MerlinType> types = merlin.findType(filename, new MerlinPosition(this.positions.get(i)));
            if (!types.isEmpty()) {
                //System.out.println(letStatement.getLetBinding().getValueName().getText() + ": " + types.stream().map(merlinType -> merlinType.type).reduce("", (s, s2) -> s + ", " + s2));
                letStatement.setInferredType(types.get(0).type);
            }
            i++;
        }
    }
}
