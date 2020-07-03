package com.tencent.tars.tup.session;

import com.tencent.tars.tup.ServantInvokeContext;

import java.io.IOException;
import java.io.InputStream;

public abstract class Session {

    protected INetListener mNetListener;

    protected int connectTimeout = 8 * 1000;

    protected int readTimeout = 8000;

    public void setReadTimeout(int timeout) {
        this.readTimeout = timeout;
    }

    public void setConnectTimeout(int timeout) {
        this.connectTimeout = timeout;
    }

    public abstract int start(boolean restart);

    public abstract int sendData(byte[] data);

    public abstract int sendData(byte[] data, ServantInvokeContext ctx);
    public abstract int stop();


    public void setNetworkListener(INetListener networkListener) {
        mNetListener = networkListener;
    }

    public void removeListener() {
        mNetListener = null;
    }

    protected void handleData(final byte[] data) {
        //log.debug("[network]handleData()，rspData.length " + data.length + " at  " + Thread.currentThread().getName());
        if (null != mNetListener) {
            mNetListener.handleData(data);
        }
    }

    /**
     * 取出bytes数据
     */
    protected static byte[] getBytesFromIS(InputStream is, int start, int len) throws IOException {
        int pos = start;
        byte[] buffer = new byte[len];
        int actualSize = 0;
        int tempLen = len;
        while (actualSize < len && tempLen > 0) {
            int rcvSize = is.read(buffer, pos, tempLen);
            if (rcvSize < 0) {
                break;
            }
            actualSize += rcvSize;
            pos += rcvSize;
            tempLen -= rcvSize;
        }
        if (actualSize != len) {
            return null;
        }
        return buffer;
    }
}
