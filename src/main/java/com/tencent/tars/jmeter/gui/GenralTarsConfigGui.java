package com.tencent.tars.jmeter.gui;

import com.tencent.tars.jmeter.gui.widgets.JTypeComboBox;
import com.tencent.tars.jmeter.gui.widgets.ExtraArgumentsPanel;
import com.tencent.tars.jmeter.gui.widgets.FuncParameterPanel;
import com.tencent.tars.jmeter.gui.widgets.MapArgPanel;
import com.tencent.tars.jmeter.sampler.TarsSamplerBase;
import com.tencent.tars.jmeter.constants.ITarsConst;
import com.tencent.tars.protocol.JsonConst;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.gui.util.HorizontalPanel;
import org.apache.jmeter.gui.util.VerticalPanel;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jorphan.gui.JLabeledTextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * JMeter Tars协议测试 采样器UI
 *
 * @author brookechen
 */
public class GenralTarsConfigGui extends JPanel {
    private static final Logger log = LoggerFactory.getLogger(GenralTarsConfigGui.class);

    private static final int TAB_TARS_PARAMETERS = 0;
    private static final int TAB_EXTRA_PARAMETERS = 1;

    private JLabeledTextField ip;
    private JLabeledTextField port;
    private JLabeledTextField servantName;
    private JLabeledTextField funcName;

    private JTypeComboBox retTypeComboBox;

    private JLabeledTextField returnValueView;

    private JTabbedPane contentTabbedPane;
    // Body request data
    private FuncParameterPanel requestParameterPanel;
    private ExtraArgumentsPanel tarsArgsPanel;

    private MapArgPanel contextArgsPanel;
    private MapArgPanel statusArgPanel;

    public GenralTarsConfigGui() {
        init();
    }


    protected final JPanel getHeaderPanel(){
        JPanel headerPane = new VerticalPanel();
        headerPane.add(getAddressingPanel());
        headerPane.add(getTarsServantPanel());
        headerPane.add(getReturnValuePanel());
        return headerPane;
    }

    //寻址
    protected final JPanel getAddressingPanel(){
        ip = new JLabeledTextField(CustomResUtils.getResString("tars.addressing.ip"),20);
        port = new JLabeledTextField(CustomResUtils.getResString("tars.addressing.port"),10);
        JPanel addressingPane = new HorizontalPanel();
        addressingPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder())); // $NON-NLS-1$
        addressingPane.add(ip);
        addressingPane.add(port);
        return addressingPane;
    }

    //服务名
    protected final JPanel getTarsServantPanel() {
        servantName = new JLabeledTextField(CustomResUtils.getResString("tars.servant.path"), 20); // $NON-NLS-1$
        funcName = new JLabeledTextField(CustomResUtils.getResString("tars.func.name"), 10); // $NON-NLS-1$
        JPanel tarsPanel = new HorizontalPanel();
        tarsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder())); // $NON-NLS-1$
        tarsPanel.add(servantName);
        tarsPanel.add(funcName);
        return tarsPanel;
    }

    protected final JPanel getReturnValuePanel() {
        JLabel label = new JLabel(CustomResUtils.getResString("tars.retval.type"));
        retTypeComboBox = new JTypeComboBox(true); // $NON-NLS-1$
        returnValueView = new JLabeledTextField(CustomResUtils.getResString("tars.retval.content"), 30); // $NON-NLS-1$
        returnValueView.setVisible(false);
        retTypeComboBox.addItemListener(e -> {
            if(e.getStateChange() != ItemEvent.SELECTED){
                return;
            }
            if(JsonConst.TARS.equals(e.getItem().toString())){
                returnValueView.setVisible(true);
            }else{
                returnValueView.setVisible(false);
                returnValueView.setText("");
            }
        });
        JPanel tarsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        tarsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder())); // $NON-NLS-1$
        tarsPanel.add(retTypeComboBox,FlowLayout.LEFT);
        tarsPanel.add(label,FlowLayout.LEFT);
        tarsPanel.add(returnValueView,FlowLayout.RIGHT);
        return tarsPanel;
    }

    protected final JTabbedPane getContentPanel() {
        contentTabbedPane = new JTabbedPane();
        tarsArgsPanel = new ExtraArgumentsPanel();
        requestParameterPanel = new FuncParameterPanel(CustomResUtils.getResString("tars.request.parameter"));
        contentTabbedPane.add(CustomResUtils.getResString("tars.request.parameter"), requestParameterPanel);// $NON-NLS-1$
        contentTabbedPane.add(CustomResUtils.getResString("tars.param.extra.title"), tarsArgsPanel);// $NON-NLS-1$

        contextArgsPanel = new MapArgPanel(CustomResUtils.getResString("tars.tup.req.context"));
        statusArgPanel = new MapArgPanel(CustomResUtils.getResString("tars.tup.req.status"));
        contentTabbedPane.add(CustomResUtils.getResString("tars.tup.req.context"), contextArgsPanel);// $NON-NLS-1$
        contentTabbedPane.add(CustomResUtils.getResString("tars.tup.req.status"), statusArgPanel);// $NON-NLS-1$

        contentTabbedPane.setSelectedIndex(TAB_TARS_PARAMETERS);
        return contentTabbedPane;
    }

    private void init() {
        this.setLayout(new BorderLayout());
        JPanel sharkRequestPanel = new VerticalPanel();
        sharkRequestPanel.setLayout(new BorderLayout());
        sharkRequestPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),
                CustomResUtils.getResString("tars.header.title"))); // $NON-NLS-1$
        sharkRequestPanel.add(getHeaderPanel(), BorderLayout.NORTH);
        sharkRequestPanel.add(getContentPanel(), BorderLayout.CENTER);
        this.add(sharkRequestPanel, BorderLayout.CENTER);
    }

    /**
     * Set the text, etc. in the UI.
     */
    public void configure(TestElement el) {
        setName(el.getName());
        ip.setText(el.getPropertyAsString(ITarsConst.SERVANT_IP));
        port.setText(el.getPropertyAsString(ITarsConst.SERVANT_PORT));
        servantName.setText(el.getPropertyAsString(ITarsConst.SERVANT_PATH));
        funcName.setText(el.getPropertyAsString(ITarsConst.FUNC_NAME));
        retTypeComboBox.setSelectedItem(el.getPropertyAsString(ITarsConst.TARS_RETURN_TYPE));
        returnValueView.setText(el.getPropertyAsString(ITarsConst.TARS_RETURN_VALUE));
        Arguments funcParam = (Arguments) el.getProperty(ITarsConst.FUNC_ARGUMENTS).getObjectValue();
        if (funcParam != null) {
            requestParameterPanel.configure(funcParam);
        }
        Arguments arguments = (Arguments) el.getProperty(ITarsConst.ARGUMENTS).getObjectValue();
        if (arguments != null) {
            tarsArgsPanel.configure(arguments);
        }
        Arguments contextArgs = (Arguments) el.getProperty(ITarsConst.CONTEXT_ARGUMENTS).getObjectValue();
        if (contextArgs != null) {
            contextArgsPanel.configure(contextArgs);
        }
        Arguments statusArgs = (Arguments) el.getProperty(ITarsConst.STATUS_ARGUMENTS).getObjectValue();
        if (statusArgs != null) {
            statusArgPanel.configure(statusArgs);
        }
    }

    /**
     * Save the GUI values in the sampler.
     */
    public void modifyTestElement(TestElement element) {
        TarsSamplerBase sampler = (TarsSamplerBase) element;
        sampler.setServantIp(ip.getText());
        sampler.setServantPort(port.getText());
        sampler.setServantPath(servantName.getText());
        sampler.setFuncName(funcName.getText());
        if(retTypeComboBox.getSelectedItem() != null){
            sampler.setReturnType(retTypeComboBox.getSelectedItem().toString());
            sampler.setReturnValue(returnValueView.getText());
        }

        Arguments args = (Arguments) tarsArgsPanel.createTestElement();
        sampler.setArguments(args);
        Arguments dataArgs = (Arguments) requestParameterPanel.createTestElement();
        sampler.setDataArguments(dataArgs);
        Arguments contextArgs = (Arguments) contextArgsPanel.createTestElement();
        sampler.setContextArguments(contextArgs);
        Arguments statusArgs = (Arguments) statusArgPanel.createTestElement();
        sampler.setStatusArguments(statusArgs);
    }


    public void clear() {
        servantName.setText("");
        funcName.setText("");
        retTypeComboBox.setSelectedItem(JsonConst.INT);
        returnValueView.setText("");
        tarsArgsPanel.clear();
        requestParameterPanel.clear();
        contextArgsPanel.clear();
        statusArgPanel.clear();
        contentTabbedPane.setSelectedIndex(TAB_TARS_PARAMETERS);
    }
}
