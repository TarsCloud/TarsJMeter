package com.tencent.tars.jmeter.gui.widgets;

import com.tencent.tars.jmeter.gui.CustomResUtils;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.config.gui.ArgumentsPanel;
import org.apache.jmeter.config.gui.RowDetailDialog;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.property.JMeterProperty;
import org.apache.jorphan.gui.GuiUtils;
import org.apache.jorphan.gui.ObjectTableModel;
import org.apache.jorphan.reflect.Functor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.Iterator;

public abstract class ArgumentsPanelBase extends ArgumentsPanel {
    private static final Logger log = LoggerFactory.getLogger(ArgumentsPanelBase.class);
    private static final long serialVersionUID = 1L;

    protected static int NAME_COL = 0;
    protected static int VALUE_COL = 1;

    /**
     * When pasting from the clipboard, split lines on linebreak or '&'
     */
    private static final String CLIPBOARD_LINE_DELIMITERS = "\n|&"; //$NON-NLS-1$

    /**
     * When pasting from the clipboard, split parameters on tab or '='
     */
    private static final String CLIPBOARD_ARG_DELIMITERS = "\t|="; //$NON-NLS-1$


    public ArgumentsPanelBase(String label) {
        this(label,true);
    }

    public ArgumentsPanelBase(String label,boolean disableButton) {
        super(label, null, false, false, null, disableButton);
    }


    @Override
    protected void initializeTableModel() {
        if (tableModel == null) {
            tableModel = new ObjectTableModel(new String[]{COLUMN_RESOURCE_NAMES_0, COLUMN_RESOURCE_NAMES_1},
                    Argument.class,
                    new Functor[]{
                            new Functor("getName"), // $NON-NLS-1$
                            new Functor("getValue")},  // $NON-NLS-1$\
                    new Functor[]{
                            new Functor("setName"), // $NON-NLS-1$
                            new Functor("setValue")}, // $NON-NLS-1$
                    new Class[]{String.class, String.class});
        }
    }


    @Override
    public TestElement createTestElement() {
        log.info("createTestElement");
        Arguments args = getUnclonedParameters();
        super.configureTestElement(args);
        return (TestElement) args.clone();
    }


    protected abstract Arguments getDefaultArguments();

    private Arguments getUnclonedParameters() {
        stopTableEditing();
        @SuppressWarnings("unchecked")
        Iterator<Argument> modelData = (Iterator<Argument>) tableModel.iterator();
        Arguments args = new Arguments();
        while (modelData.hasNext()) {
            Argument arg = modelData.next();
            args.addArgument(arg);
        }
        return args;
    }

    @Override
    public void configure(TestElement el) {
        super.configure(el);
        if (el instanceof Arguments) {
            tableModel.clearData();
            for (JMeterProperty jMeterProperty : ((Arguments) el).getArguments()) {
                Argument arg = (Argument) jMeterProperty.getObjectValue();
                tableModel.addRow(arg);
            }
        }
        checkButtonsStatus();
    }

    @Override
    protected void addFromClipboard() {
        addFromClipboard(CLIPBOARD_LINE_DELIMITERS, CLIPBOARD_ARG_DELIMITERS);
    }


    protected void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
        // register the right click menu
        JTable table = getTable();
        final JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem variabilizeItem = new JMenuItem(CustomResUtils.getResString("tars.mouse.transform.into.variable"));
        JMenuItem detailItem = new JMenuItem(CustomResUtils.getResString("tars.mouse.detail.edit"));
        variabilizeItem.addActionListener(e -> transformNameIntoVariable());
        detailItem.addActionListener(e -> showDetail());
        popupMenu.add(variabilizeItem);
        popupMenu.add(detailItem);
        table.setComponentPopupMenu(popupMenu);
    }

    @Override
    public void clear() {
        super.clear();
        this.configure(getDefaultArguments());
    }

    private void showDetail() {
        int[] rowsSelected = getTable().getSelectedRows();
        GuiUtils.stopTableEditing(getTable());
        if (rowsSelected.length == 1) {
            getTable().clearSelection();
            RowDetailDialog detailDialog = new RowDetailDialog(tableModel, rowsSelected[0]);
            detailDialog.setVisible(true);
        }
    }

    @Override
    protected void sizeColumns(JTable _table) {
        _table.setRowHeight(30);
    }

    /**
     * replace the argument value of the selection with a variable
     * the variable name is derived from the parameter name
     */
    private void transformNameIntoVariable() {
        int[] rowsSelected = getTable().getSelectedRows();
        for (int selectedRow : rowsSelected) {
            String name = (String) tableModel.getValueAt(selectedRow, NAME_COL);
            if (name != null && !name.trim().isEmpty()) {
                name = name.trim();
                name = name.replaceAll("\\$", "_");
                name = name.replaceAll("\\{", "_");
                name = name.replaceAll("\\}", "_");
                tableModel.setValueAt("${" + name + "}", selectedRow, VALUE_COL);
            }
        }
    }
}
