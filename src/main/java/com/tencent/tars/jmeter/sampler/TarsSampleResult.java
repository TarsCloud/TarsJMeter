/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.tars.jmeter.sampler;

import org.apache.jmeter.samplers.SampleResult;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a specialisation of the SampleResult class for the tars protocol.
 */
public class TarsSampleResult extends SampleResult {

    private static final long serialVersionUID = 241L;

    private static final String TYPE_TARS = "Tars";

    private String requestUrl = "";

    private String servantId = "";

    private int retCode = 0;

    private String tarsErrorMessage = "";

    private String requestBody = "";

    private Map<String, String> requestHeaders = new HashMap<>();

    private Map<String, String> responseHeaders = new HashMap<>();

    public TarsSampleResult() {
        super();
    }

    public TarsSampleResult(long elapsed) {
        super(elapsed, true);
    }

    /**
     * Construct a 'parent' result for an already-existing result, essentially
     * cloning it
     *
     * @param res existing sample result
     */
    public TarsSampleResult(TarsSampleResult res) {
        super(res);
        this.requestUrl = res.requestUrl;
        servantId = res.servantId;
        retCode = res.retCode;
        tarsErrorMessage = res.tarsErrorMessage;
        this.requestBody = res.requestBody;
    }

    public String getRequestHeaders() {
        this.requestHeaders.put(TYPE_TARS, getRequestUrl());
        StringBuilder builder = new StringBuilder();
        for (String key : this.requestHeaders.keySet()) {
            builder.append(key).append("\t: ").append(this.requestHeaders.get(key)).append("\n");
        }
        return builder.toString();
    }

    public String getResponseHeaders() {
        StringBuilder builder = new StringBuilder();
        for (String key : this.responseHeaders.keySet()) {
            builder.append(key).append("\t: ").append(this.responseHeaders.get(key)).append("\n");
        }
        return builder.toString();

    }

    public void addRequestHeader(String key, String value) {
        this.requestHeaders.put(key, value);
    }

    public void addResponseHeader(String key, String value) {
        this.responseHeaders.put(key, value);
    }

    public String getErrorMessage() {
        return tarsErrorMessage;
    }

    public void setErrorMessage(String tarsErrorMessage) {
        this.tarsErrorMessage = tarsErrorMessage;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getServantId() {
        return servantId;
    }

    public void setServantId(String servantId) {
        this.servantId = servantId;
    }

    public int getRetCode() {
        return retCode;
    }

    public void setRetCode(int retCode) {
        this.retCode = retCode;
    }
}
