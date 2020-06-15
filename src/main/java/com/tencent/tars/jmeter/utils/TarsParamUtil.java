package com.tencent.tars.jmeter.utils;

import com.google.gson.JsonObject;
import com.tencent.tars.protocol.JsonConst;

import static com.tencent.tars.protocol.json.JsonStreamUtil.*;

public class TarsParamUtil {

    public static JsonObject getValueByType(String type) {
        switch (type) {
            case JsonConst.BOOLEAN:
                return getBoolValue(false);
            case JsonConst.BYTE:
                return getByteValue((byte) 0);
            case JsonConst.DOUBLE:
                return getDoubleValue(0.0);
            case JsonConst.FLOAT:
                return getFloatValue(0.0f);
            case JsonConst.SHORT:
                return getShortValue((short) 0);
            case JsonConst.STRING:
                return getStringValue("");
            case JsonConst.INT:
                return getIntValue(0);
            case JsonConst.LONG:
                return getLongValue(0);
            case JsonConst.BOOLEAN_VEC:
                return getBoolVectorValue(new boolean[]{false});
            case JsonConst.BYTE_VEC:
                return getByteVectorValue(new byte[]{(byte) 0});
            case JsonConst.DOUBLE_VEC:
                return getDoubleVectorValue(new double[]{0});
            case JsonConst.FLOAT_VEC:
                return getFloatVectorValue(new float[]{0});
            case JsonConst.SHORT_VEC:
                return getShortVectorValue(new short[]{0});
            case JsonConst.STRING_VEC:
                return getStringVectorValue(new String[]{""});
            case JsonConst.INT_VEC:
                return getIntVectorValue(new int[]{0});
            case JsonConst.LONG_VEC:
                return getLongVectorValue(new long[]{0});
        }
        return null;
    }
}
