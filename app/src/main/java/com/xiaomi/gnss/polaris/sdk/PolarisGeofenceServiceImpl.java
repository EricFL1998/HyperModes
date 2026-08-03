package com.xiaomi.gnss.polaris.sdk;

import android.content.ComponentName;
import android.content.Context;
import android.location.Location;
import com.xiaomi.gnss.polaris.geofence.IMiGeoManagerService;
import com.xiaomi.gnss.polaris.geofence.MiGeofence;
import com.xiaomi.gnss.polaris.sdk.exception.PolarisException;
import com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService;
import java.util.List;

/* JADX INFO: loaded from: classes3.dex */
class PolarisGeofenceServiceImpl implements PolarisGeofenceService {
    private static PolarisGeofenceServiceImpl manager;
    private final String TAG = "MiGeofenceServiceImpl";
    private String callPackageName;
    private Context context;
    private IMiGeoManagerService miGeoManagerService;

    private PolarisGeofenceServiceImpl(Context context, IMiGeoManagerService iMiGeoManagerService) {
        this.context = context;
        this.miGeoManagerService = iMiGeoManagerService;
        this.callPackageName = context.getPackageName();
    }

    private void checkConnect() throws PolarisException {
        IMiGeoManagerService iMiGeoManagerService = this.miGeoManagerService;
        if (iMiGeoManagerService == null || !iMiGeoManagerService.asBinder().isBinderAlive()) {
            throw new PolarisException("service not connect");
        }
    }

    private void checkIllegalParams(MiGeofence miGeofence) throws PolarisException {
        if (miGeofence.getLatitude() > 90.0d || miGeofence.getLatitude() < -90.0d || miGeofence.getLongitude() > 180.0d || miGeofence.getLongitude() < -180.0d || miGeofence.getRadius() <= 0) {
            throw new PolarisException("Exception -->: illegal parameters!");
        }
    }

    public static synchronized PolarisGeofenceServiceImpl getInstance(Context context, IMiGeoManagerService iMiGeoManagerService) {
        try {
            PolarisGeofenceServiceImpl polarisGeofenceServiceImpl = manager;
            if (polarisGeofenceServiceImpl == null) {
                manager = new PolarisGeofenceServiceImpl(context, iMiGeoManagerService);
            } else {
                polarisGeofenceServiceImpl.context = context;
                polarisGeofenceServiceImpl.miGeoManagerService = iMiGeoManagerService;
                polarisGeofenceServiceImpl.callPackageName = context.getPackageName();
            }
        } catch (Throwable th) {
            throw th;
        }
        return manager;
    }

    @Override // com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService
    public String addGeofence(MiGeofence miGeofence, int i10) throws PolarisException {
        checkConnect();
        checkIllegalParams(miGeofence);
        try {
            return this.miGeoManagerService.addGeofenceWithFlag(this.callPackageName, miGeofence, i10);
        } catch (Exception e10) {
            e10.printStackTrace();
            throw new PolarisException("Exception -->" + e10.getMessage());
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService
    public void deleteGeofence(String str) throws PolarisException {
        checkConnect();
        try {
            this.miGeoManagerService.deleteGeofenceById(this.callPackageName, str);
        } catch (Exception e10) {
            e10.printStackTrace();
            throw new PolarisException("Exception -->" + e10.getMessage());
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService
    public ComponentName getComponent() throws PolarisException {
        checkConnect();
        try {
            return this.miGeoManagerService.getComponent(this.callPackageName);
        } catch (Exception e10) {
            e10.printStackTrace();
            throw new PolarisException("Exception -->" + e10.getMessage());
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService
    public MiGeofence getGeofence(String str) throws PolarisException {
        checkConnect();
        try {
            return this.miGeoManagerService.findGeofenceById(this.callPackageName, str);
        } catch (Exception e10) {
            e10.printStackTrace();
            throw new PolarisException("Exception -->" + e10.getMessage());
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService
    public String getVendorVersion() throws PolarisException {
        try {
            return this.miGeoManagerService.getVendorVersion();
        } catch (Exception e10) {
            e10.printStackTrace();
            throw new PolarisException("Exception -->" + e10.getMessage());
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService
    public List<MiGeofence> listGeofence() throws PolarisException {
        checkConnect();
        try {
            return this.miGeoManagerService.listGeofence(this.callPackageName);
        } catch (Exception e10) {
            e10.printStackTrace();
            throw new PolarisException("Exception -->" + e10.getMessage());
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService
    public void registerComponent(ComponentName componentName) throws PolarisException {
        checkConnect();
        try {
            this.miGeoManagerService.registerComponent(this.callPackageName, componentName);
        } catch (Exception e10) {
            e10.printStackTrace();
            throw new PolarisException("Exception -->" + e10.getMessage());
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService
    public void sendDebugEvent(Location location, int i10, MiGeofence miGeofence) throws PolarisException {
        checkConnect();
        try {
            this.miGeoManagerService.sendDebugEvent(this.callPackageName, location, i10, miGeofence);
        } catch (Exception e10) {
            e10.printStackTrace();
            throw new PolarisException("Exception -->" + e10.getMessage());
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService
    public void unregisterComponent() throws PolarisException {
        registerComponent(null);
    }

    @Override // com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService
    public void updateGeofence(MiGeofence miGeofence) throws PolarisException {
        checkConnect();
        checkIllegalParams(miGeofence);
        try {
            this.miGeoManagerService.updateGeofence(this.callPackageName, miGeofence);
        } catch (Exception e10) {
            e10.printStackTrace();
            throw new PolarisException("Exception -->" + e10.getMessage());
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService
    public void deleteGeofence(MiGeofence miGeofence) throws PolarisException {
        checkConnect();
        try {
            this.miGeoManagerService.deleteGeofence(this.callPackageName, miGeofence);
        } catch (Exception e10) {
            e10.printStackTrace();
            throw new PolarisException("Exception -->" + e10.getMessage());
        }
    }

    @Override // com.xiaomi.gnss.polaris.sdk.geofence.PolarisGeofenceService
    public String addGeofence(MiGeofence miGeofence) throws PolarisException {
        checkConnect();
        checkIllegalParams(miGeofence);
        try {
            return this.miGeoManagerService.addGeofence(this.callPackageName, miGeofence);
        } catch (Exception e10) {
            e10.printStackTrace();
            throw new PolarisException("Exception -->" + e10.getMessage());
        }
    }
}
