package com.tencent.tars.jmeter.sampler;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tencent.tars.jmeter.constants.ITarsConst;
import com.tencent.tars.jmeter.utils.TarsParamArgument;
import com.tencent.tars.jmeter.utils.TarsParamUtil;
import com.tencent.tars.protocol.JsonConst;
import com.tencent.tars.protocol.json.JsonStreamUtil;
import com.tencent.tars.utils.ErrorCode;
import com.tencent.tars.utils.TextUtils;
import org.apache.jmeter.config.Arguments;
import org.apache.jmeter.samplers.AbstractSampler;
import org.apache.jmeter.samplers.Entry;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.property.CollectionProperty;
import org.apache.jmeter.testelement.property.PropertyIterator;
import org.apache.jmeter.testelement.property.TestElementProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * tars shark都会能用到的 sampler base方法
 * @author brookechen
 */
public abstract class TarsSamplerBase extends AbstractSampler implements ITarsConst {
    private static final Logger log = LoggerFactory.getLogger(TarsSamplerBase.class);

    public static Locale locale = Locale.getDefault();

    //用时间确保唯一性
    public static final SimpleDateFormat sFmtTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS Z", locale);

    @Override
    public SampleResult sample(Entry e) {
        //System.out.println(String.format("%s thread %s sample start." ,sFmtTime.format(System.currentTimeMillis()),Thread.currentThread().getName()));
        TarsSampleResult result = new TarsSampleResult();
        result.sampleStart();
        result.setSampleLabel(this.getName());
        result.setServantId(this.getServantId());
        String requestUrl = getRequestUri();
        result.setRequestUrl(requestUrl);
        try {
            result = sample(result);
            result.setSuccessful(getSuccessfulStatus(result));
        } catch (Throwable err) {
            result.setRetCode(result.getRetCode() + ErrorCode.ErrorSampleBaseException);
            errorResult(err, result);
        }
        result.sampleEnd();
        return result;
    }

    public boolean getSuccessfulStatus(TarsSampleResult result) {
        return result.getRetCode() == 0;
    }

    protected abstract TarsSampleResult sample(TarsSampleResult result);

    public void successResult(TarsSampleResult res, String retData) {
        res.setDataType(SampleResult.TEXT);
        res.setRequestHeaders(res.getRequestHeaders());
        res.setResponseHeaders(res.getResponseHeaders());
        res.setSamplerData(JsonStreamUtil.toPrettyFormat(getRequestBody()));
        res.setResponseData(retData, "UTF-8");
        res.setResponseCode("" + res.getRetCode());
        res.setResponseMessage("");
    }

    /**
     * local error where sampler request.
     */
    public void errorResult(Throwable e, TarsSampleResult res) {
        res.setDataType(SampleResult.TEXT);
        res.setRequestHeaders(res.getRequestHeaders());
        res.setResponseHeaders(res.getResponseHeaders());
        res.setSamplerData(JsonStreamUtil.toPrettyFormat(getRequestBody()));
        res.setResponseData("", "UTF-8");
        res.setResponseCode("" + res.getRetCode());

        String retMessage = "";
        if (!TextUtils.isEmpty(res.getErrorMessage())) {
            retMessage = "CustomErr:" + res.getErrorMessage() + ", ";
        }
        retMessage += ErrorCode.getErrMessage(res.getRetCode());

        if (e != null) {
            ByteArrayOutputStream text = new ByteArrayOutputStream(200);
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            e.printStackTrace(printWriter);
            e.printStackTrace(new PrintStream(text));
            log.warn(writer.toString());
            retMessage += " Throw:" + writer.toString();
        }
        retMessage = retMessage.replace("\n", "|");
        res.setResponseMessage(retMessage);
        res.setSuccessful(false);
    }

    // global config

    public void setServantIp(String ip) {
        this.setProperty(SERVANT_IP, ip);
    }

    public void setServantPort(String portStr) {
        try {
            int port = Integer.parseInt(portStr);
            this.setProperty(SERVANT_PORT, port);
        } catch (NumberFormatException e) {
            log.info("set servant port error with NumberFormatException: {}", portStr);
        }
    }

    public void setServantPath(String servantPath) {
        this.setProperty(SERVANT_PATH, servantPath);
    }

    public void setFuncName(String interfaceName) {
        this.setProperty(FUNC_NAME, interfaceName);
    }

    public void setReturnType(String type) {
        this.setProperty(TARS_RETURN_TYPE, type);
    }

    public void setReturnValue(String customValue) {
        this.setProperty(TARS_RETURN_VALUE, customValue);
    }


    public int getServantPort() {
        return this.getPropertyAsInt(SERVANT_PORT, -1);
    }

    public String getServantIp() {
        return this.getPropertyAsString(SERVANT_IP, "");
    }

    public String getServantPath() {
        return this.getPropertyAsString(SERVANT_PATH, "");
    }

    public String getFuncName() {
        return this.getPropertyAsString(FUNC_NAME, "");
    }

    public String getReturnType() {
        return this.getPropertyAsString(TARS_RETURN_TYPE, JsonConst.INT);
    }

    public JsonObject getReturnValue() {
        String type = getReturnType();
        if (type.equals(JsonConst.TARS)) {
            String returnValue = this.getPropertyAsString(TARS_RETURN_VALUE, "");
            if (TextUtils.isEmpty(returnValue)) {
                return JsonStreamUtil.getIntValue(0);
            }
            return JsonParser.parseString(returnValue).getAsJsonObject();
        } else {
            return TarsParamUtil.getValueByType(type);
        }
    }

    public String getServantId() {
        return getServantPath() + "." + getFuncName();
    }

    public String getRequestUri() {
        return getServantId() + " > " + getServantIp() + ":" + getServantPort() + "\n";
    }


    // function param


    // extra config


    // gets called from ctor, so has to be final
    public final void setArguments(Arguments value) {
        setProperty(new TestElementProperty(ARGUMENTS, value));
    }

    public Map<String, String> getArgumentMap() {
        return ((Arguments) getProperty(ARGUMENTS).getObjectValue()).getArgumentsAsMap();
    }


    /**
     * 这个设计用于支持多种RetCode都需要被判定为成功的路径
     * 多对retCode,dataRetCode格式大概会是这样的：
     * 0,0;-1,0;9,0;0,135
     */
    public ArrayList<Integer> getExpectedRetCodes() {
        ArrayList<Integer> ret = new ArrayList<>();
        String retCodes = getArgumentMap().get(KEY_RET_CODE);
        if (TextUtils.isEmpty(retCodes)) {
            ret.add(0);
        }
        String[] temp = retCodes.split(";");
        for (String i : temp) {
            try {
                int retCode = Integer.parseInt(i);
                ret.add(retCode);
            } catch (Exception e) {
                //有错误的数据忽略掉
            }
        }
        return ret;
    }

    public String getProxyHost() {
        String ret = getArgumentMap().get(KEY_PROXY_HOST);
        if (ret == null) {
            ret = "";
        }
        return ret;
    }

    public int getProxyPort() {
        try {
            return Integer.parseInt(getArgumentMap().get(KEY_PROXY_PORT));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public boolean isKeepAlive() {
        return "true".equalsIgnoreCase(getArgumentMap().get(KEY_KEEP_ALIVE));
    }

    public int getConnectTimeout() {
        try {
            return Integer.parseInt(getArgumentMap().get(KEY_CONNECT_TIMEOUT));
        } catch (NumberFormatException e) {
            return 8000;
        }
    }

    public int getReadTimeout() {
        try {
            return Integer.parseInt(getArgumentMap().get(KEY_READ_TIMEOUT));
        } catch (NumberFormatException e) {
            return 8000;
        }
    }

    public short getTupVersion() {
        try {
            return Short.parseShort(getArgumentMap().get(KEY_REQ_VERSION));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public byte getTupPacketType() {
        try {
            return Byte.parseByte(getArgumentMap().get(KEY_REQ_PKT_TYPE));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getTupMessageType() {
        try {
            return Integer.parseInt(getArgumentMap().get(KEY_REQ_MSG_TYPE));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getTupTimeout() {
        try {
            return Integer.parseInt(getArgumentMap().get(KEY_REQ_I_TIMEOUT));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setDataArguments(Arguments arguments) {
        setProperty(new TestElementProperty(FUNC_ARGUMENTS, arguments));
    }

    public void setContextArguments(Arguments arguments) {
        setProperty(new TestElementProperty(CONTEXT_ARGUMENTS, arguments));
    }

    public void setStatusArguments(Arguments arguments) {
        setProperty(new TestElementProperty(STATUS_ARGUMENTS, arguments));
    }

    public Map<String, String> getContextMap() {
        return ((Arguments) getProperty(CONTEXT_ARGUMENTS).getObjectValue()).getArgumentsAsMap();
    }

    public Map<String, String> getStatusMap() {
        return ((Arguments) getProperty(STATUS_ARGUMENTS).getObjectValue()).getArgumentsAsMap();
    }

    public JsonObject[] getRequestParameters() {
        CollectionProperty properties = ((Arguments) getProperty(FUNC_ARGUMENTS).getObjectValue()).getArguments();
        JsonObject[] values = new JsonObject[properties.size()];
        PropertyIterator iterator = properties.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            TarsParamArgument value = (TarsParamArgument) iterator.next().getObjectValue();
            values[i] = JsonParser.parseString(value.getValue()).getAsJsonObject();
            i++;
        }
        return values;
    }

    public String[] getParameterNames() {
        CollectionProperty properties = ((Arguments) getProperty(FUNC_ARGUMENTS).getObjectValue()).getArguments();
        String[] names = new String[properties.size()];
        PropertyIterator iterator = properties.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            names[i] = iterator.next().getName();
            i++;
        }
        return names;
    }

    public JsonObject getRequestBody() {
        JsonObject retJson = new JsonObject();
        String[] names = getParameterNames();
        JsonObject[] values = getRequestParameters();
        for (int i = 0; i < names.length; i++) {
            retJson.add(names[i], values[i]);
        }
        return retJson;
    }

    public JsonObject getResponseBody(JsonObject[] retArgs) {
        JsonObject retJson = new JsonObject();
        String[] names = getParameterNames();
        for (int i = 0; i < names.length; i++) {
            retJson.add(names[i], retArgs[i]);
        }
        return retJson;
    }
}
