package it.unibz.inf.ontouml.vp.views;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vp.plugin.ApplicationManager;
import com.vp.plugin.diagram.IDiagramUIModel;
import com.vp.plugin.model.IAssociation;
import com.vp.plugin.model.IAssociationClass;
import com.vp.plugin.model.IAssociationEnd;
import com.vp.plugin.model.IAttribute;
import com.vp.plugin.model.IGeneralization;
import com.vp.plugin.model.IModelElement;
import com.vp.plugin.model.IProject;
import com.vp.plugin.model.factory.IModelElementFactory;
import com.vp.plugin.view.IDialog;
import it.unibz.inf.ontouml.vp.model.ProjectConfigurations;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class GufoExportView extends JPanel {
  private static final long serialVersionUID = 1L;

  private JTextField IRItxt;
  private JComboBox<String> formatBox;
  private JComboBox<String> uriFormatBox;
  private JComboBox<String> inverseBox;
  private JComboBox<String> objectBox;
  private JComboBox<String> analysisBox;
  private JComboBox<String> packagesBox;

  private JComboBox<String> namesBox;
  private JComboBox<String> languagesBox;
  private JComboBox<String> packagesIdBox;
  private JTabbedPane tabbedPane;
  private JCheckBoxTree packageTree;
  private JCheckBoxTree diagramTree;

  private JTable table;
  private JTable table2;

  JPanel treePanelPackage;
  JPanel treePanelDiagram;

  private JButton btnExport;
  private JButton btnCancel;

  private IDialog _dialog;

  private boolean wasCancelled = false;

  private HashSet<String> elementsPackageTree = new HashSet<>();
  private HashSet<String> elementsDiagramTree = new HashSet<>();

  private IModelElement[] elementsMapping;
  private IModelElement[] packagesMapping;

  public GufoExportView(ProjectConfigurations configurations) {
    setSize(new Dimension(680, 550));
    setLayout(new GridLayout(1, 1));

    JTabbedPane mainTabbedPane = new JTabbedPane();

    JPanel mainPanel = new JPanel();
    JPanel elementMappingPanel = new JPanel();
    JPanel packageMappingPanel = new JPanel();

    mainTabbedPane.add(mainPanel, "Basic Settings");
    mainTabbedPane.add(elementMappingPanel, "Element Correspondence");
    mainTabbedPane.add(packageMappingPanel, "Package Correspondence");

    JPanel optionsPanelLeft = new JPanel();
    JPanel optionsPanelRight = new JPanel();
    JPanel treePanel = new JPanel();
    JPanel buttonsPanel = new JPanel();
    JPanel buttonsPanelTable1 = new JPanel();
    JPanel buttonsPanelTable2 = new JPanel();

    mainPanel.setLayout(new GridBagLayout());
    elementMappingPanel.setLayout(new GridBagLayout());
    packageMappingPanel.setLayout(new GridBagLayout());
    optionsPanelLeft.setLayout(new GridBagLayout());
    optionsPanelRight.setLayout(new GridBagLayout());
    treePanel.setLayout(new GridBagLayout());
    buttonsPanel.setLayout(new GridBagLayout());
    buttonsPanelTable1.setLayout(new GridBagLayout());
    buttonsPanelTable2.setLayout(new GridBagLayout());

    mainPanel.setPreferredSize(new Dimension(650, 540));
    elementMappingPanel.setPreferredSize(new Dimension(650, 480));
    packageMappingPanel.setPreferredSize(new Dimension(650, 540));
    optionsPanelLeft.setPreferredSize(new Dimension(280, 120));
    optionsPanelRight.setPreferredSize(new Dimension(320, 120));
    treePanel.setPreferredSize(new Dimension(600, 300));
    buttonsPanel.setPreferredSize(new Dimension(600, 40));
    buttonsPanelTable1.setPreferredSize(new Dimension(280, 40));

    GridBagConstraints gbc_main = new GridBagConstraints();
    gbc_main.insets = new Insets(1, 1, 1, 1);
    gbc_main.anchor = GridBagConstraints.FIRST_LINE_START;
    gbc_main.weightx = 0.5;
    gbc_main.weighty = 1;
    gbc_main.gridx = 0;
    gbc_main.gridy = 0;
    mainPanel.add(optionsPanelLeft, gbc_main);
    gbc_main.anchor = GridBagConstraints.FIRST_LINE_END;
    gbc_main.weightx = 0.5;
    gbc_main.weighty = 0.5;
    gbc_main.gridx = 1;
    gbc_main.gridy = 0;
    mainPanel.add(optionsPanelRight, gbc_main);
    gbc_main.anchor = GridBagConstraints.PAGE_START;
    gbc_main.weightx = 1.0;
    gbc_main.weighty = 0.8;
    gbc_main.gridwidth = 4;
    gbc_main.gridx = 0;
    gbc_main.gridy = 1;
    gbc_main.fill = GridBagConstraints.HORIZONTAL;
    mainPanel.add(treePanel, gbc_main);
    gbc_main.anchor = GridBagConstraints.FIRST_LINE_END;
    gbc_main.weightx = 0.5;
    gbc_main.weighty = 0.5;
    gbc_main.gridx = 0;
    gbc_main.gridy = 2;
    mainPanel.add(buttonsPanel, gbc_main);

    JLabel iriLabel = new JLabel("Base IRI:");
    JLabel formatLabel = new JLabel("Output format:");
    JLabel URIformatLabel = new JLabel("Create URIs using:");

    IRItxt = new JTextField();

    String[] formatStrings = {"Turtle", "N-Triples", "N-Quads"};
    formatBox = new JComboBox<>(formatStrings);
    ((JLabel) formatBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

    String[] uriFormatBoxString = {"name", "id"};
    uriFormatBox = new JComboBox<>(uriFormatBoxString);
    ((JLabel) uriFormatBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
    IRItxt.setText("http://example.com");

    iriLabel.setPreferredSize(new Dimension(120, 28));
    formatLabel.setPreferredSize(new Dimension(120, 28));
    URIformatLabel.setPreferredSize(new Dimension(120, 28));
    IRItxt.setPreferredSize(new Dimension(140, 28));
    formatBox.setPreferredSize(new Dimension(140, 28));
    uriFormatBox.setPreferredSize(new Dimension(140, 28));

    GridBagConstraints gbc_insidePanelLeft = new GridBagConstraints();
    gbc_insidePanelLeft.fill = GridBagConstraints.BOTH;
    gbc_insidePanelLeft.insets = new Insets(2, 2, 2, 0);
    gbc_insidePanelLeft.weightx = 0.5;
    gbc_insidePanelLeft.gridx = 0;
    gbc_insidePanelLeft.gridy = 0;
    gbc_insidePanelLeft.anchor = GridBagConstraints.WEST;
    optionsPanelLeft.add(iriLabel, gbc_insidePanelLeft);
    gbc_insidePanelLeft.weightx = 0.5;
    gbc_insidePanelLeft.gridx = 1;
    gbc_insidePanelLeft.gridy = 0;
    optionsPanelLeft.add(IRItxt, gbc_insidePanelLeft);
    gbc_insidePanelLeft.weightx = 0.5;
    gbc_insidePanelLeft.gridx = 0;
    gbc_insidePanelLeft.gridy = 1;
    optionsPanelLeft.add(formatLabel, gbc_insidePanelLeft);
    gbc_insidePanelLeft.weightx = 0.5;
    gbc_insidePanelLeft.gridx = 1;
    gbc_insidePanelLeft.gridy = 1;
    optionsPanelLeft.add(formatBox, gbc_insidePanelLeft);
    gbc_insidePanelLeft.weightx = 0.5;
    gbc_insidePanelLeft.gridx = 0;
    gbc_insidePanelLeft.gridy = 2;
    optionsPanelLeft.add(URIformatLabel, gbc_insidePanelLeft);
    gbc_insidePanelLeft.weightx = 0.5;
    gbc_insidePanelLeft.gridx = 1;
    gbc_insidePanelLeft.gridy = 2;
    optionsPanelLeft.add(uriFormatBox, gbc_insidePanelLeft);

    JLabel inverseLabel = new JLabel("Create inverse properties:");
    JLabel objectLabel = new JLabel("Create sub-properties:");
    JLabel analysisLabel = new JLabel("Run pre analysis:");
    JLabel prefixPackageLabel = new JLabel("Add prefix per package:");

    String[] inverseBoxString = {"false", "true"};
    inverseBox = new JComboBox<>(inverseBoxString);
    ((JLabel) inverseBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

    String[] objectBoxString = {"false", "true"};
    objectBox = new JComboBox<>(objectBoxString);
    ((JLabel) objectBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

    String[] analysisBoxString = {"false", "true"};
    analysisBox = new JComboBox<>(analysisBoxString);
    ((JLabel) analysisBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);
    analysisBox.setSelectedItem("true");

    String[] packagesBoxString = {"false", "true"};
    packagesBox = new JComboBox<>(packagesBoxString);
    ((JLabel) packagesBox.getRenderer()).setHorizontalAlignment(JLabel.CENTER);

    inverseLabel.setPreferredSize(new Dimension(180, 28));
    objectLabel.setPreferredSize(new Dimension(180, 28));
    analysisLabel.setPreferredSize(new Dimension(180, 28));
    prefixPackageLabel.setPreferredSize(new Dimension(180, 28));
    inverseBox.setPreferredSize(new Dimension(90, 28));
    objectBox.setPreferredSize(new Dimension(90, 28));
    analysisBox.setPreferredSize(new Dimension(90, 28));
    packagesBox.setPreferredSize(new Dimension(90, 28));

    GridBagConstraints gbc_insidePanelRight = new GridBagConstraints();
    gbc_insidePanelRight.fill = GridBagConstraints.BOTH;
    gbc_insidePanelRight.insets = new Insets(2, 0, 2, 0);
    gbc_insidePanelRight.weightx = 0.5;
    gbc_insidePanelRight.gridx = 0;
    gbc_insidePanelRight.gridy = 0;
    gbc_insidePanelRight.anchor = GridBagConstraints.WEST;
    optionsPanelRight.add(objectLabel, gbc_insidePanelRight);
    gbc_insidePanelRight.weightx = 0.5;
    gbc_insidePanelRight.gridx = 1;
    gbc_insidePanelRight.gridy = 0;
    gbc_insidePanelRight.anchor = GridBagConstraints.EAST;
    optionsPanelRight.add(objectBox, gbc_insidePanelRight);
    gbc_insidePanelRight.weightx = 0.5;
    gbc_insidePanelRight.gridx = 0;
    gbc_insidePanelRight.gridy = 1;
    gbc_insidePanelRight.anchor = GridBagConstraints.WEST;
    optionsPanelRight.add(inverseLabel, gbc_insidePanelRight);
    gbc_insidePanelRight.weightx = 0.5;
    gbc_insidePanelRight.gridx = 1;
    gbc_insidePanelRight.gridy = 1;
    gbc_insidePanelRight.anchor = GridBagConstraints.EAST;
    optionsPanelRight.add(inverseBox, gbc_insidePanelRight);
    gbc_insidePanelRight.weightx = 0.5;
    gbc_insidePanelRight.gridx = 0;
    gbc_insidePanelRight.gridy = 2;
    gbc_insidePanelRight.anchor = GridBagConstraints.WEST;
    optionsPanelRight.add(prefixPackageLabel, gbc_insidePanelRight);
    gbc_insidePanelRight.weightx = 0.5;
    gbc_insidePanelRight.gridx = 1;
    gbc_insidePanelRight.gridy = 2;
    gbc_insidePanelRight.anchor = GridBagConstraints.EAST;
    optionsPanelRight.add(packagesBox, gbc_insidePanelRight);
    gbc_insidePanelRight.weightx = 0.5;
    gbc_insidePanelRight.gridx = 0;
    gbc_insidePanelRight.gridy = 3;
    gbc_insidePanelRight.anchor = GridBagConstraints.WEST;
    optionsPanelRight.add(analysisLabel, gbc_insidePanelRight);
    gbc_insidePanelRight.weightx = 0.5;
    gbc_insidePanelRight.gridx = 1;
    gbc_insidePanelRight.gridy = 3;
    gbc_insidePanelRight.anchor = GridBagConstraints.EAST;
    optionsPanelRight.add(analysisBox, gbc_insidePanelRight);

    JPanel packagePanel = new JPanel();
    JPanel diagramPanel = new JPanel();

    tabbedPane = new JTabbedPane();
    packageTree = new JCheckBoxTree("package");
    diagramTree = new JCheckBoxTree("diagram");

    // TODO: enable selection trees
    packageTree.setEnabled(false);
    diagramTree.setEnabled(false);

    JScrollPane scrollableTextAreaPackage = new JScrollPane(packageTree);
    JScrollPane scrollableTextAreaDiagram = new JScrollPane(diagramTree);

    scrollableTextAreaPackage.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollableTextAreaPackage.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    scrollableTextAreaDiagram.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
    scrollableTextAreaDiagram.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

    packagePanel.setLayout(new GridLayout(1, 1));
    packagePanel.setPreferredSize(new Dimension(550, 270));

    diagramPanel.setLayout(new GridLayout(1, 1));
    diagramPanel.setPreferredSize(new Dimension(550, 270));

    packagePanel.add(scrollableTextAreaPackage);

    diagramPanel.add(scrollableTextAreaDiagram);

    tabbedPane.addTab("Package Explorer", packagePanel);
    tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);

    tabbedPane.addTab("Diagram Explorer", diagramPanel);
    tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);

    tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

    treePanel.add(tabbedPane);
    treePanel.setLayout(new GridLayout(1, 1));

    btnExport = new JButton("Export");
    btnCancel = new JButton("Cancel");

    btnExport.setPreferredSize(new Dimension(80, 35));
    btnCancel.setPreferredSize(new Dimension(80, 35));

    GridBagConstraints gbc_buttonsPanel = new GridBagConstraints();
    gbc_buttonsPanel.fill = GridBagConstraints.HORIZONTAL;
    gbc_buttonsPanel.insets = new Insets(2, 2, 2, 15);
    gbc_buttonsPanel.weightx = 0.5;
    gbc_buttonsPanel.gridx = 0;
    gbc_buttonsPanel.gridy = 0;
    gbc_buttonsPanel.anchor = GridBagConstraints.PAGE_END;
    buttonsPanel.add(btnExport, gbc_buttonsPanel);
    gbc_buttonsPanel.weightx = 0.5;
    gbc_buttonsPanel.gridx = 1;
    gbc_buttonsPanel.gridy = 0;
    buttonsPanel.add(btnCancel, gbc_buttonsPanel);

    // TABLE 1

    final IProject project = ApplicationManager.instance().getProjectManager().getProject();

    final String[] anyLevelElements = {
      IModelElementFactory.MODEL_TYPE_CLASS,
      IModelElementFactory.MODEL_TYPE_ATTRIBUTE,
      IModelElementFactory.MODEL_TYPE_ASSOCIATION,
      IModelElementFactory.MODEL_TYPE_ASSOCIATION_END,
    };

    elementsMapping = project.toAllLevelModelElementArray(anyLevelElements);
    String[] nameElements = new String[elementsMapping.length];

    for (int i = 0; i < elementsMapping.length; i++)
      nameElements[i] = getDisplayName(elementsMapping[i]);

    namesBox = new JComboBox<>(nameElements);
    ((JLabel) namesBox.getRenderer()).setHorizontalAlignment(JLabel.LEFT);

    languagesBox = new JComboBox<>(getLanguagesCode());
    ((JLabel) languagesBox.getRenderer()).setHorizontalAlignment(JLabel.LEFT);

    String[][] data;
    String[] columnNamesArr;
    ArrayList<String> columnNamesList;
    DefaultTableModel defaultTableModel;
    TableColumnModel tableColumnModel;
    JScrollPane scrollPane;
    JButton addButton;
    JButton deleteButton;

    columnNamesList = new ArrayList<>();
    columnNamesList.add("Element");
    columnNamesList.add("Language");
    columnNamesList.add("Text");

    data = new String[1][columnNamesList.size()];

    columnNamesArr = new String[columnNamesList.size()];
    for (int i = 0; i < columnNamesList.size(); i++) {
      columnNamesArr[i] = columnNamesList.get(i);
      data[0][i] = "";
    }

    // defaultTableModel = new DefaultTableModel(data, columnNamesArr);
    defaultTableModel = new DefaultTableModel(null, columnNamesArr);

    table = new JTable(defaultTableModel);
    tableColumnModel = table.getColumnModel();
    table.setGridColor(Color.LIGHT_GRAY);

    for (int i = 0; i < columnNamesList.size(); i++) {
      tableColumnModel.getColumn(i).setPreferredWidth(columnNamesList.get(i).length());
    }
    table.setPreferredScrollableViewportSize(table.getPreferredSize());
    scrollPane = new JScrollPane(table);

    namesBox.setEditable(false);
    languagesBox.setEditable(false);
    table.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(namesBox));
    table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(languagesBox));
    table.getColumnModel().getColumn(0).setPreferredWidth(150);
    table.getColumnModel().getColumn(1).setPreferredWidth(20);
    table.getColumnModel().getColumn(2).setPreferredWidth(80);
    table.setRowHeight(28);

    addButton = new JButton("Add");
    deleteButton = new JButton("Delete");

    addButton.addActionListener(
        actionEvent -> {
          Vector<String> rowData = new Vector<>();
          rowData.add(namesBox.getItemAt(0));
          rowData.add(languagesBox.getItemAt(0));
          rowData.add("");
          defaultTableModel.addRow(rowData);

          table.validate();
        });

    deleteButton.addActionListener(
        actionEvent -> {
          // Vector<?> rowData = null;
          int rowCount = defaultTableModel.getRowCount();
          if (rowCount > 0) {
            defaultTableModel.removeRow(rowCount - 1);
            table.validate();
          }
        });

    GridBagConstraints gbc_buttonsPanelTable1 = new GridBagConstraints();
    gbc_buttonsPanelTable1.fill = GridBagConstraints.BOTH;
    gbc_buttonsPanelTable1.insets = new Insets(1, 1, 1, 1);
    gbc_buttonsPanelTable1.weightx = 0.5;
    gbc_buttonsPanelTable1.gridx = 0;
    gbc_buttonsPanelTable1.gridy = 0;
    gbc_buttonsPanelTable1.anchor = GridBagConstraints.PAGE_END;
    buttonsPanelTable1.add(addButton, gbc_buttonsPanelTable1);
    gbc_buttonsPanelTable1.weightx = 0.5;
    gbc_buttonsPanelTable1.gridx = 1;
    gbc_buttonsPanelTable1.gridy = 0;
    buttonsPanelTable1.add(deleteButton, gbc_buttonsPanelTable1);

    scrollPane.setPreferredSize(new Dimension(550, 400));
    buttonsPanelTable1.setPreferredSize(new Dimension(400, 30));

    GridBagConstraints gbc_mappingTable1 = new GridBagConstraints();
    // gbc_insidePanelLeft.fill = GridBagConstraints.HORIZONTAL;
    gbc_mappingTable1.insets = new Insets(1, 1, 1, 1);
    gbc_mappingTable1.gridx = 0;
    gbc_mappingTable1.gridy = 0;
    elementMappingPanel.add(scrollPane, gbc_mappingTable1);
    gbc_mappingTable1.gridx = 0;
    gbc_mappingTable1.gridy = 1;
    gbc_mappingTable1.anchor = GridBagConstraints.EAST;
    elementMappingPanel.add(buttonsPanelTable1, gbc_mappingTable1);

    // TABLE 2

    String[] packages = {
      IModelElementFactory.MODEL_TYPE_PACKAGE, IModelElementFactory.MODEL_TYPE_MODEL
    };

    packagesMapping = project.toAllLevelModelElementArray(packages);
    String[] nameElementsPack = new String[packagesMapping.length + 1];
    nameElementsPack[0] = project.getName();

    for (int i = 0; i < packagesMapping.length; i++)
      nameElementsPack[i + 1] = packagesMapping[i].getName();

    packagesIdBox = new JComboBox<>(nameElementsPack);
    ((JLabel) packagesIdBox.getRenderer()).setHorizontalAlignment(JLabel.LEFT);

    String[][] data_table2;
    String[] columnNamesArr_table2;
    ArrayList<String> columnNamesList_table2;
    DefaultTableModel defaultTableModel_table2;
    TableColumnModel tableColumnModel_table2;
    JScrollPane scrollPane_table2;
    JButton addButton_table2;
    JButton deleteButton_table2;

    columnNamesList_table2 = new ArrayList<>();
    columnNamesList_table2.add("Package");
    columnNamesList_table2.add("Prefix");
    columnNamesList_table2.add("URI");

    data_table2 = new String[1][columnNamesList_table2.size()];

    columnNamesArr_table2 = new String[columnNamesList_table2.size()];
    for (int i = 0; i < columnNamesList_table2.size(); i++) {
      columnNamesArr_table2[i] = columnNamesList_table2.get(i);
      data_table2[0][i] = "";
    }

    //		defaultTableModel_table2 = new DefaultTableModel(data_table2, columnNamesArr_table2);

    defaultTableModel_table2 = new DefaultTableModel(null, columnNamesArr_table2);

    table2 = new JTable(defaultTableModel_table2);
    table2.setGridColor(Color.LIGHT_GRAY);
    tableColumnModel_table2 = table2.getColumnModel();

    for (int i = 0; i < columnNamesList_table2.size(); i++) {
      tableColumnModel_table2
          .getColumn(i)
          .setPreferredWidth(columnNamesList_table2.get(i).length());
    }
    table2.setPreferredScrollableViewportSize(table2.getPreferredSize());
    scrollPane_table2 = new JScrollPane(table2);

    packagesIdBox.setEditable(false);
    table2.getColumnModel().getColumn(0).setCellEditor(new DefaultCellEditor(packagesIdBox));
    table2.getColumnModel().getColumn(0).setPreferredWidth(150);
    table2.getColumnModel().getColumn(1).setPreferredWidth(20);
    table2.getColumnModel().getColumn(2).setPreferredWidth(80);
    table2.setRowHeight(28);

    addButton_table2 = new JButton("Add");
    deleteButton_table2 = new JButton("Delete");

    addButton_table2.addActionListener(
        actionEvent -> {
          Vector<String> rowData = new Vector<>();
          rowData.add(packagesIdBox.getItemAt(0));
          rowData.add("");
          rowData.add("");
          defaultTableModel_table2.addRow(rowData);
          table2.validate();
        });

    deleteButton_table2.addActionListener(
        actionEvent -> {
          // Vector<?> rowData = null;
          int rowCount = defaultTableModel_table2.getRowCount();
          if (rowCount > 0) {
            defaultTableModel_table2.removeRow(rowCount - 1);
            table2.validate();
          }
        });

    GridBagConstraints gbc_buttonsPanelTable2 = new GridBagConstraints();
    gbc_buttonsPanelTable2.fill = GridBagConstraints.BOTH;
    gbc_buttonsPanelTable2.insets = new Insets(1, 1, 1, 1);
    gbc_buttonsPanelTable2.weightx = 0.5;
    gbc_buttonsPanelTable2.gridx = 0;
    gbc_buttonsPanelTable2.gridy = 0;
    gbc_buttonsPanelTable2.anchor = GridBagConstraints.PAGE_END;
    buttonsPanelTable2.add(addButton_table2, gbc_buttonsPanelTable2);
    gbc_buttonsPanelTable2.weightx = 0.5;
    gbc_buttonsPanelTable2.gridx = 1;
    gbc_buttonsPanelTable2.gridy = 0;
    buttonsPanelTable2.add(deleteButton_table2, gbc_buttonsPanelTable2);

    scrollPane_table2.setPreferredSize(new Dimension(550, 400));
    buttonsPanelTable2.setPreferredSize(new Dimension(400, 30));

    GridBagConstraints gbc_mappingTable2 = new GridBagConstraints();
    // gbc_insidePanelLeft.fill = GridBagConstraints.HORIZONTAL;
    gbc_mappingTable2.insets = new Insets(1, 1, 1, 1);
    gbc_mappingTable2.gridx = 0;
    gbc_mappingTable2.gridy = 0;
    packageMappingPanel.add(scrollPane_table2, gbc_mappingTable2);
    gbc_mappingTable2.gridx = 0;
    gbc_mappingTable2.gridy = 1;
    gbc_mappingTable2.anchor = GridBagConstraints.EAST;
    packageMappingPanel.add(buttonsPanelTable2, gbc_mappingTable2);

    add(mainTabbedPane);

    updateComponentsValues(configurations);
  }

  public JTextField getIRItxt() {
    return IRItxt;
  }

  public void setIRItxt(JTextField iRItxt) {
    IRItxt = iRItxt;
  }

  public JComboBox<String> getFormatBox() {
    return formatBox;
  }

  public void setFormatBox(JComboBox<String> formatBox) {
    this.formatBox = formatBox;
  }

  public JComboBox<String> getUriFormatBox() {
    return uriFormatBox;
  }

  public void setUriFormatBox(JComboBox<String> uriFormatBox) {
    this.uriFormatBox = uriFormatBox;
  }

  public JComboBox<String> getInverseBox() {
    return inverseBox;
  }

  public void setInverseBox(JComboBox<String> inverseBox) {
    this.inverseBox = inverseBox;
  }

  public JComboBox<String> getObjectBox() {
    return objectBox;
  }

  public void setObjectBox(JComboBox<String> objectBox) {
    this.objectBox = objectBox;
  }

  public JComboBox<String> getAnalysisBox() {
    return analysisBox;
  }

  public void setAnalysisBox(JComboBox<String> analysisBox) {
    this.analysisBox = analysisBox;
  }

  public JComboBox<String> getNamesBox() {
    return namesBox;
  }

  public void setNamesBox(JComboBox<String> namesBox) {
    this.namesBox = namesBox;
  }

  public static long getSerialversionuid() {
    return serialVersionUID;
  }

  public JComboBox<String> getLanguagesBox() {
    return languagesBox;
  }

  public JComboBox<String> getPackagesIdBox() {
    return packagesIdBox;
  }

  public JTabbedPane getTabbedPane() {
    return tabbedPane;
  }

  public JCheckBoxTree getPackageTree() {
    return packageTree;
  }

  public JCheckBoxTree getDiagramTree() {
    return diagramTree;
  }

  public JTable getTable() {
    return table;
  }

  public JTable getTable2() {
    return table2;
  }

  public JPanel getTreePanelPackage() {
    return treePanelPackage;
  }

  public JPanel getTreePanelDiagram() {
    return treePanelDiagram;
  }

  public JButton getBtnExport() {
    return btnExport;
  }

  public JButton getBtnCancel() {
    return btnCancel;
  }

  public IDialog get_dialog() {
    return _dialog;
  }

  public HashSet<String> getElementsPackageTree() {
    return elementsPackageTree;
  }

  public HashSet<String> getElementsDiagramTree() {
    return elementsDiagramTree;
  }

  public IModelElement[] getElementsMapping() {
    return elementsMapping;
  }

  public IModelElement[] getPackagesMapping() {
    return packagesMapping;
  }

  protected JComponent makeTextPanel(String text) {
    JPanel panel = new JPanel(false);
    JLabel filler = new JLabel(text);
    filler.setHorizontalAlignment(JLabel.CENTER);
    panel.setLayout(new GridLayout(1, 1));
    panel.add(filler);
    return panel;
  }

  public boolean getWasCancelled() {
    return wasCancelled;
  }

  private static String[] getLanguagesCode() {

    return new String[] {
      "default", "aa", "ab", "ae", "af", "ak", "am", "an", "ar", "as", "av", "ay", "az", "ba", "be",
      "bg", "bh", "bm", "bi", "bn", "bo", "br", "bs", "ca", "ce", "ch", "co", "cr", "cs", "cu",
      "cv", "cy", "da", "de", "dv", "dz", "ee", "el", "en", "eo", "es", "et", "eu", "fa", "ff",
      "fi", "fj", "fo", "fr", "fy", "ga", "gd", "gl", "gn", "gu", "gv", "ha", "he", "hi", "ho",
      "hr", "ht", "hu", "hy", "hz", "ia", "id", "ie", "ig", "ii", "ik", "io", "is", "it", "iu",
      "ja", "jv", "ka", "kg", "ki", "kj", "kk", "kl", "km", "kn", "ko", "kr", "ks", "ku", "kv",
      "kw", "ky", "la", "lb", "lg", "li", "ln", "lo", "lt", "lu", "lv", "mg", "mh", "mi", "mk",
      "ml", "mn", "mr", "ms", "mt", "my", "na", "nb", "nd", "ne", "ng", "nl", "nn", "no", "nr",
      "nv", "ny", "oc", "oj", "om", "or", "os", "pa", "pi", "pl", "ps", "pt", "qu", "rm", "rn",
      "ro", "ru", "rw", "sa", "sc", "sd", "se", "sg", "si", "sk", "sl", "sm", "sn", "so", "sq",
      "sr", "ss", "st", "su", "sv", "sw", "ta", "te", "tg", "th", "ti", "tk", "tl", "tn", "to",
      "tr", "ts", "tt", "tw", "ty", "ug", "uk", "ur", "uz", "ve", "vi", "vo", "wa", "wo", "xh",
      "yi", "yo", "za", "zh", "zu"
    };
  }

  /** Sets a direct reference to the container dialog after initialization. */
  public void setContainerDialog(IDialog dialog) {
    this._dialog = dialog;
  }

  /** Updates project configurations with components' information. */
  public void updateConfigurationsValues(ProjectConfigurations configurations) {
    if (tabbedPane.getSelectedIndex() == 0) saveSelectedElements("Package Tree");
    else saveSelectedElements("Diagram Tree");

    configurations.setExportGUFOIRI(IRItxt.getText());
    configurations.setExportGUFOFormat(formatBox.getSelectedItem().toString());
    configurations.setExportGUFOURIFormat(uriFormatBox.getSelectedItem().toString());
    configurations.setExportGUFOInverseBox("true".equals(inverseBox.getSelectedItem()));
    configurations.setExportGUFOObjectBox("true".equals(objectBox.getSelectedItem()));
    configurations.setExportGUFOAnalysisBox("true".equals(analysisBox.getSelectedItem()));
    configurations.setExportGUFOPackagesBox("true".equals(packagesBox.getSelectedItem()));
    configurations.setExportGUFOElementMapping(getTableElementMapping());
    configurations.setExportGUFOPackageMapping(getTablePackageMapping());

    if (tabbedPane.getSelectedIndex() == 0)
      configurations.setExportGUFOElementsPackageTree(elementsPackageTree);
    else configurations.setExportGUFOElementsDiagramTree(elementsDiagramTree);
  }

  /**
   * Updates components with project configurations' information.
   *
   * @param configurations
   */
  private void updateComponentsValues(ProjectConfigurations configurations) {

    if (configurations.getExportGUFOIRI() != null && !configurations.getExportGUFOIRI().equals(""))
      IRItxt.setText(configurations.getExportGUFOIRI());

    if (configurations.getExportGUFOFormat() != null
        && !configurations.getExportGUFOFormat().equals(""))
      formatBox.setSelectedItem(configurations.getExportGUFOFormat());

    if (configurations.getExportGUFOURIFormat() != null
        && !configurations.getExportGUFOURIFormat().equals(""))
      uriFormatBox.setSelectedItem(configurations.getExportGUFOURIFormat());

    if (configurations.getExportGUFOElementsPackageTree() != null)
      packageTree.setNodesCheck(configurations.getExportGUFOElementsPackageTree());

    if (configurations.getExportGUFOElementsDiagramTree() != null)
      diagramTree.setNodesCheck(configurations.getExportGUFOElementsDiagramTree());

    // "false" is add first, so its index is 0. This is a cheap workaround due to issue on string
    // comparison, let's just use check boxes next time
    inverseBox.setSelectedIndex(configurations.getExportGUFOInverseBox() ? 1 : 0);
    objectBox.setSelectedIndex(configurations.getExportGUFOObjectBox() ? 1 : 0);
    analysisBox.setSelectedIndex(configurations.getExportGUFOAnalysisBox() ? 1 : 0);
    packagesBox.setSelectedIndex(configurations.getExportGUFOPackagesBox() ? 1 : 0);
  }

  private void saveSelectedElements(String tree) {

    TreePath[] paths;
    HashSet<String> aux;

    if (tree.equals("Package Tree")) {
      paths = packageTree.getCheckedPaths();
      aux = this.elementsPackageTree;
    } else {
      paths = diagramTree.getCheckedPaths();
      aux = this.elementsDiagramTree;
    }

    for (TreePath path : paths) {
      Object[] object = path.getPath();
      for (Object o : object) {

        if (o instanceof DefaultMutableTreeNode) {

          DefaultMutableTreeNode node = (DefaultMutableTreeNode) o;

          if (node.getUserObject() instanceof IModelElement)
            aux.add(((IModelElement) node.getUserObject()).getId());

          if (node.getUserObject() instanceof IDiagramUIModel)
            aux.add(((IDiagramUIModel) node.getUserObject()).getId());
        }
      }
    }
  }

  public HashSet<String> getSavedElements() {

    if (tabbedPane.getSelectedIndex() == 0) return elementsPackageTree;
    else return elementsDiagramTree;
  }

  public String getTableElementMapping() {
    HashMap<String, HashMap<String, HashMap<String, String>>> results = new HashMap<>();
    String elementId = "";

    for (int row = 0; row < table.getRowCount(); row++) {

      HashMap<String, String> content;
      HashMap<String, HashMap<String, String>> label;

      String element = table.getValueAt(row, 0).toString();
      String language = table.getValueAt(row, 1).toString();
      String text = table.getValueAt(row, 2).toString();

      for (IModelElement iModelElement : elementsMapping) {

        if (iModelElement.getName() == null) continue;

        if (iModelElement.getName().equals(element)) {
          elementId = iModelElement.getId();
        }
      }

      if (!results.containsKey(elementId)) {
        label = new HashMap<String, HashMap<String, String>>();
        content = new HashMap<String, String>();
        label.put("label", content);
        results.put(elementId, label);

      } else {
        label = results.get(elementId);
        content = label.get("label");
      }

      content.put(language, text);
    }

    // TODO: replace Gson with fasterxml
    Gson gson = new Gson();
    String json = gson.toJson(results);

    JsonObject convertedObject = new Gson().fromJson(json, JsonObject.class);

    return convertedObject.toString();
  }

  public String getTablePackageMapping() {
    final IProject project = ApplicationManager.instance().getProjectManager().getProject();
    HashMap<String, HashMap<String, String>> results = new HashMap<>();
    String elementId = "";

    for (int row = 0; row < table2.getRowCount(); row++) {
      HashMap<String, String> content;

      String packages = table2.getValueAt(row, 0).toString();
      String prefix = table2.getValueAt(row, 1).toString();
      String uri = table2.getValueAt(row, 2).toString();

      for (IModelElement element : packagesMapping) {

        if (element.getName() == null) continue;

        if (element.getName().equals(packages)) elementId = element.getId();
      }

      if (project.getName().equals(packages)) elementId = project.getId();

      if (!results.containsKey(elementId)) {
        content = new HashMap<>();
        content.put("prefix", prefix);
        content.put("uri", uri);
      } else {
        content = results.get(elementId);
      }

      results.put(elementId, content);
      content.put("prefix", prefix);
      content.put("uri", uri);
    }

    // TODO: replace Gson with fasterxml
    Gson gson = new Gson();
    String json = gson.toJson(results);

    JsonObject convertedObject = new Gson().fromJson(json, JsonObject.class);

    return convertedObject.toString();
  }

  public static String getDisplayName(Object obj) {
    if (obj == null) return "";

    if (obj instanceof String) return obj.toString();

    if (obj instanceof IDiagramUIModel) {
      IDiagramUIModel diagram = (IDiagramUIModel) obj;

      return diagram.getName() != null ? diagram.getName() : "Diagram";
    }

    if (obj instanceof IAttribute) {
      IAttribute attribute = (IAttribute) obj;

      String attributeName = attribute.getName() != null ? attribute.getName() : "";

      String attributeType = "";
      if (attribute.getType() != null) attributeType = attribute.getTypeAsString();

      return attributeName + " : " + attributeType;
    }

    if (obj instanceof IAssociation) {
      IAssociation association = (IAssociation) obj;
      String assocName = "";

      if (association.getName() != null && !association.getName().equals(""))
        assocName = association.getName() + " ";

      String nameFrom = association.getFrom() == null ? "" : association.getFrom().getName();
      String nameTo = association.getTo() == null ? "" : association.getTo().getName();

      return assocName + "(" + nameFrom + " -> " + nameTo + ")";
    }

    if (obj instanceof IAssociationEnd) {
      IAssociationEnd assocEnd = (IAssociationEnd) obj;

      String endName = assocEnd.getName() != null ? assocEnd.getName() : "";
      String typeName = assocEnd.getType() != null ? assocEnd.getTypeAsString() : "";

      return endName + " : " + typeName;
    }

    if (obj instanceof IGeneralization) {
      IGeneralization generalization = (IGeneralization) obj;

      String childTypeName = generalization.getTo() != null ? generalization.getTo().getName() : "";
      String parentTypeName =
          generalization.getFrom() != null ? generalization.getFrom().getName() : "";

      return "(" + childTypeName + " -> " + parentTypeName + ")";
    }

    if (obj instanceof IAssociationClass) {
      IAssociationClass assocClass = (IAssociationClass) obj;

      String nameFrom = assocClass.getFrom() != null ? assocClass.getFrom().getName() : "";
      String nameTo = assocClass.getTo() != null ? assocClass.getTo().getName() : "";

      return "(" + nameFrom + " -> " + nameTo + ")";
    }

    if (obj instanceof IModelElement) {
      IModelElement element = (IModelElement) obj;
      return element.getName() != null ? element.getName() : "ModelElement " + element.getId();
    }

    return "Element";
  }

  public void onExport(ActionListener onExportAction) {
    ActionListener[] currentListeners = btnExport.getActionListeners();

    for (int i = 0; currentListeners != null && i < currentListeners.length; i++) {
      btnExport.removeActionListener(currentListeners[i]);
    }

    btnExport.addActionListener(onExportAction);
  }

  public void onCancel(ActionListener onCancelAction) {
    ActionListener[] currentListeners = btnCancel.getActionListeners();

    for (int i = 0; currentListeners != null && i < currentListeners.length; i++) {
      btnCancel.removeActionListener(currentListeners[i]);
    }

    btnCancel.addActionListener(onCancelAction);
  }
}
