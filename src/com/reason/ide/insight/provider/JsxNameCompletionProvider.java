package com.reason.ide.insight.provider;

import static com.intellij.psi.search.GlobalSearchScope.allScope;
import static com.intellij.util.PsiIconUtil.getProvidersIcon;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.reason.Log;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.RmlFile;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.PsiInnerModule;
import com.reason.lang.core.psi.PsiModule;

import java.util.*;

import com.reason.lang.core.psi.impl.*;
import org.jetbrains.annotations.NotNull;

public class JsxNameCompletionProvider {
    private static final Log LOG = Log.create("insight.jsx.name");

    private JsxNameCompletionProvider() {
    }

    public static void addCompletions(@NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("JSX name expression completion");

        FileBase originalFile = (FileBase) element.getContainingFile();
        Project project = originalFile.getProject();
        GlobalSearchScope scope = allScope(project);

        Collection<PsiModule> modules = PsiFinder.getInstance(project).findComponents(scope);
        LOG.debug(" -> Modules found", modules);
        for (PsiModule module : modules) {
            String moduleName = module.getModuleName();
            if (moduleName != null) {
                FileBase containingFile = module instanceof FileBase ? (FileBase) module : (FileBase) module.getContainingFile();
                // if component is the file, don't add it to the completion result
                if (!module.getQualifiedName().equals(originalFile.getModuleName())) {
                    resultSet.addElement(
                            LookupElementBuilder.create(moduleName)
                                    .withIcon(getProvidersIcon(module, 0))
                                    .withTypeText(
                                            module instanceof PsiInnerModule
                                                    ? containingFile.getModuleName()
                                                    : containingFile.shortLocation(project))
                                    .withInsertHandler(
                                            (context, item) -> insertTagNameHandler(project, context, moduleName)));
                }
            }
        }
    }

    private static void insertTagNameHandler(@NotNull Project project, @NotNull InsertionContext context, @NotNull String tagName) {
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
