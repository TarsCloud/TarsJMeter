package com.tencent.tars.jmeter.utils;

import com.tencent.tars.protocol.JsonConst;
import com.tencent.tars.protocol.json.JsonStreamUtil;
import org.apache.jmeter.config.Argument;
import org.apache.jmeter.testelement.property.StringProperty;

import java.io.Serializable;

public class TarsParamArgument extends Argument implements Serializable {

    private static final String TYPE = "TarsParamArgument.type";


    public TarsParamArgument(){
    }

    public TarsParamArgument(String name, String value, String type) {
        super(name, value);
        setMetaData("=");
        setType(type);
    }

    public void setType(String type) {
        if (getType().equals(type)) {
            return;
        } else if (type.equals(JsonConst.TARS)) {
            setValue("");
        } else {
            setValue(JsonStreamUtil.toPrettyFormat(TarsParamUtil.getValueByType(type)));
        }
        setProperty(new StringProperty(TYPE, type));
    }

    public String getType() {
        return getPropertyAsString(TYPE, JsonConst.TARS);
    }


}
