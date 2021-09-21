package com.reason.ide.editors;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorLocation;
import com.intellij.openapi.fileEditor.FileEditorState;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.UserDataHolderBase;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.table.JBTable;
import com.reason.hints.InsightManager;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CmtFileEditor extends UserDataHolderBase implements FileEditor {

  private final @NotNull Project m_project;
  private final @NotNull VirtualFile m_file;

  private JBTabbedPane m_rootTabbedPane;

  public CmtFileEditor(@NotNull Project project, @NotNull VirtualFile file) {
    m_project = project;
    m_file = file;
  }

  @NotNull
  @Override
  public JComponent getComponent() {
    m_rootTabbedPane = new JBTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);

    InsightManager insightManager = m_project.getService(InsightManager.class);
    List<String> meta = insightManager.dumpMeta(m_file);
    String xmlSource = insightManager.dumpTree(m_file);
    List<String> types = insightManager.dumpInferredTypes(m_file);

    m_rootTabbedPane.addTab(
        "Meta", AllIcons.Nodes.DataTables, new JBScrollPane(new Table(new MetaTableModel(meta))));
    m_rootTabbedPane.addTab(
        "AST", AllIcons.FileTypes.Xml, new CmtXmlComponent(m_project, m_rootTabbedPane, xmlSource));
    m_rootTabbedPane.addTab(
        "Inferred",
        AllIcons.Nodes.DataSchema,
        new JBScrollPane(new Table(new InferredTableModel(types))));

    return m_rootTabbedPane;
  }

  @Nullable
  @Override
  public JComponent getPreferredFocusedComponent() {
    return m_rootTabbedPane;
  }

  @NotNull
  @Override
  public String getName() {
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

  @Nullable
  @Override
  public FileEditorLocation getCurrentLocation() {
    return null;
  }

  @Override
  public void setState(@NotNull FileEditorState state) {}

  @Override
  public void addPropertyChangeListener(@NotNull PropertyChangeListener listener) {}

  @Override
  public void removePropertyChangeListener(@NotNull PropertyChangeListener listener) {}

  static class Table extends JBTable {

    public Table(TableModel model) {
      super(model);
      setTableHeader(createDefaultTableHeader());
      invalidate();
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
