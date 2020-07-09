package com.tencent.tars.tup.session;

public interface INetListener {
    void sentBytesDispatch(int length, final byte[] sentData);

    void handleData(final byte[] data);
}
