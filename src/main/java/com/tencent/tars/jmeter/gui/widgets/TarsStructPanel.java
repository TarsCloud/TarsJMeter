package com.tencent.tars.jmeter.gui.widgets;

import com.tencent.tars.jmeter.gui.CustomResUtils;
import com.tencent.tars.protocol.Tars2JsonMojo;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.gui.JLabeledTextField;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class TarsStructPanel extends JPanel implements ActionListener {

    private static final String LOAD = "load";

    private static final String FILE_CHOOSE = "choose";


    // tars名称
    private JLabeledTextField tarsStructName;

    // tars文件所在路径
    private JLabeledTextField tarsFileDirTV;

    //弹出文件夹选择对话框
    private JButton dirChooseButton;

    private JSyntaxTextArea valueTA;


    public TarsStructPanel(JSyntaxTextArea valueTA) {
        super();
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        this.valueTA = valueTA;
        tarsStructName = new JLabeledTextField(CustomResUtils.getResString("tars.tars2json.struct.name"), 5);
        tarsFileDirTV = new JLabeledTextField(CustomResUtils.getResString("tars.tars2json.path"), 25);
        dirChooseButton = new JButton(CustomResUtils.getResString("tars.tars2json.browse"));
        dirChooseButton.setActionCommand(FILE_CHOOSE);
        dirChooseButton.addActionListener(this);
        JButton loadButton = new JButton(CustomResUtils.getResString("tars.tars2json.translate"));
        loadButton.setActionCommand(LOAD);
        loadButton.addActionListener(this);
        this.add(tarsStructName, BorderLayout.WEST);
        this.add(tarsFileDirTV, BorderLayout.CENTER);
        this.add(dirChooseButton, BorderLayout.EAST);
        this.add(loadButton, BorderLayout.EAST);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        if (action.equals(LOAD)) {
            doLoad();
        } else if (action.equals(FILE_CHOOSE)) {
            doFileChoose();
        }
    }

    private void doFileChoose() {
        JFileChooser chooser = new JFileChooser(".");
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                String name = f.getName();
                return f.isDirectory() || name.toLowerCase().endsWith(".tars") || name.toLowerCase().endsWith(".jce");
            }

            @Override
            public String getDescription() {
                return "*.tars;*.jce;dir include tars or jce";
            }
        });
        chooser.setMultiSelectionEnabled(false);
        int returnVal = chooser.showOpenDialog(dirChooseButton);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String filepath = chooser.getSelectedFile().getAbsolutePath();
            tarsFileDirTV.setText(filepath);
        }
    }

    private void doLoad() {
        new Thread(() -> {
            String text = Tars2JsonMojo.getJsonStr(tarsStructName.getText(), tarsFileDirTV.getText());
            SwingUtilities.invokeLater(() -> valueTA.setText(text));
        }).start();
    }
}
