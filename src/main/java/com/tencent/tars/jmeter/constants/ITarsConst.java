package com.tencent.tars.jmeter.constants;

public interface ITarsConst {

    String ARGUMENTS = "TarsSampler.Arguments"; // $NON-NLS-1$

    String FUNC_ARGUMENTS = "TarsSampler.FuncArguments"; // $NON-NLS-1$

    String CONTEXT_ARGUMENTS = "TarsSampler.ContextArguments"; // $NON-NLS-1$

    String STATUS_ARGUMENTS = "TarsSampler.StatusArguments"; // $NON-NLS-1$

    String SERVANT_IP = "TarsSampler.ip";

    String SERVANT_PORT = "TarsSampler.port";
    String SERVANT_PATH = "TarsSampler.servant";

    String FUNC_NAME = "TarsSampler.funcName";

    String TARS_RETURN_TYPE = "TarsSampler.returnType";

    String TARS_RETURN_VALUE = "TarsSampler.returnValue";

    String RETURN_VALUE_NAME = "returnValue";

    // http proxy url
    String KEY_PROXY_HOST = "proxyHost";

    String KEY_PROXY_PORT = "proxyPort";

    // 是否使用长链接
    String KEY_KEEP_ALIVE = "isKeepAlive";
    //连接超时值
    String KEY_CONNECT_TIMEOUT = "connectTimeout";
    //数据请求超时值
    String KEY_READ_TIMEOUT = "readTimeout";
    //成功框架标记
    String KEY_RET_CODE = "successRetCode";

    String KEY_REQ_VERSION = "Tup.iVersion";
    String KEY_REQ_PKT_TYPE = "Tup.cPacketType";
    String KEY_REQ_MSG_TYPE = "Tup.iMessageType";
    String KEY_REQ_I_TIMEOUT = "Tup.iTimeout";
}
