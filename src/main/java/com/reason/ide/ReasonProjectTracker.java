package com.reason.ide;

import com.intellij.AppTopics;
import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.SystemInfo;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.util.messages.MessageBusConnection;
import com.reason.ide.format.ReformatOnSave;
import com.reason.ide.hints.RmlDocumentListener;
import com.reason.insight.InsightManager;

import java.io.File;

public class ReasonProjectTracker extends AbstractProjectComponent {

    private final Logger m_log = Logger.getInstance("ReasonML");

    private RmlDocumentListener m_documentListener;
    private MessageBusConnection m_messageBusConnection;
    private VirtualFileListener m_vfListener;

    protected ReasonProjectTracker(Project project) {
        super(project);
    }

    @Override
    public void projectOpened() {
        if (SystemInfo.is64Bit) {
            // Try to locate Rincewind
            String osPrefix = getOsPrefix();
            if (!osPrefix.isEmpty()) {
                IdeaPluginDescriptor plugin = PluginManager.getPlugin(PluginId.getId("reasonml"));
                if (plugin != null) {
                    InsightManager insightManager = myProject.getComponent(InsightManager.class);
                    String rincewindFilename = insightManager.getRincewindFilename(osPrefix);
                    File path = plugin.getPath();

                    File file = new File(path, rincewindFilename);
                    if (file.exists()) {
                        m_log.info("Found " + rincewindFilename);
                    } else {
                        m_log.info("Downloading " + rincewindFilename + "...");

                    }
                } else {
                    m_log.info("Can't find plugin directory");
                }
            }
        } else {
            m_log.info("32Bit system detected, can't use rincewind");
        }

        m_documentListener = new RmlDocumentListener(myProject);
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(m_documentListener);

        m_messageBusConnection = myProject.getMessageBus().connect();
        m_messageBusConnection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new RmlFileEditorListener(myProject));
        m_messageBusConnection.subscribe(AppTopics.FILE_DOCUMENT_SYNC, new ReformatOnSave(myProject));

        m_vfListener = new VirtualFileListener(myProject);
        VirtualFileManager.getInstance().addVirtualFileListener(m_vfListener);
    }

    @Override
    public void projectClosed() {
        EditorFactory.getInstance().getEventMulticaster().removeDocumentListener(m_documentListener);
        VirtualFileManager.getInstance().removeVirtualFileListener(m_vfListener);
        if (m_messageBusConnection != null) {
            m_messageBusConnection.disconnect();
        }
    }

    private String getOsPrefix() {
        if (SystemInfo.isWindows) {
            m_log.info("Detected windows (64bits)");
            return "w";
        }

        if (SystemInfo.isLinux) {
            m_log.info("Detected Linux (64bits)");
            return "l";
        }

        if (SystemInfo.isMac) {
            m_log.info("Detected Mac (64bits)");
            return "o";
        }

        m_log.info("Os not detected: " + SystemInfo.getOsNameAndVersion());
        return "";
    }
}
