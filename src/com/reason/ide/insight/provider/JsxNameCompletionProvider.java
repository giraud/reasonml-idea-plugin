package com.reason.ide.insight.provider;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionParameters;
import com.intellij.codeInsight.completion.CompletionProvider;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.ProcessingContext;
import com.reason.ide.Debug;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.RmlFile;
import com.reason.lang.core.PsiFinder;
import com.reason.lang.core.psi.PsiModule;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.stream.Collectors;

import static com.intellij.util.PsiIconUtil.getProvidersIcon;
import static com.reason.lang.core.ORFileType.interfaceOrImplementation;

public class JsxNameCompletionProvider extends CompletionProvider<CompletionParameters> {
    private final Debug m_debug;

    public JsxNameCompletionProvider() {
        m_debug = new Debug(Logger.getInstance("ReasonML.insight.jsxname"));
    }

    @Override
    protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext processingContext, @NotNull CompletionResultSet resultSet) {
        m_debug.debug("JSX name expression completion");

        PsiFinder psiFinder = PsiFinder.getInstance();

        RmlFile originalFile = (RmlFile) parameters.getOriginalFile();
        String fileModuleName = originalFile.asModuleName();
        Project project = originalFile.getProject();

        // Find all files that are components !
        Collection<FileBase> files = psiFinder.findFileModules(project, interfaceOrImplementation).stream().filter(FileBase::isComponent).collect(Collectors.toList());
        m_debug.debug("Files found", files.size());
        for (FileBase file : files) {
            String moduleName = file.asModuleName();
            if (!fileModuleName.equals(moduleName) && file.isComponent()) {
                resultSet.addElement(LookupElementBuilder.
                        create(file.asModuleName()).
                        withIcon(getProvidersIcon(file, 0)).
                        withTypeText(file.shortLocation(project)).
                        withInsertHandler((context, item) -> insertTagNameHandler(project, context, moduleName))
                );
            }
        }

        Collection<PsiModule> innerModules = psiFinder.findComponents(project, GlobalSearchScope.allScope(project));
        m_debug.debug("Inner modules found", innerModules.size());
        for (PsiModule module : innerModules) {
            resultSet.addElement(LookupElementBuilder.
                    create(module.getName()).
                    withIcon(getProvidersIcon(module, 0)).
                    withTypeText(((FileBase) module.getContainingFile()).asModuleName()).
                    withInsertHandler((context, item) -> insertTagNameHandler(project, context, module.getName()))
            );

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
