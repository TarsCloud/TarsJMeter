package com.tencent.tars.protocol.json;

import com.google.gson.*;
import com.tencent.tars.protocol.JsonConst;
import com.tencent.tars.protocol.exceptions.JsonDecodeException;
import com.tencent.tars.protocol.TarsStructUtil;
import com.tencent.tars.protocol.tars.TarsStructBase;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

/**
 * @author brookechen
 */
public class JsonStreamUtil {
    /**
     * 把JceStruct按模板录制成json数据
     * @param base 数据对象
     * @param template  json模板
     * @return 填充有实际对象的json格式数据
     */
    public static JsonObject getJsonForTars(TarsStructBase base, JsonObject template){
        byte[] requestData = TarsStructUtil.tarsStructToUTF8ByteArray(base);
        return new JsonInputStream(requestData).read(template);
    }

    public static String toPrettyFormat(JsonObject jsonObject) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    public static String toPrettyFormat(Map maoObject) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(maoObject,Map.class);
    }

    static String getType(JsonObject object) {
        if (object == null) {
            throw new JsonDecodeException("get type error: jsonObject is null");
        }
        if (object.get(JsonConst.KEY_TYPE) == null) {
            throw new JsonDecodeException("get type is null:" + object.toString());
        }
        String type = object.get(JsonConst.KEY_TYPE).getAsString();
        if (type == null || type.isEmpty()) {
            throw new JsonDecodeException("get type is null:" + object.toString());
        }
        return type;
    }

    static int getTag(JsonObject object) {
        if (object == null) {
            throw new JsonDecodeException("get tag error: jsonObject is null");
        }
        if (object.get(JsonConst.KEY_TAG) == null) {
            throw new JsonDecodeException("get tag is null:" + object.toString());
        }
        JsonElement tagElement = object.get(JsonConst.KEY_TAG);

        int tag = tagElement.getAsInt();
        if (tag < 0) {
            throw new JsonDecodeException("get tag error, tag==" + tag + " , jsonObject:" + object.toString());
        }
        return tag;
    }

    static JsonElement getKey(JsonObject object){
        if (object == null) {
            throw new JsonDecodeException("get map key error: jsonObject is null");
        }
        JsonElement element = object.get(JsonConst.KEY_MAP_KEY);
        if (element == null) {
            throw new JsonDecodeException("get map key is null:" + object.toString());
        }
        return element;
    }

    static JsonElement getValue(JsonObject object) {
        if (object == null) {
            throw new JsonDecodeException("get value error: jsonObject is null");
        }
        JsonElement element = object.get(JsonConst.KEY_VALUE);
        if (element == null) {
            throw new JsonDecodeException("get value is null:" + object.toString());
        }
        return element;
    }

    static boolean isVector(JsonObject object) {
        return JsonConst.VECTOR.equalsIgnoreCase(getType(object));
    }

    static boolean isMap(JsonObject object) {
        return JsonConst.MAP.equalsIgnoreCase(getType(object));
    }

    static boolean isCustom(JsonObject object) {
        return JsonConst.TARS.equalsIgnoreCase(getType(object));
    }

    static boolean isPrimitive(JsonObject object) {
        String type = getType(object);
        return JsonConst.BOOLEAN.equalsIgnoreCase(type)
                || JsonConst.INT.equalsIgnoreCase(type)
                || JsonConst.SHORT.equalsIgnoreCase(type)
                || JsonConst.LONG.equalsIgnoreCase(type)
                || JsonConst.FLOAT.equalsIgnoreCase(type)
                || JsonConst.DOUBLE.equalsIgnoreCase(type)
                || JsonConst.BYTE.equalsIgnoreCase(type)
                || JsonConst.STRING.equalsIgnoreCase(type)
                || JsonConst.BOOLEAN_VEC.equalsIgnoreCase(type)
                || JsonConst.INT_VEC.equalsIgnoreCase(type)
                || JsonConst.SHORT_VEC.equalsIgnoreCase(type)
                || JsonConst.LONG_VEC.equalsIgnoreCase(type)
                || JsonConst.FLOAT_VEC.equalsIgnoreCase(type)
                || JsonConst.DOUBLE_VEC.equalsIgnoreCase(type)
                || JsonConst.BYTE_VEC.equalsIgnoreCase(type)
                || JsonConst.STRING_VEC.equalsIgnoreCase(type);
    }

    static <T> T[] jsonToArray(JsonElement element, Class<T[]> clazz) {
        Gson gson = new Gson();
        return gson.fromJson(element, clazz);
    }

    public static JsonObject getDoubleValue(double value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.DOUBLE);
        ret.addProperty(JsonConst.KEY_VALUE, value);
        return ret;
    }

    public static JsonObject getDoubleVectorValue(double[] value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.DOUBLE_VEC);
        ret.add(JsonConst.KEY_VALUE, JsonParser.parseString(new Gson().toJson(Collections.singletonList(value))).getAsJsonArray());
        return ret;
    }

    public static JsonObject getFloatValue(float value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.FLOAT);
        ret.addProperty(JsonConst.KEY_VALUE, value);
        return ret;
    }

    public static JsonObject getFloatVectorValue(float[] value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.FLOAT_VEC);
        ret.add(JsonConst.KEY_VALUE,JsonParser.parseString(new Gson().toJson(Collections.singletonList(value))).getAsJsonArray());
        return ret;
    }


    public static JsonObject getLongValue(long value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.LONG);
        ret.addProperty(JsonConst.KEY_VALUE, value);
        return ret;
    }

    public static JsonObject getLongVectorValue(long[] value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.LONG_VEC);
        ret.add(JsonConst.KEY_VALUE,JsonParser.parseString(new Gson().toJson(Collections.singletonList(value))).getAsJsonArray());
        return ret;
    }

    public static JsonObject getShortValue(short value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.SHORT);
        ret.addProperty(JsonConst.KEY_VALUE, value);
        return ret;
    }

    public static JsonObject getShortVectorValue(short[] value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.SHORT_VEC);
        ret.add(JsonConst.KEY_VALUE,JsonParser.parseString(new Gson().toJson(Collections.singletonList(value))).getAsJsonArray());
        return ret;
    }

    public static JsonObject getByteValue(byte value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.BYTE);
        ret.addProperty(JsonConst.KEY_VALUE, value);
        return ret;
    }

    public static JsonObject getByteVectorValue(byte[] value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.BYTE_VEC);
        ret.add(JsonConst.KEY_VALUE,JsonParser.parseString(new Gson().toJson(Collections.singletonList(value))).getAsJsonArray());
        return ret;
    }

    public static JsonObject getBoolValue(boolean value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.BOOLEAN);
        ret.addProperty(JsonConst.KEY_VALUE, value);
        return ret;
    }

    public static JsonObject getBoolVectorValue(boolean[] value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.BOOLEAN_VEC);
        ret.add(JsonConst.KEY_VALUE,JsonParser.parseString(new Gson().toJson(Collections.singletonList(value))).getAsJsonArray());
        return ret;
    }

    public static JsonObject getIntValue(int value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.INT);
        ret.addProperty(JsonConst.KEY_VALUE, value);
        return ret;
    }

    public static JsonObject getIntVectorValue(int[] value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.INT_VEC);
        ret.add(JsonConst.KEY_VALUE,JsonParser.parseString(new Gson().toJson(Collections.singletonList(value))).getAsJsonArray());
        return ret;
    }

    public static JsonObject getStringValue(String value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.STRING);
        ret.addProperty(JsonConst.KEY_VALUE, value);
        return ret;
    }

    public static JsonObject getStringVectorValue(String[] value) {
        JsonObject ret = new JsonObject();
        ret.addProperty(JsonConst.KEY_TYPE, JsonConst.STRING_VEC);
        ret.add(JsonConst.KEY_VALUE, JsonParser.parseString(new Gson().toJson(Arrays.asList(value))).getAsJsonArray());
        return ret;
    }
}
