package com.reason.ide.testAssistant;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.popup.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.ui.awt.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.ide.search.index.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.intellij.notification.NotificationType.*;
import static com.intellij.openapi.application.ApplicationManager.*;

public class GotoTestDataAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            DataContext dataContext = e.getDataContext();
            List<String> fileNames = findRelatedFiles(project, dataContext);
            if (fileNames.isEmpty()) {
                Notifications.Bus.notify(
                        new ORNotification("testdata", "Found no testdata files", "Cannot find related files", INFORMATION),
                        project);
                return;
            }

            Editor editor = e.getData(CommonDataKeys.EDITOR);
            JBPopupFactory popupFactory = JBPopupFactory.getInstance();
            RelativePoint point =
                    editor == null
                            ? popupFactory.guessBestPopupLocation(dataContext)
                            : popupFactory.guessBestPopupLocation(editor);

            TestDataNavigationHandler.navigate(point, fileNames, project);
        }
    }

    private @NotNull List<String> findRelatedFiles(@NotNull Project project, @NotNull DataContext context) {
        PsiFile file = context.getData(CommonDataKeys.PSI_FILE);
        if (file instanceof FileBase) {
            FileModuleIndexService topModulesIndex = getApplication().getService(FileModuleIndexService.class);
            GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
            FileModuleData relatedData;

            String[] tokens = splitModuleName(((FileBase) file).getModuleName());
            if (tokens.length == 1) {
                List<FileModuleData> relatedModuleData = topModulesIndex.getTopModuleData(tokens[0] + "_test", scope);
                relatedData = relatedModuleData.isEmpty() ? null : relatedModuleData.iterator().next();
                if (relatedData == null) {
                    relatedModuleData = topModulesIndex.getTopModuleData(tokens[0] + "_spec", scope);
                    relatedData = relatedModuleData.isEmpty() ? null : relatedModuleData.iterator().next();
                }
            } else {
                List<FileModuleData> relatedModuleData = topModulesIndex.getTopModuleData(tokens[0], scope);
                relatedData = relatedModuleData.isEmpty() ? null : relatedModuleData.iterator().next();
            }

            if (relatedData != null) {
                return Collections.singletonList(relatedData.getPath());
            }
        }

        return Collections.emptyList();
    }

    private String @NotNull [] splitModuleName(@NotNull String moduleName) {
        int underscoreIndex = moduleName.lastIndexOf("_");
        return 0 < underscoreIndex
                ? new String[]{moduleName.substring(0, underscoreIndex), moduleName.substring(underscoreIndex + 1)}
                : new String[]{moduleName};
    }
}
