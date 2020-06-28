package com.tencent.tars.jmeter.sampler;

import org.apache.jmeter.samplers.Interruptible;
import org.apache.jmeter.samplers.SampleResult;

import java.net.InetSocketAddress;
import java.net.Proxy;

public abstract class TarsAbstractImpl implements Interruptible {

    protected final TarsSamplerBase testElement;

    protected int errCode = 0;
    protected String errorMessage = "";
    protected Throwable localThrowable = null;
    protected String threadName = Thread.currentThread().getName();

    protected TarsAbstractImpl(TarsSamplerBase testElement) {
        this.testElement = testElement;
    }

    public abstract TarsSampleResult sample(TarsSampleResult result);

    protected void errorResult(Throwable t, TarsSampleResult res) {
         testElement.errorResult(t, res);
    }

    protected Proxy getProxy() {
        if (testElement.getProxyHost().isEmpty() || testElement.getProxyPort() <= 0) {
            return null;
        }
        return new Proxy(Proxy.Type.HTTP,
                new InetSocketAddress(testElement.getProxyHost(), testElement.getProxyPort()));
    }

    protected void updateResult(TarsSampleResult result, String resultData) {
        result.setRetCode(errCode);
        result.setErrorMessage(this.errorMessage);
        if (testElement.getSuccessfulStatus(result)) {
            testElement.successResult(result, resultData);
        } else {
            errorResult(this.localThrowable, result);
        }
        this.reset();
    }

    public void reset(){
        this.localThrowable = null;
        this.errCode = 0;
    }
}
