package com.tencent.tars.protocol.json;

import com.google.gson.*;
import com.tencent.tars.protocol.JsonConst;
import com.tencent.tars.protocol.exceptions.JsonEncodeException;
import com.tencent.tars.protocol.tars.TarsOutputStream;
import com.tencent.tars.protocol.tars.TarsStructBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Map;

import static com.tencent.tars.protocol.json.JsonStreamUtil.*;
/**
 * json to tars bytes.
 *
 * @author brookechen
 */
public class JsonOutputStream {

    private TarsOutputStream os;

    public JsonOutputStream() {
        this.os = new TarsOutputStream();
    }

    /**
     * direct path json file or json string to tars bytes
     */
    public JsonOutputStream(String pathOrJson) {
        this();
        JsonObject rootObject;
        if (new File(pathOrJson).isFile()) {
            InputStreamReader reader;
            try {
                FileInputStream is = new FileInputStream(pathOrJson);
                reader = new InputStreamReader(is);
            } catch (FileNotFoundException e) {
                // impossible branch.
                return;
            }
            rootObject = JsonParser.parseReader(reader).getAsJsonObject();
        } else {
            rootObject = JsonParser.parseString(pathOrJson).getAsJsonObject();
        }
        this.write(rootObject);
    }

    public void write(JsonObject rootObject) {
        if (os == null) {
            os = new TarsOutputStream();
        }
        for (Map.Entry<String, JsonElement> map : rootObject.entrySet()) {
            JsonElement element = map.getValue();
            if (element.isJsonObject()) {
                JsonObject subObject = element.getAsJsonObject();
                writeItem(subObject);
            } else{
                throw new JsonEncodeException("this is not tars struct.");
            }
        }
    }

    private void writeItem(JsonObject subObject) {
        if (isPrimitive(subObject)) {
            writePrimitive(subObject);
        } else if (isVector(subObject)) {
            writeVector(subObject);
        } else if (isMap(subObject)) {
            writeMap(subObject);
        } else if (isCustom(subObject)) {
            writeTars(subObject);
        }
    }

    private void writePrimitive(JsonObject jsonObject) {
        if (jsonObject == null) {
            throw new JsonEncodeException("write primitive jsonObject error: jsonObject is null");
        }
        String type = getType(jsonObject);
        int tag = getTag(jsonObject);
        JsonElement valueElement = getValue(jsonObject);
        if (JsonConst.BOOLEAN.equalsIgnoreCase(type)) {
            boolean value = valueElement.getAsBoolean();
            os.write(value, tag);
        } else if (JsonConst.INT.equalsIgnoreCase(type)) {
            int value = valueElement.getAsInt();
            os.write(value, tag);
        } else if (JsonConst.SHORT.equalsIgnoreCase(type)) {
            short value = valueElement.getAsShort();
            os.write(value, tag);
        } else if (JsonConst.LONG.equalsIgnoreCase(type)) {
            long value = valueElement.getAsLong();
            os.write(value, tag);
        } else if (JsonConst.FLOAT.equalsIgnoreCase(type)) {
            float value = valueElement.getAsFloat();
            os.write(value, tag);
        } else if (JsonConst.DOUBLE.equalsIgnoreCase(type)) {
            double value = valueElement.getAsDouble();
            os.write(value, tag);
        } else if (JsonConst.BYTE.equalsIgnoreCase(type)) {
            byte value = valueElement.getAsByte();
            os.write(value, tag);
        } else if (JsonConst.STRING.equalsIgnoreCase(type)) {
            String value = valueElement.getAsString();
            os.write(value, tag);
        } else if (JsonConst.BOOLEAN_VEC.equalsIgnoreCase(type)) {
            Boolean[] value = jsonToArray(valueElement, Boolean[].class);
            os.write(value, tag);
        } else if (JsonConst.INT_VEC.equalsIgnoreCase(type)) {
            Integer[] value = jsonToArray(valueElement, Integer[].class);
            os.write(value, tag);
        } else if (JsonConst.SHORT_VEC.equalsIgnoreCase(type)) {
            Short[] value = jsonToArray(valueElement, Short[].class);
            os.write(value, tag);
        } else if (JsonConst.LONG_VEC.equalsIgnoreCase(type)) {
            Long[] value = jsonToArray(valueElement, Long[].class);
            os.write(value, tag);
        } else if (JsonConst.FLOAT_VEC.equalsIgnoreCase(type)) {
            Float[] value = jsonToArray(valueElement, Float[].class);
            os.write(value, tag);
        } else if (JsonConst.DOUBLE_VEC.equalsIgnoreCase(type)) {
            Double[] value = jsonToArray(valueElement, Double[].class);
            os.write(value, tag);
        } else if (JsonConst.BYTE_VEC.equalsIgnoreCase(type)) {
            //这个稍微有点点不同，是SIMPLE_LIST
            Byte[] oValue = jsonToArray(valueElement, Byte[].class);
            byte[] value = new byte[oValue.length];
            for (int i = 0; i < oValue.length; i++) {
                value[i] = oValue[i];
            }
            os.write(value, tag);
        } else if (JsonConst.STRING_VEC.equalsIgnoreCase(type)) {
            String[] value = jsonToArray(valueElement, String[].class);
            os.write(value, tag);
        } else {
            throw new JsonEncodeException("write primitive type error:" + type);
        }
    }

    private void writeTars(JsonObject jsonObject) {
        JsonElement valueElement = getValue(jsonObject);
        int tag = getTag(jsonObject);
        this.os.reserve(2);
        this.os.writeHead(TarsStructBase.STRUCT_BEGIN, tag);
        this.write(valueElement.getAsJsonObject());
        this.os.reserve(2);
        this.os.writeHead(TarsStructBase.STRUCT_END, 0);
    }

    private void writeVector(JsonObject jsonObject) {
        JsonElement valueElement = getValue(jsonObject);
        int tag = getTag(jsonObject);
        this.os.reserve(8);
        this.os.writeHead(TarsStructBase.LIST, tag);
        JsonArray valueArray = valueElement.getAsJsonArray();
        this.os.write(valueArray.size(), 0);
        for (int j = 0; j < valueArray.size(); j++) {
            JsonElement subElement = valueArray.get(j);
            if (subElement.isJsonObject()) {
                JsonObject subObject = subElement.getAsJsonObject();
                writeItem(subObject);
            }
        }
    }

    private void writeMap(JsonObject jsonObject) {
        JsonElement valueElement = getValue(jsonObject);
        JsonArray valueArray = valueElement.getAsJsonArray();
        int tag = getTag(jsonObject);
        this.os.reserve(8);
        this.os.writeHead(TarsStructBase.MAP, tag);
        this.os.write(valueArray == null ? 0 : valueArray.size(), 0);
        if (valueArray != null) {
            for (int i = 0; i < valueArray.size(); i++) {
                JsonObject kvItem = valueArray.get(i).getAsJsonObject();
                JsonObject keyJsonObject = getKey(kvItem).getAsJsonObject();
                JsonObject valueJsonObject = getValue(kvItem).getAsJsonObject();
                writeItem(keyJsonObject); // json数据的key这里一定要是 tag == 0 ,已经在tars2json里面做好了
                writeItem(valueJsonObject); // json数据的value这里一定要是 value == 1
            }
        }
    }

    public byte[] getBytes() {
        return this.os.toByteArray();
    }

    public ByteBuffer getByteBuffer(){
        return ByteBuffer.wrap(this.os.toByteArray());
    }

    public JsonArray getBytesAsJsonArray() {
        return new Gson().toJsonTree(getBytes(), Byte[].class).getAsJsonArray();
    }
}
