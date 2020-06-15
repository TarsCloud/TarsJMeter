package com.tencent.tars.protocol.exceptions;

@SuppressWarnings("serial")
public class JsonEncodeException extends RuntimeException {

    public JsonEncodeException(String string) {
        super(string);
    }
}
