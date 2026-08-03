package com.xiaomi.gnss.polaris.sdk.exception;

public class PolarisException extends Exception {
    public PolarisException() {
    }

    public PolarisException(String str) {
        super(str);
    }

    public PolarisException(String str, Throwable th) {
        super(str, th);
    }

    public PolarisException(Throwable th) {
        super(th);
    }

    public PolarisException(String str, Throwable th, boolean z10, boolean z11) {
        super(str, th, z10, z11);
    }
}
