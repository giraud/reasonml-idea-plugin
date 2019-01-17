package com.reason.ide.testAssistant;

import com.intellij.notification.Notifications;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.awt.RelativePoint;
import com.reason.ide.ORNotification;
import com.reason.ide.files.FileBase;
import com.reason.lang.core.PsiFinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static com.intellij.notification.NotificationType.INFORMATION;

public class GotoTestDataAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            DataContext dataContext = e.getDataContext();
            List<String> fileNames = findRelatedFiles(project, dataContext);
            if (fileNames.isEmpty()) {
                Notifications.Bus.notify(new ORNotification("testdata", "Found no testdata files", "Cannot find related files", INFORMATION, null), project);
                return;
            }

            Editor editor = e.getData(CommonDataKeys.EDITOR);
            JBPopupFactory popupFactory = JBPopupFactory.getInstance();
            RelativePoint point = editor == null ? popupFactory.guessBestPopupLocation(dataContext) : popupFactory.guessBestPopupLocation(editor);

            TestDataNavigationHandler.navigate(point, fileNames, project);
        }
    }

    @NotNull
    private List<String> findRelatedFiles(@NotNull Project project, @NotNull DataContext context) {
        PsiFile file = context.getData(CommonDataKeys.PSI_FILE);
        if (file instanceof FileBase) {
            FileBase relatedFile;

            GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
            String[] tokens = splitModuleName(((FileBase) file).asModuleName());
            if (tokens.length == 1) {
                relatedFile = PsiFinder.getInstance().findFileModule(project, tokens[0] + "_test", scope);
                if (relatedFile == null) {
                    relatedFile = PsiFinder.getInstance().findFileModule(project, tokens[0] + "_spec", scope);
                }
            } else {
                relatedFile = PsiFinder.getInstance().findFileModule(project, tokens[0], scope);
            }

            if (relatedFile != null) {
                return Collections.singletonList(relatedFile.getVirtualFile().getPath());
            }
        }

        return Collections.emptyList();
    }

    @NotNull
    private String[] splitModuleName(@NotNull String moduleName) {
        int underscoreIndex = moduleName.lastIndexOf("_");
        return 0 < underscoreIndex ? new String[]{moduleName.substring(0, underscoreIndex), moduleName.substring(underscoreIndex + 1)} : new String[]{moduleName};
    }

    @Nullable
    private String guessTestData(@NotNull DataContext dataContext) {
        return null;
    }
}
