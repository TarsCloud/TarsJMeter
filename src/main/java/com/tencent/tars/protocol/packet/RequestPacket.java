//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.tencent.tars.protocol.packet;

import com.tencent.tars.protocol.TarsStructUtil;
import com.tencent.tars.protocol.tars.TarsInputStream;
import com.tencent.tars.protocol.tars.TarsOutputStream;
import com.tencent.tars.protocol.tars.TarsStructBase;
import com.tencent.tars.utils.TextUtils;

import java.util.HashMap;
import java.util.Map;

public final class RequestPacket extends TarsStructBase {
    public short iVersion = 0;
    public byte cPacketType = 0;
    public int iMessageType = 0;
    public int iRequestId = 0;
    public String sServantName = null;
    public String sFuncName = null;
    public byte[] sBuffer;
    public int iTimeout = 0;
    public Map<String, String> context;
    public Map<String, String> status;
    static byte[] cache_sBuffer = null;
    static Map<String, String> cache_context = null;

    public RequestPacket() {
    }

    public RequestPacket(short iVersion, byte cPacketType, int iMessageType, int iRequestId, String sServantName, String sFuncName, byte[] sBuffer, int iTimeout, Map<String, String> context, Map<String, String> status) {
        this.iVersion = iVersion;
        this.cPacketType = cPacketType;
        this.iMessageType = iMessageType;
        this.iRequestId = iRequestId;
        this.sServantName = sServantName;
        this.sFuncName = sFuncName;
        this.sBuffer = sBuffer;
        this.iTimeout = iTimeout;
        this.context = context;
        this.status = status;
    }

    public boolean equals(Object o) {
        RequestPacket t = (RequestPacket)o;

        return TarsUtil.equals(1, t.iVersion) && TarsUtil.equals(1, t.cPacketType) && TarsUtil.equals(1, t.iMessageType) && TarsUtil.equals(1, t.iRequestId) && TarsUtil.equals(1, t.sServantName) && TarsUtil.equals(1, t.sFuncName) && TarsUtil.equals(1, t.sBuffer) && TarsUtil.equals(1, t.iTimeout) && TarsUtil.equals(1, t.context) && TarsUtil.equals(1, t.status);
    }

    public Object clone() {
        Object o = null;

        try {
            o = super.clone();
        } catch (CloneNotSupportedException var3) {
            assert false;
        }

        return o;
    }

    public void writeTo(TarsOutputStream _os) {
        _os.write(this.iVersion, 1);
        _os.write(this.cPacketType, 2);
        _os.write(this.iMessageType, 3);
        _os.write(this.iRequestId, 4);
        _os.write(this.sServantName, 5);
        _os.write(this.sFuncName, 6);
        _os.write(this.sBuffer, 7);
        _os.write(this.iTimeout, 8);
        _os.write(this.context, 9);
        _os.write(this.status, 10);
    }

    public void readFrom(TarsInputStream _is) {
        try {
            this.iVersion = _is.read(this.iVersion, 1, true);
            this.cPacketType = _is.read(this.cPacketType, 2, true);
            this.iMessageType = _is.read(this.iMessageType, 3, true);
            this.iRequestId = _is.read(this.iRequestId, 4, true);
            this.sServantName = _is.readString(5, true);
            this.sFuncName = _is.readString(6, true);
            if (null == cache_sBuffer) {
                cache_sBuffer = new byte[]{0};
            }

            this.sBuffer = (byte[])_is.read(cache_sBuffer, 7, true);
            this.iTimeout = _is.read(this.iTimeout, 8, true);
            if (null == cache_context) {
                cache_context = new HashMap();
                cache_context.put("", "");
            }

            this.context = (Map)_is.read(cache_context, 9, true);
            if (null == cache_context) {
                cache_context = new HashMap();
                cache_context.put("", "");
            }

            this.status = (Map)_is.read(cache_context, 10, true);
        } catch (Exception var3) {
            var3.printStackTrace();
            System.out.println("RequestPacket decode error " + TextUtils.byteArrayToHexStr(this.sBuffer));
            throw new RuntimeException(var3);
        }
    }

    public void display(StringBuilder _os, int _level) {
        TarsDisplayer _ds = new TarsDisplayer(_os, _level);
        _ds.display(this.iVersion, "iVersion");
        _ds.display(this.cPacketType, "cPacketType");
        _ds.display(this.iMessageType, "iMessageType");
        _ds.display(this.iRequestId, "iRequestId");
        _ds.display(this.sServantName, "sServantName");
        _ds.display(this.sFuncName, "sFuncName");
        _ds.display(this.sBuffer, "sBuffer");
        _ds.display(this.iTimeout, "iTimeout");
        _ds.display(this.context, "context");
        _ds.display(this.status, "status");
    }

    public static void main(String[] args) {
        RequestPacket req = new RequestPacket();
        req.sFuncName="ok";
        req.sServantName="112";
        req.sBuffer = new byte[] {1,10};
        req.context = new HashMap<>();
        req.status = new HashMap<>();
        byte[] bs = TarsStructUtil.tarsStructToUTF8ByteArray(req);
        req.sFuncName="OK2";
        TarsStructUtil.getTarsStruct(bs, req);
        System.out.println(req.sFuncName);
    }

}
