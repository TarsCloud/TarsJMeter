package com.tencent.tars.protocol.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencent.tars.protocol.JsonConst;
import com.tencent.tars.protocol.exceptions.JsonEncodeException;
import com.tencent.tars.protocol.tars.TarsInputStream;
import com.tencent.tars.protocol.tars.TarsStructBase;
import com.tencent.tars.protocol.exceptions.TarsDecodeException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.Map;

import static com.tencent.tars.protocol.json.JsonStreamUtil.*;


/**
 * tars bytes to json.
 *
 * @author brookechen
 */
public class JsonInputStream {

    private TarsInputStream is;

    public JsonInputStream(byte[] data) {
        this.is = new TarsInputStream(data);
    }

    public JsonObject readJsonString(String jsonString) {
        JsonObject rootJson = JsonParser.parseString(jsonString).getAsJsonObject();
        return read(rootJson);
    }

    public JsonObject readJsonFile(String path) {
        JsonObject rootObject;
        if (new File(path).isFile()) {
            InputStreamReader reader;
            try {
                FileInputStream is = new FileInputStream(path);
                reader = new InputStreamReader(is);
            } catch (FileNotFoundException e) {
                // impossible branch.
                return null;
            }
            rootObject = JsonParser.parseReader(reader).getAsJsonObject();
            return read(rootObject);
        }
        return null;
    }

    /**
     * 把接收到的byte流读取到jsonObject数据结构中
     */
    public JsonObject read(JsonObject rootObject) {
        if (is == null) {
            is = new TarsInputStream();
        }
        JsonObject retJson = new JsonObject();
        for (Map.Entry<String, JsonElement> elementEntry : rootObject.entrySet()) {
            String key = elementEntry.getKey();
            JsonElement element = elementEntry.getValue();
            if (element.isJsonObject()) {
                JsonObject subObject = element.getAsJsonObject();
                JsonObject retSubJson = readItem(subObject);
                retJson.add(key, retSubJson);
            }
        }
        return retJson;
    }

    public JsonObject getItem(JsonObject subObject) {
        return readItem(subObject);
    }

    private JsonObject readItem(JsonObject subObject) {
        if (isPrimitive(subObject)) {
            return readPrimitive(subObject);
        } else if (isVector(subObject)) {
            return readVector(subObject);
        } else if (isMap(subObject)) {
            return readMap(subObject);
        } else if (isCustom(subObject)) {
            return readTars(subObject);
        }
        return null;
    }

    /**
     * 读 tars primitive 结构放到json中去
     *
     * @param param 期待的样本
     * @return tars primitive 对应的json数据结构
     */
    private JsonObject readPrimitive(JsonObject param) {
        if (param == null) {
            throw new JsonEncodeException("read primitive jsonObject error: jsonObject is null");
        }
        String type = getType(param);
        int tag = getTag(param);
        JsonObject retJson = new JsonObject();
        retJson.addProperty(JsonConst.KEY_TYPE, type);
        if(JsonConst.VOID.equalsIgnoreCase(type)){
            retJson.addProperty(JsonConst.KEY_VALUE,"null");
        } else if (JsonConst.BOOLEAN.equalsIgnoreCase(type)) {
            boolean value = is.read(false, tag, false);
            retJson.addProperty(JsonConst.KEY_VALUE, value);
        } else if (JsonConst.INT.equalsIgnoreCase(type)) {
            int value = is.read(0, tag, false);
            retJson.addProperty(JsonConst.KEY_VALUE, value);
        } else if (JsonConst.SHORT.equalsIgnoreCase(type)) {
            short value = is.read((short) 0, tag, false);
            retJson.addProperty(JsonConst.KEY_VALUE, value);
        } else if (JsonConst.LONG.equalsIgnoreCase(type)) {
            long value = is.read(0L, tag, false);
            retJson.addProperty(JsonConst.KEY_VALUE, value);
        } else if (JsonConst.FLOAT.equalsIgnoreCase(type)) {
            float value = is.read(0f, tag, false);
            retJson.addProperty(JsonConst.KEY_VALUE, value);
        } else if (JsonConst.DOUBLE.equalsIgnoreCase(type)) {
            double value = is.read(0d, tag, false);
            retJson.addProperty(JsonConst.KEY_VALUE, value);
        } else if (JsonConst.BYTE.equalsIgnoreCase(type)) {
            byte value = is.read((byte) 0, tag, false);
            retJson.addProperty(JsonConst.KEY_VALUE, value);
        } else if (JsonConst.STRING.equalsIgnoreCase(type)) {
            String value = is.read("", tag, false);
            retJson.addProperty(JsonConst.KEY_VALUE, value);
        } else if (JsonConst.BOOLEAN_VEC.equalsIgnoreCase(type)) {
            boolean[] value = is.read((boolean[]) null, tag, false);
            JsonArray arrayValue = new JsonArray();
            if (value != null) {
                for (boolean b : value) {
                    arrayValue.add(b);
                }
            }
            retJson.add(JsonConst.KEY_VALUE, arrayValue);
        } else if (JsonConst.INT_VEC.equalsIgnoreCase(type)) {
            int[] value = is.read((int[]) null, tag, false);
            JsonArray arrayValue = new JsonArray();
            if (value != null) {
                for (int b : value) {
                    arrayValue.add(b);
                }
            }
            retJson.add(JsonConst.KEY_VALUE, arrayValue);
        } else if (JsonConst.SHORT_VEC.equalsIgnoreCase(type)) {
            int[] value = is.read((int[]) null, tag, false);
            JsonArray arrayValue = new JsonArray();
            if (value != null) {
                for (int b : value) {
                    arrayValue.add(b);
                }
            }
            retJson.add(JsonConst.KEY_VALUE, arrayValue);
        } else if (JsonConst.LONG_VEC.equalsIgnoreCase(type)) {
            long[] value = is.read((long[]) null, tag, false);
            JsonArray arrayValue = new JsonArray();
            if (value != null) {
                for (long b : value) {
                    arrayValue.add(b);
                }
            }
            retJson.add(JsonConst.KEY_VALUE, arrayValue);
        } else if (JsonConst.FLOAT_VEC.equalsIgnoreCase(type)) {
            float[] value = is.read((float[]) null, tag, false);
            JsonArray arrayValue = new JsonArray();
            if (value != null) {
                for (float b : value) {
                    arrayValue.add(b);
                }
            }
            retJson.add(JsonConst.KEY_VALUE, arrayValue);
        } else if (JsonConst.DOUBLE_VEC.equalsIgnoreCase(type)) {
            double[] value = is.read((double[]) null, tag, false);
            JsonArray arrayValue = new JsonArray();
            if (value != null) {
                for (double b : value) {
                    arrayValue.add(b);
                }
            }
            retJson.add(JsonConst.KEY_VALUE, arrayValue);
        } else if (JsonConst.BYTE_VEC.equalsIgnoreCase(type)) {
            byte[] value = is.read((byte[]) null, tag, false);
            JsonArray arrayValue = new JsonArray();
            if (value != null) {
                for (byte b : value) {
                    arrayValue.add(b);
                }
            }
            retJson.add(JsonConst.KEY_VALUE, arrayValue);
        } else if (JsonConst.STRING_VEC.equalsIgnoreCase(type)) {
            String[] value = is.read(new String[]{""}, tag, false);
            JsonArray arrayValue = new JsonArray();
            if (value != null) {
                for (String b : value) {
                    arrayValue.add(b);
                }
            }
            retJson.add(JsonConst.KEY_VALUE, arrayValue);
        } else {
            throw new JsonEncodeException("decode byte type error:" + type);
        }
        retJson.addProperty(JsonConst.KEY_TAG, tag);
        return retJson;
    }

    /**
     * 读 tars vector 结构放到json中去
     *
     * @param param 期待的样本
     * @return tars vector 对应的json数据结构
     */
    private JsonObject readVector(JsonObject param) {
        String type = getType(param);
        int tag = getTag(param);
        JsonArray templateArray = getValue(param).getAsJsonArray();
        JsonObject retJson = new JsonObject();
        retJson.addProperty(JsonConst.KEY_TYPE, type);
        JsonArray valueArray = new JsonArray();
        retJson.add(JsonConst.KEY_VALUE, valueArray);
        if (this.is.skipToTag(tag)) {
            TarsInputStream.HeadData hd = new TarsInputStream.HeadData();
            this.is.readHead(hd);
            if (hd.type == TarsStructBase.LIST) {
                int size = this.is.read(0, 0, true);
                if (size < 0) {
                    throw new TarsDecodeException("size invalid: " + size);
                }
                for (int i = 0; i < size; ++i) {
                    JsonObject subObject = templateArray.get(0).getAsJsonObject();
                    subObject = readItem(subObject);
                    valueArray.add(subObject);
                }
            } else {
                throw new TarsDecodeException("type mismatch.");
            }
        }
        retJson.addProperty(JsonConst.KEY_TAG, tag);
        return retJson;
    }

    /**
     * 读 tars map 结构放到json中去
     *
     * @param param 期待的样本
     * @return tars map 对应的json数据结构
     */
    private JsonObject readMap(JsonObject param) {
        String type = getType(param);
        int tag = getTag(param);
        JsonElement valueElement = getValue(param);
        JsonArray templateArray = valueElement.getAsJsonArray();
        JsonObject retJson = new JsonObject();
        retJson.addProperty(JsonConst.KEY_TYPE, type);
        JsonArray valueArray = new JsonArray();
        retJson.add(JsonConst.KEY_VALUE, valueArray);
        if (this.is.skipToTag(tag)) {
            TarsInputStream.HeadData hd = new TarsInputStream.HeadData();
            this.is.readHead(hd);
            if (hd.type == TarsStructBase.MAP) {
                int size = this.is.read(0, 0, true);
                JsonObject kv = templateArray.get(0).getAsJsonObject();
                JsonObject keyTemplate = kv.getAsJsonObject(JsonConst.KEY_MAP_KEY);
                JsonObject valueTemplate = kv.getAsJsonObject(JsonConst.KEY_VALUE);
                if (size < 0) throw new TarsDecodeException("size invalid: " + size);
                for (int i = 0; i < size; ++i) {
                    JsonObject key = readItem(keyTemplate);
                    JsonObject value = readItem(valueTemplate);
                    JsonObject keyValue = new JsonObject();
                    keyValue.add(JsonConst.KEY_MAP_KEY, key);
                    keyValue.add(JsonConst.KEY_VALUE, value);
                    valueArray.add(keyValue);
                }
            } else {
                throw new TarsDecodeException("type mismatch.");
            }
        }
        retJson.addProperty(JsonConst.KEY_TAG, tag);
        return retJson;
    }

    /**
     * 读 tars struct 结构放到json中去
     *
     * @param param 期待的样本
     * @return tars struct 对应的json数据结构
     */
    private JsonObject readTars(JsonObject param) {
        String type = getType(param);
        int tag = getTag(param);
        JsonObject valueObject = getValue(param).getAsJsonObject();
        JsonObject retJson = new JsonObject();
        retJson.addProperty(JsonConst.KEY_TYPE, type);
        if (this.is.skipToTag(tag)) {
            TarsInputStream.HeadData hd = new TarsInputStream.HeadData();
            this.is.readHead(hd);
            if (hd.type != TarsStructBase.STRUCT_BEGIN) {
                throw new TarsDecodeException("type mismatch.");
            }
            JsonObject retValueObject = read(valueObject);
            this.is.skipToStructEnd();
            retJson.add(JsonConst.KEY_VALUE, retValueObject);
        } else {
            retJson.add(JsonConst.KEY_VALUE, null);
        }
        retJson.addProperty(JsonConst.KEY_TAG, tag);
        return retJson;
    }
}
