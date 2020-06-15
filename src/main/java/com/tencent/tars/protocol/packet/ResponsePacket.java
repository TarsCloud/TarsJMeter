package com.tencent.tars.protocol.packet;


import com.tencent.tars.protocol.tars.TarsInputStream;
import com.tencent.tars.protocol.tars.TarsOutputStream;
import com.tencent.tars.protocol.tars.TarsStructBase;

public class ResponsePacket extends TarsStructBase {
    public short iVersion = 0;
    public byte cPacketType = 0;
    public int iRequestId = 0;
    public int iMessageType = 0;
    public int iRet = 0;
    public byte [] SBuffer = null;
    public java.util.Map<String, String> status = null;
    public String sResultDesc = "";
    public java.util.Map<String, String> context = null;

    public ResponsePacket(){
    }

    public void writeTo(TarsOutputStream _os){
        _os.write(iVersion, 1);
        _os.write(cPacketType, 2);
        _os.write(iRequestId, 3);
        _os.write(iMessageType, 4);
        _os.write(iRet, 5);
        if (null != SBuffer){
            _os.write(SBuffer, 6);
        }
        if (null != status){
            _os.write(status, 7);
        }
        if (null != sResultDesc){
            _os.write(sResultDesc, 8);
        }
        if (null != context){
            _os.write(context, 9);
        }
    }

    static byte [] cache_SBuffer;
    static java.util.Map<String, String> cache_status;
    static java.util.Map<String, String> cache_context;

    static {
        cache_SBuffer = (byte[]) new byte[1];
        byte __var_6 = 0;
        ((byte[])cache_SBuffer)[0] = __var_6;

        cache_status = new java.util.HashMap<String, String>();
        String __var_7 = "";
        String __var_8 = "";
        cache_status.put(__var_7, __var_8);

        cache_context = new java.util.HashMap<String, String>();
        String __var_9 = "";
        String __var_10 = "";
        cache_context.put(__var_9, __var_10);

    }

    public void readFrom(TarsInputStream _is){
        this.iVersion = (short) _is.read(iVersion, 1, true);
        this.cPacketType = (byte) _is.read(cPacketType, 2, true);
        this.iRequestId = (int) _is.read(iRequestId, 3, true);
        this.iMessageType = (int) _is.read(iMessageType, 4, true);
        this.iRet = (int) _is.read(iRet, 5, true);
        this.SBuffer = (byte []) _is.read(cache_SBuffer, 6, false);
        this.status = (java.util.Map<String, String>) _is.read(cache_status, 7, false);
        this.sResultDesc =  _is.readString(8, false);
        this.context = (java.util.Map<String, String>) _is.read(cache_context, 9, false);
    }
}
