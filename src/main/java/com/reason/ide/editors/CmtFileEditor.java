package com.reason.ide.editors;

import com.intellij.icons.*;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.fileEditor.*;
import com.intellij.openapi.project.*;
import com.intellij.openapi.util.*;
import com.intellij.openapi.vfs.*;
import com.intellij.ui.*;
import com.intellij.ui.components.*;
import com.intellij.ui.table.*;
import com.reason.hints.*;
import org.jetbrains.annotations.*;

import javax.swing.*;
import javax.swing.table.*;
import java.beans.*;
import java.util.*;

public class CmtFileEditor extends UserDataHolderBase implements FileEditor {
    private final Project myProject;
    private final VirtualFile myFile;
    private TabbedPaneWrapper myRootTabbedPane;

    public CmtFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
        myProject = project;
        myFile = file;
    }

    @Override
    public @NotNull VirtualFile getFile() {
        return myFile;
    }

    @Override
    public @NotNull JComponent getComponent() {
        // using workaround found at https://youtrack.jetbrains.com/issue/IDEA-272890
        // myRootTabbedPane = new JBTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        PrevNextActionsDescriptor descriptor = new PrevNextActionsDescriptor(IdeActions.ACTION_NEXT_EDITOR_TAB, IdeActions.ACTION_PREVIOUS_EDITOR_TAB);
        myRootTabbedPane = new TabbedPaneWrapper.AsJBTabs(myProject, SwingConstants.TOP, descriptor, this);

        InsightManager insightManager = myProject.getService(InsightManager.class);
        List<String> meta = insightManager.dumpMeta(myFile);
        String xmlSource = insightManager.dumpTree(myFile);
        List<String> types = insightManager.dumpInferredTypes(myFile);

        myRootTabbedPane.addTab("Meta", AllIcons.Nodes.DataTables, new JBScrollPane(Table.create(new MetaTableModel(meta))), "Meta information");
        //noinspection DialogTitleCapitalization
        myRootTabbedPane.addTab("AST", AllIcons.FileTypes.Xml, new CmtXmlComponent(myProject, myRootTabbedPane, xmlSource), "Abstract Syntax Tree");
        myRootTabbedPane.addTab("Inferred", AllIcons.Nodes.DataSchema, new JBScrollPane(Table.create(new InferredTableModel(types))), "Inferred types from AST");

        return myRootTabbedPane.getComponent();
    }

    @Override
    public @Nullable JComponent getPreferredFocusedComponent() {
        return myRootTabbedPane.getComponent();
    }

    @Override
    public @NotNull String getName() {
        return "CMT Editor";
    }

    @Override
    public boolean isModified() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void dispose() {
        Disposer.dispose(this);
    }

    @Override
    public @Nullable FileEditorLocation getCurrentLocation() {
        return null;
    }

    @Override
    public void setState(@NotNull FileEditorState state) {
    }

    @Override
    public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    @Override
    public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {
    }

    static class Table extends JBTable {
        private Table(TableModel model) {
            super(model);
        }

        public static @NotNull Table create(TableModel model) {
            Table table = new Table(model);
            table.setTableHeader(table.createDefaultTableHeader());
            table.invalidate();
            return table;
        }
    }

    static class MetaTableModel extends AbstractTableModel {
        private final List<String[]> m_entries = new ArrayList<>();

        public MetaTableModel(@NotNull List<String> entries) {
            for (String entry : entries) {
                m_entries.add(entry.split("\\|", 2));
            }
        }

        @Override
        public int getRowCount() {
            return m_entries.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public @NotNull String getColumnName(int col) {
            return col == 0 ? "Key" : "Value";
        }

        @Override
        public @NotNull Class<?> getColumnClass(int col) {
            return String.class;
        }

        @Override
        public Object getValueAt(int row, int col) {
            String[] values = m_entries.get(row);
            if (values != null && col < values.length) {
                return values[col];
            }
            return "";
        }
    }

    static class InferredTableModel extends AbstractTableModel {
        private final List<String[]> m_types = new ArrayList<>();

        public InferredTableModel(@NotNull List<String> types) {
            for (String type : types) {
                m_types.add(type.split("\\|"));
            }
        }

        @Override
        public int getRowCount() {
            return m_types.size() - 1;
        }

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public @NotNull String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "Kind";
                case 1:
                    return "Position";
                case 2:
                    return "Name";
                case 3:
                    return "QName";
                case 4:
                    return "Type";
                default:
                    return " ";
            }
        }

        @Override
        public @NotNull Class<?> getColumnClass(int col) {
            return String.class;
        }

        @Override
        public Object getValueAt(int row, int col) {
            String[] values = m_types.get(row + 1);
            if (values != null && col < values.length) {
                return values[col];
            }
            return "";
        }
    }
}
