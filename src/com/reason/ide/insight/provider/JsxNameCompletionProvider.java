package com.reason.ide.insight.provider;

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
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.intellij.psi.search.GlobalSearchScope.allScope;
import static com.intellij.util.PsiIconUtil.getProvidersIcon;

public class JsxNameCompletionProvider {

    private static final Log LOG = Log.create("insight.jsxname");

    private JsxNameCompletionProvider() {
    }

    public static void addCompletions(@NotNull PsiElement element, @NotNull CompletionResultSet resultSet) {
        LOG.debug("JSX name expression completion");

        RmlFile originalFile = (RmlFile) element.getContainingFile();
        Project project = originalFile.getProject();
        GlobalSearchScope scope = allScope(project);
        PsiFinder psiFinder = PsiFinder.getInstance(project);

        Collection<PsiModule> modules = psiFinder.findComponents(scope);
        LOG.debug("Modules found", modules.size());
        for (PsiModule module : modules) {
            boolean isInner = module instanceof PsiInnerModule;
            String moduleName = isInner ? module.getName() : ((FileBase) module).asModuleName();
            if (moduleName != null) {
                resultSet.addElement(LookupElementBuilder.
                        create(moduleName).
                        withIcon(getProvidersIcon(module, 0)).
                        withTypeText(isInner ? ((FileBase) module.getContainingFile()).asModuleName() : ((FileBase) module).shortLocation(project)).
                        withInsertHandler((context, item) -> insertTagNameHandler(project, context, moduleName))
                );
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
