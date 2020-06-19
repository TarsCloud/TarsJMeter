package com.tencent.tars.jmeter.sampler;

import com.google.gson.JsonObject;
import com.tencent.tars.jmeter.constants.ITarsConst;
import com.tencent.tars.protocol.json.JsonStreamUtil;
import com.tencent.tars.tup.ServantInvokeContext;
import com.tencent.tars.tup.TupClient;
import com.tencent.tars.utils.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * tars 接口测试
 *
 * @author brookechen
 */
public class TarsImpl extends TarsAbstractImpl {
    private static final Logger log = LoggerFactory.getLogger(TarsImpl.class);

    private TupClient client = null;
    private boolean isKeepAlive;
    private boolean isProfileNetStarted = false;


    public TarsImpl(TarsSamplerBase testElement) {
        super(testElement);
        isKeepAlive = testElement.isKeepAlive();
        if (isKeepAlive) {
            beforeTransmit(null);
        }
    }

    /**
     * 数据发送前
     * <p>
     * public short iVersion = 0;
     * public byte cPacketType = 0;
     * public int iMessageType = 0;
     */
    private void beforeTransmit(TarsSampleResult result) {
        if (client == null) {
            client = TupClient.builder()
                    .setIp(testElement.getServantIp())
                    .setPort(testElement.getServantPort())
                    .setServant(testElement.getServantPath())
                    .setFunc(testElement.getFuncName())
                    .setProxy(this.getProxy())
                    .setConnectTimeout(testElement.getConnectTimeout())
                    .setReadTimeout(testElement.getReadTimeout())
                    .setTransmitType(testElement.getTransmitType())
                    .build();
        }
        try {
            log.debug(String.format("Thread %s before tup network init.", threadName));
            client.init();
            log.debug(String.format("Thread %s after tup network init.", threadName));
        } catch (Throwable t) {
            this.localThrowable = t;
            this.errCode += ErrorCode.ErrorPrepareNetwork;
        }
        if (result != null) {
            updateResult(result, "");
        }
    }

    private void transmitData(TarsSampleResult result) {
        log.debug(String.format("Thread %s before transmit data.", threadName));
        StringBuilder resultData = new StringBuilder();
        try {
            //根据UI配置的parameters来构造每个Tup包的RequestPacket
            client.setMessageType(testElement.getTupMessageType());
            client.setTimeout(testElement.getTupTimeout());
            client.setPacketType(testElement.getTupPacketType());
            client.setVersion(testElement.getTupVersion());
            client.setContext(testElement.getContextMap());
            client.setStatus(testElement.getStatusMap());

            ServantInvokeContext context = new ServantInvokeContext(testElement.getReturnValue(), testElement.getRequestParameters());
            this.errCode = client.invokeMethod(context);
            resultData.append(ITarsConst.RETURN_VALUE_NAME + ":\n");
            resultData.append(JsonStreamUtil.toPrettyFormat(context.getJsonRetVal())).append("\n\n");
            resultData.append("Status" + ":\n");
            resultData.append(JsonStreamUtil.toPrettyFormat(context.getStatus())).append("\n\n");
            resultData.append("Parameters" + ":\n");
            JsonObject[] retArgs = (JsonObject[]) context.getRetArguments();
            if (retArgs != null &&
                    retArgs.length > 0) {
                resultData.append(JsonStreamUtil.toPrettyFormat(testElement.getResponseBody(retArgs)));
            } else {
                log.warn("retArgs:" + (retArgs == null ? "null" : Arrays.toString(retArgs)));
            }
        } catch (Throwable t) {
            this.localThrowable = t;
            this.errCode += ErrorCode.ErrorTarsException;
        }
        updateResult(result, resultData.toString());
        log.debug(String.format("Thread %s after transmit data.", threadName));
    }

    private void afterTransmit(TarsSampleResult result) {
        log.debug(String.format("Thread %s before stop network.", threadName));
        try {
            client.stop();
        } catch (Throwable t) {
            if (this.localThrowable == null) {
                this.localThrowable = t;
            }
            if (this.errCode == 0) {
                this.errCode += ErrorCode.ErrorStopNetwork;
            }
            updateResult(result, "");
        }
        log.debug(String.format("Thread %s after stop network.", threadName));
    }

    @Override
    public TarsSampleResult sample(TarsSampleResult result) {
        updateResult(result, "");
        if (!isKeepAlive) {
            beforeTransmit(result);
        }
        if (!testElement.getSuccessfulStatus(result)) {
            errorResult(this.localThrowable, result);
            //initNetwork阶段就已经失败了.
            return result;
        }
        transmitData(result);
        if (!isKeepAlive) {
            afterTransmit(result);
        }
        return result;
    }

    @Override
    public boolean interrupt() {
        if (client != null) {
            client.stop();
        }
        client = null;
        return true;
    }
}
