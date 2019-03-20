package com.reason.ide.insight.provider;

import com.intellij.codeInsight.AutoPopupController;
import com.intellij.codeInsight.completion.CompletionResultSet;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorModificationUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.reason.Log;
import com.reason.ide.files.FileBase;
import com.reason.ide.files.RmlFile;
import com.reason.ide.search.FileModuleIndexService;
import com.reason.ide.search.PsiFinder;
import com.reason.lang.core.psi.PsiInnerModule;
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
        String fileModuleName = originalFile.asModuleName();
        Project project = originalFile.getProject();
        GlobalSearchScope scope = allScope(project);
        PsiManager psiManager = PsiManager.getInstance(project);

        // TODO: use psiFinder to get all unified PsiModule that are components

        // Find all files that are components !
        Collection<VirtualFile> components = FileModuleIndexService.getService().getComponents(project, scope);
        LOG.debug("Files found", components.size());
        for (VirtualFile component : components) {
            FileBase file = (FileBase) psiManager.findFile(component);
            if (file != null) {
                String moduleName = file.asModuleName();
                if (!fileModuleName.equals(moduleName)) {
                    resultSet.addElement(LookupElementBuilder.
                            create(moduleName).
                            withIcon(getProvidersIcon(file, 0)).
                            withTypeText(file.shortLocation(project)).
                            withInsertHandler((context, item) -> insertTagNameHandler(project, context, moduleName))
                    );
                }
            }
        }

        // Find all inner modules that are components
        PsiFinder psiFinder = PsiFinder.getInstance(project);
        Collection<PsiInnerModule> innerModules = psiFinder.findComponents(scope);
        LOG.debug("Inner modules found", innerModules.size());
        for (PsiInnerModule module : innerModules) {
            String moduleName = module.getName();
            if (moduleName != null) {
                resultSet.addElement(LookupElementBuilder.
                        create(moduleName).
                        withIcon(getProvidersIcon(module, 0)).
                        withTypeText(((FileBase) module.getContainingFile()).asModuleName()).
                        withInsertHandler((context, item) -> insertTagNameHandler(project, context, moduleName))
                );
            }

        }
    }

    private static void insertTagNameHandler(@NotNull Project project, InsertionContext context, @NotNull String tagName) {
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
