package com.xiaomi.gnss.polaris.sdk;

import com.xiaomi.gnss.polaris.sdk.exception.PolarisException;

/* JADX INFO: loaded from: classes3.dex */
public interface IPolarisManager {
    void connectPolarisServiceSync() throws PolarisException;

    String getPolarisSdkVersion();

    String getPolarisServerVersion() throws PolarisException;

    IChildService getSubService(PolarisManager.ServiceType serviceType) throws PolarisException;

    boolean isPolarisSupport();

    boolean isSubServiceSupport(PolarisManager.ServiceType serviceType);
}
