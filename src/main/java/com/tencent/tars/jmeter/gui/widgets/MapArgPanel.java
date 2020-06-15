package com.tencent.tars.jmeter.gui.widgets;

import org.apache.jmeter.config.Arguments;

/**
 * 用于添加 Tup Tars RequestPacket 中的 map context和 map status
 */
public class MapArgPanel extends ArgumentsPanelBase {

    public MapArgPanel(String label) {
        super(label,false);
        init();
        clearBorderForMainPanel();
    }

    @Override
    protected Arguments getDefaultArguments() {
        return new Arguments();
    }
}
