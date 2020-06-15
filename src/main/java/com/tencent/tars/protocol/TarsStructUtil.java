package com.tencent.tars.protocol;

import com.tencent.tars.protocol.tars.TarsInputStream;
import com.tencent.tars.protocol.tars.TarsOutputStream;
import com.tencent.tars.protocol.tars.TarsStructBase;

public class TarsStructUtil {
    private final static String ENCODE_TYPE = "UTF-8"; // 编码

    /**
     * 序列化(编码格式:utf-8)
     *
     * @param base
     * @return
     */
    public static byte[] tarsStructToUTF8ByteArray(TarsStructBase base) {
        TarsOutputStream tos = new TarsOutputStream();
        tos.setServerEncoding(ENCODE_TYPE);
        base.writeTo(tos);
        return tos.toByteArray();
    }

    /**
     * 解析结构
     *
     * @param data
     * @param base
     * @return
     */
    public static <T extends TarsStructBase> T getTarsStruct(final byte[] data, final T base) {
        if (null == base) {
            return null;
        }

        base.recyle();
        base.readFrom(createUTF8InputStream(data));
        return base;
    }

    private static TarsInputStream createUTF8InputStream(byte[] data) {
        TarsInputStream tis = new TarsInputStream(data);
        tis.setServerEncoding(ENCODE_TYPE);
        return tis;
    }

}
