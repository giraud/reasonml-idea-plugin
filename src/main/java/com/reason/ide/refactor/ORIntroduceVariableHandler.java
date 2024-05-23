package com.reason.ide.refactor;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.psi.*;
import com.intellij.psi.util.*;
import com.intellij.refactoring.*;
import com.intellij.refactoring.introduce.inplace.*;
import com.intellij.refactoring.util.*;
import com.reason.ide.files.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.reason.ide.refactor.SearchByOffset.findExpressionAtCaret;

public class ORIntroduceVariableHandler implements RefactoringActionHandler {
    @Override public void invoke(@NotNull Project project, @NotNull Editor editor, @Nullable PsiFile file, @Nullable DataContext dataContext) {
        if (file instanceof FileBase fileBase) {
            SelectionModel selection = editor.getSelectionModel();
            if (selection.hasSelection()) {
                PsiElement expression = SearchByOffset.findExpressionInRange(fileBase, selection.getSelectionStart(), selection.getSelectionEnd());
                if (expression == null) {
                    CommonRefactoringUtil.showErrorHint(project, editor, RefactoringBundle.message("refactoring.introduce.selection.error"),
                            RefactoringBundle.message("introduce.variable.title"), HelpID.INTRODUCE_VARIABLE);
                    return;
                }

                //performOnElement(editor, element);
                if (editor.getSettings().isVariableInplaceRenameEnabled()) {
                    OccurrencesChooser.simpleChooser(editor).showChooser(expression, findOccurrences(expression), new Pass<OccurrencesChooser.ReplaceChoice>() {
                        @Override public void pass(OccurrencesChooser.ReplaceChoice replaceChoice) {
                            performInPlaceIntroduce(editor, expression, true);
                        }
                    });
                }
            }
            //else {
            //    PsiElement expressionAtCaret = findExpressionAtCaret(fileBase, editor.getCaretModel().getOffset());
            //    PsiElement psiElement = expressionAtCaret != null ? expressionAtCaret.getParent() : null;
            //    exprs = psiElement != null ? List.of(psiElement) : Collections.emptyList();
            //}
        }
    }

    private void performInPlaceIntroduce(Editor editor, PsiElement expression, boolean b) {
        System.out.println("editor = " + editor + ", expression = " + expression + ", b = " + b);
        // TODO implement method
    }

    private List<PsiElement> findOccurrences(PsiElement expression) {
        List<PsiElement> occurrences = new ArrayList<>();

        new PsiRecursiveElementVisitor() {
            @Override public void visitElement(@NotNull PsiElement element) {
                super.visitElement(element);  // TODO implement method override
            }
        }.visitElement(expression);

        occurrences.add(expression);
        return occurrences;
    }

    @Override public void invoke(@NotNull Project project, PsiElement @NotNull [] elements, DataContext dataContext) {
        // This doesn’t get called from the editor.
    }
}
