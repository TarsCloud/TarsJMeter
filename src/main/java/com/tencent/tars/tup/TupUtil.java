package com.tencent.tars.tup;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.tencent.tars.protocol.JsonConst;
import com.tencent.tars.protocol.json.JsonInputStream;
import com.tencent.tars.protocol.json.JsonOutputStream;
import com.tencent.tars.protocol.exceptions.JsonEncodeException;
import com.tencent.tars.protocol.tars.TarsInputStream;
import com.tencent.tars.protocol.tars.TarsOutputStream;

import java.nio.ByteBuffer;


public class TupUtil {

    private static boolean isTarsStruct(JsonObject jsonObject) {
        try {
            jsonObjectToTarsStream(jsonObject);
            return true;
        } catch (JsonEncodeException e) {
            return false;
        }
    }

    private static JsonObject generateFullJsonObject(Object[] objs) {
        JsonObject rootObj = new JsonObject();
        int tag = 1;
        for (Object obj : objs) {
            JsonObject jo = (JsonObject) obj;
            if (isTarsStruct(jo)) {
                JsonObject wapper = new JsonObject();
                wapper.addProperty(JsonConst.KEY_TYPE, JsonConst.TARS);
                wapper.addProperty(JsonConst.KEY_TAG, tag);
                wapper.add(JsonConst.KEY_VALUE, jo);
                rootObj.add(String.valueOf(tag), wapper);
            } else {
                    /*
                    如果是基础类型，数据是如下这样的。
                    {
                        "type" : "string"
                        "value" : "testString"
                    }
                     */
                jo.addProperty(JsonConst.KEY_TAG, tag);
                rootObj.add(String.valueOf(tag), jo);
            }
            tag++;
        }
        return rootObj;
    }

    /**
     *
     * @param ios  JsonInputStream
     * @param context 内容上下文
     * @return JsonObject
     */
    public static JsonObject getReturn(JsonInputStream ios, ServantInvokeContext context) {
        return ios.getItem(context.getJsonRetVal());
    }

    /**
     * @param buffer  接收到的数据Buffer
     * @param context 包含发送参数、返回值、返回参数列表的内容
     */
    public static void parseTarsStream(byte[] buffer, ServantInvokeContext context) {
        Object[] outTars = new Object[context.getArgumentValues().length + 1];
        if (context.getArgumentValues().length > 0 && context.getArgumentValues()[0] instanceof JsonObject) {
            JsonInputStream inputStream = new JsonInputStream(buffer);
            context.setJsonRetVal(getReturn(inputStream, context));
            //性能测试分支
            JsonObject rootObj = generateFullJsonObject(context.getArgumentValues());
            JsonObject outJson = inputStream.read(rootObj);
            JsonObject[] outJsonArgs = new JsonObject[context.getArgumentValues().length];
            for (int i = 1; i <= context.getArgumentValues().length; i++) {
                JsonObject item = outJson
                        .get(String.valueOf(i))
                        .getAsJsonObject();

                if (JsonConst.TARS.equalsIgnoreCase(item.get(JsonConst.KEY_TYPE).getAsString())) {
                    JsonElement element = item.get(JsonConst.KEY_VALUE);
                    if (!element.isJsonNull()) {
                        outJsonArgs[i - 1] = element.getAsJsonObject();
                    }
                } else {
                    item.remove(JsonConst.KEY_TAG);
                    outJsonArgs[i - 1] = item;
                }
            }
            context.setRetVal(outTars[0]);
            context.setRetArguments(outJsonArgs);
        } else {
            TarsInputStream ios = new TarsInputStream(buffer);
            outTars[0] = ios.read(context.getRetValue(), 0, false);
            int tag = 1;
            for (Object param : context.getArgumentValues()) {
                outTars[tag] = ios.read(param, tag, false);
                tag++;
            }
            context.setRetVal(outTars[0]);
            context.setRetArguments(outTars);
        }
    }


    private static ByteBuffer jsonObjectToTarsStream(JsonObject rootObj) {
        JsonOutputStream oos = new JsonOutputStream();
        oos.write(rootObj);
        return oos.getByteBuffer();
    }

    public static ByteBuffer paramsToJceStream(Object[] objs) {
        if (objs.length > 0 && objs[0] instanceof JsonObject) {
            //性能测试分支
            JsonObject rootObj = generateFullJsonObject(objs);
            return jsonObjectToTarsStream(rootObj);
        } else {
            TarsOutputStream oos = new TarsOutputStream();
            int tag = 1;
            for (Object obj : objs) {
                oos.write(obj, tag);
                tag++;
            }
            return oos.getByteBuffer();
        }
    }
}
