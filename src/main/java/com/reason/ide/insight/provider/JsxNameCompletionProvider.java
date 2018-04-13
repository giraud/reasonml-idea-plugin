package com.reason.ide.insight.provider;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.util.ProcessingContext;
import com.reason.ide.files.RmlFile;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.PsiSignatureUtil;
import com.reason.lang.core.psi.PsiModule;
import org.jetbrains.annotations.NotNull;

import java.util.List;

import static com.intellij.util.PsiIconUtil.getProvidersIcon;
import static com.reason.lang.core.MlFileType.interfaceOrImplementation;

public class JsxNameCompletionProvider extends CompletionProvider<CompletionParameters> {
    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
        //System.out.println("JsxNameCompletionProvider.addCompletions");

        RmlFile originalFile = (RmlFile) parameters.getOriginalFile();
        String fileModuleName = originalFile.asModuleName();
        Project project = originalFile.getProject();

        // Find all files that are components ! TODO: components can be sub modules
        List<PsiModule> modules = PsiFinder.findFileModules(project, interfaceOrImplementation);
        for (PsiModule module : modules) {
            String moduleName = module.getName();
            if (!fileModuleName.equals(moduleName) && module.isComponent()) {
                resultSet.addElement(LookupElementBuilder.create(module).
                        withIcon(getProvidersIcon(module, 0)).
                        withTypeText(PsiSignatureUtil.getProvidersType(module.getLetExpression("make"))).
                        withInsertHandler((context, item) -> insertTagNameHandler(project, context, moduleName))
                );
            }
        }
    }

    private static void insertTagNameHandler(Project project, InsertionContext context, String tagName) {
        char completionChar = context.getCompletionChar();
        if (completionChar == ' ') {
            context.setAddCompletionChar(false);
        }

        Editor editor = context.getEditor();
        EditorModificationUtil.insertStringAtCaret(editor, " ></" + tagName + ">");
        editor.getCaretModel().moveToOffset(editor.getCaretModel().getOffset() - 4 - tagName.length());

        AutoPopupController.getInstance(project).autoPopupMemberLookup(editor, null);
    }
}
