package com.tencent.tars.jmeter.gui;

import com.tencent.tars.jmeter.sampler.TarsSamplerProxy;
import org.apache.jmeter.gui.GUIMenuSortOrder;
import org.apache.jmeter.samplers.gui.AbstractSamplerGui;
import org.apache.jmeter.testelement.TestElement;

import javax.swing.*;
import java.awt.*;

/**
 * @author brookechen
 */
@GUIMenuSortOrder(1)
public class GeneralTarsSamplerGui extends AbstractSamplerGui {

    private GenralTarsConfigGui tarsConfigGui;

    public GeneralTarsSamplerGui() {
        super();
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        tarsConfigGui = new GenralTarsConfigGui();
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, makeTitlePanel(), tarsConfigGui);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setOneTouchExpandable(true);
        add(splitPane);
    }


    @Override
    public String getLabelResource() {
        return null;
    }

    @Override
    public String getStaticLabel() {
        return CustomResUtils.getResString("tars.title.general");
    }

    @Override
    public TestElement createTestElement() {
        TarsSamplerProxy sampler = new TarsSamplerProxy();
        modifyTestElement(sampler);
        return sampler;
    }

    @Override
    public void modifyTestElement(TestElement sampler) {
        sampler.clear();
        tarsConfigGui.modifyTestElement(sampler);
        super.configureTestElement(sampler);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(TestElement element) {
        super.configure(element);
        tarsConfigGui.configure(element);
    }

    @Override
    public void clearGui() {
        super.clearGui();
        tarsConfigGui.clear();
    }
}
