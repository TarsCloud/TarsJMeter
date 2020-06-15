package com.tencent.tars.jmeter.sampler;

import com.tencent.tars.utils.ErrorCode;

public class TarsSamplerProxy extends TarsSamplerBase {
    private transient TarsAbstractImpl impl;

    public TarsSamplerProxy() {
        super();
    }

    @Override
    protected TarsSampleResult sample(TarsSampleResult result) {
        if (impl == null) { // Not called from multiple threads, so this is OK
            try {
                impl = new TarsImpl(this);
            } catch (Exception ex) {
                result.setRetCode(result.getRetCode() + ErrorCode.ErrorProxyException);
                errorResult(ex, result);
            }
        }
        return impl.sample(result);
    }
}