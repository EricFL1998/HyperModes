package com.xiaomi.gnss.polaris;

import com.xiaomi.gnss.polaris.geofence.IMiGeoManagerService;

interface IPolarisService {
    String getPolarisVersion();

    IMiGeoManagerService getGeoManagerService();
}
