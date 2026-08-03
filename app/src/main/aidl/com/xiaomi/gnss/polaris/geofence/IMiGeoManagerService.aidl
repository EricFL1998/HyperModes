package com.xiaomi.gnss.polaris.geofence;

import android.content.ComponentName;
import android.location.Location;
import android.os.Bundle;
import com.xiaomi.gnss.polaris.geofence.MiGeofence;

interface IMiGeoManagerService {
    String getVendorVersion();
    String addGeofenceWithFlag(String packageName, inout MiGeofence geofence, int flag);
    String addGeofence(String packageName, inout MiGeofence geofence);
    void deleteGeofence(String packageName, in MiGeofence geofence);
    void deleteGeofenceById(String packageName, String geofenceId);
    void updateGeofence(String packageName, inout MiGeofence geofence);
    List<MiGeofence> listGeofence(String packageName);
    MiGeofence findGeofenceById(String packageName, String geofenceId);
    void registerComponent(String packageName, in ComponentName component);
    ComponentName getComponent(String packageName);
    void sendDebugEvent(String packageName, in Location location, int event, in MiGeofence geofence);
    Bundle getAllGeofenceStatus(String packageName);
    int getGeofenceStatus(String packageName, String geofenceId);
}
