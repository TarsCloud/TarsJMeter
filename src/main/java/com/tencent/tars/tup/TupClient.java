package com.tencent.tars.tup;

import com.tencent.tars.protocol.TarsStructUtil;
import com.tencent.tars.protocol.packet.RequestPacket;
import com.tencent.tars.protocol.packet.ResponsePacket;
import com.tencent.tars.tup.session.INetListener;
import com.tencent.tars.tup.session.TcpSession;
import com.tencent.tars.utils.ErrorCode;

import java.net.Proxy;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * TupClient实现
 * @author brookechen
 */
public class TupClient implements INetListener {

    private TcpSession session;

    private Proxy mProxy = null;
    private String mIp; //IP
    private int mPort; //端口
    private String mServant;
    private String mFunc;
    private int connectTimeout = 8000;
    private int readTimeout = 8000;


    private short iVersion = 0;
    private byte cPacketType = 0;
    private int iMessageType = 0;
    private final AtomicInteger requestNo = new AtomicInteger(0);
    private int iTimeout = 4000;
    private Map<String, String> context = new HashMap<>();
    private Map<String, String> status = new HashMap<>();


    private ResponsePacket currentResp = null;


    private int startErrorCode = 0;
    private final AtomicBoolean mRunning = new AtomicBoolean(false);

    public static final class Builder {

        private Proxy mProxy = null;
        private String mIp; //IP
        private int mPort; //端口
        private String mServant;
        private String mFunc;
        private int connectTimeout = 8000;
        private int readTimeout = 8000;

        public Builder setProxy(Proxy proxy) {
            this.mProxy = proxy;
            return this;
        }

        public Builder setIp(String ip) {
            this.mIp = ip;
            return this;
        }

        public Builder setPort(int port) {
            this.mPort = port;
            return this;
        }

        public Builder setServant(String servant) {
            this.mServant = servant;
            return this;
        }

        public Builder setFunc(String func) {
            this.mFunc = func;
            return this;
        }

        public Builder setConnectTimeout(int connectTimeout) {
            this.connectTimeout = connectTimeout;
            return this;
        }

        public Builder setReadTimeout(int readTimeout) {
            this.readTimeout = readTimeout;
            return this;
        }

        public TupClient build() {
            TupClient client = new TupClient();
            client.mProxy = this.mProxy;
            client.mIp = this.mIp;
            client.mPort = this.mPort;
            client.mServant = this.mServant;
            client.mFunc = this.mFunc;
            client.connectTimeout = this.connectTimeout;
            client.readTimeout = this.readTimeout;
            client.session = new TcpSession(new IPEndPoint(client.mIp, client.mPort), client.mProxy);
            client.session.setConnectTimeout(client.connectTimeout);
            client.session.setReadTimeout(client.readTimeout);
            return client;
        }
    }

    private TupClient() {
    }

    public void init() {
        if (mRunning.get()) {
            stop();
        }
        if (!mRunning.get()) {
            session.setNetworkListener(this);
            startErrorCode = session.start(false);
            if (startErrorCode == ErrorCode.ERR_NONE) {
                mRunning.set(true);
            }
        }
    }

    public void stop() {
        if (!mRunning.get()) {
            return;
        }
        if (null != this.session) {
            session.stop();
        }
        mRunning.set(false);
    }

    @Override
    public void handleData(final byte[] data) {
        if (data == null) {
            return;
        }
        currentResp = TarsStructUtil.getTarsStruct(data, new ResponsePacket());
    }

    public static Builder builder() {
        return new Builder();
    }

    private int sendRequestPacket(byte[] sbuffer) {
        RequestPacket requestPacket = new RequestPacket();
        requestPacket.sServantName = this.mServant;
        requestPacket.sFuncName = this.mFunc;
        requestPacket.iMessageType = this.iMessageType;
        requestPacket.iRequestId = requestNo.get();
        requestPacket.iVersion = this.iVersion;
        requestPacket.cPacketType = this.cPacketType;
        requestPacket.iTimeout = this.iTimeout;
        requestPacket.sBuffer = sbuffer;
        requestPacket.context = this.context;
        requestPacket.status = this.status;
        byte[] data = TarsStructUtil.tarsStructToUTF8ByteArray(requestPacket);
        return this.session.sendData(data);
    }

    public void setVersion(short iVersion) {
        this.iVersion = iVersion;
    }

    public void setPacketType(byte cPacketType) {
        this.cPacketType = cPacketType;
    }

    public void setMessageType(int iMessageType) {
        this.iMessageType = iMessageType;
    }

    public void setTimeout(int iTimeout) {
        this.iTimeout = iTimeout;
    }

    public void setContext(Map<String, String> context) {
        this.context = context;
    }

    public void setStatus(Map<String, String> status) {
        this.status = status;
    }

    public Map<String, String> getContext() {
        return this.context;
    }

    public Map<String, String> getStatus() {
        return this.status;
    }

    public int invokeMethod(ServantInvokeContext context) {
        int localErrorCode = ErrorCode.ERR_NONE;
        if (startErrorCode != 0) {
            //如果网络初始化失败，不用往下走了,直接分发错误码上去
            return startErrorCode;
        }
        requestNo.addAndGet(1);
        ByteBuffer bf = TupUtil.paramsToJceStream(context.getArgumentValues());
        localErrorCode += this.sendRequestPacket(bf.array());
        if (localErrorCode != ErrorCode.ERR_NONE) {
            return localErrorCode;
        }
        ResponsePacket responsePacket = currentResp;
        if (responsePacket == null) {
            localErrorCode += ErrorCode.ERR_TUP_RESPONSE_BODY_EMPTY;
            return localErrorCode;
        } else if (responsePacket.iRequestId != requestNo.get()) {
            localErrorCode += ErrorCode.ERR_TUP_REQUEST_SEQ_NOT_MATCH;
            return localErrorCode;
        } else {
            //数据返回码不为0的时候，也有可能有数据需要透传上去
            if (responsePacket.iRet != 0) {
                localErrorCode += responsePacket.iRet;
            }
            try {
                context.setStatus(responsePacket.status);
                TupUtil.parseTarsStream(responsePacket.SBuffer, context);
            } catch (Exception e) {
                localErrorCode += ErrorCode.ERR_TUP_RESPONSE_DECODE;
            }
        }
        return localErrorCode;
    }
}
