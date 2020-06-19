package com.tencent.tars.jmeter.gui.widgets;

import com.tencent.tars.jmeter.gui.CustomResUtils;
import com.tencent.tars.jmeter.utils.ArgTriple;
import com.tencent.tars.jmeter.utils.TarsParamArgument;
import com.tencent.tars.jmeter.utils.Triple;
import com.tencent.tars.protocol.JsonConst;
import com.tencent.tars.utils.TextUtils;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.AbstractConfigGui;
import org.apache.jmeter.config.gui.RowDetailDialog;
import org.apache.jmeter.gui.util.HeaderAsPropertyRenderer;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class FuncParameterPanel extends AbstractConfigGui implements ActionListener {

    private static final long serialVersionUID = 240L;

    /**
     * The title label for this component.
     */
    private JLabel tableLabel;

    /**
     * The table containing the list of arguments.
     */
    private transient JTable table;

    /**
     * The model for the arguments table.
     */
    protected transient ObjectTableModel tableModel; 

    private JComponent mainPanel;

    /**
     * A button for adding new arguments to the table.
     */
    private JButton add;

    /**
     * A button for removing arguments from the table.
     */
    private JButton delete;

    /**
     * Added background support for reporting tool
     */
    private Color background;

    /**
     * Button to move an argument up
     */
    private JButton up;

    /**
     * Button to move an argument down
     */
    private JButton down;

    /**
     * Button to show the detail of an argument
     */
    private JButton showDetail;

    /**
     * Command for adding a row to the table.
     */
    private static final String ADD = "add"; // $NON-NLS-1$

    /**
     * Command for adding rows from the clipboard
     */
    private static final String ADD_FROM_CLIPBOARD = "addFromClipboard"; // $NON-NLS-1$

    /**
     * Command for removing a row from the table.
     */
    private static final String DELETE = "delete"; // $NON-NLS-1$

    private static final String TRANSFORM = "transform";

    public static final String TARS2JSON = "tars2json";

    /**
     * When pasting from the clipboard, split lines on linebreak
     */
    private static final String CLIPBOARD_LINE_DELIMITERS = "\n"; //$NON-NLS-1$

    /**
     * When pasting from the clipboard, split parameters on tab
     */
    private static final String CLIPBOARD_ARG_DELIMITERS = "\t"; //$NON-NLS-1$

    /**
     * Command for showing detail.
     */
    private static final String DETAIL = "detail"; // $NON-NLS-1$

    public static final String NAME = "name"; // $NON-NLS-1$

    public static final String VALUE = "value"; // $NON-NLS-1$

    public static final String TYPE = "type"; // $NON-NLS-1$

    private static final LinkedList<Triple<String, String, String>> DEFAULT_ARGS = new LinkedList<>();

    static {
        DEFAULT_ARGS.add(new ArgTriple("Request", "", JsonConst.TARS));
        DEFAULT_ARGS.add(new ArgTriple("Response", "", JsonConst.TARS));
    }

    public FuncParameterPanel(String label) {
        this(label, null, null);
    }

    public FuncParameterPanel(String label, Color bkg, ObjectTableModel model) {
        tableLabel = new JLabel(label);
        this.background = bkg;
        this.tableModel = model;
        init();
    }

    @Override
    public Collection<String> getMenuCategories() {
        return null;
    }

    @Override
    public String getLabelResource() {
        return "user_defined_variables"; // $NON-NLS-1$
    }

    /* Implements JMeterGUIComponent.createTestElement() */
    @Override
    public TestElement createTestElement() {
        Arguments args = new Arguments();
        modifyTestElement(args);
        return args;
    }

    /* Implements JMeterGUIComponent.modifyTestElement(TestElement) */
    @Override
    public void modifyTestElement(TestElement args) {
        GuiUtils.stopTableEditing(table);
        if (args instanceof Arguments) {
            Arguments arguments = (Arguments) args;
            arguments.clear();
            @SuppressWarnings("unchecked") // only contains Argument (or HTTPArgument)
                    Iterator<TarsParamArgument> modelData = (Iterator<TarsParamArgument>) tableModel.iterator();
            while (modelData.hasNext()) {
                TarsParamArgument arg = modelData.next();
                if (TextUtils.isEmpty(arg.getName()) && TextUtils.isEmpty(arg.getValue())) {
                    continue;
                }
                arg.setMetaData("="); // $NON-NLS-1$
                arguments.addArgument(arg);
            }
        }
        super.configureTestElement(args);
    }

    public int getArgumentNum() {
        return tableModel.getRowCount();
    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);
        if (el instanceof Arguments) {
            tableModel.clearData();
            for (JMeterProperty jMeterProperty : (Arguments) el) {
                TarsParamArgument arg = (TarsParamArgument) jMeterProperty.getObjectValue();
                tableModel.addRow(arg);
            }
        }
        checkButtonsStatus();
    }

    /**
     * Get the table used to enter arguments.
     *
     * @return the table used to enter arguments
     */
    protected JTable getTable() {
        return table;
    }

    /**
     * Get the title label for this component.
     *
     * @return the title label displayed with the table
     */
    protected JLabel getTableLabel() {
        return tableLabel;
    }

    /**
     * Get the button used to delete rows from the table.
     *
     * @return the button used to delete rows from the table
     */
    protected JButton getDeleteButton() {
        return delete;
    }

    /**
     * Get the button used to add rows to the table.
     *
     * @return the button used to add rows to the table
     */
    protected JButton getAddButton() {
        return add;
    }

    protected void checkButtonsStatus() {
        // Disable DELETE if there are no rows in the table to delete.
        if (tableModel.getRowCount() == 0) {
            delete.setEnabled(false);
            showDetail.setEnabled(false);
        } else {
            delete.setEnabled(true);
            showDetail.setEnabled(true);
        }
    }

    @Override
    public void clearGui() {
        super.clearGui();
        clear();
    }

    public void clear() {
        GuiUtils.stopTableEditing(table);
        tableModel.clearData();
        Arguments newArgs = new Arguments();
        for (Triple<String, String, String> triple : DEFAULT_ARGS) {
            newArgs.addArgument(new TarsParamArgument(triple.getLeft(), triple.getMiddle(), triple.getRight()));
        }
        // default the parameter.
        this.configure(newArgs);
    }

    /**
     * Invoked when an action occurs. This implementation supports the add and
     * delete buttons.
     *
     * @param e the event that has occurred
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        switch (action) {
            case DELETE:
                deleteArgument();
                break;
            case ADD:
                addArgument();
                break;
            case ADD_FROM_CLIPBOARD:
                addFromClipboard();
                break;
            case DETAIL:
                showDetail();
                break;
            case TRANSFORM:
                transformNameIntoVariable();
                break;
            case TARS2JSON:
                tars2json();
                break;
        }
    }

    /**
     * ensure that a row is visible in the viewport
     *
     * @param rowIndex row index
     */
    private void scrollToRowIfNotVisible(int rowIndex) {
        if (table.getParent() instanceof JViewport) {
            Rectangle visibleRect = table.getVisibleRect();
            final int cellIndex = 0;
            Rectangle cellRect = table.getCellRect(rowIndex, cellIndex, false);
            if (visibleRect.y > cellRect.y) {
                table.scrollRectToVisible(cellRect);
            } else {
                Rectangle rect2 = table.getCellRect(rowIndex + getNumberOfVisibleRows(table), cellIndex, true);
                int width = rect2.y - cellRect.y;
                table.scrollRectToVisible(new Rectangle(cellRect.x, cellRect.y, cellRect.width, cellRect.height + width));
            }
        }
    }

    /**
     * @param table {@link JTable}
     * @return number of visible rows
     */
    private static int getNumberOfVisibleRows(JTable table) {
        Rectangle vr = table.getVisibleRect();
        int first = table.rowAtPoint(vr.getLocation());
        vr.translate(0, vr.height);
        return table.rowAtPoint(vr.getLocation()) - first;
    }

    /**
     * Show Row Detail
     */
    private void showDetail() {
        //get the selected rows before stopping editing
        // or the selected will be unselected
        int[] rowsSelected = table.getSelectedRows();
        GuiUtils.stopTableEditing(table);

        if (rowsSelected.length == 1) {
            table.clearSelection();
            RowDetailDialog detailDialog = new RowDetailDialog(tableModel, rowsSelected[0]);
            detailDialog.setVisible(true);
        }
    }

    private void tars2json() {
        int[] rowsSelected = table.getSelectedRows();
        GuiUtils.stopTableEditing(table);

        if (rowsSelected.length == 1) {
            table.clearSelection();
            Tars2JsonDialog tars2JsonDialog = new Tars2JsonDialog(tableModel, rowsSelected[0]);
            tars2JsonDialog.setVisible(true);
        }
    }

    /**
     * Remove the currently selected argument from the table.
     */
    protected void deleteArgument() {
        GuiUtils.cancelEditing(table);

        int[] rowsSelected = table.getSelectedRows();
        int anchorSelection = table.getSelectionModel().getAnchorSelectionIndex();
        table.clearSelection();
        if (rowsSelected.length > 0) {
            for (int i = rowsSelected.length - 1; i >= 0; i--) {
                tableModel.removeRow(rowsSelected[i]);
            }

            // Table still contains one or more rows, so highlight (select)
            // the appropriate one.
            if (tableModel.getRowCount() > 0) {
                if (anchorSelection >= tableModel.getRowCount()) {
                    anchorSelection = tableModel.getRowCount() - 1;
                }
                table.setRowSelectionInterval(anchorSelection, anchorSelection);
            }

            checkButtonsStatus();
        }
    }

    /**
     * Add a new argument row to the table.
     */
    protected void addArgument() {
        // If a table cell is being edited, we should accept the current value
        // and stop the editing before adding a new row.
        GuiUtils.stopTableEditing(table);

        tableModel.addRow(makeNewArgument());

        checkButtonsStatus();

        // Highlight (select) and scroll to the appropriate row.
        int rowToSelect = tableModel.getRowCount() - 1;
        table.setRowSelectionInterval(rowToSelect, rowToSelect);
        table.scrollRectToVisible(table.getCellRect(rowToSelect, 0, true));

    }

    protected void addFromClipboard(String lineDelimiter, String argDelimiter) {
        GuiUtils.stopTableEditing(table);
        int rowCount = table.getRowCount();
        try {
            String clipboardContent = GuiUtils.getPastedText();
            if (clipboardContent == null) {
                return;
            }
            String[] clipboardLines = clipboardContent.split(lineDelimiter);
            for (String clipboardLine : clipboardLines) {
                String[] clipboardCols = clipboardLine.split(argDelimiter);
                if (clipboardCols.length > 0) {
                    Argument argument = createArgumentFromClipboard(clipboardCols);
                    tableModel.addRow(argument);
                }
            }
            if (table.getRowCount() > rowCount) {
                checkButtonsStatus();

                // Highlight (select) and scroll to the appropriate rows.
                int rowToSelect = tableModel.getRowCount() - 1;
                table.setRowSelectionInterval(rowCount, rowToSelect);
                table.scrollRectToVisible(table.getCellRect(rowCount, 0, true));
            }
        } catch (IOException ioe) {
            JOptionPane.showMessageDialog(this,
                    "Could not add read arguments from clipboard:\n" + ioe.getLocalizedMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (UnsupportedFlavorException ufe) {
            JOptionPane.showMessageDialog(this,
                    "Could not add retrieve " + DataFlavor.stringFlavor.getHumanPresentableName()
                            + " from clipboard" + ufe.getLocalizedMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    protected void addFromClipboard() {
        addFromClipboard(CLIPBOARD_LINE_DELIMITERS, CLIPBOARD_ARG_DELIMITERS);
    }

    protected Argument createArgumentFromClipboard(String[] clipboardCols) {
        Argument argument = makeNewArgument();
        argument.setName(clipboardCols[0]);
        if (clipboardCols.length > 1) {
            argument.setValue(clipboardCols[1]);
            if (clipboardCols.length > 2) {
                argument.setDescription(clipboardCols[2]);
            }
        }
        return argument;
    }

    /**
     * Create a new Argument object.
     *
     * @return a new Argument object
     */
    protected Argument makeNewArgument() {
        String name = "param_" + tableModel.getRowCount();
        return new TarsParamArgument(name, "", JsonConst.TARS); // $NON-NLS-1$ // $NON-NLS-2$
    }

    protected void stopTableEditing() {
        GuiUtils.stopTableEditing(table);
    }

    /**
     * Initialize the table model used for the arguments table.
     */
    protected void initializeTableModel() {
        if (tableModel == null) {
            tableModel = new ObjectTableModel(new String[]{NAME, VALUE, TYPE},
                    TarsParamArgument.class,
                    new Functor[]{
                            new Functor("getName"), // $NON-NLS-1$
                            new Functor("getValue"),
                            new Functor("getType")},  // $NON-NLS-1$
                    new Functor[]{
                            new Functor("setName"), // $NON-NLS-1$
                            new Functor("setValue"), // $NON-NLS-1$
                            new Functor("setType")},  // $NON-NLS-1$
                    new Class[]{String.class, String.class, String.class});
        }
    }

    /**
     * Resize the table columns to appropriate widths.
     *
     * @param _table the table to resize columns for
     */
    protected void sizeColumns(JTable _table) {
        TableColumn firstColumn = _table.getColumnModel().getColumn(0);
        TableColumn middleColumn = _table.getColumnModel().getColumn(1);
        TableColumn lastColumn = _table.getColumnModel().getColumn(2);
        firstColumn.setPreferredWidth(150);
        firstColumn.setMinWidth(150);
        firstColumn.setMaxWidth(150);
        middleColumn.setPreferredWidth(800);
        middleColumn.setMinWidth(200);
        lastColumn.setPreferredWidth(100);
        lastColumn.setMaxWidth(100);
        lastColumn.setMinWidth(100);
        _table.setRowHeight(30);
    }

    /**
     * Create the main GUI panel which contains the argument table.
     *
     * @return the main GUI panel
     */
    private JComponent makeMainPanel() {
        initializeTableModel();
        table = new JTable(tableModel);
        table.getTableHeader().setDefaultRenderer(new HeaderAsPropertyRenderer());
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        if (this.background != null) {
            table.setBackground(this.background);
        }
        JMeterUtils.applyHiDPI(table);
        return makeScrollPane(table);
    }

    /**
     * Create a panel containing the title label for the table.
     *
     * @return a panel containing the title label
     */
    protected Component makeLabelPanel() {
        JPanel labelPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        labelPanel.add(tableLabel);
        if (this.background != null) {
            labelPanel.setBackground(this.background);
        }
        return labelPanel;
    }

    private void makeMenuItem() {
        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem variabilizeItem = new JMenuItem(CustomResUtils.getResString("tars.mouse.transform.into.variable"));
        JMenuItem detailItem = new JMenuItem(CustomResUtils.getResString("tars.mouse.detail.edit"));
        variabilizeItem.setActionCommand(TRANSFORM);
        variabilizeItem.setEnabled(true);
        detailItem.setActionCommand(DETAIL);
        detailItem.setEnabled(true);
        variabilizeItem.addActionListener(this);
        detailItem.addActionListener(this);
        popupMenu.add(variabilizeItem);
        popupMenu.add(detailItem);
        table.setComponentPopupMenu(popupMenu);
    }

    /**
     * replace the argument value of the selection with a variable
     * the variable name is derived from the parameter name
     */
    private void transformNameIntoVariable() {
        int[] rowsSelected = getTable().getSelectedRows();
        for (int selectedRow : rowsSelected) {
            String name = (String) tableModel.getValueAt(selectedRow, 0);
            if (name != null && !name.trim().isEmpty()) {
                name = name.trim();
                name = name.replaceAll("\\$", "_");
                name = name.replaceAll("\\{", "_");
                name = name.replaceAll("}", "_");
                tableModel.setValueAt("${" + name + "}", selectedRow, 1);
            }
        }
    }

    /**
     * Create a panel containing the add and delete buttons.
     *
     * @return a GUI panel containing the buttons
     */
    private JPanel makeButtonPanel() {

        showDetail = new JButton(JMeterUtils.getResString("detail")); // $NON-NLS-1$
        showDetail.setActionCommand(DETAIL);
        showDetail.setEnabled(true);

        add = new JButton(JMeterUtils.getResString("add")); // $NON-NLS-1$
        add.setActionCommand(ADD);
        add.setEnabled(true);

        // A button for adding new arguments to the table from the clipboard
        JButton addFromClipboard = new JButton(JMeterUtils.getResString("add_from_clipboard")); // $NON-NLS-1$
        addFromClipboard.setActionCommand(ADD_FROM_CLIPBOARD);
        addFromClipboard.setEnabled(true);

        delete = new JButton(JMeterUtils.getResString("delete")); // $NON-NLS-1$
        delete.setActionCommand(DELETE);

        JButton loadFormTars = new JButton(CustomResUtils.getResString("tars.tars2json.translate"));
        loadFormTars.setActionCommand(TARS2JSON);
        loadFormTars.setEnabled(true);
        checkButtonsStatus();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        if (this.background != null) {
            buttonPanel.setBackground(this.background);
        }
        showDetail.addActionListener(this);
        add.addActionListener(this);
        addFromClipboard.addActionListener(this);
        delete.addActionListener(this);
        loadFormTars.addActionListener(this);
        buttonPanel.add(showDetail);
        buttonPanel.add(add);
        buttonPanel.add(addFromClipboard);
        buttonPanel.add(delete);
        buttonPanel.add(loadFormTars);
        return buttonPanel;
    }

    /**
     * Initialize the components and layout of this component.
     */
    private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
        JPanel p = this;

        p.setLayout(new BorderLayout());
        p.add(makeLabelPanel(), BorderLayout.NORTH);
        mainPanel = makeMainPanel();
        p.add(mainPanel, BorderLayout.CENTER);
        // Force a minimum table height of 70 pixels
        p.add(Box.createVerticalStrut(70), BorderLayout.WEST);
        p.add(makeButtonPanel(), BorderLayout.SOUTH);
        table.revalidate();
        JTypeComboBox typeComboBox = new JTypeComboBox();
        DefaultCellEditor cellEditor = new DefaultCellEditor(typeComboBox);
        table.getColumn(TYPE).setCellEditor(cellEditor);
        sizeColumns(table);
        makeMenuItem();
    }

    /**
     * Clear border around "table with arguments".
     * Extra border is not required when the panel is already surrounded by a border.
     */
    public void clearBorderForMainPanel() {
        GuiUtils.emptyBorder(mainPanel);
    }
}
