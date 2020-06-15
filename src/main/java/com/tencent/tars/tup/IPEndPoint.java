package com.tencent.tars.tup;

public class IPEndPoint implements Cloneable {
    private int mPort;
    private String mIp;

    @Override
    protected IPEndPoint clone() throws CloneNotSupportedException {
        super.clone();
        return new IPEndPoint(mIp, mPort);
    }

    public IPEndPoint(String ip, int port) {
        mIp = ip;
        mPort = port;
    }

    public void setPort(int port) {
        mPort = port;
    }

    public int getPort() {
        return mPort;
    }

    public void setIp(long ip) {
        String[] temp = new String[4];
        temp[0] = Integer.valueOf((int) ((ip) & 0xff)).toString();
        temp[1] = Integer.valueOf((int) ((ip >> 8) & 0xff)).toString();
        temp[2] = Integer.valueOf((int) ((ip >> 16) & 0xff)).toString();
        temp[3] = Integer.valueOf((int) ((ip >> 24) & 0xff)).toString();
        mIp = temp[0] + "." + temp[1] + "." + temp[2] + "." + temp[3];
    }

    public void setIp(String ip) {
        mIp = ip;
    }

    public String getIp() {
        return mIp;
    }

    @Override
    public String toString() {
        if (mPort >= 0) {
            return mIp + ":" + mPort;
        } else {
            return mIp;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof IPEndPoint)) {
            return false;
        }
        IPEndPoint ip = (IPEndPoint) o;
        return ip.mIp.equals(mIp) && ip.mPort == mPort;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

}
