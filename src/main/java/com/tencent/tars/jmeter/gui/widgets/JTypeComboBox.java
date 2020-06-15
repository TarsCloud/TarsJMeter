package com.tencent.tars.jmeter.gui.widgets;

import com.tencent.tars.protocol.JsonConst;

import javax.swing.*;

public class JTypeComboBox extends JComboBox<String> {
    public JTypeComboBox() {
        this(false);
    }

    public JTypeComboBox(boolean isReturnValue){
        super();
        this.addItem(JsonConst.TARS);
        this.addItem(JsonConst.STRING);
        this.addItem(JsonConst.INT);
        this.addItem(JsonConst.BOOLEAN);
        this.addItem(JsonConst.LONG);
        this.addItem(JsonConst.SHORT);
        this.addItem(JsonConst.DOUBLE);
        this.addItem(JsonConst.FLOAT);
        this.addItem(JsonConst.BYTE);
        this.addItem(JsonConst.VOID);
        this.addItem(JsonConst.STRING_VEC);
        this.addItem(JsonConst.INT_VEC);
        this.addItem(JsonConst.BOOLEAN_VEC);
        this.addItem(JsonConst.LONG_VEC);
        this.addItem(JsonConst.SHORT_VEC);
        this.addItem(JsonConst.DOUBLE_VEC);
        this.addItem(JsonConst.FLOAT_VEC);
        this.addItem(JsonConst.BYTE_VEC);
        this.addItem(JsonConst.MAP);
        if(isReturnValue){
            this.setSelectedItem(JsonConst.INT);
        }
    }
}
