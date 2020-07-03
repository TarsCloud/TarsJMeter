package com.tencent.tars.tup.session;

import com.tencent.tars.tup.IPEndPoint;
import com.tencent.tars.tup.ServantInvokeContext;
import com.tencent.tars.utils.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class TcpSession extends Session {
    private static final Logger log = LoggerFactory.getLogger(TcpSession.class);

    private AtomicBoolean mStopped = new AtomicBoolean(true); // 停止标记位
    private final Object mSndLock = new Object();
    private Socket mSocket;
    private DataOutputStream mSocketWriter; // 写入流
    private DataInputStream mSocketReader; // 读取流
    private IPEndPoint mIPEndPoint;
    private Proxy mProxy = null;

    private String threadName = Thread.currentThread().getName();

    public TcpSession(IPEndPoint endPoint, Proxy proxy) {
        this.mIPEndPoint = endPoint;
        this.mProxy = proxy;
    }

    public TcpSession(IPEndPoint endPoint) {
        this.mIPEndPoint = endPoint;
    }

    /**
     * tcp network是否已启动
     */
    private boolean isStarted() {
        return !mStopped.get();
    }

    /**
     * 启动网络，支持重启网络
     */
    @Override
    public synchronized int start(boolean isRestart) {
        if (isStarted() && !isRestart) {
            log.info("startService()" + " isStarted() " + isStarted());
            return ErrorCode.ERR_NONE;
        }
        int ret = checkSocket(mIPEndPoint);
        if (ret != ErrorCode.ERR_NONE) {
            log.info("startService()" + " checkSocket() ret = " + ret);
            return ret;
        }
        return ErrorCode.ERR_NONE;
    }

    public int recvInSync() {
        int retcode = ErrorCode.ERR_NONE;
        try {
            byte[] respData;
            int size = mSocketReader.readInt() - 4;
            respData = getBytesFromIS(mSocketReader, 0, size);
            if (respData == null) {
                String err = "decode resp data get error data. because of get length (" + size + ") data return null at " + threadName;
                log.error(err);
                retcode = ErrorCode.ERR_NETWORK_RECV_NULL;
                return retcode;
            }
            if (respData.length != size) {
                String err = "decode resp data get error data. because of error length (" + size + ") or getBytes error at " + threadName;
                log.error(err);
                retcode = ErrorCode.ERR_NETWORK_RECV_0;
                return retcode;
            }
            handleData(respData);
        } catch (EOFException e) {
            log.error("sendDataInSync() has a EOFException at " + threadName, e);
            retcode = ErrorCode.ERR_NETWORK_TCP_RECV_EOF;
        } catch (IOException e) {
            log.error("sendDataInSync() has a IOException at " + threadName, e);
            retcode = ErrorCode.ERR_NETWORK_TCP_RECV_IOE;
        } catch (Throwable t) {
            log.error("sendDataInSync() has a Throwable at " + threadName, t);
            retcode = ErrorCode.ERR_NETWORK_TCP_RECV_THROWABLE;
        }
        return retcode;
    }

    @Override
    public int sendData(final byte[] data) {
        this.threadName = Thread.currentThread().getName();
        if (!isSocketConnected()) {
            log.error("ERR_NETWORK_SOCKET_NOT_CONNECTED");
            return ErrorCode.ERR_NETWORK_SOCKET_NOT_CONNECTED;
        }
        return sendDataInSync(data);
    }

    @Override
    public int sendData(final byte[] data, ServantInvokeContext ctx) {
        this.threadName = Thread.currentThread().getName();
        if (!isSocketConnected()) {
            log.error("ERR_NETWORK_SOCKET_NOT_CONNECTED");
            return ErrorCode.ERR_NETWORK_SOCKET_NOT_CONNECTED;
        }
        ctx.setSendBytes(data.length + 4);
        return sendDataInSync(data);
    }

    private int sendDataInSync(final byte[] data) {
        try {
            mSocketWriter.writeInt(data.length + 4);
            mSocketWriter.write(data);
            mSocketWriter.flush();
        } catch (SocketException e) {
            log.error("sendDataInSync()" + " has a SocketException at" + threadName, e);
            return ErrorCode.ERR_NETWORK_SOCKET_TIMEOUT_EXCEPTION_RW;
        } catch (Throwable t) {
            log.error("sendDataInSync()" + " has a Throwable at" + threadName, t);
            return ErrorCode.ERR_NETWORK_EXCEPTION;
        }
        return recvInSync();
    }

    /**
     * 关闭网络
     * 关闭过程中的错误信息不需要。
     */
    public synchronized int stop() {
        INetListener temp = this.mNetListener;
        removeListener();
        boolean ret = stopSocket();
        if (!ret) {
            return ErrorCode.ERR_NETWORK_CLOSE_FAILED;
        }
        mStopped.set(true);
        this.setNetworkListener(temp);
        return ErrorCode.ERR_NONE;
    }

    /**
     * 从ip策略中尝试获取到可用ip
     */
    private int checkSocket(final IPEndPoint ipPoint) {
        if (null == ipPoint) {
            return ErrorCode.ERR_NETWORK_ILLEGAL_ARGUMENT;
        }

        int ret;
        try {
            if (startSocket(ipPoint)) {
                ret = ErrorCode.ERR_NONE;
            } else {
                ret = ErrorCode.ERR_NETWORK_START_UNKNOWN;
            }
        } catch (UnknownHostException e) {
            ret = ErrorCode.ERR_NETWORK_UNKNOWN_HOST_EXCEPTION;
            log.error("checkSocket() UnknownHostException ", e);
        } catch (SocketTimeoutException e) {
            ret = ErrorCode.ERR_NETWORK_TCP_CHECK_SOCKET_TIMEOUT_FAILED;
            log.error("checkSocket() SocketTimeoutException ", e);
        } catch (Throwable t) {
            ret = ErrorCode.ERR_NETWORK_START_THROWABLE;
            log.error("checkSocket() Throwable ", t);
        }
        return ret;
    }


    private boolean startSocket(final IPEndPoint ipPoint) throws IOException {
        if (!isSocketClosed()) {
            stopSocket();
        }

        mIPEndPoint = ipPoint;
        InetAddress serverAddr = InetAddress.getByName(ipPoint.getIp());
        mSocket = acquireSocketWithTimeout(serverAddr, ipPoint.getPort());
        mSocketWriter = new DataOutputStream(mSocket.getOutputStream());
        mSocketReader = new DataInputStream(mSocket.getInputStream());
        mSocket.setSoTimeout(this.readTimeout);
        return isSocketConnected();
    }


    private boolean stopSocket() {
        if (isSocketClosed()) {
            log.info("stopService socket success:true");
            return true;
        }

        boolean ret = true;
        try {
            mSocket.close();
            mSocketReader.close();
            mSocketWriter.close();
            mSocket = null;
            mProxy = null;
            // 睡眠1秒，待资源释放结束
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("stopSocket() " + "InterruptedException ", e);
        } catch (IOException e) {
            ret = false;
            log.error("stopSocket() " + "mSocket.close() ", e);
        }
        return ret;
    }


    private Socket acquireSocketWithTimeout(InetAddress dstAddress, int dstPort) throws IOException {
        log.info("MMConnectionManager--> acquireSocketWithTimeOut, addr: " + dstAddress + ", port: " + dstPort);
        Socket socket;
        if (this.mProxy == null) {
            socket = new Socket();
        } else {
            socket = new Socket(this.mProxy);
        }
        socket.setSoLinger(false, 0);
        socket.connect(new InetSocketAddress(dstAddress, dstPort), connectTimeout);
        return socket;
    }

    private boolean isSocketClosed() {
        if (null == mSocket) {
            return true;
        }
        return (mSocket.isClosed());
    }

    private boolean isSocketConnected() {
        if (null == mSocket) {
            return false;
        }
        return (!isSocketClosed() && mSocket.isConnected());
    }
}
