package com.tencent.tars.tup.session;

import com.tencent.tars.tup.IPEndPoint;
import com.tencent.tars.tup.ServantInvokeContext;
import com.tencent.tars.utils.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.concurrent.atomic.AtomicBoolean;

public class UdpSession extends Session {
    private static final Logger log = LoggerFactory.getLogger(UdpSession.class);

    private AtomicBoolean mStopped = new AtomicBoolean(true); // 停止标记位
    private final Object mSndLock = new Object();
    private DatagramSocket mSocket;
    private DatagramPacket mSendPacket;
    private DatagramPacket mRecvPacket;
    private int recvSize = 1024;
    // private static final int RECV_MAX_SIZE = 16384;
    private IPEndPoint mIPEndPoint;

    private String threadName = Thread.currentThread().getName();

    public UdpSession(IPEndPoint endPoint) {
        this.mIPEndPoint = endPoint;
    }


    /**
     * udp network是否已启动
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
        if (!isSocketClosed()) {
            stopSocket();
        }
        int ret = checkSocket(mIPEndPoint);
        if (ret != ErrorCode.ERR_NONE) {
            log.info("startService()" + " checkSocket() ret = " + ret);
            return ret;
        }
        return ErrorCode.ERR_NONE;
    }


    /**
     * 启动网络，支持重启网络
     */
    /**
     * 从ip策略中尝试获取到可用ip
     */
    private int checkSocket(final IPEndPoint ipPoint) {
        int ret;
        try {
            mSocket = new DatagramSocket();
            InetAddress serverAddr = InetAddress.getByName(ipPoint.getIp());
            mSocket.connect(serverAddr,ipPoint.getPort());
            mSocket.setSoTimeout(this.readTimeout);
            ret = 0;
        } catch (UnknownHostException e) {
            ret = ErrorCode.ERR_NETWORK_UNKNOWN_HOST_EXCEPTION;
            log.error("checkSocket() UnknownHostException ", e);
        } catch (Throwable t) {
            ret = ErrorCode.ERR_NETWORK_START_THROWABLE;
            log.error("checkSocket() Throwable ", t);
        }
        return ret;
    }


    public int recvInSync() {
        int retcode = ErrorCode.ERR_NONE;
        try {
            byte[] respData;
            mRecvPacket =  new DatagramPacket(new byte[recvSize],recvSize);
            mSocket.receive(mRecvPacket);
            ByteArrayInputStream iss = new ByteArrayInputStream(mRecvPacket.getData());
            DataInputStream is = new DataInputStream(iss);
            int size = is.readInt() - 4;
            respData = getBytesFromIS(is, 0, size);
            is.close();
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
            retcode = ErrorCode.ERR_NETWORK_UDP_RECV_EOF;
        } catch (IOException e) {
            log.error("sendDataInSync() has a IOException at " + threadName, e);
            retcode = ErrorCode.ERR_NETWORK_UDP_RECV_IOE;
        } catch (Throwable t) {
            log.error("sendDataInSync() has a Throwable at " + threadName, t);
            retcode = ErrorCode.ERR_NETWORK_UDP_RECV_THROWABLE;
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
            ByteArrayOutputStream oss = new ByteArrayOutputStream();
            DataOutputStream os = new DataOutputStream(oss);
            os.writeInt(data.length + 4);
            os.write(data);
            mSendPacket = new DatagramPacket(oss.toByteArray(),data.length+4);
            oss.close();
            mSocket.send(mSendPacket);
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

    private boolean stopSocket() {
        if (isSocketClosed()) {
            log.info("stopService socket success:true");
            return true;
        }

        boolean ret = true;
        try {
            mSocket.close();
            mRecvPacket = null;
            mSendPacket = null;
            mSocket = null;
            // 睡眠1秒，待资源释放结束
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            log.error("stopSocket() " + "InterruptedException ", e);
        }
        return ret;
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
