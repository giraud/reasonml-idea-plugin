package com.reason.bs;

import com.intellij.execution.filters.TextConsoleBuilderFactory;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleView;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

public class BucklescriptToolWindowFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull final Project project, @NotNull ToolWindow toolWindow) {
//        EventLog.getProjectComponent(project).initDefaultContent();
//    }
//
//    static void createContent(Project project, ToolWindow toolWindow, EventLogConsole console, String title) {
        // update default Event Log tab title
        ContentManager contentManager = toolWindow.getContentManager();
//        Content generalContent = contentManager.getContent(0);
//        if (generalContent != null && contentManager.getContentCount() == 1) {
//            generalContent.setDisplayName("General");
//        }
//
        ConsoleViewImpl console = (ConsoleViewImpl) TextConsoleBuilderFactory.getInstance().createBuilder(project).getConsole();
        console.print("test window", ConsoleViewContentType.NORMAL_OUTPUT);
//        final Editor editor = console.getConsoleEditor();
//        JPanel editorPanel = new JPanel(new AbstractLayoutManager() {
//            private int getOffset() {
//                return JBUI.scale(4);
//            }
//
//            @Override
//            public Dimension preferredLayoutSize(Container parent) {
//                Dimension size = parent.getComponent(0).getPreferredSize();
//                return new Dimension(size.width + getOffset(), size.height);
//            }
//
//            @Override
//            public void layoutContainer(Container parent) {
//                int offset = getOffset();
//                parent.getComponent(0).setBounds(offset, 0, parent.getWidth() - offset, parent.getHeight());
//            }
//        }) {
//            @Override
//            public Color getBackground() {
//                return ((EditorEx)editor).getBackgroundColor();
//            }
//        };
//        editorPanel.add(editor.getComponent());
//

//        SimpleToolWindowPanel panel = new SimpleToolWindowPanel(false, true) {
//            @Override
//            public Object getData(@NonNls String dataId) {
//                return PlatformDataKeys.HELP_ID.is(dataId) ? EventLog.HELP_ID : super.getData(dataId);
//            }
//        };
//        panel.setContent(console.getComponent());
//        panel.addAncestorListener(new EventLogToolWindowFactory.LogShownTracker(project));

//        ActionToolbar toolbar = createToolbar(/*project, editor,*/ console);
//        toolbar.setTargetComponent(console.getComponent()/*editor.getContentComponent()*/);
//        panel.setToolbar(toolbar.getComponent());
//

        console.addCustomConsoleAction(new BucklescriptConsole.ClearLogAction(console));
        BucklescriptCompiler bucklescriptCompiler = ServiceManager.getService(project, BucklescriptCompiler.class);
        console.attachToProcess(bucklescriptCompiler.getHandler());
        bucklescriptCompiler.startNotify();

        Content content = ContentFactory.SERVICE.getInstance().createContent(console.getComponent(), "", true);
        contentManager.addContent(content);
        contentManager.setSelectedContent(content);
    }

    private static ActionToolbar createToolbar(/*Project project, Editor editor,*/ ConsoleViewImpl console) {
//        DefaultActionGroup group = new DefaultActionGroup();
//        group.add(new EventLogToolWindowFactory.EditNotificationSettings(project));
//        group.add(new EventLogToolWindowFactory.DisplayBalloons());
//        group.add(new EventLogToolWindowFactory.ToggleSoftWraps(editor));
//        group.add(new ScrollToTheEndToolbarAction(editor));
//        group.add(ActionManager.getInstance().getAction(IdeActions.ACTION_MARK_ALL_NOTIFICATIONS_AS_READ));
//        group.add(new BucklescriptConsole.ClearLogAction(console));
//        group.add(new ContextHelpAction(EventLog.HELP_ID));

//        return ActionManager.getInstance().createActionToolbar(ActionPlaces.UNKNOWN, group, false);
        return null;
    }

//    private static class DisplayBalloons extends ToggleAction implements DumbAware {
//        public DisplayBalloons() {
//            super("Show balloons", "Enable or suppress notification balloons", AllIcons.General.Balloon);
//        }
//
//        @Override
//        public boolean isSelected(AnActionEvent e) {
//            return NotificationsConfigurationImpl.getInstanceImpl().SHOW_BALLOONS;
//        }
//
//        @Override
//        public void setSelected(AnActionEvent e, boolean state) {
//            NotificationsConfigurationImpl.getInstanceImpl().SHOW_BALLOONS = state;
//        }
//    }

//    private static class EditNotificationSettings extends DumbAwareAction {
//        private final Project myProject;
//
//        public EditNotificationSettings(Project project) {
//            super("Settings", "Edit notification settings", AllIcons.General.Settings);
//            myProject = project;
//        }
//
//        @Override
//        public void actionPerformed(AnActionEvent e) {
//            ShowSettingsUtil.getInstance().editConfigurable(myProject, new NotificationsConfigurable());
//        }
//    }

//    private static class ToggleSoftWraps extends ToggleUseSoftWrapsToolbarAction {
//        private final Editor myEditor;
//
//        public ToggleSoftWraps(Editor editor) {
//            super(SoftWrapAppliancePlaces.CONSOLE);
//            myEditor = editor;
//        }
//
//        @Override
//        protected Editor getEditor(AnActionEvent e) {
//            return myEditor;
//        }
//    }

    //    private static class LogShownTracker extends AncestorListenerAdapter {
//        private final Project myProject;
//
//        public LogShownTracker(Project project) {
//            myProject = project;
//        }
//
//        @Override
//        public void ancestorAdded(AncestorEvent event) {
//            ToolWindow log = EventLog.getEventLog(myProject);
//            if (log != null && log.isVisible()) {
//                EventLog.getLogModel(myProject).logShown();
//            }
//        }
//    }
    static class BucklescriptConsole {
        public static class ClearLogAction extends DumbAwareAction {
            private /*BucklescriptConsole*/ ConsoleView myConsole;

            public ClearLogAction(/*BucklescriptConsole*/ConsoleView console) {
                super("Clear All", "Clear the contents of the Event Log", AllIcons.Actions.GC);
                myConsole = console;
            }

            @Override
            public void update(AnActionEvent e) {
                Editor editor = e.getData(CommonDataKeys.EDITOR);
                e.getPresentation().setEnabled(editor != null && editor.getDocument().getTextLength() > 0);
            }

            @Override
            public void actionPerformed(final AnActionEvent e) {
//                LogModel model = myConsole.myProjectModel;
//                for (Notification notification : model.getNotifications()) {
//                    notification.expire();
//                    model.removeNotification(notification);
//                }
//                model.setStatusMessage(null, 0);

                myConsole.clear();
//                final Editor editor = e.getData(CommonDataKeys.EDITOR);
//                if (editor != null) {
//                    editor.getDocument().deleteString(0, editor.getDocument().getTextLength());
//                }
            }
        }
    }
}
