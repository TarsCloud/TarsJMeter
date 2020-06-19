package com.tencent.tars.tup;

import com.google.gson.JsonObject;
import com.tencent.tars.protocol.JsonConst;

import java.util.Map;

public class ServantInvokeContext {
    private Object[] arguments;
    private Object[] retArguments = null;
    private Object retVal;
    private JsonObject jsonRetVal;
    private Map<String,String> status;

    public ServantInvokeContext(JsonObject retVal, JsonObject[] arguments) {
        this.arguments = arguments;
        this.jsonRetVal = retVal;
    }

    public ServantInvokeContext(Object retVal, Object[] arguments) {
        this.arguments = arguments;
        this.retVal = retVal;
    }

    public void setStatus(Map<String, String> status) {
        this.status = status;
    }

    public Map<String, String> getStatus() {
        return status;
    }

    public Object[] getArgumentValues() {
        return arguments;
    }

    public Object getRetValue() {
        return retVal;
    }

    public void setArguments(Object[] arguments) {
        this.arguments = arguments;
    }

    public void setRetVal(Object retVal) {
        this.retVal = retVal;
    }

    public void setJsonRetVal(JsonObject retVal) {
        this.jsonRetVal = retVal;
    }

    public JsonObject getJsonRetVal() {
        if (this.jsonRetVal == null) {
            this.jsonRetVal = new JsonObject();
            this.jsonRetVal.addProperty(JsonConst.KEY_TYPE, JsonConst.VOID);
            this.jsonRetVal.addProperty(JsonConst.KEY_TAG, 0);
            this.jsonRetVal.addProperty(JsonConst.KEY_VALUE, "null");
        }
        if (this.jsonRetVal.get(JsonConst.KEY_TAG) == null) {
            this.jsonRetVal.addProperty(JsonConst.KEY_TAG, 0);
        }
        return this.jsonRetVal;
    }

    public Object[] getRetArguments() {
        return retArguments;
    }

    public void setRetArguments(Object[] retArguments) {
        this.retArguments = retArguments;
    }
}
