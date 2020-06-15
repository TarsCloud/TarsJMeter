package com.tencent.tars.jmeter.gui.widgets;

import org.apache.jmeter.gui.action.KeyStrokes;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.ComponentUtil;
import org.apache.jorphan.gui.ObjectTableModel;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Tars2JsonDialog  extends JDialog implements ActionListener, DocumentListener {

    /** Command for CANCEL. */
    private static final String CLOSE = "close"; // $NON-NLS-1$

    private static final String UPDATE = "update"; // $NON-NLS-1$

    private JTextField nameTF;

    private JSyntaxTextArea valueTA;

    private JButton closeButton;

    private ObjectTableModel tableModel;

    private int selectedRow;

    private boolean textChanged = true; // change to false after the first insert

    private boolean updateToTable;


    public Tars2JsonDialog() {
        super((JFrame) null, "tars2json", true); //$NON-NLS-1$
        updateToTable = false;
    }

    public Tars2JsonDialog(ObjectTableModel tableModel, int selectedRow) {
        super((JFrame) null, "tars2json", true); //$NON-NLS-1$
        updateToTable = true;
        this.tableModel = tableModel;
        this.selectedRow = selectedRow;
        init();
    }

    @Override
    protected JRootPane createRootPane() {
        JRootPane rootPane = new JRootPane();
        // Hide Window on ESC
        Action escapeAction = new AbstractAction("ESCAPE") {

            private static final long serialVersionUID = -8699034338969407625L;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setVisible(false);
            }
        };
        // Do update on Enter
        Action enterAction = new AbstractAction("ENTER") {

            private static final long serialVersionUID = -1529005452976176873L;

            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                doUpdate(actionEvent);
                setVisible(false);
            }
        };
        ActionMap actionMap = rootPane.getActionMap();
        actionMap.put(escapeAction.getValue(Action.NAME), escapeAction);
        actionMap.put(enterAction.getValue(Action.NAME), enterAction);
        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        inputMap.put(KeyStrokes.ESC, escapeAction.getValue(Action.NAME));
        inputMap.put(KeyStrokes.ENTER, enterAction.getValue(Action.NAME));
        return rootPane;
    }

    private void init() { // WARNING: called from ctor so must not be overridden (i.e. must be private or final)
        this.getContentPane().setLayout(new BorderLayout(10,10));

        JLabel nameLabel = new JLabel(JMeterUtils.getResString("name")); //$NON-NLS-1$
        nameTF = new JTextField(JMeterUtils.getResString("name"), 20); //$NON-NLS-1$
        nameTF.getDocument().addDocumentListener(this);
        JPanel namePane = new JPanel(new BorderLayout());
        namePane.add(nameLabel, BorderLayout.WEST);
        namePane.add(nameTF, BorderLayout.CENTER);

        JLabel valueLabel = new JLabel(JMeterUtils.getResString("value")); //$NON-NLS-1$
        valueTA = JSyntaxTextArea.getInstance(30, 80);
        valueTA.getDocument().addDocumentListener(this);
        setValues(selectedRow);
        JPanel valuePane = new JPanel(new BorderLayout());
        valuePane.add(valueLabel, BorderLayout.NORTH);
        JTextScrollPane jTextScrollPane = JTextScrollPane.getInstance(valueTA);
        valuePane.add(jTextScrollPane, BorderLayout.CENTER);

        JPanel detailPanel = new JPanel(new BorderLayout());
        detailPanel.add(namePane, BorderLayout.NORTH);

        detailPanel.add(valuePane, BorderLayout.CENTER);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(7, 3, 3, 3));
        mainPanel.add(detailPanel, BorderLayout.CENTER);

        TarsStructPanel loadPane = new TarsStructPanel(valueTA);
        mainPanel.add(loadPane,BorderLayout.SOUTH);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton updateButton = new JButton(JMeterUtils.getResString("update")); //$NON-NLS-1$
        if(updateToTable){
            updateButton.setActionCommand(UPDATE);
            updateButton.addActionListener(this);
        }
        closeButton = new JButton(JMeterUtils.getResString("close")); //$NON-NLS-1$
        closeButton.setActionCommand(CLOSE);
        closeButton.addActionListener(this);

        if(updateToTable){
            buttonsPanel.add(updateButton);
        }
        buttonsPanel.add(closeButton);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);

        this.getContentPane().add(mainPanel);
        nameTF.requestFocusInWindow();

        this.pack();
        ComponentUtil.centerComponentInWindow(this);
    }

    /**
     * Do search
     * @param e {@link ActionEvent}
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(CLOSE)) {
            this.setVisible(false);
        } else if (action.equals(UPDATE) && updateToTable) {
            doUpdate(e);
        }
    }


    /**
     * Set TextField and TA values from model
     * @param selectedRow Selected row
     */
    private void setValues(int selectedRow) {
        nameTF.setText((String)tableModel.getValueAt(selectedRow, 0));
        valueTA.setInitialText((String)tableModel.getValueAt(selectedRow, 1));
        valueTA.setCaretPosition(0);
        textChanged = false;
    }

    /**
     * Update model values
     * @param actionEvent the event that led to this call
     */
    protected void doUpdate(ActionEvent actionEvent) {
        tableModel.setValueAt(nameTF.getText(), selectedRow, 0);
        tableModel.setValueAt(valueTA.getText(), selectedRow, 1);
        // Change Cancel label to Close
        closeButton.setText(JMeterUtils.getResString("close")); //$NON-NLS-1$
        textChanged = false;
    }

    /**
     * Change the label of Close button to Cancel (after the first text changes)
     */
    private void changeLabelButton() {
        if (!textChanged) {
            closeButton.setText(JMeterUtils.getResString("cancel")); //$NON-NLS-1$
            textChanged = true;
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        changeLabelButton();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        changeLabelButton();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        changeLabelButton();
    }
}
