package com.tencent.tars.protocol.exceptions;

@SuppressWarnings("serial")
public class JsonDecodeException extends RuntimeException {

    public JsonDecodeException(String string) {
        super(string);
    }

}
