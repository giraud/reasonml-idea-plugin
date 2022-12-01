package com.reason.ide.testAssistant;

import com.intellij.notification.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.ui.popup.*;
import com.intellij.openapi.vfs.*;
import com.intellij.psi.*;
import com.intellij.psi.search.*;
import com.intellij.ui.awt.*;
import com.reason.ide.files.*;
import com.reason.ide.search.*;
import com.reason.lang.core.*;
import com.reason.lang.core.psi.*;
import jpsplugin.com.reason.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static com.intellij.notification.NotificationType.*;

public class GotoTestDataAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            DataContext dataContext = e.getDataContext();
            List<String> fileNames = findRelatedFiles(project, dataContext);
            if (fileNames.isEmpty()) {
                Notifications.Bus.notify(
                        new ORNotification("testdata", "Found no testdata files", "Cannot find related files", INFORMATION, null),
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

    @NotNull
    private List<String> findRelatedFiles(@NotNull Project project, @NotNull DataContext context) {
        PsiFile file = context.getData(CommonDataKeys.PSI_FILE);
        if (file instanceof FileBase) {
            VirtualFile relatedFile;

            GlobalSearchScope scope = GlobalSearchScope.projectScope(project);
            PsiFinder psiFinder = project.getService(PsiFinder.class);
            RPsiModule relatedModule;

            String[] tokens = splitModuleName(((FileBase) file).getModuleName());
            if (tokens.length == 1) {
                Set<RPsiModule> relatedModules =
                        psiFinder.findModulesbyName(
                                tokens[0] + "_test",
                                ORFileType.implementationOnly,
                                module -> module instanceof FileBase
                        );
                relatedModule = relatedModules.isEmpty() ? null : relatedModules.iterator().next();
                if (relatedModule == null) {
                    relatedModules =
                            psiFinder.findModulesbyName(
                                    tokens[0] + "_spec",
                                    ORFileType.implementationOnly,
                                    module -> module instanceof FileBase
                            );
                    relatedModule = relatedModules.isEmpty() ? null : relatedModules.iterator().next();
                }
            } else {
                Set<RPsiModule> relatedModules =
                        psiFinder.findModulesbyName(
                                tokens[0],
                                ORFileType.implementationOnly,
                                module -> module instanceof FileBase
                        );
                relatedModule = relatedModules.isEmpty() ? null : relatedModules.iterator().next();
            }

            if (relatedModule != null) {
                return Collections.singletonList(((FileBase) relatedModule).getVirtualFile().getPath());
            }
        }

        return Collections.emptyList();
    }

    private String @NotNull [] splitModuleName(@NotNull String moduleName) {
        int underscoreIndex = moduleName.lastIndexOf("_");
        return 0 < underscoreIndex
                ? new String[]{
                moduleName.substring(0, underscoreIndex), moduleName.substring(underscoreIndex + 1)
        }
                : new String[]{moduleName};
    }
}
