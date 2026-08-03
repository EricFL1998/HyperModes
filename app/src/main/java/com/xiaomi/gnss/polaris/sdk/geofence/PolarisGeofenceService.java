package com.xiaomi.gnss.polaris.sdk.geofence;

import android.content.ComponentName;
import android.location.Location;
import com.xiaomi.gnss.polaris.geofence.MiGeofence;
import com.xiaomi.gnss.polaris.sdk.IChildService;
import com.xiaomi.gnss.polaris.sdk.exception.PolarisException;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
public interface PolarisGeofenceService extends IChildService {
    String addGeofence(MiGeofence miGeofence) throws PolarisException;

    String addGeofence(MiGeofence miGeofence, int i10) throws PolarisException;

    void deleteGeofence(MiGeofence miGeofence) throws PolarisException;

    void deleteGeofence(String str) throws PolarisException;

    ComponentName getComponent() throws PolarisException;

    MiGeofence getGeofence(String str) throws PolarisException;

    String getVendorVersion() throws PolarisException;

    List<MiGeofence> listGeofence() throws PolarisException;

    void registerComponent(ComponentName componentName) throws PolarisException;

    void sendDebugEvent(Location location, int i10, MiGeofence miGeofence) throws PolarisException;

    void unregisterComponent() throws PolarisException;

    void updateGeofence(MiGeofence miGeofence) throws PolarisException;
}
