package com.tencent.tars.utils;

/**
 * 错误码归拢
 *
 * @author brookechen
 */
public class ErrorCode {
    /**
     * no error.
     */
    public static final int ERR_NONE = 0;

    /**
     * 获取拼装错误的信息
     */
    public static String getErrMessage(int errorCode) {
        return getNetError(errorCode)
                + getTupError(errorCode)
                + getJMeterError(errorCode)
                + getServerError(errorCode);
    }

    /**
     * Tars retCode error.
     * -(1~99)
     */
    public final static int SERVERDECODEERR = -1;

    public final static int SERVERENCODEERR = -2;

    public final static int SERVERNOFUNCERR = -3;

    public final static int SERVERNOSERVANTERR = -4;

    public final static int SERVERRESETGRID = -5;

    public final static int SERVERQUEUETIMEOUT = -6;

    public final static int ASYNCCALLTIMEOUT = -7;

    public final static int PROXYCONNECTERR = -8;

    public static final int SERVEROVERLOAD = -9;

    public final static int SERVERUNKNOWNERR = -99;


    public static String getServerError(int localError) {
        int serverError = filterServerErrCode(localError);
        switch (serverError) {
            case ERR_NONE:
                return "";
            case SERVERDECODEERR:
                return "Server decode error.";
            case SERVERENCODEERR:
                return "Server encode error.";
            case SERVERNOFUNCERR:
                return "Server no function error.";
            case SERVERNOSERVANTERR:
                return "Server not servant error.";
            case SERVERRESETGRID:
                return "Server error set grid.";
            case SERVERQUEUETIMEOUT:
                return "server queue timeout.";
            case ASYNCCALLTIMEOUT:
                return "async call timeout.";
            case PROXYCONNECTERR:
                return "proxy connect error.";
            case SERVEROVERLOAD:
                return "server overload.";
            case SERVERUNKNOWNERR:
            default:
                return "Server unknown code:" + serverError + ".";
        }
    }

    /**
     * jmeter层错误码
     * -(1~99)*100
     */
    private static final int ERROR_JMETER_BASE = 100;

    public static final int ErrorProxyException = -1 * ERROR_JMETER_BASE;
    public static final int ErrorTarsException = -2 * ERROR_JMETER_BASE;
    public static final int ErrorSampleBaseException = -3 * ERROR_JMETER_BASE;
    public static final int ErrorPrepareNetwork = -4 * ERROR_JMETER_BASE;
    public static final int ErrorStopNetwork = -5 * ERROR_JMETER_BASE;
    public static final int ERROR_JMETER_UNKNOWN = -99 * ERROR_JMETER_BASE;


    public static String getJMeterError(int localError) {
        int jMeterError = filterJMeterErrCode(localError);
        switch (jMeterError) {
            case ERR_NONE:
                return "";
            case ErrorProxyException:
                return "JMeter ErrorProxyException, ";
            case ErrorTarsException:
                return "JMeter ErrorTarsException, ";
            case ErrorSampleBaseException:
                return "JMeter ErrorSampleBaseException, ";
            case ErrorPrepareNetwork:
                return "JMeter ErrorPrepareNetwork, ";
            case ErrorStopNetwork:
                return "JMeter ErrorStopNetwork, ";
            case ERROR_JMETER_UNKNOWN:
            default:
                return "JMeter unknown code:" + jMeterError + ", ";
        }
    }


    /**
     * tup层错误码
     * -(1~99)*10000
     */
    private static final int ERROR_TUP_BASE = 10000;
    public static final int ERR_TUP_RESPONSE_BODY_EMPTY = -1 * ERROR_TUP_BASE;
    public static final int ERR_TUP_RESPONSE_DECODE = -2 * ERROR_TUP_BASE;
    public static final int ERR_TUP_REQUEST_SEQ_NOT_MATCH = -3 * ERROR_TUP_BASE;
    public static final int ERR_TUP_UNKNOWN = -99 * ERROR_TUP_BASE;


    public static String getTupError(int localErr) {
        int tupError = filterTupErrCode(localErr);
        switch (tupError) {
            case ERR_NONE:
                return "";
            case ERR_TUP_RESPONSE_BODY_EMPTY:
                return "TUP ERR_TUP_RESPONSE_BODY_EMPTY, ";
            case ERR_TUP_RESPONSE_DECODE:
                return "TUP ERR_TUP_RESPONSE_DECODE, ";
            case ERR_TUP_REQUEST_SEQ_NOT_MATCH:
                return "TUP ERR_TUP_REQUEST_SEQ_NOT_MATCH, ";
            case ERR_TUP_UNKNOWN:
            default:
                return "Tup unknown code:" + tupError + ", ";
        }
    }

    /**
     * 网络层错误码
     * -(1~99)*1000000
     */
    private static final int ERROR_NETWORK_BASE = 1000000;

    public static final int ERR_NETWORK_TCP_CHECK_SOCKET_TIMEOUT_FAILED = -1 * ERROR_NETWORK_BASE; // tcp check socket timeout
    public static final int ERR_NETWORK_UNKNOWN_HOST_EXCEPTION = -2 * ERROR_NETWORK_BASE; // 未知主机
    public static final int ERR_NETWORK_SOCKET_TIMEOUT_EXCEPTION_RW = -3 * ERROR_NETWORK_BASE; // 读写时的socket超时（业务数据可能已经到了后台）
    public static final int ERR_NETWORK_EXCEPTION = -4 * ERROR_NETWORK_BASE; // 其他网络 异常
    public static final int ERR_NETWORK_SOCKET_NOT_CONNECTED = -5 * ERROR_NETWORK_BASE; // socket未连接（一般不会出现）
    public static final int ERR_NETWORK_CLOSE_FAILED = -6 * ERROR_NETWORK_BASE; // 关闭连接失败
    public static final int ERR_NETWORK_ILLEGAL_ARGUMENT = -7 * ERROR_NETWORK_BASE; // 非法参数
    public static final int ERR_NETWORK_TCP_RECV_EOF = -8 * ERROR_NETWORK_BASE; // 回包读取EOF
    public static final int ERR_NETWORK_TCP_RECV_IOE = -9 * ERROR_NETWORK_BASE; // 回包read IOE
    public static final int ERR_NETWORK_TCP_RECV_THROWABLE = -10 * ERROR_NETWORK_BASE; // 回包读取时，抛出异常
    public static final int ERR_NETWORK_RECV_NULL = -11 * ERROR_NETWORK_BASE; // 回包读取时，resp为空
    public static final int ERR_NETWORK_RECV_0 = -12 * ERROR_NETWORK_BASE; // 回包读取时，读到0
    public static final int ERR_NETWORK_START_THROWABLE = -13 * ERROR_NETWORK_BASE; // 连接网络时throwable
    public static final int ERR_NETWORK_START_UNKNOWN = -14 * ERROR_NETWORK_BASE; // 连接网络时throwable
    public static final int ERR_NETWORK_UNKNOWN = -99 * ERROR_NETWORK_BASE;


    public static String getNetError(int localErr) {
        int netErr = filterNetworkCode(localErr);
        switch (netErr) {
            case ERR_NONE:
                return "";
            case ERR_NETWORK_TCP_CHECK_SOCKET_TIMEOUT_FAILED:                             //-30000; // tcp check socket timeout
                return "NetErr: tcp check socket timeout, ";
            case ERR_NETWORK_UNKNOWN_HOST_EXCEPTION:                             //-70000; // 未知主机
                return "NetErr: unknown host, ";
            case ERR_NETWORK_SOCKET_TIMEOUT_EXCEPTION_RW:                             //-120000; // 读写时的socket超时（业务数据可能已经到了后台）
                return "NetErr: read socket timeout, ";
            case ERR_NETWORK_EXCEPTION:                                     //-150000; // 其他网络异常
                return "NetErr: other network exception, ";
            case ERR_NETWORK_SOCKET_NOT_CONNECTED:                             //-180000; // socket未连接（一般不会出现）
                return "NetErr: socket not connected, ";
            case ERR_NETWORK_CLOSE_FAILED:                             //-210000; // 关闭连接失败
                return "NetErr: network close fail, ";
            case ERR_NETWORK_ILLEGAL_ARGUMENT:                             //-240000; // 非法参数
                return "NetErr: network illegal argument, ";
            case ERR_NETWORK_TCP_RECV_EOF:                             //-560000; // 回包读取EOF
                return "NetErr: tcp recv eof, ";
            case ERR_NETWORK_TCP_RECV_IOE:                             //-570000; // 回包read IOE
                return "NetErr: tcp recv ioe, ";
            case ERR_NETWORK_TCP_RECV_THROWABLE:                             //-580000; // 回包读取时，抛出异常
                return "NetErr: tcp recv throwable, ";
            case ERR_NETWORK_RECV_NULL:                             //-590000; // 回包读取时，resp为空
                return "NetErr: network recv null, ";
            case ERR_NETWORK_RECV_0:                             //-600000; // 回包读取时，读到0
                return "NetErr: network recv 0 size, ";
            case ERR_NETWORK_START_THROWABLE:
                return "NetErr: check socket throwable, ";
            case ERR_NETWORK_START_UNKNOWN:
                return "NetErr: start socket unknown error, ";
            case ERR_NETWORK_UNKNOWN:
            default:
                return "Network unknown code:" + netErr;
        }
    }

    public static int filterServerErrCode(final int retCode) {
        return (retCode % ERROR_JMETER_BASE);

    }

    /**
     * 获取jmeter层错误码
     */
    public static int filterJMeterErrCode(final int retCode) {
        return (retCode % ERROR_TUP_BASE) - filterServerErrCode(retCode);
    }

    /**
     * 获得tup层错误码
     */
    public static int filterTupErrCode(final int retCode) {
        return (retCode % ERROR_NETWORK_BASE) - filterJMeterErrCode(retCode) - filterServerErrCode(retCode);
    }


    /**
     * 获得网络错误码
     */
    public static int filterNetworkCode(final int retCode) {
        return (retCode % 100000000) - filterTupErrCode(retCode) - filterJMeterErrCode(retCode) - filterServerErrCode(retCode);
    }
}
